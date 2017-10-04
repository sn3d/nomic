#!/bin/bash

if [ "$TRAVIS_BRANCH" == "master" ]; then
    echo -e 'Releasing branch:$['$TRAVIS_BRANCH'] tag: ['$TRAVIS_TAG']'
    ./gradlew build test
    case "$TRAVIS_TAG" in
        "")
            ./gradlew clean assemble nomic-dist:bintrayUpload -PbuildNr=${TRAVIS_BUILD_NUMBER}
        ;;
        *)
            ./gradlew clean  assemble nomic-dist:bintrayUpload -Pversion=${TRAVIS_TAG}
        ;;
    esac
else
    echo -e 'Building and testing branch: ['$TRAVIS_BRANCH']'
    ./gradlew clean build test
fi