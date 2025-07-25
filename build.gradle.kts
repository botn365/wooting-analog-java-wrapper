import com.falsepattern.zigbuild.options.ZigBuildOptions
import com.falsepattern.zigbuild.target.ZigTargetTriple
import com.falsepattern.zigbuild.tasks.ZigBuildTask
import com.falsepattern.zigbuild.toolchain.ZigVersion

plugins {
    `java-library`
    id("com.palantir.git-version") version "4.0.0"
    id("com.falsepattern.zigbuild") version "0.1.1"
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

zig {
    toolchain {
        version = ZigVersion.of("0.14.1")
    }
}

val zigPrefix = layout.buildDirectory.dir("zig-build")

listOf(
    ZigTargetTriple.X86_64_LINUX_GNU,
    ZigTargetTriple.X86_64_WINDOWS_GNU,
    ZigTargetTriple.X86_64_MACOS_NONE,
    ZigTargetTriple.AARCH64_MACOS_NONE,
).forEach { target ->
    val targetName = target.resolve()
    val prefix = zigPrefix.map { it.dir(targetName) }
    val task = tasks.register<ZigBuildTask>("buildNatives$targetName") {
        options {
            steps.add("install")
            this.target = target
            optimize = ZigBuildOptions.Optimization.ReleaseSmall
            this.compilerArgs.add("-Dstrip=true")
        }
        workingDirectory = layout.projectDirectory
        prefixDirectory = prefix
        clearPrefixDirectory = true
        sourceFiles.from(
            layout.projectDirectory.dir("build.zig"),
            layout.projectDirectory.dir("build.zig.zon"),
            layout.projectDirectory.dir("native")
        )
    }

    tasks.named<ProcessResources>("processResources") {
        dependsOn(task)
        into("/natives/$targetName") {
            from(prefix.map { it.dir("lib") })
            include("*.dll", "*.so", "*.dylib")
        }
    }
}