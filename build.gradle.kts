plugins {
	kotlin("jvm") version "1.7.20"
	id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.cha0s_f4me"
version = "1.1"

repositories {
	mavenCentral()
	maven("https://repo.papermc.io/repository/maven-public/")

	maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
	// Minecraft dependencies
	compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
	implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.5.0")
	implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.5.0")
	implementation("net.kyori:adventure-extra-kotlin:4.11.0")

	// Cloud dependencies (minecraft)
	implementation("cloud.commandframework:cloud-core:1.7.1")
	implementation("cloud.commandframework:cloud-kotlin-extensions:1.7.1")
	implementation("cloud.commandframework:cloud-paper:1.7.1")

	// Discord dependencies
	implementation("dev.kord:kord-core:0.8.0-M16")
}

java {
	toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
