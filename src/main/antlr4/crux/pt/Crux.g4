grammar Crux;

//Lexer Rules
VOID: 'void';
BOOL: 'bool';
INT: 'int';

AND: '&&';
OR: '||';
NOT: '!';
IF: 'if';
ELSE: 'else';
FOR: 'for';
BREAK: 'break';
TRUE: 'true';
FALSE: 'false';
RETURN: 'return';
CONTINUE: 'continue';
LOOP: 'loop';

OPEN_PAREN:	'(';
CLOSE_PAREN: ')';
OPEN_BRACE:	'{';
CLOSE_BRACE: '}';
OPEN_BRACKET: '[';
CLOSE_BRACKET: ']';
ADD: '+';
SUB: '-';
MUL: '*';
DIV: '/';
GREATER_EQUAL: '>=';
LESSER_EQUAL: '<=';
NOT_EQUAL: '!=';
EQUAL: '==';
GREATER_THAN: '>';
LESS_THAN: '<';
ASSIGN: '=';
COMMA: ',';
SEMICOLON: ';';

INTEGER
 : '0'
 | [1-9] [0-9]*
 ;

IDENTIFIER: [a-zA-Z] [a-zA-Z0-9_]*;

WhiteSpaces
 : [ \t\r\n]+ -> skip
 ;

Comment
 : '//' ~[\r\n]* -> skip
 ;

ERROR: . ;
EOF_TOKEN: EOF;

//Parser Rules
program: declList EOF;

declList: decl*;

decl
 : varDecl
 | arrayDecl
 | functionDefn
 ;

varDecl: type IDENTIFIER SEMICOLON;

arrayDecl: type IDENTIFIER OPEN_BRACKET INTEGER CLOSE_BRACKET SEMICOLON;

functionDefn: type IDENTIFIER OPEN_PAREN paramList CLOSE_PAREN stmtBlock;

paramList: (param (COMMA param)*)?;

param: type IDENTIFIER;

stmtBlock: OPEN_BRACE stmtList? CLOSE_BRACE;

stmtList: stmt*;

stmt
 : varDecl
 | callStmt
 | assignStmt
 | ifStmt
 | loopStmt
 | breakStmt
 | continueStmt
 | returnStmt
 ;

callStmt: callExpr SEMICOLON;

assignStmt: designator ASSIGN expr0 SEMICOLON;

ifStmt: IF expr0 stmtBlock (ELSE stmtBlock)?;

loopStmt: LOOP stmtBlock;

breakStmt: BREAK SEMICOLON;

continueStmt: CONTINUE SEMICOLON;

returnStmt: RETURN expr0? SEMICOLON;

callExpr: IDENTIFIER OPEN_PAREN exprList CLOSE_PAREN;

exprList: expr0 (COMMA expr0)* | /* empty */ ;

expr0: expr1 (op0 expr1)?;

expr1: expr2 | expr1 op1 expr2?;

expr2: expr3 | expr2 op2 expr3?;

expr3: NOT expr3
     | OPEN_PAREN expr0 CLOSE_PAREN
     | designator
     | callExpr
     | literal
 ;

designator: IDENTIFIER (OPEN_BRACKET expr0 CLOSE_BRACKET)?;

op0: GREATER_EQUAL | LESSER_EQUAL | NOT_EQUAL | EQUAL | GREATER_THAN | LESS_THAN;

op1: ADD | SUB | OR;

op2: MUL | DIV | AND;

literal: INTEGER | TRUE | FALSE;

type: VOID | INT | BOOL;
