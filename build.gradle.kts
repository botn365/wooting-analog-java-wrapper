import gradlebuild.ZigBuild

plugins {
    id("java-library")
    id("gradlebuild.zig")
    id("com.palantir.git-version") version "0.15.0"
}

val gitVersion: groovy.lang.Closure<String> by extra
version = gitVersion()

group = "com.github.botn365"

repositories {
    mavenCentral()
}

dependencies {
    implementation("commons-io:commons-io:2.6")
    implementation("commons-codec:commons-codec:1.5")
}

java {
    toolchain {
        // JDK 17 is needed for the `jni_md.h` that allows `JNIEXPORT` to be overriden
        languageVersion = JavaLanguageVersion.of(17)
    }

    // Consumers require Java 8 compatibility
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withSourcesJar()
    withJavadocJar()
}

val zigOutDir = layout.buildDirectory.dir("zig")
zig {
    zigVersion = "0.14.0-dev.3237+ddff1fa4c"
    outputDir = zigOutDir
    targets {
        create("x86_64-linux-gnu")
        create("x86_64-windows-gnu")
        create("x86_64-macos-none")
        create("aarch64-macos-none")
    }.configureEach {
        optimizer = "ReleaseSmall"
        sources.from(layout.projectDirectory.dir("build.zig"))
        sources.from(layout.projectDirectory.dir("build.zig.zon"))
    }
}
tasks.withType<ZigBuild>().all {
    val zigBuild = this
    val isWindows = zigBuild.target.map { it.contains("windows") }
    val zigClean = tasks.register<Delete>("zigClean${zigBuild.name.removePrefix("zigBuild")}")
    zigBuild.dependsOn(zigClean)
    zigClean.configure {
        group = "zig"
        delete(zigBuild.outputDirectory)
    }

    tasks.named<ProcessResources>("processResources") {
        into("/natives/"+zigBuild.target.get()) {
            from(zigBuild.outputDirectory.dir(isWindows.map { if (it) "bin" else "lib" }))
            include("*.dll", "*.so", "*.dylib")
            //rename("(\\w+)\\.(dll|so|dylib)", "$1-${zigBuild.target.get()}.$2")
        }
    }
}
