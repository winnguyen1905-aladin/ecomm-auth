# perf: non-blocking geo lookup

## What changed
- `GeoLocationService` now uses a reactive `WebClient` with a 500ms timeout.
- Per-IP region results are cached for 10 minutes (Caffeine).
- On error or timeout the lookup falls back to `RegionPartition.US`.
- Replaced `printStackTrace()` with SLF4J warn logging.

## Impact
Removes a blocking call from the WebFlux login path and cuts repeat-login
latency. Behavior is unchanged when the provider is reachable.
