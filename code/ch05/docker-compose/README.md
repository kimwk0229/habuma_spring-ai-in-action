= Docker Compose

This directory contains Docker Compose files for starting the Chroma vector store
and MongoDB, as required in chapter 4 of Spring AI in Action.

Chroma is the vector store used by the RAG functionality in the Board Game Buddy
example, starting in chapter 3. MongoDB is used as a database for storing chat
memory for the custom persistent implementation of `ChatMemory` in chapter 4
(e.g., the `board-game-buddy_2` project).

These will need to be running before those projects will work.

== Chroma

To start only Chroma, be sure that Docker is running and then you can start
Chroma like this:

```
$ docker compose --file chroma.yaml up
```

You can shutdown Chroma (but maintain the data volume) with the following command:

```
$ docker compose --file chroma.yaml down
```

Or you can shutdown Chroma and remove the data volume with the following command:

```
$ docker compose --file chroma.yaml down --volumes
```

== Starting both Chroma and MongoDB

To start MongoDB along with Chroma for the example created in section 4.5.1, be
sure that Docker is running and then you can start Chroma and MongoDB like this:

```
$ docker compose --file chroma.yaml --file mongo.yaml up
```

You can shutdown MongoDB (but maintain the data volume) with the following command:

```
$ docker compose --file chroma.yaml --file mongo.yaml down
```

Or you can shutdown MongoDB and remove the data volume with the following command:

```
$ docker compose --file chroma.yaml --file mongo.yaml down --volumes
```
