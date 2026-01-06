# WildFly 30+ Deployment & Optimization Guide

## Table of Contents
1. [Quick Start - Maven Deployment](#quick-start---maven-deployment)
2. [Testing & Validation](#testing--validation)
3. [JVM Optimization](#jvm-optimization)
4. [Production JVM Configuration](#production-jvm-configuration)
5. [WildFly Configuration](#wildfly-configuration)
6. [Deployment Structure](#deployment-structure)
7. [Performance Tuning](#performance-tuning)
8. [Monitoring & Troubleshooting](#monitoring--troubleshooting)
9. [Production Checklist](#production-checklist)

---

## Quick Start - Maven Deployment

### Prerequisites

1. **WildFly Server Running**: Must be accessible on `localhost:9990`
   ```bash
   # Start WildFly (from WILDFLY_HOME)
   ./bin/standalone.sh
   
   # Or on Windows
   bin\standalone.bat
   ```

2. **Build the Application**:
   ```bash
   mvn clean package -DskipTests
   ```

### Deploy Using Maven

```bash
cd c:\petclinic\spring-petclinic-migrated

# Deploy to WildFly and capture logs
mvn wildfly:deploy 2>&1 | Tee-Object -FilePath logs/deployment.log
```

### Accessing the Application

After successful deployment: **http://localhost:8080/petclinic**

---

## Testing & Validation

### Quick Tests

```bash
# 1. Welcome page
curl http://localhost:8080/petclinic/

# 2. Veterinarians (JSON)
curl http://localhost:8080/petclinic/vets.json

# 3. Veterinarians (XML)
curl http://localhost:8080/petclinic/vets.xml

# 4. Error handling
curl http://localhost:8080/petclinic/oups
```

---

## JVM Optimization

### Development Configuration

**For local development/testing on laptops** - Edit `standalone.conf.bat` (Windows):

```batch
rem ### Development Configuration ###
rem # Optimized for local testing on laptops (512MB heap)

if "x%JBOSS_JAVA_SIZING%" == "x" (
    rem # Memory: 512MB heap is sufficient for development
    rem # Metaspace: 256MB max prevents unbounded class loading
    set "JBOSS_JAVA_SIZING=-Xms512m -Xmx512m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m"
)
set "JAVA_OPTS=%JBOSS_JAVA_SIZING%"

rem # G1 Garbage Collector (default in Java 9+)
set "JAVA_OPTS=%JAVA_OPTS% -XX:+UseG1GC"
set "JAVA_OPTS=%JAVA_OPTS% -XX:MaxGCPauseMillis=200"

rem # Pre-allocate heap for consistent performance
set "JAVA_OPTS=%JAVA_OPTS% -XX:+AlwaysPreTouch"

rem # Standard WildFly settings
set "JAVA_OPTS=%JAVA_OPTS% -Djava.net.preferIPv4Stack=true"
set "JAVA_OPTS=%JAVA_OPTS% -Djboss.modules.system.pkgs=org.jboss.byteman"
set "JAVA_OPTS=%JAVA_OPTS% -Djava.awt.headless=true"
set "JAVA_OPTS=%JAVA_OPTS% -Dfile.encoding=UTF-8"
```

### Configuration Breakdown

#### Heap Settings (`-Xms512m -Xmx512m`)
- **Equal sizes** eliminate heap resizing overhead
- 512MB is optimal for local development
- Prevents consuming all laptop memory

#### Metaspace (`-XX:MaxMetaspaceSize=256m`)
- Stores class metadata (Spring + Hibernate = many classes)
- Prevents `OutOfMemoryError: Metaspace` during redeployments
- **Never remove this limit** - it protects against classloader leaks

#### G1 Garbage Collector
- **Default in Java 9+** but explicitly enabled for clarity
- Best for server applications
- Concurrent collection = low latency
- `MaxGCPauseMillis=200`: Target 200ms pause time (imperceptible to users)

#### AlwaysPreTouch
- Allocates all heap memory at JVM startup
- **Benefits**: Eliminates runtime page faults, consistent performance
- **Trade-off**: Slightly slower startup (~1-2 seconds) but worth it

#### Networking (`preferIPv4Stack=true`)
- Prevents DNS resolution delays
- Avoids mysterious socket binding failures
- **Critical for WildFly** to start reliably





#### 9. **Error Handling Test**

```bash
# Test exception handling
curl http://localhost:8080/petclinic/oups

# Expected: Custom error page with exception details
```

#### 10. **API Data Format Test (JavaScript Test)**

Create a test script in browser console:

```javascript
// Test JSON API
fetch('/petclinic/vets.json')
  .then(r => r.json())
  .then(data => console.log('JSON Response:', data))
  .catch(e => console.error('Error:', e));

// Test XML API
fetch('/petclinic/vets.xml')
  .then(r => r.text())
  .then(data => console.log('XML Response:', data))
  .catch(e => console.error('Error:', e));
```

### Logging Configuration

All deployment operations are logged to the `logs/` directory:

```
logs/
├── build.log              # Maven build output
├── deployment.log         # Deployment execution logs
└── deployment-verbose.log # Verbose deployment with debug details
```

**View logs in real-time:**

```bash
# Windows PowerShell
Get-Content logs/deployment.log -Wait

# Or tail command
tail -f logs/deployment.log
```

**Common Issues in Logs:**

| Error | Cause | Solution |
|-------|-------|----------|
| `Cannot get a connection, pool error` | Database connection failed | Start PostgreSQL, verify credentials |
| `Class loading error` | JAR version conflict | Review `jboss-deployment-structure.xml` |
| `Port already in use` | Port 8080 in use | Stop other services or use different port |
| `OutOfMemoryError: Metaspace` | Insufficient metaspace | Increase `-XX:MaxMetaspaceSize` |

---

## Production JVM Configuration

### Production Settings

**For production with PostgreSQL and real user load** (50-200 concurrent users):

```batch
rem ### Production Configuration ###
rem # Optimized for production workloads with 2GB heap

if "x%JBOSS_JAVA_SIZING%" == "x" (
    rem # Production memory: 2GB heap + 512MB metaspace
    rem # Provides headroom for:
    rem #   - Database connection pooling
    rem #   - Hibernate second-level cache
    rem #   - User sessions
    rem #   - Concurrent request handling
    set "JBOSS_JAVA_SIZING=-Xms2g -Xmx2g -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m"
)
set "JAVA_OPTS=%JBOSS_JAVA_SIZING%"

rem # G1GC Production Tuning
rem # G1HeapRegionSize=8m: Optimize for 2GB heap (fewer regions = less overhead)
rem # InitiatingHeapOccupancyPercent=45: Start concurrent GC at 45% full
set "JAVA_OPTS=%JAVA_OPTS% -XX:+UseG1GC"
set "JAVA_OPTS=%JAVA_OPTS% -XX:MaxGCPauseMillis=200"
set "JAVA_OPTS=%JAVA_OPTS% -XX:G1HeapRegionSize=8m"
set "JAVA_OPTS=%JAVA_OPTS% -XX:InitiatingHeapOccupancyPercent=45"

rem # CRITICAL: GC Logging for production troubleshooting
rem # Logs all GC events to gc.log with rotation (5 files × 10MB = 50MB max)
rem # Essential for diagnosing performance issues and memory leaks
set "JAVA_OPTS=%JAVA_OPTS% -Xlog:gc*:file=gc.log:time,uptime:filecount=5,filesize=10m"

rem # Performance optimizations
set "JAVA_OPTS=%JAVA_OPTS% -XX:+AlwaysPreTouch"
set "JAVA_OPTS=%JAVA_OPTS% -XX:+UseStringDeduplication"

rem # Standard settings
set "JAVA_OPTS=%JAVA_OPTS% -Djava.net.preferIPv4Stack=true"
set "JAVA_OPTS=%JAVA_OPTS% -Djboss.modules.system.pkgs=org.jboss.byteman"
set "JAVA_OPTS=%JAVA_OPTS% -Djava.awt.headless=true"
set "JAVA_OPTS=%JAVA_OPTS% -Dfile.encoding=UTF-8"
```

### Production Settings Explained

#### Why 2GB Heap?

**Memory breakdown for 100 concurrent users:**
```
Spring Application Context:    200 MB
Hibernate + JPA:               150 MB
HikariCP Connection Pool:      200 MB (20 connections)
User Session Data:             200 MB (2MB per user)
Second-Level Cache:            300 MB
GC Overhead (30%):             600 MB
────────────────────────────────────
Total:                        ~1.7 GB → Round to 2GB
```

**Sizing guidelines:**
| Concurrent Users | Heap Size | Database Connections |
|-----------------|-----------|---------------------|
| 10-50 | 1 GB | 10-15 |
| 50-200 | 2 GB | 15-25 |
| 200-500 | 4 GB | 25-50 |
| 500+ | 8 GB+ | 50+ or scale horizontally |

#### G1 Heap Region Size (8MB)

G1GC divides heap into fixed-size regions:

```
For 2GB heap:
├── Default (1-2MB regions): ~1000-2000 regions = more GC overhead
└── Configured (8MB regions): ~250 regions = less overhead ✓
```

**Why 8MB?**
- Hibernate entities are medium-sized (1-100KB)
- Fewer regions = less metadata = lower GC overhead
- Optimal for 2GB heap size

#### Initiating Heap Occupancy (45%)

Controls when G1 starts concurrent garbage collection:

```
100% ├─────────────────── Max Heap (emergency Full GC)
 75% ├───────────────────
 50% ├───────────────────
 45% ├─────●───────────── Start Concurrent GC (configured)
 25% ├───────────────────
  0% └─────────────────── Empty
```

- At 45% full: G1 starts background collection
- Prevents reaching 100% (which triggers slow Full GC)
- Balanced default - adjust if needed

#### GC Logging (CRITICAL for Production)

```batch
-Xlog:gc*:file=gc.log:time,uptime:filecount=5,filesize=10m
```

**Why essential:**
1. **Troubleshoot performance**: "App slow at 3pm?" → Check GC logs
2. **Detect memory leaks**: Growing old generation = leak
3. **Capacity planning**: Frequent GCs = need more memory

**Example log entry:**
```log
[2025-01-01T15:30:45.123][125.456s] GC(15) Pause Young (Normal) 
  245M->67M(2048M) 12.456ms
  
Translation:
- Timestamp: 15:30:45
- JVM uptime: 125 seconds
- GC #15 since startup
- Young generation collection
- Before: 245MB used
- After: 67MB used (178MB collected)
- Total heap: 2048MB
- Pause: 12.456ms (app stopped briefly)
```

**Log rotation**: 5 files × 10MB = 50MB disk max, automatic cleanup

#### String Deduplication

```batch
-XX:+UseStringDeduplication
```

**How it works:**
```java
// Without deduplication:
String s1 = "pending";  // char[] stored
String s2 = "pending";  // char[] stored (duplicate!)
String s3 = "pending";  // char[] stored (duplicate!)

// With deduplication:
String s1 = "pending";  // char[] stored
String s2 = "pending";  // → points to s1's char[]
String s3 = "pending";  // → points to s1's char[]
```

**Why database apps benefit:**
- Queries return MANY duplicate strings ("active", "pending", city names)
- **Typical savings**: 10-20% heap reduction
- **Cost**: Minimal (~1-2% CPU during GC)
- **Verdict**: Enable for all production apps

---

## WildFly Configuration

### Disable Unused Subsystems

WildFly enables many subsystems by default. Disabling unused ones can reduce startup time by **30-50%**.

Edit `standalone.xml` and remove/comment out:

```xml
<!-- EJB (not used in Spring apps) -->
<!-- <subsystem xmlns="urn:jboss:domain:ejb3:..."/> -->

<!-- Messaging / JMS (not used) -->
<!-- <subsystem xmlns="urn:jboss:domain:messaging-activemq:..."/> -->

<!-- JAX-RS / RESTEasy (not used) -->
<!-- <subsystem xmlns="urn:jboss:domain:jaxrs:..."/> -->

<!-- MicroProfile (not used) -->
<!-- <subsystem xmlns="urn:jboss:domain:microprofile-config-smallrye:..."/> -->
<!-- <subsystem xmlns="urn:jboss:domain:microprofile-health-smallrye:..."/> -->
<!-- <subsystem xmlns="urn:jboss:domain:microprofile-metrics-smallrye:..."/> -->

<!-- WebServices (not used) -->
<!-- <subsystem xmlns="urn:jboss:domain:webservices:..."/> -->
```

### Disable Deployment Scanner (Production)

```xml
<subsystem xmlns="urn:jboss:domain:deployment-scanner:2.0">
    <deployment-scanner path="deployments" 
                        scan-interval="0" 
                        scan-enabled="false"/>
</subsystem>
```

**Benefits**: No polling overhead, faster boot, explicit deployments only

---

## Deployment Structure

### jboss-deployment-structure.xml

Located at: `src/main/webapp/WEB-INF/jboss-deployment-structure.xml`

**Purpose**:
- Explicit control over WildFly classloading
- Prevents conflicts between WAR dependencies and WildFly modules
- Ensures deterministic behavior

**Key Exclusions**:
1. **Hibernate**: Use WAR-bundled 6.x (WildFly 30 ships with Hibernate 6.x; do NOT use the server module)
2. **Jackson**: Use WAR version for consistent JSON processing
3. **JAX-RS/RESTEasy**: Not used in this application
4. **Logging**: Use SLF4J + Logback from WAR
5. **EJB/JMS/WebServices**: Not used

**Why This Matters**:
- Prevents `ClassCastException` and `NoSuchMethodError`
- Avoids version conflicts
- Reduces startup time (less classpath scanning)

### PostgreSQL JDBC Driver

**Recommended**: Deploy as a WildFly module (not in WAR)

1. Create module directory:
   ```bash
   mkdir -p $WILDFLY_HOME/modules/org/postgresql/main
   ```

2. Copy driver:
   ```bash
   cp postgresql-42.7.1.jar $WILDFLY_HOME/modules/org/postgresql/main/
   ```

3. Create `module.xml`:
   ```xml
   <module xmlns="urn:jboss:module:1.9" name="org.postgresql">
       <resources>
           <resource-root path="postgresql-42.7.1.jar"/>
       </resources>
       <dependencies>
           <module name="javax.api"/>
           <module name="javax.transaction.api"/>
       </dependencies>
   </module>
   ```

4. Configure datasource in `standalone.xml`:
   ```xml
   <datasource jndi-name="java:jboss/datasources/PetclinicDS" pool-name="PetclinicDS">
       <connection-url>jdbc:postgresql://localhost:5432/petclinic</connection-url>
       <driver>postgresql</driver>
       <security>
           <user-name>petclinic</user-name>
           <password>petclinic</password>
       </security>
   </datasource>
   <drivers>
       <driver name="postgresql" module="org.postgresql"/>
   </drivers>
   ```

---

## Performance Tuning

### Spring Component Scanning

**Optimize** by using explicit package scanning instead of broad wildcards:

```java
@ComponentScan(basePackages = {
    "org.springframework.samples.petclinic.config",
    "org.springframework.samples.petclinic.web",
    "org.springframework.samples.petclinic.service",
    "org.springframework.samples.petclinic.repository",
    "org.springframework.samples.petclinic.aspect"
})
```

**Avoid**:
```java
@ComponentScan("org.springframework.samples")  // Too broad!
```

**Impact**: Reduces startup time by limiting classpath scanning.

### Connection Pool Tuning (HikariCP)

In `application.properties`:

```properties
# Connection pool sizing (for 2GB heap, 100 concurrent users)
hikaricp.maximum-pool-size=20
hikaricp.minimum-idle=5
hikaricp.connection-timeout=30000
hikaricp.idle-timeout=600000
hikaricp.max-lifetime=1800000

# Performance optimizations
hikaricp.cache-prep-stmts=true
hikaricp.prep-stmt-cache-size=250
hikaricp.prep-stmt-cache-sql-limit=2048
```

### Hibernate Tuning

```properties
# Second-level cache (Caffeine)
jpa.hibernate.cache.use_second_level_cache=true
jpa.hibernate.cache.region.factory_class=org.hibernate.cache.jcache.JCacheRegionFactory

# Query cache
jpa.hibernate.cache.use_query_cache=true

# Batch processing
jpa.hibernate.jdbc.batch_size=20
jpa.hibernate.order_inserts=true
jpa.hibernate.order_updates=true

# Disable statistics in production
jpa.hibernate.generate_statistics=false
```

---

## Monitoring & Troubleshooting

### Verify JVM Settings

```bash
# Get WildFly process ID
jps -l | grep jboss

# Check heap configuration
jcmd <pid> GC.heap_info

# View all JVM flags
jcmd <pid> VM.flags

# Monitor GC in real-time
jcmd <pid> GC.class_histogram
```

### Analyze GC Logs

**Tools:**
- [GCViewer](https://github.com/chewiebug/GCViewer)
- [GCEasy](https://gceasy.io/) - Upload gc.log for analysis

**What to look for:**
```log
# Good pattern (healthy GC):
[10:00:00] GC: 245M->67M (178M freed) 12ms
[10:05:00] GC: 250M->70M (180M freed) 11ms
[10:10:00] GC: 248M->68M (180M freed) 13ms

# Bad pattern (memory leak):
[10:00:00] GC: 450M->380M (70M freed) 50ms   ← High before/after
[10:05:00] GC: 470M->400M (70M freed) 55ms   ← Growing
[10:10:00] GC: 490M->420M (70M freed) 60ms   ← Growing
[10:15:00] Full GC: 512M->450M (62M) 500ms   ← Full GC needed
```

### Common Issues

| Issue | Symptom | Solution |
|-------|---------|----------|
| `OutOfMemoryError: Metaspace` | Deployment fails | Increase `-XX:MaxMetaspaceSize` to 512m |
| Slow startup | Takes 2+ minutes | Disable unused subsystems, check AlwaysPreTouch |
| High GC pauses | Users see delays | Analyze gc.log, consider more heap |
| Memory leak | Growing old gen | Enable GC logging, use heap dumps |

---

## Production Checklist

**Before deploying to production:**

### JVM Configuration
- [ ] Heap: `-Xms` equals `-Xmx` (e.g., 2g/2g)
- [ ] Metaspace: Max set to 512m
- [ ] G1GC enabled with 200ms pause target
- [ ] Region size: 8m for 2GB heap
- [ ] `AlwaysPreTouch` enabled
- [ ] String deduplication enabled
- [ ] **GC logging enabled** (file rotation configured)
- [ ] IPv4 preferred
- [ ] UTF-8 encoding set

### WildFly Configuration
- [ ] Unused subsystems disabled
- [ ] Deployment scanner disabled
- [ ] Logging level: INFO (not DEBUG)
- [ ] PostgreSQL driver: Module (not WAR)

### Application Configuration
- [ ] Connection pool sized (15-25 for 2GB)
- [ ] Hibernate second-level cache enabled
- [ ] Statistics disabled in production
- [ ] `jboss-deployment-structure.xml` configured

### Monitoring Setup
- [ ] GC logs location documented
- [ ] Disk space for logs available (50MB min)
- [ ] Monitoring tools configured (APM, metrics)
- [ ] Alerting for OutOfMemoryError

### Load Testing
- [ ] Tested with expected concurrent users
- [ ] GC behavior verified under load
- [ ] Memory usage stable over time
- [ ] Response times acceptable

---

## Container/Kubernetes Considerations

**Set container memory limits to 1.5-2x JVM heap:**

```yaml
# For -Xmx2g JVM configuration
resources:
  limits:
    memory: "3Gi"     # 2GB heap + 1GB overhead
  requests:
    memory: "2.5Gi"
```

**Why more than heap?**
```
Total Memory Needed:
├── JVM Heap: 2048 MB (-Xmx2g)
├── Metaspace: 512 MB
├── Thread Stacks: 200 MB (~200 threads)
├── Native Memory: 200 MB (NIO buffers, JIT)
└── OS Overhead: 100 MB
────────────────────────────
Total: ~3060 MB → 3GB container limit
```

---

## References

- [WildFly 30 Documentation](https://docs.wildfly.org/30/)
- [Java 17 GC Tuning](https://docs.oracle.com/en/java/javase/17/gctuning/)
- [HikariCP Configuration](https://github.com/brettwooldridge/HikariCP)
- [G1GC Tuning Guide](https://www.oracle.com/technical-resources/articles/java/g1gc.html)

---

## Quick Reference Card

### Development (Laptop)
```batch
-Xms512m -Xmx512m
-XX:MaxMetaspaceSize=256m
-XX:+UseG1GC
-XX:+AlwaysPreTouch
```

### Production (Server)
```batch
-Xms2g -Xmx2g
-XX:MaxMetaspaceSize=512m
-XX:+UseG1GC -XX:G1HeapRegionSize=8m
-XX:+AlwaysPreTouch -XX:+UseStringDeduplication
-Xlog:gc*:file=gc.log:time,uptime:filecount=5,filesize=10m
```

### Memory Sizing Formula
```
Heap = (Spring + Hibernate + ConnectionPool + Sessions) × 1.3

Example (100 users):
= (200 + 150 + 200 + 200) × 1.3
= 975 MB → Use 1-2 GB
```
