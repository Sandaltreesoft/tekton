plugins {
	java
	id("org.springframework.boot") version "3.5.0"
	id("io.spring.dependency-management") version "1.1.7"
	// --- NEW: Add SonarQube Gradle Plugin ---
	id("org.sonarqube") version "4.4.1.3373" // Make sure this version is compatible with your SonarQube server
	// --- NEW: Add JaCoCo Plugin for Code Coverage ---
	id("jacoco")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
	// --- NEW: Enable JaCoCo for Test Tasks ---
	jacoco {
		destinationFile = layout.buildDirectory.file("jacoco/test/jacocoTestReport.exec").get().asFile
		classDumpDir = layout.buildDirectory.dir("jacoco/classpathdumps").get().asFile // Optional: useful for debugging coverage
	}
	// --- NEW: Ensure JaCoCo report is generated after tests ---
	finalizedBy(tasks.jacocoTestReport)
}

// --- NEW: JaCoCo Configuration ---
jacoco {
    // Specify the JaCoCo tool version. Use a recent stable version.
    toolVersion = "0.8.11"
}

// Define a task to generate the JaCoCo test report in XML format
tasks.register("jacocoTestReport", JacocoReport) {
    dependsOn(tasks.test) // Ensure tests run before generating the report

    reports {
        xml.required = true // SonarQube prefers XML for coverage import
        html.required = true // Good for human-readable reports
        csv.required = false
    }

    // Source sets and execution data
    sourceSets(project.sourceSets.main)
    executionData(tasks.withType<Test>().map { it.jacoco.destinationFile })
}

// --- NEW: SonarQube Configuration ---
sonarqube {
    properties {
        // --- Essential SonarQube Properties ---

        // sonar.projectKey: A unique identifier for your project in SonarQube.
        // This should be constant for a given project and NOT change with versions.
        property("sonar.projectKey", "Sandaltreesoft_tekton") // <-- IMPORTANT: Replace with YOUR unique project key

        // sonar.host.url: The URL of your SonarQube server.
        // It's best practice to get this from an environment variable in CI/CD.
        property("sonar.host.url", "https://sonarcloud.io") // Tekton/CI will inject this  System.getenv("SONAR_HOST_URL")

        // sonar.token: The authentication token for SonarQube.
        // This is highly sensitive and MUST be passed securely via an environment variable from a secret.
        property("sonar.token", "ff31aae72eaad28ae452569e2343131d60e56362") // Tekton/CI will inject this securely

        // --- Analysis Scope & Reports ---    System.getenv("SONAR_TOKEN")

        // Path to your main source code files (relative to project root).
        property("sonar.sources", "src/main/java")
        // Path to your test code files (relative to project root).
        property("sonar.tests", "src/test/java")
        // Path to the compiled bytecode (relative to the project root).
        // This is crucial for deep analysis and mapping issues back to source.
        property("sonar.java.binaries", layout.buildDirectory.dir("classes/java/main").get().asFile.path)

        // Path to JUnit test results in XML format.
        property("sonar.junit.reportPaths", layout.buildDirectory.dir("test-results/test").get().asFile.path)
        // Path to JaCoCo XML coverage report.
        // This property links your generated coverage report to SonarQube analysis.
        property("sonar.coverage.jacoco.xmlReportPaths", layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml").get().asFile.path)

        // --- Optional Properties (consider if needed) ---

        // sonar.projectName: The name displayed in the SonarQube UI. Defaults to project name.
        // property("sonar.projectName", "tekton")

        // sonar.projectVersion: The version of the project.
        // property("sonar.projectVersion", version)

        // sonar.branch.name: For multi-branch analysis. Useful in CI/CD.
        // property("sonar.branch.name", System.getenv("CI_COMMIT_REF_NAME") ?: "main") // Example: uses a common CI env var for branch name
    }
}