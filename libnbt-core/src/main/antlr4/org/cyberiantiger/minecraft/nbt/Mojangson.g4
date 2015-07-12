grammar Mojangson;

options {
    output = AST;
}

object
    :   '{' pair (',' pair)* ','? '}'
    |   '{' '}' // empty object
    ;
    
pair:   QUOTED_STRING ':' value ;

list
    :   '[' object (',' object)* ']'
    |   '[' list (',' list)* ']'
    |   '[' bytelist (',' bytelist)* ']'
    |   '[' intlist (',' intlist)* ']'
    |   '[' INTEGER INTEGER_QUALIFIER? (',' INTEGER)* ']'
    |   '[' DECIMAL DECIMAL_QUALIFIER? (',' DECIMAL)* ']'
    |   '[' ']' // empty array
    ;


bytelist
    :   '<' INTEGER (',' INTEGER)* '>'
    |   '<' '>' // empty array
    ;

intlist
    :   '«' INTEGER (',' INTEGER)* '»'
    |   '«' '»' // empty array
    ;

value
    :   DECIMAL DECIMAL_QUALIFIER?
    |   INTEGER INTEGER_QUALIFIER?
    |   QUOTED_STRING
    |   object  // recursion
    |   list // recursion
    |   bytelist
    |   intlist
    |   'null'
    ;

QUOTED_STRING :  '"' (ESC | ~["\\])* '"' ;
ESC :   '\\' (["\\/bfnrt] | UNICODE) ;
UNICODE : 'u' HEX HEX HEX HEX ;
HEX : [0-9a-fA-F] ;
DECIMAL : INTEGER '.' [0-9]+ ;
INTEGER :  [-]? ('0' | [1-9] [0-9]*) ;
DECIMAL_QUALIFIER : [fFdD] ;
INTEGER_QUALIFIER : [bBsSiIlLfFdD] ;
WS  :   [ \t\n\r]+ -> skip ;
