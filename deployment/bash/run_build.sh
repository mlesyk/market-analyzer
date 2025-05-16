#!/bin/bash
if [ -d "./target/" ]
then
        rm -r target/
fi
PROJECT_NAME=$(basename `pwd`)
docker run -i --rm --name build-jar-${PROJECT_NAME} --network host -v ${HOME}/.m2:/var/maven/.m2 \
-v ${PWD}:/usr/src/app -w /usr/src/app --user $(id -u):$(id -g) -e MAVEN_CONFIG=/var/maven/.m2 \
maven:3.8.6-eclipse-temurin-11-alpine mvn -Duser.home=/var/maven -DskipTests clean package