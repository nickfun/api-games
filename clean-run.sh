#!/bin/bash
export PORT=8080
export DOMAIN=http://localhost:8080
mvn clean
mvn package && java -jar target/apigames-1.0-SNAPSHOT-fatjar.jar
