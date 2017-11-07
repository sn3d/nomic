Feature: HDFS Resource

  @debug
  Scenario: Install box with HDFS resources
    Given the "nomic-examples:hello-world" box is not installed yet
    When install the box from "../nomic-examples/hello-world"
    Then the file "hello-world.txt" is copied into nomicHdfsAppDir/nomic-examples/hello-world folder in HDFS

  Scenario: Uninstall box with HDFS resources
    Given the box "../nomic-examples/hello-world" is installed
    When Uninstall the "nomic-examples:hello-world:1.0.0" box
    Then the file "hello-world.txt" is removed from nomicHdfsAppDir/nomic-examples/hello-world folder in HDFS

  Scenario: Install resource to specific folder in root
    Given the "nomic-examples:hello-world" box is not installed yet
    When install the box from "../nomic-examples/hello-world"
    Then the file "first.txt" is copied into /test folder in HDFS root

  Scenario: Install resource to specific folder in working dir
    Given the "nomic-examples:hello-world" box is not installed yet
    When install the box from "../nomic-examples/hello-world"
    Then the file "second.txt" is copied into working dir, into /test
