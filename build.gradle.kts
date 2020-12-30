import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.4.10"
  antlr
  java
  idea
}

group = "me.hannah"
version = "1.0"

repositories {
  mavenCentral()
}

dependencies {
  antlr("org.antlr:antlr4:4.9")
  implementation("org.antlr:antlr4-runtime:4.7.2")
}


tasks {
  generateGrammarSource {
    maxHeapSize = "64m"
    arguments = arguments + listOf("-package", "me.hannah.parser")
    outputDirectory = File("generated-src/antlr/main")
  }

  compileJava {
    dependsOn(generateGrammarSource)
    dependsOn("setupTooling")
  }

  clean {
    delete("generated-src")
  }

  register<Copy>("setupTooling") {
    from("tooling")
    into("$buildDir/compiler")
  }

  register<Exec>("compileASM") {
//    isIgnoreExitValue = true
    workingDir("$buildDir/compiler/build")
//    commandLine("rm -f linkfile main.gb main.o main.sym")
    commandLine("make")
  }

  register<JavaExec>("runGB") {
    dependsOn("compileASM")
    classpath = files("$buildDir/compiler/GBTester.jar")
    args = listOf(
      "$buildDir/compiler/build/main.gb"
    )

  }

  register<JavaExec>("runEmu") {
    dependsOn("compileASM")
    classpath = files("$buildDir/compiler/Emulicious.jar")
    args = listOf(
      "muted",
      "$buildDir/compiler/build/main.gb"
    )

  }

  register<JavaExec>("runXml") {
    classpath = files("$buildDir/compiler/Interpreter.jar")
    args = listOf(
      "$buildDir/compiler/output.xml"
    )

  }

  register<JavaExec>("runCMD") {
    main = "me.hannah.MainKt"

    classpath = sourceSets["main"].runtimeClasspath

    finalizedBy("runGB")
  }

  register<JavaExec>("runGUI") {
    main = "me.hannah.MainKt"

    classpath = sourceSets["main"].runtimeClasspath

    finalizedBy("runEmu")
  }
}


//idea {
//    module {
//        sourceDirs += file("generated-src/antlr/main")
//    }
//}


sourceSets {
  main {
    java.srcDir("generated-src/antlr/main/")
  }
}

idea {
  module {
    sourceDirs = sourceDirs + file("generated-src/antlr/main")
  }
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}
