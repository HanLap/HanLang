package me.hannah

import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.FileInputStream
import java.io.FileWriter
import java.text.ParseException
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.collections.HashMap


data class VariableData(val address: Int, val size: Int)
data class ArgumentData(val offset: Int)
typealias Arguments = Map<String, ArgumentData>

class XmlParser(
  private val inputFileName: String
) {

  private val bp = "\$D000"

  private val output = FileWriter("build/compiler/build/main.asm")
  private val write: (String) -> Unit = output::write
  private fun writeLn(string: String = "") = write("  $string\n")
  private fun debug(string: String) = write("$string\n")

  companion object {
    val REGISTER_CONVENTION_8BIT = listOf("a", "c", "b", "e", "d", "l", "h")
  }

  private val heapManager = object {
    private var heapPointer = 0xD002

    private val variableMap = HashMap<String, VariableData>()

    fun declareOrAssign(name: String, size: Int): VariableData {
      if (variableMap.containsKey(name)) {
        return variableMap[name]!!
      }

      val data = VariableData(heapPointer, size)
      variableMap[name] = data
      heapPointer += size
      return data
    }

    fun get(name: String) = variableMap[name]
    fun containsKey(name: String) = variableMap.containsKey(name)
  }

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
          "Definition" -> handleDefinition(it, mapOf())
        }
      }

    output.close()
  }

  private fun handleProcedureElement(node: Element) {
    val name = node.getChildByTagName("Name").textContent

    write(".section \"$name\"\n")
    write("$name:\n")


//    debug("; save prior bp to stack")
//    writeLn("ld a, (\$d000)")
//    writeLn("ld l, a")
//    writeLn("ld a, (\$d001)")
//    writeLn("ld h, a")
//    writeLn("push hl")


    // save basePointer
    debug("; save base pointer")
    writeLn("ld ($bp), sp")
    writeLn()

    val types = node.getChildrenByTagName("ParameterTypes")
    val args: Map<String, ArgumentData> =
      types.mapIndexed { i, it ->
        it.getChildByTagName("Name").textContent to ArgumentData(i*2)
      }.toMap()

    // save arguments to heap
    debug("; push args to stack")
    types
      .zip(REGISTER_CONVENTION_8BIT)
      .forEach { (_, reg) ->
        writeLn("ld a, $reg")
        writeLn("push af")
      }
    writeLn()


    handleStatementElement(node.getChildByTagName("StatementElement"), args)


    write("+:\n")
    debug("; restore old stack pointer")
    writeLn("ld c, a")
    writeLn("ld a, (\$d000)")
    writeLn("ld l, a")
    writeLn("ld a, (\$d001)")
    writeLn("ld h, a")
    writeLn("ld sp, hl")

//    debug("; restore prior bp")
//    writeLn("pop hl")
//
//
    writeLn("ld a, c")

    writeLn("ret")

    write(".ends\n\n\n")
  }


  private fun handleStatementElement(node: Element, args: Arguments) {
    with(node.firstChild as Element) {
      // dont pop after block element
      if (nodeName == "Block") {
        handleBlockElement(this, args)
      } else {
        when (nodeName) {
          "ExpressionElement" -> handleExpressionElement(this, args)
          "Definition" -> handleDefinition(this, args)
          "Return" -> handleReturnElement(this, args)
        }
        writeLn("pop af")
        writeLn()
        writeLn()
      }
    }
  }

  private fun handleBlockElement(node: Element, args: Arguments) {
    node.childNodes
      .toElementList()
      .forEach {
        when (it.tagName) {
          "StatementElement" -> handleStatementElement(it, args)
          else -> throw ParseException(
            "Expected Statement Element, found: ${it.tagName}",
            it.getUserData("lineNumber") as Int
          )
        }
      }
  }

  private fun handleReturnElement(node: Element, args: Arguments) {
    handleExpressionElement(node.getChildByTagName("ExpressionElement"), args)

    writeLn("pop af")
    writeLn("jp +")
  }

  private fun handleExpressionElement(node: Element, args: Arguments) {
    with(node.firstChild as Element) {
      when (nodeName) {
        "Call" -> handleCall(this, args)
        "IntegerLiteral" -> handleIntegerLiteral(this)
        "Place" -> handleVariableLd(this, args)
        "Binary" -> handleNumberBinary(this, args)
      }
      writeLn("push af")
      writeLn()
    }
  }

  private fun handleDefinition(node: Element, args: Arguments) {
    val value = node
      .getChildByTagName("ExpressionElement")

    handleExpressionElement(value, args)

    val name = node
      .getChildByTagName("Name")
      .textContent


    val heap = heapManager.declareOrAssign(name, 2)

    debug("; assign var $name")
    writeLn("ld hl, $${heap.address.toString(16)}")
    writeLn("pop af")
    writeLn("ld [hl], a")
    writeLn("push af")
    writeLn()
  }

  private fun handleCall(node: Element, args: Arguments) {
    val name = node.getChildrenByTagName("Name")
      .first()
      .textContent

    val callArgs = node.getChildrenByTagName("ExpressionElement")

    callArgs.forEach { handleExpressionElement(it, args) }

    debug("; calling $name")
    ((callArgs.size - 1) downTo 0).forEach {
      writeLn("pop af")
      writeLn("ld ${REGISTER_CONVENTION_8BIT[it]}, a")
    }


    writeLn("call $name")

  }


  private fun handleNumberBinary(node: Element, args: Arguments) {
    val op = node.getChildByTagName("Operator").textContent
    val l = node.getChildByTagName("Left").firstChild
    val r = node.getChildByTagName("Right").firstChild

    handleExpressionElement(l as Element, args)
    handleExpressionElement(r as Element, args)

    val stmnt = when (op) {
      "add" -> "add c"
      "sub" -> "sub c"
      "mul" -> "call mul"
      "div" -> "call div"
      else -> throw Error("Unknown binary operator '$op'")
    }

    writeLn("pop af")
    writeLn("ld c, a")
    writeLn("pop af")
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


  private fun handleVariableLd(node: Element, args: Arguments) {
    val name = node
      .getChildByTagName("Variable")
      .getChildByTagName("Name")
      .textContent


    // ld from function args
//    val offset = name.let { variablePointer.getOrDeclare(it) }
//    debug("; load var $name; offset: $offset")
//    writeLn("add sp, ${(variablePointer.stackEnd - offset) - 2}")
//    writeLn("pop af")
//    writeLn("add sp, ${-(variablePointer.stackEnd - offset)}")
    if (args.containsKey(name)) {
      val data = args[name]!!
      // lf from arguments

      // load bp
      debug("; ld argument $name")
      writeLn("ld a, (\$d000)")
      writeLn("ld e, a")
      writeLn("ld a, (\$d001)")
      writeLn("ld d, a")

      // save sp
      writeLn("ld hl, sp+0")
      writeLn("ld b, h")
      writeLn("ld c, l")

      // change sp to bp and pop value
      writeLn("ld h, d")
      writeLn("ld l, e")
      writeLn("ld sp, hl")
      writeLn("add sp, ${-data.offset -2}")
      writeLn("pop af")

      // restore sp
      writeLn("ld h, b")
      writeLn("ld l, c")
      writeLn("ld sp, hl")
    } else if (heapManager.containsKey(name)) {
      // ld from heap
      val data = heapManager.get(name)
      debug("; ld heap var $name")
      writeLn("ld de, $${data?.address?.toString(16)}")
      writeLn("ld a, (de)")
    } else {
      throw Error("variable $name is neither an argument of the current function nor allocated on the heap")
    }

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
