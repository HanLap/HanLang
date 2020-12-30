package me.hannah

import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.FileInputStream
import java.io.FileWriter
import java.text.ParseException
import javax.xml.parsers.DocumentBuilderFactory

class XmlParser(
  private val inputFileName: String
) {

  private val output = FileWriter("build/compiler/build/main.asm")

  companion object {
    val REGISTER_CONVENTION_8BIT = listOf("a", "c", "b", "e", "d", "l", "h")
  }

  inner class VariablePointer {
    private val variableMap = HashMap<String, Int>()
    private var pointer = 0xD000

    fun getOrDeclare(name: String): Int {
      if (variableMap.containsKey(name)) {
        return variableMap[name]!!
      }
      variableMap[name] = pointer
      pointer += 2
      return variableMap[name]!!
    }
  }

  private val variablePointer = VariablePointer()


  fun parse() {
    val factory = DocumentBuilderFactory.newInstance()
    val builder = factory.newDocumentBuilder()

    val input = FileInputStream(inputFileName)
    val doc = builder.parse(input)
    val root = doc.documentElement


    output.write(
      """
      .include "../framework.asm"
      .include "../custom.asm"
      .section "main"
      main:
    
    
    """.trimIndent()
    )

    root.getElementsByTagName("Procedure")
      .toElementList()
      .first { it.getElementsByTagName("Name").item(0).textContent == "main" }
      .getElementsByTagName("Block")
      .item(0)
      .childNodes
      .toElementList()
      .forEach {
        when (it.tagName) {
          "StatementElement" -> handleStatementElement(it)
          else -> throw ParseException(
            "Expected Statement Element, found: ${it.tagName}",
            it.getUserData("lineNumber") as Int
          )
        }
      }



    output.write(
      """
      ret
      .ends
    """.trimIndent()
    )
    output.close()
  }


  private fun handleStatementElement(node: Element) {
    with(node.firstChild) {
      when (nodeName) {
        "ExpressionElement" -> handleExpressionElement(this as Element)
        "Definition" -> handleDefinition(this as Element)
      }
    }
    output.write("  pop af\n\n")
  }

  private fun handleExpressionElement(node: Element) {
    with(node.firstChild as Element) {
      when (nodeName) {
        "Call" -> handleCall(this)
        "IntegerLiteral" -> handleIntegerLiteral(this)
        "Place" -> handleVariableLd(this)
        "Binary" -> handleNumberBinary(this)
      }
      output.write("  push af\n")
    }
  }

  private fun handleDefinition(node: Element) {
    val name = node
      .getChildByTagName("Name")
      .textContent
      .let { variablePointer.getOrDeclare(it) }

    val value = node
      .getChildByTagName("ExpressionElement")

    handleExpressionElement(value)

    output.write(
      """
      |  pop af
      |  ld hl, $${name.toString(16)}
      |  ld [hl], a
      |  push af
      
      
      """.trimIndent().trimMargin()
    )
  }

  private fun handleCall(node: Element) {
    val args = node.getChildrenByTagName("ExpressionElement")

    args.forEach { handleExpressionElement(it) }

    ((args.size - 1) downTo 0).forEach {
      output.write(
        """
      |  pop af
      |  ld ${REGISTER_CONVENTION_8BIT[it]}, a
      |
      
      
      """.trimIndent().trimMargin()
      )
    }

    node.getChildrenByTagName("Name")
      .first()
      .textContent
      .let { output.write("\n  call $it\n\n") }
  }


  private fun handleNumberBinary(node: Element) {
    val op = node.getChildByTagName("Operator").textContent
    val l = node.getChildByTagName("Left").firstChild
    val r = node.getChildByTagName("Right").firstChild

    handleExpressionElement(l as Element)
    handleExpressionElement(r as Element)

    val stmnt = when(op) {
      "add" -> "add c"
      "sub" -> "sub c"
      "mul" -> "call mul"
      "div" -> "call div"
      else -> throw Error("Unknown binary operator '$op'")
    }

    output.write(
      """
     |  pop af
     |  ld c, a
     |  pop af
     |  $stmnt
     |
     
     
    """.trimIndent().trimMargin()
    )
  }

  private fun handleIntegerLiteral(node: Element) {
    node.getElementsByTagName("Value").item(0).textContent
      .let {
        output.write("  ld a, $it\n")
      }
  }


  private fun handleVariableLd(node: Element) {
    val name = node
      .getChildByTagName("Variable")
      .getChildByTagName("Name")
      .textContent
      .let { variablePointer.getOrDeclare(it) }

    output.write(
      """
    |  ld hl, $${name.toString(16)}
    |  ld a, [hl]
    |
    
    
    """.trimIndent().trimMargin()
    )
  }

  private fun NodeList.toElementList() =
    (0 until length)
      .map { item(it) as Element }

  private fun Element.getChildrenByTagName(name: String) =
    childNodes
      .toElementList()
      .filter { it.tagName == name }

  private fun Element.getChildByTagName(name: String) = getChildrenByTagName(name).first()
}
