# Build Instructions for kArmor Plugin

This document outlines the steps to successfully build the kArmor Minecraft plugin as a .JAR file.

## Prerequisites
- Java 17 or higher
- Gradle installed

## Key Dependencies
The plugin requires the following dependency:
- **Group**: `xyz.refinedev.phoenix`
- **Artifact**: `pxAPI`
- **Version**: `1.8.12` (as specified in Phoenix-API-master/pom.xml)
- **Repository**: `https://maven.refinedev.xyz/public-repo/`

## Build Configuration
The `build.gradle` file must include:

1. **Repository Configuration**:
   ```gradle
   repositories {
       mavenLocal()
       maven { url 'https://repo.papermc.io/repository/maven-public/' }
       mavenCentral()
       maven { url 'https://maven.refinedev.xyz/public-repo/' }
   }
   ```

2. **Dependency Configuration**:
   ```gradle
   dependencies {
       compileOnly 'io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT'
       implementation 'xyz.refinedev.phoenix:pxAPI:1.8.12'
   }
   ```

## Building the JAR
Run the following command to build the shaded JAR (includes dependencies):
```bash
gradle shadowJar
```

The output JAR will be located at `build/libs/kArmor-1.0.0.jar`.

## Troubleshooting
- If the build fails with "Could not find xyz.refinedev.phoenix:pxAPI", ensure:
  - The repository URL has a trailing slash: `https://maven.refinedev.xyz/public-repo/`
  - The dependency version matches the one in the pom.xml file (1.8.12, not 2.0)
- The repository URL must be correctly formatted; missing or extra slashes can cause resolution issues.

## Notes
- The Phoenix API dependency is available at the specified Maven repository.
- The shadowJar task bundles all required dependencies into the final JAR.
