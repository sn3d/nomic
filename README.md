# Nomic

[![Build Status](https://travis-ci.org/sn3d/nomic.svg?branch=master)](https://travis-ci.org/sn3d/nomic)
[![Download](https://api.bintray.com/packages/sn3d/nomic-repo/nomic-app/images/download.svg) ](https://bintray.com/sn3d/nomic-repo/nomic-app/_latestVersion)
[![Docs](https://readthedocs.org/projects/nomic/badge/?version=latest) ](http://nomic.readthedocs.io/en/latest/)

Nomic is small tool/package manager that automatise our process of deployment and installation of analytics applications
into Hadoop ecosystem. The analytics are packaged into archive called *Box*. Each box has own descriptor script file in
root called `nomic.box` where is determined what and where is installed. The descriptor script is based on Groovy
with own DSL.

## How to build

You can easily build distribution of Nomic from source code by executing command project root:

    ./gradlew assembly

After command is succesfully executed, you can find ZIP, TAR and also RPM distribution in `nomic-dist/build/distributions`
folder. Now you can continue with installation depends on distribution you pick.

## Documentation

The documentation is available [here](http://nomic.readthedocs.io/en/latest).

## For developers

Some useful commands:

- `./gradlew clean build` build and run unit tests. 
- `./gradlew assemble` build the distribution of Nomic app. The output is in `./nomic-dist/build/distributions` folder.
- `./gradlew integTest` run integration tests
- `cd ./docs && make html` build a HTML documentation in `./docs/_build/html` 

## License

   Copyright 2017 vrabel.zdenko@gmail.com

   Licensed under the [Apache License, Version 2.0](./LICENSE)
