Manage your groupings in one place, use them in many.

A grouping is a collection of members (e.g., all full-time Hilo faculty). UH Groupings allows you to create groupings, manage grouping memberships, control members' self-service options, designate sync destinations, and more.

Groupings can be synchronized with one or more of the following: email LISTSERV lists, attributes for access control via CAS and LDAP, etc. Additionally, UH Groupings allows you to leverage group data from official sources, which can substantially reduce the manual overhead of membership management.

UH Groupings utilizes the Internet2 Grouper project.  Grouper is an enterprise access management system designed for the highly distributed management environment and heterogeneous information technology environment common to universities.

[![License](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/apereo/cas/blob/master/LICENSE)
[![Build Status](https://travis-ci.org/uhawaii-system-its-ti-iam/uh-groupings-ui.png?branch=master)](https://travis-ci.org/uhawaii-system-its-ti-iam/uh-groupings-ui)
[![Coverage Status](https://coveralls.io/repos/github/uhawaii-system-its-ti-iam/uh-groupings-ui/badge.svg?branch=master)](https://coveralls.io/github/uhawaii-system-its-ti-iam/uh-groupings-ui?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/69274196397f4587b88b0ecce5856d0a)](https://www.codacy.com/app/mhodgesatuh/uhgroupings?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=uhawaii-system-its-ti-iam/uhgroupings&amp;utm_campaign=Badge_Grade)
##### Build Tool
First, you need to setup [Maven Wrapper](https://www.baeldung.com/maven-wrapper) which allows the use of Maven withought installing it.

    $ mvn -N io.takari:maven:wrapper

##### Java
You'll need a Java JDK to build and run the project (version 1.8).

The files for the project are kept in a code repository,
available from here:

https://github.com/uhawaii-system-its-ti-iam/uh-groupings-ui

##### Building
To run the Application from the Command Line:

    $ ./mvnw clean spring-boot:run

To build a deployable war file for local development, if preferred:

    $ ./mvnw clean package

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

    $ ./mvnw clean test

To run a test class:

    $ ./mvnw clean test -Dtest=StringsTest

To run a single method in a test class:

    $ ./mvnw clean test -Dtest=StringsTest#trunctate

##### Running System Tests
The project files include a handful of System Tests.
For this project, System Tests are defined as those tests that may
call live remote systems, such as a search against the production
LDAP server. A standard build of the application will exclude the
System Tests, but you can explicitly run them by specifying the
appropriate command line argument.

To run the System Tests:

    $ ./mvnw -Dtest=*SystemTest clean test
