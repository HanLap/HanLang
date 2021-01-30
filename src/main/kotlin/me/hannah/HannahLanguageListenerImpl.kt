package me.hannah

import me.hannah.parser.HannahLanguageBaseListener
import me.hannah.parser.HannahLanguageParser
import me.hannah.parser.HannahLanguageParser.*
import org.antlr.v4.runtime.tree.ErrorNode
import java.io.FileWriter

class HannahLanguageListenerImpl(
  private val writer: FileWriter,
  private val functionDefinitions: Map<String, FunctionTypes> = mapOf()
) : HannahLanguageBaseListener() {

  override fun visitErrorNode(node: ErrorNode?) {
    println("error at line ${node!!.sourceInterval.a}")
  }

  override fun enterRoot(ctx: RootContext?) {
    writer.write(
      """<Program xmlns="http://sp.informatik.uni-ulm.de/schema/AST.xsd">"""
    )
  }

  override fun exitRoot(ctx: RootContext?) {
    writer.write(
      """</Program>"""
    )
  }

  override fun enterId(ctx: IdContext?) {
    writer.write("<Name>${ctx!!.ID().text}</Name>")
  }

  override fun enterVarDec(ctx: VarDecContext?) {
    writer.write("<Definition>")
  }


  //  y <- 10

  /*
  <var declaration>
     <id>y</id>

     <expression>
      <intLit>
        10
      </intLit>
    </expression>
  </var declaration>

   */

  override fun exitVarDec(ctx: VarDecContext?) {
    // ToDo: replace with correct element type
    writer.write("""<TypeElement><Primitive>int8</Primitive></TypeElement></Definition>""".trimIndent())
  }

  override fun enterStmnt(ctx: StmntContext?) {
    writer.write("""<StatementElement>""")
  }

  override fun exitStmnt(ctx: StmntContext?) {
    writer.write("""</StatementElement>""")
  }

  override fun enterCall(ctx: CallContext?) {
    writer.write("""<ExpressionElement><Call>""")
  }

  override fun exitCall(ctx: CallContext?) {
    writer.write("</Call></ExpressionElement>")
  }

  override fun enterVarRef(ctx: VarRefContext?) {
    writer.write("""<ExpressionElement><Place><Variable>""")
  }

  override fun exitVarRef(ctx: VarRefContext?) {
    writer.write("""</Variable></Place></ExpressionElement>""")
  }

  override fun enterIntLit(ctx: IntLitContext?) {
    writer.write("""<ExpressionElement><IntegerLiteral><Value>${ctx!!.NUMBER().text}</Value></IntegerLiteral></ExpressionElement>""")
  }

  override fun enterPlusExpr(ctx: PlusExprContext?) {
    writer.write("""<ExpressionElement><Binary><Operator>add</Operator>""")
  }

  override fun exitPlusExpr(ctx: PlusExprContext?) {
    writer.write("""</Binary></ExpressionElement>""")
  }

  override fun enterMinusExpr(ctx: MinusExprContext?) {
    writer.write("""<ExpressionElement><Binary><Operator>sub</Operator>""")
  }

  override fun exitMinusExpr(ctx: MinusExprContext?) {
    writer.write("""</Binary></ExpressionElement>""")
  }

  override fun enterMulExpr(ctx: MulExprContext?) {
    writer.write("""<ExpressionElement><Binary><Operator>mul</Operator>""")
  }

  override fun exitMulExpr(ctx: MulExprContext?) {
    writer.write("""</Binary></ExpressionElement>""")
  }

  override fun enterDivExpr(ctx: DivExprContext?) {
    writer.write("""<ExpressionElement><Binary><Operator>div</Operator>""")
  }

  override fun exitDivExpr(ctx: DivExprContext?) {
    writer.write("""</Binary></ExpressionElement>""")
  }

  override fun enterRExpr(ctx: RExprContext?) {
    writer.write("<Right>")
  }

  override fun exitRExpr(ctx: RExprContext?) {
    writer.write("</Right>")
  }

  override fun enterLTerm(ctx: LTermContext?) {
    writer.write("<Left>")
  }

  override fun exitLTerm(ctx: LTermContext?) {
    writer.write("</Left>")
  }

  override fun enterRTerm(ctx: RTermContext?) {
    writer.write("<Right>")
  }

  override fun exitRTerm(ctx: RTermContext?) {
    writer.write("</Right>")
  }

  override fun enterLFactor(ctx: LFactorContext?) {
    writer.write("<Left>")
  }

  override fun exitLFactor(ctx: LFactorContext?) {
    writer.write("</Left>")
  }


  override fun enterTypeDef(ctx: TypeDefContext?) {

  }


  override fun enterFnDef(ctx: FnDefContext?) {
    val name = ctx!!.fnName().ID().text

    writer.write("<Procedure><Name>$name</Name>")
  }

  override fun exitFnDef(ctx: FnDefContext?) {
    val name = ctx!!.fnName().ID().text
    val functionType = functionDefinitions[name]!!

    writer.write("<TypeElement><Primitive>${functionType.rtrn}</Primitive></TypeElement>")

    ctx.fnArgs().ID()
      .map { it.text }
      .zip(functionType.args)
      .forEach { (name, type) -> writer.write("<ParameterTypes><Name>$name</Name><TypeElement><Primitive>$type</Primitive></TypeElement></ParameterTypes>") }

    writer.write("</Procedure>")
  }

  override fun enterExprBody(ctx: ExprBodyContext?) {
    writer.write("<StatementElement><Return>")
  }

  override fun exitExprBody(ctx: ExprBodyContext?) {
    writer.write("</Return></StatementElement>")
  }

  override fun enterBlockBody(ctx: BlockBodyContext?) {
    writer.write("<StatementElement><Block>")
  }

  override fun exitBlockBody(ctx: BlockBodyContext?) {
    writer.write("</Block></StatementElement>")
  }

  override fun enterBlockReturn(ctx: BlockReturnContext?) {
    writer.write("<StatementElement><Return>")
  }

  override fun exitBlockReturn(ctx: BlockReturnContext?) {
    writer.write("</Return></StatementElement>")
  }
}
