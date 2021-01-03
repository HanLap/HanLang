package me.hannah

import me.hannah.parser.*
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.tree.ParseTreeWalker
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import javax.xml.parsers.DocumentBuilder

import javax.xml.parsers.DocumentBuilderFactory


fun main(args: Array<String>) {
  File("build/compiler/").mkdirs()

  langParser(args[0])

  XmlParser("build/compiler/output.xml").parse()
}

fun getFunctionDefinitions(inputFile: String): Map<String, FunctionTypes> {
  val input = CharStreams.fromFileName(inputFile)

  val lexer = HannahLanguageLexer(input)
  val tokens = CommonTokenStream(lexer)
  val parser = HannahLanguageParser(tokens)

  return FunctionDefinitionFinder().find(parser)
}

fun langParser(inputFile: String) {
  println(File("").absolutePath + "/$inputFile")

  val functionDefinitions = getFunctionDefinitions(inputFile)

  val input = CharStreams.fromFileName(inputFile)

  val lexer = HannahLanguageLexer(input)
  val tokens = CommonTokenStream(lexer)
  val parser = HannahLanguageParser(tokens)

  val writer = FileWriter("build/compiler/output.xml")
  ParseTreeWalker.DEFAULT.walk(
    HannahLanguageListenerImpl(
      writer,
      functionDefinitions
    ),
    parser.root()
  )
  writer.close()
}


