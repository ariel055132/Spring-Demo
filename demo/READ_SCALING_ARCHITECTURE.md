# Read Scaling Architecture for Demo Service

## Goal

Scale read throughput and reduce read latency for QR code APIs while preserving write correctness.

## Current Baseline

- API gateway is already in place and routes QR code traffic to the demo service.
- QR redirects already use Valkey as cache-aside fast path.
- Database currently behaves as a single primary from the app perspective.

## Target Architecture

```text
Client
  |
  v
Gateway (port 8080)
  |
  +--> Write APIs (POST/PATCH/DELETE) ---> Demo Service ---> PostgreSQL Primary
  |
  +--> Read APIs  (GET) -----------------> Demo Service ---> RoutingDataSource
                                                        |-> READ (replica)
                                                        |-> WRITE (fallback)

Demo Service
  |
  +--> Valkey cache (cache-aside for hot reads and redirects)
```

## How Routing Works in This Project

- Routing decision is transaction-based:
- `@Transactional(readOnly = true)` => read datasource.
- `@Transactional` (default read/write) => write datasource.
- If replica URL is not configured, read datasource automatically falls back to primary.

Implemented components:

- `com.example.demo.config.datasource.ReadWriteDataSourceConfig`
- `com.example.demo.config.datasource.ReadWriteRoutingDataSource`
- `com.example.demo.config.datasource.DataSourceRole`

## Endpoint Mapping (QR Code)

Write path (primary DB):

- `POST /api/qrcode/create`
- `PATCH /api/qrcode/{shortCode}`
- `DELETE /api/qrcode/delete/{shortCode}`
- `GET /api/qrcode/r/{shortCode}` (reads + scan event write + counter increment)

Read path (replica-ready):

- `GET /api/qrcode/user/{userId}`
- `GET /api/qrcode/detail/{shortCode}`
- `GET /api/qrcode/{shortCode}/image`
- `GET /api/qrcode/{shortCode}/analytics`
- `GET /api/qrcode/r/{shortCode}/test`

Note: Redirect endpoint remains on write path because it records events and increments counters in the same flow.

## Cache Strategy

Keep current Valkey strategy and formalize it:

- Redirect: cache-first, DB fallback, cache warm.
- QR detail/list: Spring cache plus DB query fallback.
- Invalidation on mutation: delete specific cache key on update/delete.

Recommended enhancement:

- Move from broad `allEntries=true` eviction to key-based eviction for user/detail caches to reduce churn.

## Consistency Policy

- Strong consistency required:
  - Mutation responses
  - Redirect scan counter updates
- Eventual consistency acceptable:
  - User QR list
  - QR details
  - Analytics reads

Replica lag handling:

- Default: tolerate short stale windows for read endpoints.
- Optional: add read-your-own-write mode via header (for example `X-Consistency: strong`) to force primary for follow-up reads.

## Configuration

Primary/write datasource:

- `spring.datasource.write.*`

Replica/read datasource:

- `spring.datasource.read.*`
- `READ_DATASOURCE_URL` environment variable controls activation.

Fallback behavior:

- Empty read URL => reads transparently use primary.

## Observability and SLOs

Track these metrics:

- Read QPS by endpoint
- p95/p99 latency for read endpoints
- Cache hit ratio (`qrcode:*`)
- DB primary vs replica query count
- Replica lag

Target SLOs for QR reads:

- `GET /api/qrcode/detail/{shortCode}` p95 < 60ms
- `GET /api/qrcode/user/{userId}` p95 < 120ms
- Redirect cache hit ratio > 90%

## Rollout Plan

1. Phase 1 (done in code): enable routing datasource with primary fallback.
2. Phase 2: provision PostgreSQL replica and set `READ_DATASOURCE_URL`.
3. Phase 3: monitor lag/latency and tune read pool size.
4. Phase 4: optional gateway split by method/path to separate read and write deployments.

## Risk Controls

- Feature-flag style activation through read URL presence.
- No behavior change when replica is absent.
- Keep primary as default target datasource.
- Run smoke tests for create/update/delete/redirect before enabling replica traffic.
