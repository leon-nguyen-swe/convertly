subprojects {
    group = "dev.convertly"
    version = "0.0.1-SNAPSHOT"

    apply(plugin = "java")
    repositories { mavenCentral() }
    extensions.configure<JavaPluginExtension> {
        toolchain { languageVersion = JavaLanguageVersion.of(21) }
    }
}

