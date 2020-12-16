// Generated from me/hannah/parser/HannahLanguage.g4 by ANTLR 4.9
package me.hannah.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link HannahLanguageParser}.
 */
public interface HannahLanguageListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link HannahLanguageParser#root}.
	 * @param ctx the parse tree
	 */
	void enterRoot(HannahLanguageParser.RootContext ctx);
	/**
	 * Exit a parse tree produced by {@link HannahLanguageParser#root}.
	 * @param ctx the parse tree
	 */
	void exitRoot(HannahLanguageParser.RootContext ctx);
	/**
	 * Enter a parse tree produced by {@link HannahLanguageParser#stmnts}.
	 * @param ctx the parse tree
	 */
	void enterStmnts(HannahLanguageParser.StmntsContext ctx);
	/**
	 * Exit a parse tree produced by {@link HannahLanguageParser#stmnts}.
	 * @param ctx the parse tree
	 */
	void exitStmnts(HannahLanguageParser.StmntsContext ctx);
	/**
	 * Enter a parse tree produced by {@link HannahLanguageParser#stmnt}.
	 * @param ctx the parse tree
	 */
	void enterStmnt(HannahLanguageParser.StmntContext ctx);
	/**
	 * Exit a parse tree produced by {@link HannahLanguageParser#stmnt}.
	 * @param ctx the parse tree
	 */
	void exitStmnt(HannahLanguageParser.StmntContext ctx);
	/**
	 * Enter a parse tree produced by {@link HannahLanguageParser#args}.
	 * @param ctx the parse tree
	 */
	void enterArgs(HannahLanguageParser.ArgsContext ctx);
	/**
	 * Exit a parse tree produced by {@link HannahLanguageParser#args}.
	 * @param ctx the parse tree
	 */
	void exitArgs(HannahLanguageParser.ArgsContext ctx);
	/**
	 * Enter a parse tree produced by {@link HannahLanguageParser#arg}.
	 * @param ctx the parse tree
	 */
	void enterArg(HannahLanguageParser.ArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link HannahLanguageParser#arg}.
	 * @param ctx the parse tree
	 */
	void exitArg(HannahLanguageParser.ArgContext ctx);
	/**
	 * Enter a parse tree produced by {@link HannahLanguageParser#call}.
	 * @param ctx the parse tree
	 */
	void enterCall(HannahLanguageParser.CallContext ctx);
	/**
	 * Exit a parse tree produced by {@link HannahLanguageParser#call}.
	 * @param ctx the parse tree
	 */
	void exitCall(HannahLanguageParser.CallContext ctx);
	/**
	 * Enter a parse tree produced by {@link HannahLanguageParser#varDec}.
	 * @param ctx the parse tree
	 */
	void enterVarDec(HannahLanguageParser.VarDecContext ctx);
	/**
	 * Exit a parse tree produced by {@link HannahLanguageParser#varDec}.
	 * @param ctx the parse tree
	 */
	void exitVarDec(HannahLanguageParser.VarDecContext ctx);
	/**
	 * Enter a parse tree produced by {@link HannahLanguageParser#varRef}.
	 * @param ctx the parse tree
	 */
	void enterVarRef(HannahLanguageParser.VarRefContext ctx);
	/**
	 * Exit a parse tree produced by {@link HannahLanguageParser#varRef}.
	 * @param ctx the parse tree
	 */
	void exitVarRef(HannahLanguageParser.VarRefContext ctx);
	/**
	 * Enter a parse tree produced by {@link HannahLanguageParser#intLit}.
	 * @param ctx the parse tree
	 */
	void enterIntLit(HannahLanguageParser.IntLitContext ctx);
	/**
	 * Exit a parse tree produced by {@link HannahLanguageParser#intLit}.
	 * @param ctx the parse tree
	 */
	void exitIntLit(HannahLanguageParser.IntLitContext ctx);
	/**
	 * Enter a parse tree produced by {@link HannahLanguageParser#id}.
	 * @param ctx the parse tree
	 */
	void enterId(HannahLanguageParser.IdContext ctx);
	/**
	 * Exit a parse tree produced by {@link HannahLanguageParser#id}.
	 * @param ctx the parse tree
	 */
	void exitId(HannahLanguageParser.IdContext ctx);
}