# gradle-include-modules-transitives

Reproducer for **[TKA-10120](https://mend-io.atlassian.net/browse/TKA-10120)**: Gradle not scanning transitives when restricted to a single module via `gradle.includeModules`.

## Project Structure

```
payments-monorepo/
├── settings.gradle                    # declares all 3 submodules
├── build.gradle                       # root config (Java 11, mavenCentral)
├── wss-unified-agent.config           # Mend config (see below)
│
├── payments-data-consumer/            # ← TARGET MODULE (gradle.includeModules)
│   └── build.gradle
│       ├── com.amazonaws:aws-lambda-java-events:3.14.0  [direct]
│       │   └── joda-time:joda-time:2.10.8               [transitive ← MISSING in bug]
│       └── project(:payments-data-consumer-lib)         [local project dep]
│
├── payments-data-consumer-lib/        # local library pulled in by consumer
│   └── build.gradle
│       └── software.amazon.awssdk:secretsmanager:2.20.157  [direct]
│           └── apache-client → commons-codec               [transitive ← MISSING in bug]
│
└── payments-api/                      # NOT in includeModules — should be ignored
    └── build.gradle
        ├── org.springframework:spring-webmvc:6.0.11   (should NOT appear in scan)
        └── com.google.guava:guava:32.1.2-jre          (should NOT appear in scan)
```

## Bug Description

When Mend scans `payments-data-consumer` in isolation using `gradle.includeModules=payments-data-consumer`, the following transitive dependencies are **missing** from scan results:

| Missing Dep | Expected Resolution Path |
|---|---|
| `joda-time:joda-time:2.10.8` | `payments-data-consumer` → `aws-lambda-java-events:3.14.0` → `joda-time` |
| `commons-codec` | `payments-data-consumer` → `payments-data-consumer-lib` → `secretsmanager` → `apache-client` → `commons-codec` |

Scan results are also **inconsistent across runs** using the same zip artifact.

## How to Reproduce

1. Fill in your `apiKey` in `wss-unified-agent.config`
2. Download the Unified Agent:
   ```bash
   curl -LO https://unified-agent.s3.amazonaws.com/wss-unified-agent.jar
   ```
3. Run the scan from the repo root:
   ```bash
   java -jar wss-unified-agent.jar -c wss-unified-agent.config -d .
   ```
4. Check the Mend dashboard for the `gradle-include-modules-transitives` project and verify whether `joda-time` and `commons-codec` appear.

## Expected vs Actual

| Dependency | Expected in scan | Actual (buggy) |
|---|---|---|
| `joda-time:joda-time:2.10.8` | ✅ Present | ❌ Missing |
| `commons-codec` | ✅ Present | ❌ Missing |
| `spring-webmvc` (payments-api) | ❌ Absent | Should be absent |
| `guava` (payments-api) | ❌ Absent | Should be absent |

## Critical Config Combination

```properties
gradle.includeModules=payments-data-consumer
gradle.aggregateModules=true
gradle.resolveDependencies=true
resolveAllDependencies=false
```

## Gradle Dependency Tree (for reference)

Run to verify the full expected tree:
```bash
./gradlew :payments-data-consumer:dependencies --configuration runtimeClasspath
```

This will show `joda-time` and `commons-codec` in the Gradle-resolved tree — confirming the bug is in Mend's resolution logic, not in the project's dependency declarations.

## Related

- Jira: [TKA-10120](https://mend-io.atlassian.net/browse/TKA-10120)
- Gradle: 7.6.4
- Java: 11
- UA config: `gradle.includeModules=payments-data-consumer`, `gradle.aggregateModules=true`, `gradle.resolveDependencies=true`, `resolveAllDependencies=false`
