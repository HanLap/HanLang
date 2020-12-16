grammar HannahLanguage;


/*
 * Parser Rules
 */
root    : stmnts EOF ;
stmnts  : stmnts stmnt | stmnt ;
stmnt   : call | varDec ;

args    : args arg | arg ;
arg     : intLit | '(' call ')' | varRef ;
call    : args '->' id | '->' id | arg id arg ;

varDec  : id '<-' arg ;
varRef  : id ;

intLit  : NUMBER ;
id      : ID  ;

/*
 * Lexer Rules
 */

ID         : [a-zA-Z]+ ;
NUMBER     : [0-9]+ ;

COMMENT    : '//'~([\n\r])* -> skip ;
END        : [\n\r]+ -> skip ;
WS         : [ \t]+  -> skip ;
