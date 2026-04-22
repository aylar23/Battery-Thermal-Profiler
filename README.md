# Battery Thermal Profiler

An Android app that profiles battery and thermal behavior over time, estimates per-app drain, and generates shareable reports.

## Modules

- `:app` — entry point, navigation
- `:core:data` — Room, DataStore, repository implementations
- `:core:domain` — models + use cases (pure Kotlin)
- `:feature:dashboard` — live stats screen
- `:feature:apps` — per-app breakdown
- `:feature:report` — report generation

## Architecture

```mermaid
flowchart TD
  app[:app] --> dash[:feature:dashboard]
  app --> apps[:feature:apps]
  app --> report[:feature:report]
  dash --> domain[:core:domain]
  apps --> domain
  report --> domain
  app --> data[:core:data]
  data --> domain
```

## Diagram placeholder

TODO: replace with a detailed architecture diagram + data flow.

