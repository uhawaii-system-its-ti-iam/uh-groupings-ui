A web application to display groupings used by UH.

[![Build Status](https://travis-ci.org/uhawaii-system-its-ti-iam/uhgroupings.png?branch=master)](https://travis-ci.org/uhawaii-system-its-ti-iam/uhgroupings)
[![Coverage Status](https://coveralls.io/repos/github/uhawaii-system-its-ti-iam/uhgroupings/badge.svg?branch=master)](https://coveralls.io/github/uhawaii-system-its-ti-iam/uhgroupings?branch=master)
##### Build Tool
First, you need to download and install maven (version 3.2.1+).

Be sure to set up a M2_REPO environment variable.

##### Java
You'll need a Java JDK to build and run the project (version 1.8).

The files for the project are kept in a code repository,
available from here:

https://github.com/uhawaii-system-its-ti-iam/uhgroupings

##### Building
Install the necessary project dependencies:

    $ mvn install

To run the Application from the Command Line:

    $ mvn clean spring-boot:run

To build a deployable war file for local development, if preferred:

    $ mvn clean package

You should have a deployable war file in the target directory.
Deploy as usual in a servlet container, e.g. tomcat.

##### Running Unit Tests
The project includes Unit Tests for various parts of the system.
For this project, Unit Tests are defined as those tests that will
rely on only the local development computer.
A development build of the application will run the Unit Tests.
A test and production build of the application will run both the
Unit Tests and the System Tests (which may require network access).
You can also run specific Unit Tests using the appropriate command
line arguments.

To run the Unit Tests with a standard build:

    $ mvn clean test

To run a test class:

    $ mvn clean test -Dtest=StringsTest

To run a single method in a test class:

    $ mvn clean test -Dtest=StringsTest#trunctate

##### Running System Tests
The project files include a handful of System Tests.
For this project, System Tests are defined as those tests that may
call live remote systems, such as a search against the production
LDAP server. A standard build of the application will exclude the
System Tests, but you can explicitly run them by specifying the
appropriate command line argument.

To run the System Tests:

    $ mvn -Dtest=*SystemTest clean test
