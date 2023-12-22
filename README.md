Manage your groupings in one place, use them in many.

A grouping is a collection of members (e.g., all full-time Hilo faculty). UH Groupings allows you to manage grouping memberships, control members' self-service options, designate sync destinations, and more.

Groupings can be synchronized with one or more of the following: email LISTSERV lists, attributes for access control via CAS and LDAP, etc. Additionally, UH Groupings allows you to leverage group data from official sources, which can substantially reduce the manual overhead of membership management.

UH Groupings utilizes the Internet2 Grouper project.  Grouper is an enterprise access management system designed for the highly distributed management environment and heterogeneous information technology environment common to universities.

[![License](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/apereo/cas/blob/master/LICENSE)
[![Build and Test Status](https://github.com/uhawaii-system-its-ti-iam/uh-groupings-ui/actions/workflows/build_badge.yml/badge.svg)](https://github.com/uhawaii-system-its-ti-iam/uh-groupings-ui/actions/workflows/build_badge.yml)
[![Coverage Status](https://github.com/uhawaii-system-its-ti-iam/uh-groupings-ui/blob/badges/jacoco.svg)](https://github.com/uhawaii-system-its-ti-iam/uh-groupings-ui/actions/workflows/coverage.yml)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/5584742766ed46faa855dafe41a1cdc9)](https://www.codacy.com/gh/uhawaii-system-its-ti-iam/uh-groupings-ui/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=uhawaii-system-its-ti-iam/uh-groupings-ui&amp;utm_campaign=Badge_Grade)
[![CodeQL](https://github.com/uhawaii-system-its-ti-iam/uh-groupings-api/actions/workflows/codeql.yml/badge.svg)](https://github.com/uhawaii-system-its-ti-iam/uh-groupings-ui/actions/workflows/codeql.yml)

##### Java
You'll need a Java JDK to build and run the project (version 17).

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
