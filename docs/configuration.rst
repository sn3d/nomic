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

.. code-block:: properties

  ################################################################################
  # General Nomic configuration
  ################################################################################

  # user what will be used by Nomic application. Also folders and files in HDFS
  # will be owned by this used.
  #
  # default value is system user you're logged with
  nomic.user = ${user.name}

  # Nomic home on FileSystem where nomic will look for configuration, write
  # logs etc.
  #
  # default value is in user's home folder as '.nomic' folder
  nomic.home = ${user.home}"/.nomic"

  # This is home folder that will be used by Nomic in HDFS.
  #
  # default value is hdfs://server/user/{user.name}
  nomic.hdfs.home = "/user/"${nomic.user}

  # Point to 'app' folder in HDFS where are all boxes deployed
  #
  # default value is hdfs://server/user/{user.name}/app
  nomic.hdfs.app.dir = ${nomic.hdfs.home.dir}"/app"

  # Point to 'repository' folder in HDFS where Nomic will store metadata
  #
  # default value is hdfs://server/user/{user.name}/.nomic
  nomic.hdfs.repository.dir = ${nomic.hdfs.home.dir}"/.nomic"


  ################################################################################
  # HDFS configuration
  ################################################################################
  # What kind of adapter to HDFS will be used. The possible values are 'hdfs' and 'simulator'.
  # Simulator just simulate HDFS on your FileSystem. It's useful for debuging etc..
  hdfs.adapter = "hdfs"

  # The directory where simulator adapter will store all files. Uncomment this
  # configuration property if you set adater to 'simulator'
  #
  #hdfs.simulator.basedir = ${nomic.home}"/hdfs"

  # Point to Hadoop core configuration file. It's relevant for real 'hdfs' adapter.
  hdfs.core.site = ${nomic.home}"/conf/core-site.xml"

  # Point to Hadoop HDFS configuration file. It's relevant for real 'hdfs' adapter.
  hdfs.hdfs.site = ${nomic.home}"/conf/hdfs-site.xml"

  ################################################################################
  # HIVE configuration
  ################################################################################

  # host for Hive JDBC
  hive.host = "localhost:10000"

  # this is JDBC connection string to HIVE
  hive.jdbc.url = "jdbc:hive2://"${hive.host}

  # The default HIVE schema
  hive.schema = ${nomic.user}

  # Username for HIVE connection
  hive.user = ${nomic.user}

  # Password for HIVE connection
  hive.password = ""


  ################################################################################
  # OOZIE configuration
  ################################################################################

  # URL that point to oozie server where is Oozie REST API available. It's
  # just hostname and port without `/oozie/v1` postfix. This postfix is handled
  # by application itself
  oozie.url = "https://localhost:11000"

  # Job tracker URL that will be used as pre-filled value when you submitting
  # Oozie job (coordinator)
  oozie.jobTracker = ""




.. _HOCON: https://github.com/lightbend/config/blob/master/HOCON.md
