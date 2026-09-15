## OOTB (Out-of-the-box) UH Groupings UI

This README is for the [`ootb` branch](https://github.com/uhawaii-system-its-ti-iam/uh-groupings-ui/tree/ootb).

OOTB is a local, self-contained version of UH Groupings. It uses mock data in memory instead of Grouper, CAS, or LDAP, so you can run and develop the UI without UH infrastructure or live services.

Production UH Groupings lives on `main`. This `ootb` branch is only for that local environment.

### Requirements
You need Java 17 and a checkout of both this UI and [`uh-groupings-api` on `ootb`](https://github.com/uhawaii-system-its-ti-iam/uh-groupings-api/tree/ootb). The UI talks to the local OOTB API; both must use the `ootb` profile.

### Getting started
1. Check out the `ootb` branch in **both** `uh-groupings-api` and `uh-groupings-ui`.
2. In each project's IDE run configuration, set:

```
Active Profiles: ootb
```

3. Start the API first, then the UI. From the command line, pass the OOTB profile in each project (Maven otherwise defaults to `localhost` and starts the Grouper/CAS path):

```
$ ./mvnw clean spring-boot:run -Dspring-boot.run.profiles=ootb
```

The API listens on `http://localhost:8081/uhgroupingsapi`. The UI listens on `http://localhost:8080/uhgroupings` and uses that local API (`url.api.2.1.base`).

4. After startup, open the UI at `http://localhost:8080/uhgroupings`. You should not be redirected to CAS. Mock users (for example DefaultMember) appear in the login/profile menu, and the UI should call `http://localhost:8081/uhgroupingsapi`.

You can also package a war with `./mvnw clean package` if you prefer to deploy into a servlet container such as Tomcat. Set `SPRING_PROFILES_ACTIVE=ootb` on the container before starting it; the profile is not baked into the WAR.

### How it works
On the `ootb` profile, the UI does not use CAS. It talks to the OOTB API, which injects `OotbGrouperApiService` instead of the production Grouper client. The API loads a static JSON data harness into memory. Common membership, group, subject, and attribute calls use the same response shapes as production, but some operations are unimplemented or return empty results.

```
UI → API → OotbGrouperApiService → In-Memory Data
```

Restarting the API resets its in-memory grouping data. Restarting the UI only reloads local user profiles; it does not clear API state. Nothing is written to external systems.

### Limitations
- No real Grouper, CAS, or LDAP. Login uses local mock user profiles, so CAS/LDAP authentication and live Grouper behavior cannot be tested here.
- Data is mock JSON, not a live directory. Grouping changes live in the API process until that API is restarted.
- Some production Grouper features are not implemented in OOTB and may return null or empty results.
- Not suitable for performance or security testing.

Use `main` when you need real Grouper, CAS, or LDAP.
