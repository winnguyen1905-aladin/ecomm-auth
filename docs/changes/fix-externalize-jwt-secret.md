# fix: externalize JWT secret

## What changed
- `jwt.base64-secret` is now sourced from the `JWT_SECRET` env var with no fallback.
- Access token lifetime reduced from 90,000,000s to 900s.
- Refresh token lifetime reduced from 100,000,000s to 604,800s.
- Application fails fast on boot if `JWT_SECRET` is unset.

## Migration
Set `JWT_SECRET` in your environment before starting the service. Rotate the old
committed key everywhere it was trusted.
