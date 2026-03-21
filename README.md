## UH Groupings Project
### The UI for UH Groupings.

Manage your groupings in one place, use them in many.

A grouping is a collection of members (e.g., all full-time Hilo faculty). UH Groupings allows you to manage grouping memberships, control members' self-service options, designate sync destinations, and more.

Groupings can be synchronized with one or more of the following: email LISTSERV lists, attributes for access control via CAS and LDAP, etc. Additionally, UH Groupings allows you to leverage group data from official sources, which can substantially reduce the manual overhead of membership management.

UH Groupings utilizes the Internet2 Grouper project.  Grouper is an enterprise access management system designed for the highly distributed management environment and heterogeneous information technology environment common to universities.

[![License](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/apereo/cas/blob/master/LICENSE)
[![Build and Test Status](https://github.com/uhawaii-system-its-ti-iam/uh-groupings-ui/actions/workflows/build_badge.yml/badge.svg)](https://github.com/uhawaii-system-its-ti-iam/uh-groupings-ui/actions/workflows/build_badge.yml)
[![Coverage Status](https://github.com/uhawaii-system-its-ti-iam/uh-groupings-ui/blob/badges/jacoco.svg)](https://github.com/uhawaii-system-its-ti-iam/uh-groupings-ui/actions/workflows/coverage.yml)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/5584742766ed46faa855dafe41a1cdc9)](https://www.codacy.com/gh/uhawaii-system-its-ti-iam/uh-groupings-ui/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=uhawaii-system-its-ti-iam/uh-groupings-ui&amp;utm_campaign=Badge_Grade)
[![CodeQL](https://github.com/uhawaii-system-its-ti-iam/uh-groupings-api/actions/workflows/codeql.yml/badge.svg)](https://github.com/uhawaii-system-its-ti-iam/uh-groupings-ui/actions/workflows/codeql.yml)

### Java
You'll need a Java JDK to build and run the project (version 17).

The files for the project are kept in a code repository,
available from here:

https://github.com/uhawaii-system-its-ti-iam/uh-groupings-ui

### Building
To run the Application from the Command Line:

    $ ./mvnw clean spring-boot:run

To build a deployable war file for local development, if preferred:

    $ ./mvnw clean package

You should have a deployable war file in the target directory.
Deploy as usual in a servlet container, e.g. tomcat.

### Running Unit Tests
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

### Running System Tests
The project files include a handful of System Tests.
For this project, System Tests are defined as those tests that may
call live remote systems, such as a search against the production
LDAP server. A standard build of the application will exclude the
System Tests, but you can explicitly run them by specifying the
appropriate command line argument.

To run the System Tests:

    $ ./mvnw -Dtest=*SystemTest clean test

## OOTB (Out-of-the-box) Project
### The UI for OOTB.

#### How UH Groupings Currently Works
The UH Groupings project relies on external systems to function properly, notably:
- **GrouperClient**: Used to communicate with an external Grouper system over the network.
- **CAS (Certified Authentication Service)**: Used to authenticate users before granting access.
- **LDAP Authentication**: Used for directory-based identity verification.
- **External API services**: Used to retrieve and modify live grouping data.

The application must communicate with external systems before it can display or update information.

Example use case:
1. A user logs in to UH Groupings using CAS.
2. The system uses GrouperClient to send requests to an external Grouper service.
3. The external system processes the request and returns data.
4. The website displays or modifies that data using API calls.

#### OOTB Project Objective
The OOTB project is designed to run locally without external dependencies. Its goal is to provide a fully self-contained development environment for the UH Groupings project that behaves like an open-source project on GitHub.

Everything runs independently in a local environment without network communication. Instead of calling external APIs, using GrouperClient, making network requests, and depending on CAS or LDAP authentication, the OOTB project uses:
- a predefined data harness (mock/sample JSON data),
- Spring Boot to manage configuration and service logic internally,
- in-memory data objects registered as Spring beans,
- and local simulation of all API operations (add, remove, update).

Example use case:
1. Static data is preloaded into the application at startup.
2. Spring Boot converts the data into managed beans.
3. Services operate on this in-memory data instead of external systems.
4. The UI interacts with the API exactly as it would in production.

### High-level overview of OOTB

OOTB is a local simulation of the UH Groupings system. It preserves the same API contracts and service structure as the production environment, but replaces all external integrations with in-memory data managed by Spring Boot. It works by loading predefined mock data at application startup, registering that data as Spring-managed beans, and allowing the service layer to operate on those beans instead of calling external systems.

#### 1. Application Startup
When the application starts in the ootb profile:
1. Static JSON data (the data harness) is loaded.
2. The JSON is converted into WS result objects (e.g., WsSubject, WsGroup, etc.).
3. These objects are wrapped in Results classes (e.g., SubjectsResults, GetMembersResults).
4. The results are registered as singleton Spring beans.

At this point, the application has a fully initialized in-memory dataset that mimics what would normally come from Grouper APIs.

#### 2. Service Selection via Spring Profile
The `GrouperService` interface has two implementations:
- `GrouperApiService` (real, production integration)
- `OotbGrouperApiService` (local simulation)

Spring determines which implementation to inject based on the active profile:
- `main`: uses real Grouper integration
- `ootb`: uses OOTB in-memory implementation

This allows switching between environments without changing business logic.

#### 3. In-Memory Data Operations
Instead of calling GrouperClient, making network requests, authenticating with CAS, and querying LDAP, OOTB performs all operations locally using `OotbGroupingPropertiesService`. This service:
- Reads preloaded result beans
- Updates membership lists
- Modifies attribute assignments
- Simulates subject lookups
- Maintains consistent state across all result objects

All changes happen in memory during runtime.

#### 4. UI Interaction
From the UI’s perspective API endpoints behave exactly the same, response objects have identical structures, and method signatures remain unchanged. The difference is only in the data source:

Production:
```
UI → API → GrouperApiService → External Grouper System
```

OOTB:
```
UI → API → OotbGrouperApiService → OotbGroupingPropertiesService → In-Memory Data
```

No external communication occurs in OOTB mode.

#### 5. Application Shutdown
Since OOTB uses static mock data, no permanent changes are written, all modifications exist only in memory, and restarting the application resets the dataset to its original state. This ensures a clean and repeatable development environment.

#### Summary
OOTB works by:
- Loading static mock data at startup
- Registering it as Spring-managed singleton beans
- Using a local implementation of GrouperService
- Simulating all membership, group, subject, and attribute operations in memory
- Preserving identical API contracts

The result is a fully isolated, production-like development environment that requires no external systems and poses no risk to live services.

### Getting started with OOTB
To activate OOTB mode, in both `uh-groupings-api` and `uh-groupings-ui`, change the active Spring profile from `localhost` to `ootb`.

To do this, modify your IDE run configurations and set:
```
Active Profiles: ootb
```

### When to use OOTB vs Main

Choosing between OOTB and Main depends on what you are trying to accomplish during development.

#### OOTB
`ootb` is ideal for local development, UI work, and safe testing.

Use `ootb` when:
- Developing or modifying UI components
- Testing features without external authentication
- Working offline
- You do not have access to CAS, LDAP, or Grouper services
- You want predictable, stable sample data
- You need a safe environment that cannot impact live services

#### Main
`main` should be used when working with real integrations and production-like environments. `main` interacts with external systems and may depend on network access and credentials.

Use `main` when:
- You need to test real GrouperClient integration
- Validating CAS or LDAP authentication behavior
- Debugging issues related to external services
- Preparing for production deployment
- You need to verify behavior against real, live data

### Limitations of OOTB

While the OOTB environment provides a convenient way to run and develop the Groupings application locally, it does not fully replicate the behavior of the production system. Limitations include:

- **No external service integration**: OOTB removes dependencies on services such as GrouperClient, CAS authentication, and LDAP. As a result, authentication flows and external API communication cannot be tested.
- **Static mock data**: The system relies on predefined JSON data instead of a live database or Grouper API responses. Any changes to groups, members, or attributes must be manually updated in the Data Harness.
- **Incomplete feature parity**: Some functions from the production GrouperApiService may not yet be implemented in the OOTB services, particularly when new features are introduced.
- **Limited security simulation**: Because authentication and authorization systems are removed, the environment does not fully represent real security behavior.
- **Not suitable for performance testing**: The OOTB system runs on small in-memory datasets and cannot simulate production-scale workloads.

The OOTB environment is intended primarily for local development, UI testing, and experimentation with Groupings functionality without requiring access to external infrastructure.
