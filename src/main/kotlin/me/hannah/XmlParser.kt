package me.hannah

import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.FileInputStream
import java.io.FileWriter
import java.text.ParseException
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.collections.HashMap

class XmlParser(
  private val inputFileName: String
) {

  private val output = FileWriter("build/compiler/build/main.asm")
  private val write: (String) -> Unit = output::write
  private fun writeLn(string: String = "") = write("  $string\n")
  private fun debug(string: String) = write("$string\n")

  companion object {
    val REGISTER_CONVENTION_8BIT = listOf("a", "c", "b", "e", "d", "l", "h")
  }

  inner class VariablePointer {
    private val variableStack = arrayListOf<HashMap<String, Int>>(HashMap())
    var stackEnd = 0
      private set
//    init {
//      variableStack.push(HashMap())
//    }

    fun openScope() {
      variableStack.add(HashMap())
    }

    fun closeScope() {
      val scope = variableStack.last()
      stackEnd -= scope.size * 2

      variableStack.removeLast()
    }

    fun push(reg: String) {
      stackEnd += 2
      writeLn("push $reg ; stack: $stackEnd")
    }

    fun pop(reg: String) {
      stackEnd -= 2
      writeLn("pop $reg ; stack: $stackEnd")
    }

    fun call(name: String) {
      stackEnd += 2
      writeLn("call $name\n")
    }

    fun ret() {
      stackEnd -= 2
      writeLn("ret")
    }

    fun declare(name: String): Int {
      require(!variableStack.last().containsKey(name)) {
        throw Error("variable $name already declared in this scope!")
      }
      variableStack.last()[name] = stackEnd
      stackEnd += 2
      return variableStack.last()[name]!!
    }

    fun getOrDeclare(name: String): Int {
      variableStack.reversed().forEach {
        if (it.containsKey(name)) {
          return it[name]!!
        }
      }
      variableStack.last()[name] = stackEnd
      stackEnd += 2
      return variableStack.last()[name]!!
    }
  }

  private val variablePointer = VariablePointer()
  private val pop = variablePointer::pop
  private val push = variablePointer::push

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
      
      
    """.trimIndent()
    )

    root
      .childNodes
      .toElementList()
      .forEach {
        when (it.tagName) {
          "Procedure" -> handleProcedureElement(it)
          "Definition" -> handleDefinition(it)
        }
      }

    output.close()
  }

  private fun handleProcedureElement(node: Element) {
    val name = node.getChildByTagName("Name").textContent

    output.write(
      """
      .section "$name"
      $name:
      
      """.trimIndent()
    )

    variablePointer.openScope()

    node.getChildrenByTagName("ParameterTypes")
      .forEachIndexed { i, it ->
        val pointer = variablePointer.declare(it.getChildByTagName("Name").textContent)

        output.write(
          """
          |  ld hl, $${pointer.toString(16)}
          |  ld [hl], ${REGISTER_CONVENTION_8BIT[i]}
          |
          |
        """.trimMargin()
        )
      }

    handleStatementElement(node.getChildByTagName("StatementElement"))

    variablePointer.closeScope()

    output.write(
      """
      .ends
      
      
    """.trimIndent()
    )
  }


  private fun handleStatementElement(node: Element) {
    with(node.firstChild as Element) {
      when (nodeName) {
        "ExpressionElement" -> handleExpressionElement(this)
        "Definition" -> handleDefinition(this)
        "Return" -> handleReturnElement(this)
        "Block" -> handleBlockElement(this)
      }
    }
    pop("af")
    writeLn()
    writeLn()
  }

  private fun handleBlockElement(node: Element) {
    node.childNodes
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
  }

  private fun handleReturnElement(node: Element) {
    handleExpressionElement(node.getChildByTagName("ExpressionElement"))

    pop("af")
    variablePointer.ret()
  }

  private fun handleExpressionElement(node: Element) {
    with(node.firstChild as Element) {
      when (nodeName) {
        "Call" -> handleCall(this)
        "IntegerLiteral" -> handleIntegerLiteral(this)
        "Place" -> handleVariableLd(this)
        "Binary" -> handleNumberBinary(this)
      }
      push("af")
      writeLn()
    }
  }

  private fun handleDefinition(node: Element) {
    val value = node
      .getChildByTagName("ExpressionElement")

    handleExpressionElement(value)

    val name = node
      .getChildByTagName("Name")
      .textContent

    debug("; assign var $name")
    pop("af")
    name.let { variablePointer.getOrDeclare(it) }
    writeLn("push af")
    push("af")
    writeLn()
  }

  private fun handleCall(node: Element) {
    val name = node.getChildrenByTagName("Name")
      .first()
      .textContent

    val args = node.getChildrenByTagName("ExpressionElement")

    args.forEach { handleExpressionElement(it) }

    debug("; calling $name")
    ((args.size - 1) downTo 0).forEach {
      pop("af")
      writeLn("ld ${REGISTER_CONVENTION_8BIT[it]}, a")
    }


    writeLn("call $name")
  }


  private fun handleNumberBinary(node: Element) {
    val op = node.getChildByTagName("Operator").textContent
    val l = node.getChildByTagName("Left").firstChild
    val r = node.getChildByTagName("Right").firstChild

    handleExpressionElement(l as Element)
    handleExpressionElement(r as Element)

    val stmnt = when (op) {
      "add" -> "add c"
      "sub" -> "sub c"
      "mul" -> "call mul"
      "div" -> "call div"
      else -> throw Error("Unknown binary operator '$op'")
    }

    pop("af")
    writeLn("ld c, a")
    pop("af")
    writeLn(stmnt)
    writeLn()
  }

  private fun handleIntegerLiteral(node: Element) {
    node.getElementsByTagName("Value").item(0).textContent
      .let {
        debug("; load int literal $it")
        writeLn("ld a, $it")
      }
  }


  private fun handleVariableLd(node: Element) {
    val name = node
      .getChildByTagName("Variable")
      .getChildByTagName("Name")
      .textContent

    val offset = name.let { variablePointer.getOrDeclare(it) }

    debug("; load var $name; offset: $offset")
    writeLn("add sp, ${(variablePointer.stackEnd - offset) - 2}")
    writeLn("pop af")
    writeLn("add sp, ${-(variablePointer.stackEnd - offset)}")
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
