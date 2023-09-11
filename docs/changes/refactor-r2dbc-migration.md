# refactor: R2DBC migration (not merged)

## Proposed change
- Migrate JPA repositories to R2DBC for a fully non-blocking data layer.
- Drop `spring-boot-starter-data-jpa` in favor of `spring-boot-starter-data-r2dbc`.
- Remove `subscribeOn(boundedElastic())` workarounds in controllers.

## Decision
Closed without merge. The migration touches every entity and repository and
was deemed too large for this milestone. Interim mitigation: keep JPA but
consistently offload blocking calls to a bounded scheduler.
