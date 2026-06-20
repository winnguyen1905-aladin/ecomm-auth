# test: AuthController and Keycloak coverage

## What changed
- Added the first `src/test` suite to the repository.
- `AuthControllerTest`: login success, 401 on bad creds, refresh-missing-cookie.
- `KeycloakServiceImplTest`: token parsing and non-2xx error handling via MockWebServer.
- Added `spring-boot-starter-test` and `okhttp mockwebserver` (test scope).

## Impact
Establishes a CI-runnable baseline. No production code behavior change.
