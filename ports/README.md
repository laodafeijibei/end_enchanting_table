# 1.21.1 Ports

This directory contains the Minecraft 1.21.1 port variants for the mod:

- `forge-1.21.1`: Minecraft Forge 52.1.16, Java 21
- `neoforge-1.21.1`: NeoForge 21.1.248, Java 21

Each port is an independent Gradle project. Build from the corresponding port
directory with `gradlew.bat test --no-daemon` or `gradlew.bat build --no-daemon`.
Gradle caches, runtime directories, downloaded dependency mirrors, and generated
resources are intentionally excluded from version control.
