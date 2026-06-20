# docs: align README endpoints

## What changed
- README endpoint list now reflects the `/auth/*` mappings in `AuthController`.
- Removed the nonexistent `/api/v1/auth/me` entry; documented `/auth/account`.
- Added entries for `/auth/oauth2/authorize`, `/auth/oauth2/callback`,
  `/auth/status`, and `/auth/health`.

## Impact
Documentation only. No code changes.
