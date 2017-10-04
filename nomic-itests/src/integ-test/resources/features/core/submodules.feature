Feature: Submodules

  Scenario: Install the box with submodules
    Given the boxes "nomic-examples:submodules" and "nomic-examples:submodule-1" are not yet installed
    When the box "../nomic-examples/submodules" is installed
    Then module "nomic-examples:submodules" with submodule "nomic-examples:submodule-1" are present in nomic

  Scenario: Uninstall the box with submodules
    Given the box "../nomic-examples/submodules" is installed
    When the box "nomic-examples:submodules" is removed
    Then module "nomic-examples:submodules" with submodule "nomic-examples:submodule-1" are not present in nomic
