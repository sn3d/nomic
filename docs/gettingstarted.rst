.. _gettingstarted:

Getting Started
###############

Before you start creating own boxes and installing them into
your Hadoop ecosystem, you have to get,install and setup the Nomic application.

.. Important::
  On first place check if your environment fulfills the most important
  requirement: **Java 1.8**

When the Java is present in your environment, we can get the application. The
various distribution packages are available on Bintray_. Pick the package
depends on what kind of installation you will choose. For more details about
installation on various systems, see the :ref:`installation` section. For
experimenting and playing around the Nomic, I recommend the TAR(or ZIP) distribution.

Download the latest version of application and unpack it to some working
directory:

.. code-block:: shell

  $ wget https://dl.bintray.com/sn3d/nomic-repo/nomic-0.1.25-bin.tar
  $ tar -xzf ./nomic-0.1.25-bin.tar
  $ cd ./nomic-0.1.25

Configuration
-------------

After unpacking you will need to configure Nomic application. First, you should
copy the Hadoop ``code-site.xml`` and ``hdfs-site.xml`` into ``./conf`` folder. The
Nomic use these files for connecting to HDFS. If you're not happy with copying
these files, you can determine paths to files via configuration file ``conf/nomic.conf``.

.. code-block:: properties

  hdfs.core.site=/path/to/core-site.xml
  hdfs.hdfs.site=/path/to/hdfs-site.xml

The default configuration is using user's home folder. The boxes are installed
into ``hdfs://${USER_HOME}/app`` folder and nomic store metadata into ``hdfs://${USER_HOME}/.nomic``.

You can test you configuration by executing

.. code-block:: shell

  $ ./bin/nomic config

After execution you should see how your Nomic instance is configured. If this command
failed, probably you have something wrong in ``conf/nomic.conf`` or your ``core-site.xml``
and ``hdfs-site.xml`` are invalid.

Your first box
--------------

Now it's time to create your first box. Let's imagine we've got simple Oozie ``workflow.xml``
and we want to deploy it as analytics application. We have to create a ``nomic.box`` nearby
workflow file with content:

.. code-block:: groovy

  group = "examples"
  name = "simple-workflow"
  version = "1.0.0"

  hdfs {
    resource "workflow.xml"
  }

For more information about how to write box descriptor files you can visit :ref:`nomic-dsl`
section. Then we need to pack both files into archive bundle. For that purpose we can
use java JAR utility:

.. code-block:: shell

  $ jar cf simple-workflow.nomic ./workflow.xml ./nomic.box

Huray! You've got your first box ready for deployment. Let's deploy it.

Deploying and removing
----------------------

In previous section we've created our first nomic box. We can deploy it easily by
executing command:

.. code-block:: shell

  $ ./bin/nomic install simple-workflow.nomic

In your HDFS, you should have ``workflow.xml`` available in application folder ``${USER_HOME}/app/examples/simple-workflow``.
Also after executing ``./bin/nomic list`` command, you will see the box was
installed and what version of box is available. We should see output:

.. code-block:: shell

  $ ./bin/nomic list
  examples:simple-workflow:1.0.0

One of the primary goals of Nomic application is not only deploying but also safe
removing of deployed boxes. Let's remove our box:

.. code-block:: shell

  $ ./bin/nomic remove examples:simple-workflow:1.0.0

The remove command will erase only these resources, they were deployed. It's inverse
of ``deploy`` command.

.. _Bintray: https://dl.bintray.com/sn3d/nomic-repo/
