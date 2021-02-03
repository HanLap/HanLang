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

  override fun exitVarDec(ctx: VarDecContext?) {
    // ToDo: replace with correct element type
    writer.write("""<TypeElement><Primitive>int8</Primitive></TypeElement></Definition>""".trimIndent())
  }

  override fun enterTilemapDec(ctx: TilemapDecContext?) {
    writer.write("<Definition><Name>_tilemap</Name><TypeElement><StructType><Field><Name></Name><TypeElement><ArrayType><TypeElement><Primitive>int8</Primitive></TypeElement></ArrayType></TypeElement></Field></StructType></TypeElement>")
  }

  override fun exitTilemapDec(ctx: TilemapDecContext?) {
    writer.write("</Definition>")
  }

  override fun enterStructField(ctx: StructFieldContext?) {
    writer.write("<Field>")
  }

  override fun exitStructField(ctx: StructFieldContext?) {
    writer.write("</Field>")
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

  override fun enterArrLit(ctx: ArrLitContext?) {
    writer.write("<ExpressionElement><ArrayLiteral>")
  }

  override fun exitArrLit(ctx: ArrLitContext?) {
    writer.write("</ArrayLiteral></ExpressionElement>")
  }

  override fun enterStructLit(ctx: StructLitContext?) {
    writer.write("<ExpressionElement><StructLiteral>")
  }

  override fun exitStructLit(ctx: StructLitContext?) {
    writer.write("</StructLiteral></ExpressionElement>")
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

  override fun enterOrExpr(ctx: OrExprContext?) {
    writer.write("""<ExpressionElement><Binary><Operator>or</Operator>""")
  }

  override fun exitOrExpr(ctx: OrExprContext?) {
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

  override fun enterAndExpr(ctx: AndExprContext?) {
    writer.write("""<ExpressionElement><Binary><Operator>and</Operator>""")
  }

  override fun exitAndExpr(ctx: AndExprContext?) {
    writer.write("""</Binary></ExpressionElement>""")
  }

  override fun enterEqExpr(ctx: EqExprContext?) {
    writer.write("""<ExpressionElement><Binary><Operator>eq</Operator>""")
  }

  override fun exitEqExpr(ctx: EqExprContext?) {
    writer.write("""</Binary></ExpressionElement>""")
  }

  override fun enterLtExpr(ctx: LtExprContext?) {
    writer.write("""<ExpressionElement><Binary><Operator>lt</Operator>""")
  }

  override fun exitLtExpr(ctx: LtExprContext?) {
    writer.write("""</Binary></ExpressionElement>""")
  }

  override fun enterGtExpr(ctx: GtExprContext?) {
    writer.write("""<ExpressionElement><Binary><Operator>gt</Operator>""")
  }

  override fun exitGtExpr(ctx: GtExprContext?) {
    writer.write("""</Binary></ExpressionElement>""")
  }

  override fun enterLeqExpr(ctx: LeqExprContext?) {
    writer.write("""<ExpressionElement><Binary><Operator>leq</Operator>""")
  }

  override fun exitLeqExpr(ctx: LeqExprContext?) {
    writer.write("""</Binary></ExpressionElement>""")
  }

  override fun enterGeqExpr(ctx: GeqExprContext?) {
    writer.write("""<ExpressionElement><Binary><Operator>geq</Operator>""")
  }

  override fun exitGeqExpr(ctx: GeqExprContext?) {
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

  override fun enterRCompExpr(ctx: RCompExprContext?) {
    writer.write("<Right>")
  }

  override fun exitRCompExpr(ctx: RCompExprContext?) {
    writer.write("</Right>")
  }

  override fun enterLCompExpr(ctx: LCompExprContext?) {
    writer.write("<Left>")
  }

  override fun exitLCompExpr(ctx: LCompExprContext?) {
    writer.write("</Left>")
  }

  override fun enterLBoolTerm(ctx: LBoolTermContext?) {
    writer.write("<Left>")
  }

  override fun exitLBoolTerm(ctx: LBoolTermContext?) {
    writer.write("</Left>")
  }

  override fun enterLBoolFactor(ctx: LBoolFactorContext?) {
    writer.write("<Left>")
  }

  override fun exitLBoolFactor(ctx: LBoolFactorContext?) {
    writer.write("</Left>")
  }

  override fun enterRBoolTerm(ctx: RBoolTermContext?) {
    writer.write("<Right>")
  }

  override fun exitRBoolTerm(ctx: RBoolTermContext?) {
    writer.write("</Right>")
  }

  override fun enterRBoolExpr(ctx: RBoolExprContext?) {
    writer.write("<Right>")
  }

  override fun exitRBoolExpr(ctx: RBoolExprContext?) {
    writer.write("</Right>")
  }

  override fun enterWhileStmnt(ctx: WhileStmntContext?) {
    writer.write("<While>")
  }

  override fun exitWhileStmnt(ctx: WhileStmntContext?) {
    writer.write("</While>")
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

  override fun enterRetStmnt(ctx: RetStmntContext?) {
    writer.write("<Return>")
  }

  override fun exitRetStmnt(ctx: RetStmntContext?) {
    writer.write("</Return>")
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

  override fun enterIfStmnt(ctx: IfStmntContext?) {
    writer.write("<If>")
  }

  override fun exitIfStmnt(ctx: IfStmntContext?) {
    writer.write("</If>")
  }

  override fun enterThenStmnt(ctx: ThenStmntContext?) {
    writer.write("<Then>")
  }

  override fun exitThenStmnt(ctx: ThenStmntContext?) {
    writer.write("</Then>")
  }

  override fun enterElseStmnt(ctx: ElseStmntContext?) {
    writer.write("<Else>")
  }

  override fun exitElseStmnt(ctx: ElseStmntContext?) {
    writer.write("</Else>")
  }
}
