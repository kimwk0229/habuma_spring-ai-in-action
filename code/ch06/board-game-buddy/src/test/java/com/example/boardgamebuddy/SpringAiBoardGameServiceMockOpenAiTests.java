package com.example.boardgamebuddy;

import com.example.boardgamebuddy.gamedata.GameRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.function.DefaultFunctionCallbackResolver;
import org.springframework.ai.model.function.FunctionCallbackResolver;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.stringtemplate.v4.ST;

import java.nio.charset.Charset;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(components = {
    AiConfig.class,
    GameTools.class,
    SpringAiBoardGameService.class,
    BoardGameService.class})
public class SpringAiBoardGameServiceMockOpenAiTests {

    @MockitoBean
    GameRepository gameRepository;

    @MockitoBean
    VectorStore vectorStore;

    @Autowired
    MockRestServiceServer mockServer;

    @Autowired
    SpringAiBoardGameService service;

    @TestConfiguration
    public static class TestConfig {
        @Bean
        public ChatClient.Builder chatClientBuilder(RestClient.Builder restClientBuilder, WebClient.Builder webClientBuilder, FunctionCallbackResolver functionCallbackContext) {
            OpenAiChatModel openAiChatModel = new OpenAiChatModel(
                    new OpenAiApi("https://api.openai.com", "TEST_API_KEY", restClientBuilder, webClientBuilder),
                    new OpenAiChatOptions(),
                    functionCallbackContext,
                    new RetryTemplate());
            return ChatClient.builder(openAiChatModel);
        }
    }

    @Test
    public void testStuff() throws Exception {
        String expectedAnswer = "Checkers is a game for two people.";
        String content = "{\\\"gameTitle\\\":\\\"Checkers\\\", \\\"answer\\\":\\\"" + expectedAnswer + "\\\"}";
        mockOpenAiChatResponse(content);
        Answer answer = service.askQuestion(new Question("Checkers","How many can play?"), "conversation-id-1");
        Assertions.assertThat(answer.answer()).isEqualTo(expectedAnswer);
    }

    private void mockOpenAiChatResponse(String content) throws Exception {
        ClassPathResource responseResource = new ClassPathResource("/response.json");
        ST st = new ST(StreamUtils.copyToString(responseResource.getInputStream(), Charset.defaultCharset()), '$', '$');
        st = st.add("content", content);
        mockServer.expect(requestTo("https://api.openai.com/v1/chat/completions"))
            .andRespond(withSuccess(st.render(), MediaType.APPLICATION_JSON));
    }

    @TestConfiguration
    public static class TestConfig2 {
        @Bean
        FunctionCallbackResolver functionCallbackContext() {
            return new DefaultFunctionCallbackResolver();
        }
    }

}