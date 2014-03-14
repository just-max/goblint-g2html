package g2html;

%%

%class CTokens
%public
%final
%apiprivate
%function next_token1
%type Symbol
%unicode

%{

  public enum Kind { LPAREN, RPAREN, LBRACE, RBRACE, LBRACK, RBRACK, SEMICOLON, COMMA, DOT, EQ, GT, LT, NOT, COMP,
                     QUESTION, COLON, EQEQ, LTEQ, GTEQ, NOTEQ, ANDAND, OROR, PLUSPLUS, MINUSMINUS, PLUS, MINUS, 
                     MULT, DIV, AND, OR, XOR, MOD, LSHIFT, RSHIFT, URSHIFT, PLUSEQ, MINUSEQ, MULTEQ, DIVEQ, 
                     ANDEQ, OREQ, XOREQ, MODEQ, LSHIFTEQ, RSHIFTEQ, URSHIFTEQ, ASM, AUTO, BREAK, CASE, CHAR, CONST,
                     CONTINUE, DEFAULT, DO, DOUBLE, ELSE, ENUM, EXTERN, FLOAT, FOR, GOTO, IF, INT, LONG, REGISTER,
                     RETURN, SHORT, SIGNED, SIZEOF, STATIC, STRUCT, SWITCH, TYPEDEF, UNION, UNSIGNED, VOID, VOLATILE, 
                     WHILE, TEXT, EOL, EOF, STRING, STRING_CONT, COMMENT, COMMENT_CONT, PPROC, NUMBER, WHITESP, CHARLIT }

  class Symbol {
    public Kind k;
    public String s;
    public Symbol(Kind k, String s){
      this.k = k;
      this.s = s;
    }
    public String toString(){
      return k.toString()+s;
    }
  }

  private Symbol symbol(Kind k, String s){
    return new Symbol(k,s);
  }
  
  private Symbol symbol(Kind k){
    return new Symbol(k, yytext());
  }
  
  public Symbol next_token() throws java.io.IOException {
    return next_token1();
  }

  StringBuilder string = new StringBuilder();
  
%}

/* main character classes */
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]

WhiteSpace = [ \t\f]

/* identifiers */
Identifier = [:jletter:][:jletterdigit:]*

/* integer literals */
DecIntegerLiteral = 0 | [1-9][0-9]*
DecLongLiteral    = {DecIntegerLiteral} [lL]

HexIntegerLiteral = 0 [xX] 0* {HexDigit} {1,8}
HexLongLiteral    = 0 [xX] 0* {HexDigit} {1,16} [lL]
HexDigit          = [0-9a-fA-F]
    
/* floating point literals */        
FloatLiteral  = ({FLit1}|{FLit2}|{FLit3}) {Exponent}? [fF]
DoubleLiteral = ({FLit1}|{FLit2}|{FLit3}) {Exponent}?

FLit1    = [0-9]+ \. [0-9]* 
FLit2    = \. [0-9]+ 
FLit3    = [0-9]+ 
Exponent = [eE] [+-]? [0-9]+

Number  = {FloatLiteral} | {DoubleLiteral} | {DecIntegerLiteral} | {DecLongLiteral} | {HexLongLiteral} | {HexIntegerLiteral}


%state STRING, CHARLITERAL, COMMENT

%%
<YYINITIAL> {
  {WhiteSpace}+                              { return symbol(Kind.WHITESP);   }
                                                                              
  "#include"{WhiteSpace}+"<"[^">"]+">"       { return symbol(Kind.PPROC);     }
  
  #{Identifier}+                             { return symbol(Kind.PPROC);     }
                                                                              
  /* keywords */                                                              
  "asm"                                      { return symbol(Kind.ASM);       }     
  "auto"                                     { return symbol(Kind.AUTO);      }   
  "break"                                    { return symbol(Kind.BREAK);     }   
  "case"                                     { return symbol(Kind.CASE);      }   
  "char"                                     { return symbol(Kind.CHAR);      }   
  "const"                                    { return symbol(Kind.CONST);     }   
  "continue"                                 { return symbol(Kind.CONTINUE);  }       
  "default"                                  { return symbol(Kind.DEFAULT);   }     
  "do"                                       { return symbol(Kind.DO);        } 
  "double"                                   { return symbol(Kind.DOUBLE);    }     
  "else"                                     { return symbol(Kind.ELSE);      }   
  "enum"                                     { return symbol(Kind.ENUM);      }   
  "extern"                                   { return symbol(Kind.EXTERN);    }     
  "float"                                    { return symbol(Kind.FLOAT);     }   
  "for"                                      { return symbol(Kind.FOR);       } 
  "goto"                                     { return symbol(Kind.GOTO);      }   
  "if"                                       { return symbol(Kind.IF);        } 
  "int"                                      { return symbol(Kind.INT);       } 
  "long"                                     { return symbol(Kind.LONG);      }   
  "register"                                 { return symbol(Kind.REGISTER);  }       
  "return"                                   { return symbol(Kind.RETURN);    }     
  "short"                                    { return symbol(Kind.SHORT);     }   
  "signed"                                   { return symbol(Kind.SIGNED);    }     
  "sizeof"                                   { return symbol(Kind.SIZEOF);    }     
  "static"                                   { return symbol(Kind.STATIC);    }     
  "struct"                                   { return symbol(Kind.STRUCT);    }     
  "switch"                                   { return symbol(Kind.SWITCH);    }     
  "typedef"                                  { return symbol(Kind.TYPEDEF);   }     
  "union"                                    { return symbol(Kind.UNION);     }   
  "unsigned"                                 { return symbol(Kind.UNSIGNED);  }       
  "void"                                     { return symbol(Kind.VOID);      }   
  "volatile"                                 { return symbol(Kind.VOLATILE);  }       
  "while"                                    { return symbol(Kind.WHILE);     }   
  
  /* separators */
  "("                                        { return symbol(Kind.LPAREN);    }
  ")"                                        { return symbol(Kind.RPAREN);    }
  "{"                                        { return symbol(Kind.LBRACE);    }
  "}"                                        { return symbol(Kind.RBRACE);    }
  "["                                        { return symbol(Kind.LBRACK);    }
  "]"                                        { return symbol(Kind.RBRACK);    }
  ";"                                        { return symbol(Kind.SEMICOLON); }
  ","                                        { return symbol(Kind.COMMA);     }
  "."                                        { return symbol(Kind.DOT);       }
                                            
  /* operators */                            
  "="                                        { return symbol(Kind.EQ); }
  ">"                                        { return symbol(Kind.GT); }
  "<"                                        { return symbol(Kind.LT); }
  "!"                                        { return symbol(Kind.NOT); }
  "~"                                        { return symbol(Kind.COMP); }
  "?"                                        { return symbol(Kind.QUESTION); }
  ":"                                        { return symbol(Kind.COLON); }
  "=="                                       { return symbol(Kind.EQEQ); }
  "<="                                       { return symbol(Kind.LTEQ); }
  ">="                                       { return symbol(Kind.GTEQ); }
  "!="                                       { return symbol(Kind.NOTEQ); }
  "&&"                                       { return symbol(Kind.ANDAND); }
  "||"                                       { return symbol(Kind.OROR); }
  "++"                                       { return symbol(Kind.PLUSPLUS); }
  "--"                                       { return symbol(Kind.MINUSMINUS); }
  "+"                                        { return symbol(Kind.PLUS); }
  "-"                                        { return symbol(Kind.MINUS); }
  "*"                                        { return symbol(Kind.MULT); }
  "/"                                        { return symbol(Kind.DIV); }
  "&"                                        { return symbol(Kind.AND); }
  "|"                                        { return symbol(Kind.OR); }
  "^"                                        { return symbol(Kind.XOR); }
  "%"                                        { return symbol(Kind.MOD); }
  "<<"                                       { return symbol(Kind.LSHIFT); }
  ">>"                                       { return symbol(Kind.RSHIFT); }
  ">>>"                                      { return symbol(Kind.URSHIFT); }
  "+="                                       { return symbol(Kind.PLUSEQ); }
  "-="                                       { return symbol(Kind.MINUSEQ); }
  "*="                                       { return symbol(Kind.MULTEQ); }
  "/="                                       { return symbol(Kind.DIVEQ); }
  "&="                                       { return symbol(Kind.ANDEQ); }
  "|="                                       { return symbol(Kind.OREQ); }
  "^="                                       { return symbol(Kind.XOREQ); }
  "%="                                       { return symbol(Kind.MODEQ); }
  "<<="                                      { return symbol(Kind.LSHIFTEQ); }
  ">>="                                      { return symbol(Kind.RSHIFTEQ); }
  
  /* string literal */
  \"                                         { yybegin(STRING); string.setLength(0); string.append("\""); }
                                            
  /* character literal */                   
  \'[^\']+\'                                 { return symbol(Kind.CHARLIT); }

  /* comments */
  "//" {InputCharacter}*                     { return symbol(Kind.COMMENT); }
  "/*"                                       { yybegin(COMMENT); string.setLength(0); string.append("/*"); }
  
  {Number}                                   { return symbol(Kind.NUMBER); }

  {Identifier}                               { return symbol(Kind.TEXT); }
  
  {LineTerminator}                           { return symbol(Kind.EOL);  }
}                                            
                                             
<STRING> {                                   
  \"                                         { yybegin(YYINITIAL); string.append("\""); return symbol(Kind.STRING, string.toString()); }
  [^\"\n\r]*                                 { string.append( yytext() ); }
  {LineTerminator}                           { String tmp = string.toString();
                                               string.setLength(0);
                                               return symbol(Kind.STRING_CONT, tmp); }
}                                            
                                             
                                             
<COMMENT> {                                  
  "*/"                                       { yybegin(YYINITIAL); string.append("*/"); return symbol(Kind.COMMENT, string.toString()); }
  [^*\r\n]*                                  { string.append( yytext() ); }
  "*"[^/]                                    { string.append( yytext() ); }
  {LineTerminator}                           { String tmp = string.toString();
                                               string.setLength(0);
                                               return symbol(Kind.COMMENT_CONT, tmp); }
}                                            
                                             
{InputCharacter}                             { return symbol(Kind.TEXT); }        
<<EOF>>                                      { return symbol(Kind.EOF);            }