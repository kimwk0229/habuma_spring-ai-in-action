package com.example.gamerulesloader;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
public class GameRulesLoaderApplication {

  private static final Logger LOGGER = LoggerFactory.getLogger(GameRulesLoaderApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(GameRulesLoaderApplication.class, args);
  }

  @Bean
  ApplicationRunner go(FunctionCatalog catalog) {
    Runnable composedFunction = catalog.lookup(null);
    return args -> {
      composedFunction.run();
    };
  }

  // tag::newDocumentReader[]
  @Bean
  Function<Flux<Message<byte[]>>, Flux<Document>> documentReader() {
    return resourceFlux -> resourceFlux
        .map(message -> {
          var fileName = (String) message.getHeaders().get("file_name");   // <1>
          LOGGER.info("Reading document from file: {}", fileName);

          var fileBytes = message.getPayload();
          Document document = new TikaDocumentReader(
              new ByteArrayResource(fileBytes))
              .get()
              .getFirst();
          if (isPremiumDocument(fileName)) {
            document.getMetadata().put("documentType", "PREMIUM"); // <2>
          }
          return document;
        })
        .subscribeOn(Schedulers.boundedElastic());
  }

  private boolean isPremiumDocument(String fileName) {    // <3>
    var baseFilename = fileName
        .substring(0, fileName.toString().lastIndexOf('.'));
    return baseFilename.endsWith("-premium");
  }
  // end::newDocumentReader[]

  @Bean
  Function<Flux<Document>, Flux<List<Document>>> splitter() {
    return documentFlux ->
        documentFlux
            .map(incoming ->
                new TokenTextSplitter().apply(List.of(incoming))).subscribeOn(Schedulers.boundedElastic());
  }

  @Value("classpath:/promptTemplates/nameOfTheGame.st")
  Resource nameOfTheGameTemplateResource;

  @Bean
  Function<Flux<List<Document>>, Flux<List<Document>>>
              titleDeterminer(ChatClient.Builder chatClientBuilder) {

    ChatClient chatClient = chatClientBuilder.build();

    return documentListFlux -> documentListFlux
        .map(documents -> {
          if (!documents.isEmpty()) {
            Document firstDocument = documents.getFirst(); // <1>

            GameTitle gameTitle = chatClient.prompt()
                .user(userSpec -> userSpec
                    .text(nameOfTheGameTemplateResource)
                    .param("document", firstDocument.getText())) // <2>
                .call()
                .entity(GameTitle.class);

            if (Objects.requireNonNull(gameTitle).title().equals("UNKNOWN")) {
              LOGGER.warn("Unable to determine the name of a game; " +
                  "not adding to vector store.");
              documents = Collections.emptyList();
              return documents;
            }

            LOGGER.info("Determined game title to be {}", gameTitle.title());
            documents = documents.stream().peek(document -> {
              document.getMetadata().put("gameTitle", gameTitle.getNormalizedTitle()); // <3>
            }).toList();
          }

          return documents;
        });
  }

  @Bean
  Consumer<Flux<List<Document>>> vectorStoreConsumer(VectorStore vectorStore) {
    return documentFlux -> documentFlux
      .doOnNext(documents -> {
        long docCount = documents.size();
        LOGGER.info("Writing {} documents to vector store.", docCount);

        vectorStore.accept(documents);

        LOGGER.info("{} documents have been written to vector store.", docCount);
      })
      .subscribe();
  }

}
