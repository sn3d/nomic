Feature: HDFS template

  Scenario: Install template
    Given the "nomic-examples:template" box is not installed yet
    When install the box from "../nomic-examples/template"
    Then file 'simple-template.xml' is installed in app dir with replaced values
    And file 'beautiful-code.xml' is installed in /test folder

