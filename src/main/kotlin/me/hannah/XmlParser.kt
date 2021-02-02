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

  private var labelN = 0

  companion object {
    val REGISTER_CONVENTION_8BIT = listOf("a", "c", "b", "e", "d", "l", "h")
  }

  private val tileMap = HashMap<String, Int>()


  private val output = FileWriter("build/compiler/build/main.asm")
  private fun write(string: String) = output.write(string)
  private fun writeLn(string: String = "") = write("  $string\n")
  private fun debug(string: String) = write("$string\n")

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

    handleTileMap(root)

    root
      .childNodes
      .toElementList()
      .forEach {
        when (it.tagName) {
          "Procedure" -> handleProcedureElement(it)
          "Definition" -> {
            // skip if it is tilemap definition
            if (it.getChildByTagName("Name").textContent != "_tilemap") {
              handleDefinition(it, mapOf())
            }
          }
        }
      }

    output.close()
  }

  private fun handleTileMap(root: Element) {
    write(".section \"tilemap\"\n")
    write("tilemap:\n")
    root
      .getChildrenByTagName("Definition")
      .find { it.getChildByTagName("Name").textContent == "_tilemap" }
      ?.getChildByTagName("ExpressionElement")
      ?.getChildByTagName("StructLiteral")
      ?.getChildrenByTagName("Field")
      ?.forEachIndexed { i, it ->
        val name = it.getChildByTagName("Name").textContent
        tileMap[name] = i + 1
        it.getChildByTagName("ExpressionElement")
          .getChildByTagName("ArrayLiteral")
          .getChildrenByTagName("ExpressionElement")
          .also { require(it.size == 8) { throw Error("tile $name does not have 8 int literals") } }
          .map { it.getChildByTagName("IntegerLiteral").getChildByTagName("Value").textContent }
          .forEach { writeLn(".db %$it") }
        writeLn()
      }
    write(".ends\n\n")

  }

  private fun handleProcedureElement(node: Element) {
    val name = node.getChildByTagName("Name").textContent

    val returnLabel = "ret_${labelN++}"

    write(".section \"$name\"\n")
    write("$name:\n")

    // setup tiles if this is main procedure and tilemap is not empty
    if (tileMap.isNotEmpty() && name == "main") {
      val tileCount = tileMap.size
      writeLn("call clearTilemap")
      writeLn("ld hl, tilemap")
      writeLn("ld a, 1")
      writeLn("ld c, $tileCount")
      writeLn("call loadTiles1bpp")
    }


    val types = node.getChildrenByTagName("ParameterTypes")
    val args: Map<String, ArgumentData> =
      types.mapIndexed { i, it ->
        it.getChildByTagName("Name").textContent to ArgumentData(i * 2)
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


    debug("; save prior bp to stack")
    writeLn("ld a, (\$d000)")
    writeLn("ld l, a")
    writeLn("ld a, (\$d001)")
    writeLn("ld h, a")
    writeLn("push hl")


    // save basePointer
    debug("; save base pointer")
    writeLn("ld ($bp), sp")
    writeLn()



    handleStatementElement(node.getChildByTagName("StatementElement"), args, returnLabel)


    write("$returnLabel:\n")
    debug("; move SP to BP")
    writeLn("ld c, a")
    writeLn("ld a, (\$d000)")
    writeLn("ld l, a")
    writeLn("ld a, (\$d001)")
    writeLn("ld h, a")
    writeLn("ld sp, hl")

    debug("; restore prior bp")
    writeLn("pop hl")
    writeLn("ld a, l")
    writeLn("ld (\$d000), a")
    writeLn("ld a, h")
    writeLn("ld (\$d001), a")


    writeLn("add sp, ${args.size * 2}")
    writeLn("ld a, c")

    writeLn("ret")

    write(".ends\n\n\n")
  }


  private fun handleStatementElement(node: Element, args: Arguments, returnLabel: String) {
    with(node.firstChild as Element) {
      // dont pop after ...
      when (nodeName) {
        "Block" -> handleBlockElement(this, args, returnLabel)
        "If" -> handleIfElement(this, args, returnLabel)
        "While" -> handleWhileElement(this, args, returnLabel)
        "Return" -> handleReturnElement(this, args, returnLabel)
        // pop after ...
        else -> {
          when (nodeName) {
            "ExpressionElement" -> handleExpressionElement(this, args)
            "Definition" -> handleDefinition(this, args)
          }
          writeLn("pop af")
          writeLn()
          writeLn()
        }
      }
    }
  }

  private fun handleBlockElement(node: Element, args: Arguments, returnLabel: String) {
    node.childNodes
      .toElementList()
      .forEach {
        when (it.tagName) {
          "StatementElement" -> handleStatementElement(it, args, returnLabel)
          else -> throw ParseException(
            "Expected Statement Element, found: ${it.tagName}",
            it.getUserData("lineNumber") as Int
          )
        }
      }
  }

  private fun handleReturnElement(node: Element, args: Arguments, returnLabel: String) {
    handleExpressionElement(node.getChildByTagName("ExpressionElement"), args)

    writeLn("pop af")
    writeLn("jp ${returnLabel}")
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


    // handle setTile calls differently for magic
    if (name == "setTile") {
      handleSetTile(callArgs.first(), args)
      callArgs.drop(1).forEach { handleExpressionElement(it, args) }
    } else {
      callArgs.forEach { handleExpressionElement(it, args) }
    }

    debug("; calling $name")
    ((callArgs.size - 1) downTo 0).forEach {
      writeLn("pop af")
      writeLn("ld ${REGISTER_CONVENTION_8BIT[it]}, a")
    }


    writeLn("call $name")

  }

  private fun handleSetTile(node: Element, args: Arguments) {
    val name = node
      .getChildByTagName("Place")
      .getChildByTagName("Variable")
      .getChildByTagName("Name")
      .textContent

    debug("; load tileId")
    writeLn("ld a, ${tileMap.getOrElse(name) { throw Error("tile $name is not defined and can not be accessed") }}")
    writeLn("push af")
    writeLn()
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
      "and" -> "and c"
      "or" -> "or c"
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
    when {
      args.containsKey(name) -> {
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
        writeLn("add sp, ${(args.size * 2) - data.offset}")
        writeLn("pop af")

        // restore sp
        writeLn("ld h, b")
        writeLn("ld l, c")
        writeLn("ld sp, hl")
      }
      heapManager.containsKey(name) -> {
        // ld from heap
        val data = heapManager.get(name)
        debug("; ld heap var $name")
        writeLn("ld de, $${data?.address?.toString(16)}")
        writeLn("ld a, (de)")
      }
      else -> {
        throw Error("variable $name is neither an argument of the current function scope nor allocated on the heap")
      }
    }

  }

  private fun handleWhileElement(node: Element, args: Arguments, returnLabel: String) {

    val startLabel = "while_${labelN++}"
    val endLabel = "while_${labelN++}"

    write("$startLabel:\n")
    handleCondition(node.getChildByTagName("ExpressionElement"), args, startLabel, endLabel)
    debug("; while body")
    handleStatementElement(node.getChildByTagName("StatementElement"), args, returnLabel)
    writeLn("jp $startLabel")
    write("$endLabel:\n")
  }

  private fun handleIfElement(node: Element, args: Arguments, returnLabel: String) {
    val condition = node.getChildByTagName("ExpressionElement")
    val thenN = node.getChildByTagName("Then")
    val elseN = node.getChildrenByTagName("Else").firstOrNull()

    val endLabel = "if_${labelN++}"
    val thenLabel = "if_${labelN++}"
    val elseLabel = elseN?.let { "if_${labelN++}" }

    handleCondition(condition, args, thenLabel, elseLabel ?: endLabel)

    write("$thenLabel:\n")
    handleStatementElement(thenN.getChildByTagName("StatementElement"), args, returnLabel)
    writeLn("jp $endLabel")

    elseN?.let {
      write("$elseLabel:\n")
      handleStatementElement(it.getChildByTagName("StatementElement"), args, returnLabel)
    }
    write("$endLabel:\n")
  }

  private fun handleCondition(
    node: Element,
    args: Arguments,
    thenLabel: String,
    elseLabel: String
  ) {

    handleBinaryJump(node.getChildByTagName("Binary"), args, thenLabel, elseLabel)

    writeLn("pop af")
    writeLn("ld c, a")
    writeLn("pop af")

    writeLn("cp")
    writeLn("jp nz, $elseLabel:")

  }

  private fun handleBinaryJump(
    node: Element,
    args: Arguments,
    thenLabel: String,
    elseLabel: String
  ) {
    val op = node.getChildByTagName("Operator").textContent
    val left = node.getChildByTagName("Left").firstChild
    val right = node.getChildByTagName("Right").firstChild

    // handle boolean expression and number calculation with default function
    if (!arrayOf("eq", "lt", "gt", "leq", "geq").contains(op)) {
      handleNumberBinary(node, args)
    } else {
      handleExpressionElement(left as Element, args)
      handleExpressionElement(right as Element, args)

      val skipLabel = "eval_${labelN++}"

      debug("; compare values")
      writeLn("pop af")
      writeLn("ld c, a")
      writeLn("pop af")
      writeLn("cp c")
      writeLn("ld a, 1")
      debug("; check $op")
      when (op) {
        "eq" -> {
          writeLn("jp z, $skipLabel")
        }
        "lt" -> {
          writeLn("jp  c, $skipLabel")
        }
        "gt" -> {
          writeLn("jp  nc, $skipLabel")
          writeLn("jp  nz, $skipLabel")
        }
        "leq" -> {
          writeLn("jp nc, $skipLabel")
          writeLn("jp  z, $skipLabel")
        }
        "geq" ->{
          writeLn("jp  nc, $skipLabel")
          writeLn("jp   z, $skipLabel")
        }
      }
      writeLn("ld a, 0")
      write("$skipLabel:")
      writeLn("push af")
      writeLn()
    }
  }

  private fun handleBooleanElement(node: Element, args: Arguments) {
    val op = node.getChildByTagName("Operator").textContent
    val left = node.getChildByTagName("Left").firstChild
    val right = node.getChildByTagName("Right").firstChild

    handleExpressionElement(left as Element, args)
    handleExpressionElement(right as Element, args)

    val skipLabel = "eval_${labelN++}"

    debug("; compare values")
    writeLn("pop af")
    writeLn("ld c, a")
    writeLn("pop af")
    writeLn("cp c")
    writeLn("ld a, 1")
    debug("; check $op")
    when (op) {
      "eq" -> {
        writeLn("jp z, $skipLabel")
      }
      "lt" -> {
        writeLn("jp  c, $skipLabel")
      }
      "gt" -> {
        writeLn("jp  nc, $skipLabel")
        writeLn("jp  nz, $skipLabel")
      }
      "leq" -> {
        writeLn("jp nc, $skipLabel")
        writeLn("jp  z, $skipLabel")
      }
      "geq" ->{
        writeLn("jp  nc, $skipLabel")
        writeLn("jp   z, $skipLabel")
      }
    }
    writeLn("ld a, 0")
    write("$skipLabel:")
    writeLn("push af")
    writeLn()
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
