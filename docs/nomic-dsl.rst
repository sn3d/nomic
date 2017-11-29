.. _nomic-dsl:

Nomic DSL Guide
###############

In this guide I would like to explain main cocepts of Nomic DSL and you will
find informations how to write ``nomic.box`` descriptor files. We're using
Groovy as main language here and we created declarative DSL. The descriptors are
declarative way how to tell application what can be deployed and removed. The core of
descriptor file is collection of items called ``Facts``. Each fact can be installed
and reverted. The minimum descriptor script contains 3 required facts: the box name,
the box group and version.

.. code-block:: shell

  group = "app"
  name = "some-box"
  version = "1.0.0"

Variables in descriptor
=======================

In your descriptor scripts, you can also use some useful global variables:

* **group** box group can be any string. You can also structure groups with ``/`` character.
* **name** is box name that must be unique because it's used for identification
* **version** box version
* **user** username the nomic is using for installation/uploading files into HDFS configured in ``nomic.conf``
* **homeDir** each used in HDFS might have his own home directory. It's usefull when you want to sandboxing your applications/analyses.
* **appDir** path to application directory in HDFS where are applications installed. The default value is ``${homeDir}/app``
* **nameNode** the hostname of Name Node (it's value of ``fs.defaultFS`` parameter in your Hadoop configuration)

Also each module (Hive, Hdfs etc) can expose own parameters.

* **hiveSchema** contain default HIVE schema that is configure via ``nomic.conf``. Also good if you want to sandboxing your apps.
* **hiveJdbcUrl** value of ``hive.jdbc.url`` in ``nomic.conf`` that is used by Hive facts.
* **hiveUser** value of ``hive.user`` in ``nomic.conf`` that is used by Hive facts.


Modules & dependencies
======================

You can create an application box with multiple modules. This is useful especially for larger applications when you need to
organize your content. There is also second use case of modules. Because facts inside the Box don't know nothing about
dependencies, you can solve your dependency problem via modules as well.

Let's consider we've got our application 'best-analytics' with some resources and with ``./nomic.box``:

.. code-block:: groovy

  group = "mycompany"
  name = "best-analytics"
  version = "1.0.0"

  hdfs {
    ...
  }

The box is build via command:

.. code-block:: shell

  $ jar cf best-analytics.nomic ./*

Let's imagine we would like to split the content into two modules ``kpi`` and ``rfm``.
We will create a 2 new folders with own ``nomic.box`` they will represents our
new modules.

The ``./kpi/nomic.box``:

.. code-block:: groovy

  group = "mycompany"
  name = "kpi"
  version = "1.0.0"

  ...

and the ``./rfm/nomic.box``:

.. code-block:: groovy

  group = "mycompany"
  name = "rfm"
  version = "1.0.0"

  ...

The final step is to declare these 2 new folders as modules in main ``./nomic.box``:

.. code-block:: groovy

  group = "mycompany"
  name = "best-analytics"
  version = "1.0.0"

  module 'kpi'
  module 'rfm'

The ``module`` fact ensure the main application box will have 2 new dependencies
they will be installed before any resource in main box. That means the installation
install each module first and then the ``best-analytics``. When we install this
bundle, we should see 3 new modules:

.. code-block:: shell

  $ ./bin/nomic install best-analytics.nomic
  $ ./bin/nomic list
  mycompany:best-analytics:1.0.0
  mycompany:kpi:1.0.0
  mycompany:rfm:1.0.0

Also removing of ``best-analytics`` will remove all modules in right order.

Sometimes we also need to tell that our ``rfm`` module depends on ``kpi``.
That can be achieved via ``require`` fact. Let's modify our ``./rfm/nomic.box``:

.. code-block:: groovy

  group = "mycompany"
  name = "rfm"
  version = "1.0.0"

  require name: "kpi", group: this.group, version: $this.version

Now the ``rfm`` module need ``kpi`` first what means the ``kpi`` module will be
installed first.

Facts
=====

Resource
--------

The ``resource`` fact is declaring which resource from your box will be uploaded
to where in HDFS. Let's imagine we've got box archive like:

.. code-block:: shell

  /nomic.box
  /some-file.xml

The descriptor below will install the ``some-file.xml`` into application's
folder (depends how it's configured).

.. code-block:: groovy

  group = "app"
  name = "some-box"
  version = "1.0.0"

  hdfs {
      resource 'some-file.xml'
  }

With small modification you can place any resource to any path. E.g.
following example will demonstrate how to place some file to root ``/app``:

.. code-block:: groovy

  hdfs {
      resource 'some-file.xml' to '/app/workflow.xml'
  }

If you don't place ``/`` character, the file will be paced into working
directory that is basically ``${appDir}``.

.. code-block:: groovy

  hdfs {
      resource 'some-file.xml' to 'workflows/some-workflow.xml'
  }

The example above will ensure the file in ``${appDir}/workflows/some-workflow.xml``
where the ``some-file.xml`` content  will be copied.

Also you can redefine the default working directory:

.. code-block:: groovy

  hdfs("/path/to/app") {
      resource 'some-file.xml'
  }

This example above will install ``some-file.xml`` into ``/path/to/app/some-file.xml``

As I mentioned, the facts are can be installed and uninstalled. In
the ``resource`` case, uninstall means the file will be removed. Anyway you can
mark file by setting property ``keepIt`` to ``true`` and uninstall will
keep the file:

.. code-block:: groovy

  hdfs("/path/to/app") {
      resource 'some-file.xml' keepIt true
  }


Dir
---

You can also declare presence of directory via ``dir`` fact. The declaration
will create empty new directory if is not present yet.

.. code-block:: groovy

  hdfs {
      dir "data"
  }

Because path start without ``/`` character, the directory will be created in
current working directory. This declaration also ensure uninstalling that
means the folder will be removed when uninstall or upgrade. If you wish to
keep it, you can use the ``keepIt`` parameter:

.. code-block:: groovy

  hdfs {
      dir "data" keepIt true
  }


Table
-----

You can declare in descriptor also facts for HIVE. You can declare tables,
schemes, you can also ensure the Hive scripts executions. Everything for
Hive must be wrapped in ``hive``.

Following example show how to create simple table in default schema you
have configured in ``nomic.conf``:

.. code-block:: groovy

  group = "app"
  name = "some-box"
  version = "1.0.0"

  hive {
      table 'authors' from "create_authors_table.q"
  }

In you box, you need to have the hive qurey file ``create_authors_table.q``
that will create table if it's not present in system:

.. code-block:: sql

  CREATE EXTERNAL TABLE authors(
    NAME STRING,
    SURNAME STRING
  )
  STORED AS PARQUET
  LOCATION '/data/authors';

In your hive scripts you can use placeholders they will be replaced with
values from descriptor. Values are declared via ``fields``. This is
sometime usefull when you want e.g. place table into some schema.

.. code-block:: groovy

  hive {
      fields 'APP_DATA_DIR': "${appDir}/data", 'DATABASE_SCHEMA': defaultSchema
      table 'authors' from "create_authors_table.q"
  }

The ``create_authors_table.q`` then use these placeholders:

.. code-block:: sql

  CREATE EXTERNAL TABLE ${DATABASE_SCHEMA}.authors(
    NAME STRING,
    SURNAME STRING
  )
  STORED AS PARQUET
  LOCATION '${APP_DATA_DIR}/authors';

Schema
------

This fact create Hive schema during installation and drop this schema during
uninstall procedure. This fact is useful if you want to declare multiple
schemas or if you don't want to rely on default schema.

.. code-block:: groovy

  hive {
     schema 'my_schema'
  }

As I mentioned the example above will drop the schema during uninstall process
that means also during upgrading. If you want to prevent this, you can mark
schema with ``keepIt``.

.. code-block:: groovy

  hive {
     schema 'my_schema' keepIt true
  }

You can also declare schemas in ``hive`` block. In this case, the schema will
be used as default schema across all facts inside hive block. Also you might
have multiple blocks. The example below demonstrate more complex usage of schemas.

.. code-block:: groovy

  hive("${user}_${name}_staging") {
      table 'some_table' from 'some_script.q'
  }

  hive("${user}_${name}_processing") {
      fields 'DATABASE_SCHEMA': "${user}_${name}_processing"
      table 'some_table' from 'some_script.q'
  }

  hive("${user}_${name}_archive") {
      table 'some_table' from 'some_script.q'
  }

This descriptor script will ensure 3 schemas where name of schema will be
created as composition of user name, box name and some postfix. As you can
see, each section might have own ``fields`` declaration.


Coordinator
-----------

The Nomic application is also integrate Oozie. You can declare the Oozie ``coordinator``
that is acting similar as ``resource`` but also submitting the coordinator with parameters.
This fact also ensure the coordinator will be stoped during removing.

Let's assume we've got simple coordinator available as ``coordinator.xml`` in our
Box. In description file we will declare:

.. code-block:: groovy

  group = "examples"
  name = "oozieapp"
  version = "1.0.0"

  oozie {
      coordinator "coordinator.xml" parameters SOME_PARAMETER: "value 1", "another.parameter": "value 2"
  }

This example copy the XML into HDFS, into application folder and submit a
coordinator job with given parameters like ``SOME_PARAMETER`` and also with
following pre-filled parameters:

============================= =============================================
name                          value
----------------------------- ---------------------------------------------
user.name                     The user from Nomic configuration (e.g ``me``)
nameNode                      The nameNode URL (e.g. ``hdfs://server:8020``)
jobTracker                    Job tracker hostname from configuration with port (e.g. ``server:8032``)
oozie.coord.application.path  Path to coordinator XML in HDFS (e.g. ``/app/examples/oozieapp/coordinator.xml``)
============================= =============================================
