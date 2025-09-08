# Top Songs

This is the "top songs" example application that is built in the book Spring AI
in Action, in chapter 2, section 2.4.1.

## Quick Start
You'll need an OpenAI API key. You can get one by signing up at
https://platform.openai.com/ and creating an API key. Set it to an environment
variable named `OPENAI_API_KEY` before running the application.

Run the following command to start the application:

```shell
./gradlew bootRun
```

Then try it out by submitting a request to the API exposed by the application.
Using `curl`:

```shell
curl localhost:8080/ask?year=1981
```

Or using HTTPie:

```shell
http :8080/topSongs?year=1981
```
