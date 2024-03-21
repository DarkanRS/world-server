import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
	application
	`maven-publish`
	id("com.github.johnrengelman.shadow") version "8.1.1"
	kotlin("jvm") version("1.9.21")
}

val darkanVersion: String = "1.8.0"
val ktVer: String = "1.9.21"

application {
	group = "rs.darkan"
	version = darkanVersion
	mainClass.set("com.rs.Launcher")
}

java {
	toolchain.languageVersion = JavaLanguageVersion.of(21)
}

repositories {
	mavenLocal()
	mavenCentral()
	maven("https://gitlab.com/api/v4/projects/42379000/packages/maven")
}

dependencies {
	implementation("rs.darkan:core:1.6.8")

	implementation("org.jetbrains.kotlin:kotlin-stdlib:$ktVer")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-common:$ktVer")
	implementation("org.jetbrains.kotlin:kotlin-scripting-common:$ktVer")
	implementation("org.jetbrains.kotlin:kotlin-scripting-jvm:$ktVer")
	implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host:$ktVer")
	implementation("org.jetbrains.kotlin:kotlin-main-kts:$ktVer")
	implementation("org.jetbrains.kotlin:kotlin-script-runtime:$ktVer")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC2")

	implementation("org.openjdk.jmh:jmh-core:1.37")
	implementation("com.google.code.gson:gson:2.10.1")
	implementation("com.google.guava:guava:33.0.0-jre")
	implementation("org.mongodb:mongodb-driver-sync:4.11.1")
	implementation("org.mongodb:mongodb-driver-core:4.11.1")
	implementation("io.netty:netty-all:4.1.104.Final")
	implementation("io.undertow:undertow-core:2.3.10.Final")
	implementation("it.unimi.dsi:fastutil:8.5.12")
	implementation("com.trivago:fastutil-concurrent-wrapper:0.2.2")

	// TODO: Deprecated. Upstream dead. Replace with unirest
	implementation("com.squareup.okhttp3:okhttp:4.12.0")
}

tasks.withType<ShadowJar> {
	isZip64 = true
	mergeServiceFiles()
}

publishing {
	val ciProjectId: String? = System.getenv("CI_PROJECT_ID")
	val ciJobToken: String? = System.getenv("CI_JOB_TOKEN")
	val ciPipelineId: String? = System.getenv("CI_PIPELINE_ID")
	if (ciProjectId == null || ciJobToken == null || ciPipelineId == null) {
		println("Failed to get project id, job token or pipeline id.")
		return@publishing
	}

	publications.create<MavenPublication>("library") {
		version = "${darkanVersion}-${ciPipelineId}"
		artifact("build/libs/world-server-${darkanVersion}-all.jar")
	}
	repositories.maven {
		name = "GitLab"
		url = uri("https://gitlab.com/api/v4/projects/${ciProjectId}/packages/maven")
		credentials(HttpHeaderCredentials::class) {
			name = "Job-Token"
			value = ciJobToken
		}
		authentication.create("header", HttpHeaderAuthentication::class)
	}
}
