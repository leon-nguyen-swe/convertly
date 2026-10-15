import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    id("com.diffplug.spotless") version "8.10.4" apply false
}

subprojects {
    group = "dev.convertly"
    version = "0.0.1-SNAPSHOT"

    apply(plugin = "java")

    repositories {
        mavenCentral()
    }

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    dependencies {
        add("testImplementation", "org.junit.jupiter:junit-jupiter:5.13.1")
        add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    apply(plugin = "com.diffplug.spotless")

    extensions.configure<SpotlessExtension> {
        java {
            googleJavaFormat()
        }
    }
}

