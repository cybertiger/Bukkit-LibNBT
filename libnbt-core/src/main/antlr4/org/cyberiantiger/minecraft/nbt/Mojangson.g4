grammar Mojangson;

mojangson
    :   value
    ;

list
    :   '[' object (',' object)* ']'
    |   '[' list (',' list)* ']'
    |   '[' bytelist (',' bytelist)* ']'
    |   '[' intlist (',' intlist)* ']'
    |   '[' BYTE (',' BYTE)* ']'
    |   '[' SHORT (',' SHORT)* ']'
    |   '[' INT (',' INT)* ']'
    |   '[' LONG (',' LONG)* ']'
    |   '[' FLOAT (',' FLOAT)* ']'
    |   '[' DOUBLE (',' DOUBLE)* ']'
    |   '[' ']' // empty array
    ;

object
    :   '{' pair (',' pair)* ','? '}'
    |   '{' '}' // empty object
    ;
    
pair:   STRING ':' value ;


bytelist
    :   '<' BYTELISTBYTE (',' BYTELISTBYTE)* '>'
    |   '<' '>' // empty array
    ;

intlist
    :   '«' INT (',' INT)* '»'
    |   '«' '»' // empty array
    ;

value
    : 
    |   object  // recursion
    |   list // recursion
    |   bytelist
    |   intlist
    |   BYTE
    |   SHORT
    |   INT
    |   LONG
    |   FLOAT
    |   DOUBLE
    |   'null'
    ;

STRING :  '"' (ESC | ~["\\])* '"' ;
fragment ESC :   '\\' (["\\/bfnrt] | UNICODE) ;
fragment UNICODE : 'u' HEX HEX HEX HEX ;
fragment HEX : [0-9a-fA-F] ;
fragment INTEGER :  [-]? ('0' | [1-9] [0-9]*) ;
fragment DECIMAL : INTEGER '.' [0-9]+ ;
fragment BYTELISTBYTE : INTEGER [bB]? ;
fragment BYTE : INTEGER [bB] ;
fragment SHORT : INTEGER [sS] ;
fragment INT : INTEGER [iI]? ;
fragment LONG : INTEGER [lL] ;
fragment FLOAT : INTEGER [fF] | DECIMAL [fF] ;
fragment DOUBLE : INTEGER [dD] | DECIMAL ;

WS  :   [ \t\n\r]+ -> skip ;
