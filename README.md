## OOTB (Out-of-the-box) UH Groupings UI

OOTB is a local, self-contained version of UH Groupings. It uses mock data in memory instead of Grouper, CAS, or LDAP, so you can run and develop the UI without UH infrastructure or live services.

Production UH Groupings lives on `main`. This `ootb` branch is only for that local environment.

https://github.com/uhawaii-system-its-ti-iam/uh-groupings-ui/tree/ootb

### Requirements
You need Java 17 and a checkout of both this UI and [uh-groupings-api](https://github.com/uhawaii-system-its-ti-iam/uh-groupings-api/tree/ootb) on the `ootb` branch. The UI talks to the local OOTB API; both must use the `ootb` profile.

### Getting started
1. Check out the `ootb` branch in **both** `uh-groupings-api` and `uh-groupings-ui`.
2. In each project's IDE run configuration, set:

```
Active Profiles: ootb
```

3. Start the API first, then the UI:

```
$ ./mvnw clean spring-boot:run
```

The API listens on `http://localhost:8081/uhgroupingsapi`. The UI listens on `http://localhost:8080/uhgroupings` and uses that local API (`url.api.2.1.base`).

You can also package a war with `./mvnw clean package` if you prefer to deploy into a servlet container such as Tomcat.

### How it works
On the `ootb` profile, the UI does not use CAS. It talks to the OOTB API, which injects `OotbGrouperApiService` instead of the production Grouper client. The API loads a static JSON data harness into memory, and membership, group, subject, and attribute operations keep the same API contracts as production.

```
UI → API → OotbGrouperApiService → In-Memory Data
```

Restarting either app resets in-memory data. Nothing is written to external systems.

### Limitations
- No real Grouper, CAS, or LDAP integration, so authentication and live API behavior cannot be tested here.
- Data is mock JSON, not a live directory. Changes exist only in memory until restart.
- Some production Grouper features may not be implemented yet.
- Not suitable for performance or security testing.

Use `main` when you need real Grouper, CAS, or LDAP.
