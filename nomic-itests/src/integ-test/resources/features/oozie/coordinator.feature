Feature: Oozie 'coordinator'

  Scenario: Install box with oozie coordinator
    Given the "nomic-examples:oozie" box is not installed yet
    When I install the box with ooozie coordinator
    Then the coordinator XML should be available in HDFS and should start



