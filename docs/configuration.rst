.. _configuration:

Configuration
=============

The Nomic configuration can be divided into 2 parts:

- environment configuration
- application configuration

Environment configuration
-------------------------

The environment configuration is about setting few important envirnoment
variables:

* **$JAVA_HOME:** path to java JRE which will be used
* **$NOMIC_HOME:** point to place where is Nomic application installed.
* **$NOMIC_CONF:** path to ``nomic.conf`` file. The default value is ``$NOMIC_HOME/conf/nomic.conf`` but you can specify alternative file.
* **$NOMIC_OPTS:** Java runtime options used when Nomic application is executed

All these environment variables can be set as global env. variables via system or
you can override them in ``$NOMIC_HOME/conf/setenv``.

Application configuration
-------------------------

The application configuration is more detailed configuration that is happening in
the ``nomic.conf`` file. The configuration file is following HOCON_ format and Nomic application
will look for this file in the ``$NOMIC_HOME/conf`` folder.


This example show all of the default values:

.. literalinclude:: ../nomic-app/src/main/resources/application.conf
  :language: properties



.. _HOCON: https://github.com/lightbend/config/blob/master/HOCON.md
