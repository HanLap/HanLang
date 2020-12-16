package me.hannah

import me.hannah.parser.HannahLanguageBaseListener
import me.hannah.parser.HannahLanguageListener
import me.hannah.parser.HannahLanguageParser
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.TerminalNode
import java.io.FileWriter

class HannahLanguageListenerImpl(
  private val writer: FileWriter
) : HannahLanguageBaseListener() {

  override fun visitErrorNode(node: ErrorNode?) {
    println("error at line ${node!!.sourceInterval.a}")

  }

  override fun enterRoot(ctx: HannahLanguageParser.RootContext?) {
    writer.write(
      """<Program xmlns="http://sp.informatik.uni-ulm.de/schema/AST.xsd"><Procedure><Name>main</Name><StatementElement><Block>"""
    )
  }

  override fun exitRoot(ctx: HannahLanguageParser.RootContext?) {
    writer.write(
      """</Block></StatementElement><TypeElement><Primitive>void</Primitive></TypeElement></Procedure></Program>"""
    )
  }

  override fun enterId(ctx: HannahLanguageParser.IdContext?) {
    writer.write("<Name>${ctx!!.ID().text}</Name>")
  }

  override fun enterVarDec(ctx: HannahLanguageParser.VarDecContext?) {
    writer.write("""<Definition>""".trimIndent())
  }

  override fun exitVarDec(ctx: HannahLanguageParser.VarDecContext?) {
    writer.write("""</Definition>""".trimIndent())
  }

  override fun enterStmnt(ctx: HannahLanguageParser.StmntContext?) {
    writer.write("""<StatementElement>""")
  }

  override fun exitStmnt(ctx: HannahLanguageParser.StmntContext?) {
    writer.write("""</StatementElement>""")
  }

  override fun enterCall(ctx: HannahLanguageParser.CallContext?) {
    writer.write("""<ExpressionElement><Call>""")
  }

  override fun exitCall(ctx: HannahLanguageParser.CallContext?) {
    writer.write("</Call></ExpressionElement>")
  }

  override fun enterVarRef(ctx: HannahLanguageParser.VarRefContext?) {
    writer.write("""<ExpressionElement><Place><Variable>""")
  }

  override fun exitVarRef(ctx: HannahLanguageParser.VarRefContext?) {
    writer.write("""</Variable></Place></ExpressionElement>""")
  }

  override fun enterIntLit(ctx: HannahLanguageParser.IntLitContext?) {
    writer.write("""<ExpressionElement><IntegerLiteral><Value>${ctx!!.NUMBER().text}</Value></IntegerLiteral></ExpressionElement>""")
  }
}
