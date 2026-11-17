package compilador;

import java_cup.runtime.Symbol;
import java.io.Reader;

%%

// --- Directives de JFlex ---
%public
%class Lexer
%cup
%unicode
%line
%column

/*
A JFlex, un estat exclusiu aïlla l'anàlisi, ja que quan entram en ell, per exemple,
si trobem " o ', només s'evaluaran les regles específiques d'aquell estat. 
Així evitem colisions amb paraules reservades, operadors o identificadors del llenguatge
*/

%xstate CADENA
%xstate CARACTER

// Codi Java embegut, fa possible usar el gestor d'errors i crear symbols amb atributs
%{
    private GestorErrors gestorErrors;
    // Buffer i posicions per construir literals de cadena i caràcter
    private StringBuilder cadenaBuffer = new StringBuilder();
    // Guardem la posició exacta on es va obrir la cadena, per si la cadena no es tanca o està mal formada, 
    // perquè l'error indiqui la línea i columna on s'ha obert
    private int iniciLinia; 
    private int iniciColumna;
    // Constructor que rep el Reader i el Gestor d'Errors
    public Lexer(Reader in, GestorErrors gestor) {
        this(in);
        this.gestorErrors = gestor;
    }

    // Mètode auxiliar per a tokens sense atribut
    private Symbol symbol(int type) {
        return new Symbol(type, yyline + 1, yycolumn + 1);
    }

    // Mètode auxiliar per a tokens amb valor semàntic
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }
%}

// Patrons auxiliars 
digit        = [0-9]
lletra       = [A-Za-z_]
id           = {lletra}({lletra}|{digit})*
enter        = {digit}+
espais       = [ \t\r\n]+
comentari    = "//"[^\r\n]*
car_caracter = [^'\\\n\r] | escapament
car_cadena   = [^"\\""\n""\r"] | escapament
%%

// --- Espais i comentaris ---
{espais}    { /* Ignorar espais en blanc */ }
{comentari} { /* Ignorar comentaris d'una sola línia */ }

// --- Paraules reservades ---

// Han d'anar abans d'{id} per guanyar en cas d'empat
"def"      { return symbol(sym.DEF); }
"if"       { return symbol(sym.IF); }
"else"     { return symbol(sym.ELSE); }
"while"    { return symbol(sym.WHILE); }
"for"      { return symbol(sym.FOR); }
"in"       { return symbol(sym.IN); }
"return"   { return symbol(sym.RETURN); }
"const"    { return symbol(sym.CONST); }
"int"      { return symbol(sym.INT); }
"bool"     { return symbol(sym.BOOL); }
"char"     { return symbol(sym.CHAR); }
"str"      { return symbol(sym.STR); }
"true"     { return symbol(sym.TRUE); }
"false"    { return symbol(sym.FALSE); }
"print"    { return symbol(sym.PRINT); }
"println"  { return symbol(sym.PRINTLN); }
"readInt"  { return symbol(sym.READINT); }
"readBool" { return symbol(sym.READBOOL); }
"readChar" { return symbol(sym.READCHAR); }
"readStr"  { return symbol(sym.READSTR); }
"len"      { return symbol(sym.LEN); }
"append"   { return symbol(sym.APPEND); }
// --- Identificadors ---
{id}       { return symbol(sym.ID, yytext()); }   

// --- Operadors aritmètics ---
"+"        { return symbol(sym.PLUS); }
"-"        { return symbol(sym.MINUS); }
"*"        { return symbol(sym.TIMES); }
"/"        { return symbol(sym.DIV); }
"%"        { return symbol(sym.MOD); }

// --- Operadors relacionals ---
"=="       { return symbol(sym.EQ); }
"!="       { return symbol(sym.NEQ); }
"<="       { return symbol(sym.LE); }
">="       { return symbol(sym.GE); }
"<"        { return symbol(sym.LT); }
">"        { return symbol(sym.GT); }

// --- Operadors lògics ---
"&&"       { return symbol(sym.AND); }
"||"       { return symbol(sym.OR); }
"!"        { return symbol(sym.NOT); }

// --- Operadors d'assignació i variació ---
"+="       { return symbol(sym.PLUSEQ); }
"-="       { return symbol(sym.MINUSEQ); }
"++"       { return symbol(sym.INC); }
"--"       { return symbol(sym.DEC); }
"="        { return symbol(sym.ASSIGN); }

// --- Separadors i delimitadors ---
"("        { return symbol(sym.LPAREN); }
")"        { return symbol(sym.RPAREN); }
"{"        { return symbol(sym.LBRACE); }
"}"        { return symbol(sym.RBRACE); }
"["        { return symbol(sym.LBRACKET); }
"]"        { return symbol(sym.RBRACKET); }
";"        { return symbol(sym.SEMI); }
","        { return symbol(sym.COMMA); }
"."        { return symbol(sym.DOT); } 

// --- Literals enters (INT_LIT) ---
{enter} {
    try {
        long valor = Long.parseLong(yytext());
        if (valor > 2147483647L) {
            if (gestorErrors != null) {
                gestorErrors.afegirErrorLexic(yyline + 1, yycolumn + 1, "Enter fora de rang: " + yytext());
            }
            return symbol(sym.INT_LIT, Integer.MAX_VALUE);
        }
        // Si té el format adient, retornam el valor
        return symbol(sym.INT_LIT, (int) valor);
    } catch (NumberFormatException e) {
        // En cas que superi fins i tot el límit de Long (molts dígits)
        if (gestorErrors != null) {
            gestorErrors.afegirErrorLexic(yyline + 1, yycolumn + 1, "Enter fora de rang: " + yytext());
        }
        return symbol(sym.INT_LIT, Integer.MAX_VALUE);
    }
}

// --- Obertura de literals de cadena i caràcter ---
\" {
    cadenaBuffer.setLength(0);
    iniciLinia = yyline + 1;
    iniciColumna = yycolumn + 1;
    yybegin(CADENA);
}

\' {
    cadenaBuffer.setLength(0);
    iniciLinia = yyline + 1;
    iniciColumna = yycolumn + 1;
    yybegin(CARACTER);
}

// --- Estat CADENA (STR_LIT) ---
<CADENA> {
    \" {
        yybegin(YYINITIAL);
        return symbol(sym.STR_LIT, cadenaBuffer.toString());
    }
    "\\n"  { cadenaBuffer.append('\n'); }
    "\\t"  { cadenaBuffer.append('\t'); }
    "\\\"" { cadenaBuffer.append('\"'); }
    "\\'"  { cadenaBuffer.append('\''); }
    "\\\\" { cadenaBuffer.append('\\'); }
    "\\0"  { cadenaBuffer.append('\0'); }
    \\.?   {
        if (gestorErrors != null) {
            gestorErrors.afegirErrorLexic(iniciLinia, iniciColumna, "Cadena mal formada (escapament no permès: " + yytext() + ")");
        }
        cadenaBuffer.append(yytext());
    }
    \r|\n|\r\n {
        if (gestorErrors != null) {
            gestorErrors.afegirErrorLexic(iniciLinia, iniciColumna, "Cadena mal formada (sense tancar abans del salt de línia)");
        }
        yybegin(YYINITIAL);
        return symbol(sym.STR_LIT, cadenaBuffer.toString());
    }
    <<EOF>> {
        if (gestorErrors != null) {
            gestorErrors.afegirErrorLexic(iniciLinia, iniciColumna, "Cadena mal formada (sense tancar abans de fi de fitxer)");
        }
        yybegin(YYINITIAL);
        return symbol(sym.STR_LIT, cadenaBuffer.toString());
    }
    [^\"\\\r\n]+ {
        cadenaBuffer.append(yytext());
    }
}

// --- Estat CARACTER (CHAR_LIT) ---
<CARACTER> {
    \' {
        yybegin(YYINITIAL);
        if (cadenaBuffer.length() == 1) {
            return symbol(sym.CHAR_LIT, cadenaBuffer.charAt(0));
        } else {
            if (gestorErrors != null) {
                gestorErrors.afegirErrorLexic(iniciLinia, iniciColumna, "Literal de caràcter mal format");
            }
            char c = cadenaBuffer.length() > 0 ? cadenaBuffer.charAt(0) : '\0';
            return symbol(sym.CHAR_LIT, c);
        }
    }
    "\\n"  { cadenaBuffer.append('\n'); }
    "\\t"  { cadenaBuffer.append('\t'); }
    "\\\"" { cadenaBuffer.append('\"'); }
    "\\'"  { cadenaBuffer.append('\''); }
    "\\\\" { cadenaBuffer.append('\\'); }
    "\\0"  { cadenaBuffer.append('\0'); }
    \\.? {
        if (gestorErrors != null) {
            gestorErrors.afegirErrorLexic(iniciLinia, iniciColumna, "Literal de caràcter mal format (escapament no permès: " + yytext() + ")");
        }
        cadenaBuffer.append(yytext());
    }
    \r|\n|\r\n {
        if (gestorErrors != null) {
            gestorErrors.afegirErrorLexic(iniciLinia, iniciColumna, "Literal de caràcter mal format (sense tancar abans del salt de línia)");
        }
        yybegin(YYINITIAL);
        char c = cadenaBuffer.length() > 0 ? cadenaBuffer.charAt(0) : '\0';
        return symbol(sym.CHAR_LIT, c);
    }
    <<EOF>> {
        if (gestorErrors != null) {
            gestorErrors.afegirErrorLexic(iniciLinia, iniciColumna, "Literal de caràcter mal format (sense tancar abans de fi de fitxer)");
        }
        yybegin(YYINITIAL);
        char c = cadenaBuffer.length() > 0 ? cadenaBuffer.charAt(0) : '\0';
        return symbol(sym.CHAR_LIT, c);
    }
    [^\'\\\r\n]+ {
        cadenaBuffer.append(yytext());
    }
}

// Qualsevol altre caràcter no reconegut serà un error per caràcter il·legal
[^] {
    if (gestorErrors != null) {
        gestorErrors.afegirErrorLexic(yyline + 1, yycolumn + 1, "Caràcter ilegal: " + yytext());
    }
    // No retornem cap token perquè l'escàner continuï processant el següent caràcter
    }