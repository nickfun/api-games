# api-games

A simple REST API to keep track of my video game library. Mostly a playground for learning Scala, Akka Http, Guardrail, Slick, etc.

## Usage

1. `mvn package` to build the jar
1. `honcho start` to run the app using the Procfile

The default port is 8080, overwrite with `PORT` environment variable

See the `server.yaml` file for the API specification. 
