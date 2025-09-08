= Docker Compose

This directory contains Docker Compose file for starting the Chroma vector store,
as required in chapter 3 of Spring AI in Action.

To start Chroma, be sure that Docker is running and then you can start Chroma
like this:

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
