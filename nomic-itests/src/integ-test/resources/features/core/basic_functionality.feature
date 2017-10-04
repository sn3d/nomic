Feature: Basic functionality

  Scenario: Install box
    Given the box "nomic-examples:hello-world" is not present
    When the box "../nomic-examples/hello-world" in version "1.0.0" is installed
    Then box "nomic-examples:hello-world:1.0.0" must be available in nomic
    And file "old.txt" must be available in HDFS

  Scenario: Upgrade box to next version
    Given the box "../nomic-examples/hello-world" is present as "nomic-examples:hello-world:1.0.0"
    And file "old.txt" must be available in HDFS
    When the box is upgraded to new version from "../nomic-examples/hello-world-2"
    Then box "nomic-examples:hello-world:2.0.0" must be available in nomic
    And file "new.txt" must be available in HDFS
    And file "old.txt" must be missing in HDFS
