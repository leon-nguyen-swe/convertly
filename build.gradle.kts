subprojects {
    apply(plugin = "java")
    repositories { mavenCentral() }
    extensions.configure<JavaPluginExtension> {
        toolchain { languageVersion = JavaLanguageVersion.of(21) }
    }
}
