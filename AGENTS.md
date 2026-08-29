# Project Guidelines & Versioning Rules

## App Identity
- **App Name**: `Parmis`
- **Theme**: Persian Solar Calendar 1405 (تقویم خورشیدی پارمیس ۱۴۰۵)
- **Primary Color**: Orange-Gold (`#F09400`) & Turquoise (`#006874`)

## Versioning Logic (MANDATORY on every update)
On every update / new feature iteration, update `versionName` and `versionCode` in `app/build.gradle.kts` using the following exact rollover rule:
- Format: `x.y.z`
- `z` increments with each update from 0 up to 30.
- When `z` reaches 31: `z` resets to 0 and `y` increases by 1 (`y = y + 1`).
- When `y` reaches 10: `y` resets to 0 and `x` increases by 1 (`x = x + 1`).
- `versionCode` must strictly increase monotonically (e.g. `x * 1000 + y * 100 + z` or sequential integer matching `x, y, z`).
