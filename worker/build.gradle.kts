plugins {
	id("org.springframework.boot") version "4.1.1"
	id("io.spring.dependency-management") version "1.1.7"
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.apache.pdfbox:pdfbox:3.0.8")
	implementation(project(":common"))
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
