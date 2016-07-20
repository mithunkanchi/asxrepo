Feature: Test FCMA DownStream Futures and Options.
  As a Downstream User
  I want to make sure that my futures and options are properly updated in DownStream whenever there is a change from golden source
  So that I have the right information

  Background:
    Given that the tests are to be run on "test" environment

 #New and Amend Scenarios
  Scenario Outline: Scenario sending an input xml to ESB and checking in downstream db
    Given that I prepare the "<InputXml>" for submission
    And I submit the input xml to ESB
    When I retrieve data from the Downstream db
    Then the data should match to the xml submitted

  #Input XMLS are covering both NEW and AMEND scenarios
    Examples:
      |InputXml|
      |src\\test\\resources\\data\\New\\EAH2022F-NEW.xml|
      |src\\test\\resources\\data\\New\\EAH2022F-AMEND.xml|
      |src\\test\\resources\\data\\New\\EAF2022C0002000-NEW.xml|
      |src\\test\\resources\\data\\New\\EAF2022P0002000-NEW.xml|
      |src\\test\\resources\\data\\New\\EAF2022C0002100-NEW.xml|
      |src\\test\\resources\\data\\New\\EAF2022P0002100-NEW.xml|
      |src\\test\\resources\\data\\New\\EAF2022C0002000-AMEND.xml|
      |src\\test\\resources\\data\\New\\EAF2022P0002000-AMEND.xml|
      |src\\test\\resources\\data\\New\\EAF2022C0002100-AMEND.xml|
      |src\\test\\resources\\data\\New\\EAF2022P0002100-AMEND.xml|
      |src\\test\\resources\\data\\New\\EAF2022P0002100-AMEND.xml|
    #  |src\\test\\resources\\data\\New\\XDH2016C0098040o_NEW_ONO.xml|
    #  |src\\test\\resources\\data\\New\\XDH2016P0098040o_NEW_ONO.xml|


  #Delete Tests for Options
  Scenario Outline: Scenario sending a delete  xml to ESB and checking in downstream db
    Given that I prepare the "<InputXml>" for submission
    When I submit the input xml to ESB
    Then there should be no data in db
    Examples:
      |InputXml|
      |src\\test\\resources\\data\\New\\EAF2022C0002000-DELETE.xml|
      |src\\test\\resources\\data\\New\\EAF2022P0002000-DELETE.xml|
      |src\\test\\resources\\data\\New\\EAF2022C0002100-DELETE.xml|
      |src\\test\\resources\\data\\New\\EAF2022P0002100-DELETE.xml|
      |src\\test\\resources\\data\\New\\EAH2022F-DELETE.xml|
    # |src\\test\\resources\\data\\New\\XDH2016C0098040o_DELETE_ONO.xml|
    # |src\\test\\resources\\data\\New\\XDH2016P0098040o_DELETE_ONO.xml|

  Scenario: Closing all the open connections
    Given I close all my active connections
