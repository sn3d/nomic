Feature: Hive 'table'

  Scenario: Install box with hive table
    Given the "nomic-examples:hive-advanced" box is not installed yet
     And  the "books" table does not exist
    When I install the box with hive table from "../nomic-examples/hive-advanced"
    Then table "books" in hive must exist
    And table "users" in hive must exist
    And the schema "nomic_test" must be created

  Scenario: Uninstall box with hive table
    Given the box "../nomic-examples/hive-advanced" is installed
    When Uninstall the "nomic-examples:hive-advanced:1.0.0" box
    Then table "books" in hive must be removed
    And table "users" in hive must exist

