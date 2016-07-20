Feature: Test FCMA DownStream Futures and Options.
  As a Downstream User
  I want to make sure that my futures and options are properly updated in DownStream whenever there is a change from golden source
  So that I have the right information

  Background:
    Given that the tests are to be run on "test" environment
    And I clear the downstream Poison Queue

 # Other Scenarios:
  Scenario Outline: Scenario sending an input xml to ESB and checking in downstream db
    Given that I prepare the "<InputXml>" for submission
    And I submit the input xml to ESB
    When I retrieve data from the Downstream db
    Then the data should match to the xml submitted

  #Input XMLS are covering both NEW and AMEND scenarios
    Examples:
      |InputXml|
      |src\\test\\resources\\data\\OtherScenarios\\EAH2022F-NEW.xml|
      |src\\test\\resources\\data\\OtherScenarios\\EAF2022C0002000-NEW.xml|

  #Error Scenarios:
  Scenario Outline: Scenario sending an input xml to ESB and checking in downstream db
    Given that I prepare the "<InputXml>" for submission
    And I submit the input xml to ESB
    And I check that the error has been logged in the log file
    And the inputXml is in the poison Queue
    Examples:
      |InputXml|
      |src\\test\\resources\\data\\OtherScenarios\\EAH2022F-NEW.xml|
      |src\\test\\resources\\data\\OtherScenarios\\EAH2022F_INVALID_MSG_TYPE.xml|
      |src\\test\\resources\\data\\OtherScenarios\\EAH2022F_NULL_TAG.xml|
      |src\\test\\resources\\data\\OtherScenarios\\EAF2022C0002000-NEW.xml|
      |src\\test\\resources\\data\\OtherScenarios\\EAF2022C0002000_INVALID_MSG_TYPE.xml|

  #Delete Tests before next run
  Scenario Outline: Scenario sending a delete  xml to ESB and checking in downstream db
    Given that I prepare the "<InputXml>" for submission
    When I submit the input xml to ESB
    Then there should be no data in db
    Examples:
      |InputXml|
      |src\\test\\resources\\data\\New\\EAF2022C0002000-DELETE.xml|
      |src\\test\\resources\\data\\New\\EAH2022F-DELETE.xml|

  #Error Scenarios:
  Scenario Outline: Scenario sending an input xml to ESB and checking in downstream db
    Given that I prepare the "<InputXml>" for submission
    And I submit the input xml to ESB
    And I check that the error has been logged in the log file
    And the inputXml is in the poison Queue
    Examples:
      |InputXml|
      |src\\test\\resources\\data\\New\\EAH2022F-AMEND.xml|
      |src\\test\\resources\\data\\New\\EAF2022C0002000-DELETE.xml|
      |src\\test\\resources\\data\\New\\EAH2022F-DELETE.xml|

  Scenario: Closing all the open connections
    Given I close all my active connections



