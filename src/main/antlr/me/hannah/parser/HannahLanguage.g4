grammar HannahLanguage;


/*
 * Parser Rules
 */
root          : stmnts EOF ;
stmnts        : stmnts stmnt | stmnt ;
stmnt         : call | varDec ;


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
numberFactor  : intLit | numberParen | '-' numberFactor ;


/*
<Exp> ::= <Exp> + <Term> |
    <Exp> - <Term> |
    <Term>

<Term> ::= <Term> * <Factor> |
    <Term> / <Factor> |
    <Factor>

<Factor> ::= x | y | ... |
    ( <Exp> ) |
    - <Factor>
*/


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
ID          : [a-zA-Z]+ ;
NUMBER      : [0-9]+ ;

COMMENT     : '//'~([\n\r])* -> skip ;
END         : [\n\r]+ -> skip ;
WS          : [ \t]+  -> skip ;
