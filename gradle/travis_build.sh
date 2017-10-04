#!/bin/sh

./gradlew build test

if [ "$TRAVIS_BRANCH" == "master" ]; then
    echo -e 'Releasing branch:$['$TRAVIS_BRANCH'] tag: ['$TRAVIS_TAG']'
    case "$TRAVIS_TAG" in
        "")
            ./gradlew assemble nomic-dist:bintrayUpload -PbuildNr=${TRAVIS_BUILD_NUMBER}
        ;;
        *)
            ./gradlew assemble nomic-dist:bintrayUpload -Pversion=${TRAVIS_TAG}
        ;;
    esac
fi