grammar HannahLanguage;


/*
 * Parser Rules
 */
root          : rootDef+ EOF ;
rootDef       : typeDef | fnDef | varDec;


expr          : intLit | '(' call ')' | varRef | boolExpr | '(' numberExpr ')' ;

args          : args arg | arg ;
arg           : expr ;
call          : args '->' id | '->' id | arg id arg ;

varDec        : id '<-' expr ;
varRef        : id ;


/*
* bool expressions
*/
orExpr        : boolTerm   '||' boolTerm ;
andExpr       : boolFactor '&&' boolFactor ;
boolParen     : '(' boolExpr ')' ;
notExpr       : '!' boolFactor ;

boolExpr      : boolTerm   | orExpr  ;
boolTerm      : boolFactor | andExpr ;
boolFactor    : boolLit    | notExpr | boolParen ;

/*
* arithmetics
*/
rExpr         : numberExpr   ;
lTerm         : numberTerm   ;
rTerm         : numberTerm   ;
lFactor       : numberFactor ;

plusExpr      : lTerm '+' rExpr   ;
minusExpr     : lTerm '-' rExpr   ;
mulExpr       : lFactor '*' rTerm ;
divExpr       : lFactor '/' rTerm ;
numberParen   : '(' numberExpr ')' ;

numberExpr    : numberTerm   | plusExpr | minusExpr ;
numberTerm    : numberFactor | mulExpr  | divExpr   ;
numberFactor  : expr | numberParen | '-' numberFactor ;


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
stmnt         : call | varDec ;
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

/*
 * Lexer Rules
 */

BOOL        : 'false' | 'true';
//ID          : [a-zA-Z]+ ;
ID          : [a-zA-Z][a-zA-Z0-9]* ;
NUMBER      : [0-9]+ ;

BLOCKCOMMENT: '/?'[.]*'?/' -> skip ;
COMMENT     : '//'~([\n\r])* -> skip ;
END         : [\n\r]+ -> skip ;
WS          : [ \t]+  -> skip ;
