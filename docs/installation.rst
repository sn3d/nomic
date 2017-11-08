.. _installation:

Installation
############

.. Important::
  On first place check if your environment fulfills the most important
  requirement: **Java 1.8**

Standalone Installation (Windows)
---------------------------------

For Windows systems, you can yse standalone distribution available as ZIP archive.
All you need is just download the ZIP from Bintray_, unpack it into some folder
and run ``bin\nomic.bat``.

Standalone Installation (Linux/Mac OS)
--------------------------------------

For Linux and Mac OS systems, you can use standalone distribution available as TAR
archive. All you need is just download it, unpack it and run.

.. code-block:: shell

  $ wget https://dl.bintray.com/sn3d/nomic-repo/nomic-{version}-bin.tar
  $ tar -xzf ./nomic-{version}-bin.tar
  $ cd ./nomic-{version}
  $ ./bin/nomic

All configuration files, libraries and shell scripts are placed in one folder.


Installation from RPM (RedHat)
------------------------------

If you're using Linux system with YUM or RPM, you can install application
as RPM directly:

.. code-block:: shell

  $ sudo rpm -i https://dl.bintray.com/sn3d/nomic-repo/nomic-{version}.noarch.rpm
  $ nomic


Or you can add YUM repository with Nomic application:

.. code-block:: shell

  $ wget https://bintray.com/sn3d/nomic-rhel/rpm -O bintray-sn3d-nomic-rhel.repo
  $ sudo mv bintray-sn3d-nomic-rhel.repo /etc/yum.repos.d/

And then you can use YUM for installing/upgrading:

.. code-block:: shell

  $ sudo yum install nomic
  $ nomic

Now you can type ``nomic`` in shell. You should see the nomic's output. The
configuration is placed in ``/etc/nomic``. The main application is placed
in ``/usr/share/nomic`` folder.


.. _Bintray: https://dl.bintray.com/sn3d/nomic-repo/
