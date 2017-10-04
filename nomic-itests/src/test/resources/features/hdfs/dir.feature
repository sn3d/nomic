Feature: HDFS dir

  Scenario: Create empty directories
    Given the "nomic-examples:directories" box is not installed yet
    When install the box from "../nomic-examples/directories"
    Then the directory 'dir-stay' is created in nomicHdfsHomeDir in HDFS
    And also 'dir-remove' marked as removable must be created in nomicHdfsHomeDir

  Scenario: Uninstall empty directories
    Given the box "../nomic-examples/directories" is installed
    When Uninstall the "nomic-examples:directories:1.0.0" box
    Then the directory 'dir-stay' must exists
    And 'dir-remove' marked as removable must be removed