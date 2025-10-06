# Forgified Fabric API Lite

## 📖 About

Forgified Fabric API little fork. Custom sth.

## 🛠️ Using Forgified Fabric API Lite to develop mods

To set up a NeoForge development environment, please read the [NeoForge docs](https://docs.neoforged.net/) and follow the instructions there.

The Forgified Fabric API Lite is published under the `org.sinytra.forgified-fabric-api` group. To include the full Forgified
Fabric API Lite with all modules in the development environment, add the following to your `dependencies` block in the gradle
buildscript:

### Groovy DSL

```groovy
repositories {
    maven {
        url "https://maven.kessokuteatime.work/snapshots/"
    }
}
dependencies {
    modImplementation "org.sinytra.forgified-fabric-api:forgified-fabric-api:FFAPI_VERSION"
}
```

<!--Linked to gradle documentation on properties-->
Instead of hardcoding version constants all over the build script, Gradle properties may be used to replace these
constants. Properties are defined in the `gradle.properties` file at the root of a project. More information is
available [here](https://docs.gradle.org/current/userguide/organizing_gradle_projects.html#declare_properties_in_gradle_properties_file).

## Modules

Fabric API is designed to be modular for ease of updating. This also has the advantage of splitting up the codebase into
smaller chunks.

Each module contains its own `README.md`* explaining the module's purpose and additional info on using the module.

\* The README for each module is being worked on; not every module has a README at the moment

