#!/bin/bash

cp ../../../../../../../target/sd-project-1.0-SNAPSHOT.jar ..
cd .. # returning to project's root directory

serverPort=$1

java -cp ".:./jars/libthrift-0.10.0.jar:./jars/slf4j.jar:./jars/slf4j-simple-1.7.25.jar:sd-project-1.0-SNAPSHOT.jar" graphservice.GraphClient $serverPort
