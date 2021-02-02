grammar HannahLanguage;


/*
 * Parser Rules
 */
root          : rootDef+ EOF ;
rootDef       : typeDef | fnDef | varDec | tilemapDec;


expr          : numberExpr | boolExpr | '(' call ')' | varRef | arrLit | structLit ;

args          : args arg | arg ;
arg           : expr ;
call          : (args)? '->' id | arg id arg ;

varDec        : id '<-' expr ;
varRef        : id  ;

tilemapDec    : 'tilemap' '<-' structLit ;


/*
* bool expressions
*/
rBoolExpr     : boolExpr   ;
lBoolTerm     : boolTerm   ;
rBoolTerm     : boolTerm   ;
lBoolFactor   : boolFactor ;

orExpr        : lBoolTerm   '||' rBoolExpr ;
andExpr       : lBoolFactor '&&' rBoolTerm ;

boolExpr      : boolTerm   | orExpr  ;
boolTerm      : boolFactor | andExpr ;
boolFactor    : boolLit | varRef | '(' (call | numberComp | boolFactor) ')' | '!' boolFactor ;

/*
* comparison
*/
lCompExpr     : numberExpr ;
rCompExpr     : numberExpr ;

eqExpr        : lCompExpr '='  rCompExpr ;
ltExpr        : lCompExpr '<'  rCompExpr ;
gtExpr        : lCompExpr '>'  rCompExpr ;
leqExpr       : lCompExpr '<=' rCompExpr ;
geqExpr       : lCompExpr '>=' rCompExpr ;

numberComp    : eqExpr | ltExpr | gtExpr | leqExpr | geqExpr;

/*
* arithmetics
*/
rExpr         : numberExpr   ;
lTerm         : numberTerm   ;
rTerm         : numberTerm   ;
lFactor       : numberFactor ;

plusExpr      : lTerm   '+'  rExpr ;
minusExpr     : lTerm   '-'  rExpr ;
mulExpr       : lFactor '*'  rTerm ;
divExpr       : lFactor '/'  rTerm ;
numberParen   : '(' numberExpr ')' ;

numberExpr    : numberTerm   | plusExpr | minusExpr | orExpr  ;
numberTerm    : numberFactor | mulExpr  | divExpr   | andExpr ;
numberFactor  : intLit | '(' call ')' | varRef  | numberParen | '-' numberFactor ;


/*
* flow control
*/
ifStmnt       : 'if' boolExpr thenStmnt (elseStmnt)? ;
thenStmnt     : 'then' (stmnt | blockBody) ;
elseStmnt     : 'else' (stmnt | blockBody) ;

whileStmnt    : 'loop' boolExpr (stmnt | blockBody) ;


/*
 * functions
*/
fnDef         : fnName fnArgs (exprBody | blockBody) ;
exprBody      : '=' expr ;
blockBody     : 'do' block 'end' ;
fnArgs        : ID* ;
fnName        : ID ;

block         : stmnts blockReturn? ;
stmnts        : stmnt* ;
stmnt         : call | varDec | ifStmnt | whileStmnt | retStmnt ;
retStmnt           : 'ret' expr;
blockReturn   : expr ;


typeDef       : argsTypeDef | noArgsTypeDef ;
noArgsTypeDef : fnName '::' type ;
argsTypeDef   : fnName '::' '(' defArgs '):' type ;
defArgs       : type+ ;
type          : ID  ;

/*
* literals
*/
intLit        : NUMBER ;
boolLit       : BOOL ;
id            : ID  ;

arrLit        : '[' (expr ',')* (expr)? ']' ;
structLit     : '{' (structField',')* (structField)? '}';
structField   : id ':' expr ;

/*
 * Lexer Rules
 */

BOOL        : 'false' | 'true';
//ID          : [a-zA-Z]+ ;
ID          : [a-zA-Z_][a-zA-Z0-9_]* ;
NUMBER      : [0-9]+ ;

//BLOCKCOMMENT: '/?'[.]*'?/' -> skip ;
COMMENT     : '//'~([\n\r])* -> skip ;
END         : [\n\r]+ -> skip ;
WS          : [ \t]+  -> skip ;
