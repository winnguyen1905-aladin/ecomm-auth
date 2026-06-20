# fix: CORS credentials configuration

Resolves a bug where `allowedOrigins` contained `"*"` while `allowCredentials(true)`
was set, which browsers reject per the CORS spec.

## What changed
- Origins now come from `app.cors.allowed-origins` (comma-separated).
- Wildcard origin removed; explicit dev origins are the default.
- Removed the duplicate `setAllowedHeaders` call that clobbered `addAllowedHeader("*")`.

## Impact
Credentialed requests from the SPA now succeed. No API contract change.
