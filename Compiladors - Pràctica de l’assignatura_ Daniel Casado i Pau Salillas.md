# Compiladors \- Pràctica de l’assignatura

## Autors: Daniel Casado i Pau Salillas

# Índex

[**Disseny del llenguatge	3**](#disseny-del-llenguatge)

[Tipus de dades	3](#tipus-de-dades)

[Detalls a afegir:	3](#detalls-dels-tipus:)

[Declaracions, constants i àmbits	3](#declaracions,-constants-i-àmbits)

[Expressions i operadors	4](#expressions-i-operadors)

[Instruccions i sentències	4](#instruccions-i-sentències)

[Subprogrames	4](#subprogrames)

[Funcions i mètodes predefinits	5](#funcions-i-mètodes-predefinits)

[Especificació lèxica	5](#especificació-lèxica)

[Exemple de Codi:	5](#exemple-de-codi:)

[**Anàlisi Lèxic	8**](#anàlisi-lèxic)

[**Anàlisi Sintàctic	9**](#anàlisi-sintàctic)

[**Anàlisi Semàntic	10**](#anàlisi-semàntic)

[**Generació de Codi Intermig	11**](#generació-de-codi-intermig)

[**Optimització	12**](#optimització)

[**Generació de Codi Assemblador	13**](#generació-de-codi-assemblador)

# **Disseny del llenguatge** {#disseny-del-llenguatge}

En el nostre llenguatge existeix un mètode principal anomenat def main(), que és el punt d’entrada del programa. Un subprograma pot cridar-ne d’altres, tant si estan definits més avall com a sí mateix.

## **Tipus de dades** {#tipus-de-dades}

| Tipus | Valors d’exemple | Notes |
| :---- | :---- | :---- |
| int | 42, 0 | Literals sense signe. |
| bool | true, false | Valors booleans bàsics. |
| char | 'A' | Caràcters individuals. |
| str | "Hello" | Tipus cadenes de text: opcional afegit |
| tipus\[\] | char\[\] | Llista d’elements d’un mateix tipus. Tipus definit per l’usuari. |
| tipus\[\]\[\] | int\[\]\[\] | Llista de llistes (o matriu). Tipus definit per l’usuari. |

### **Detalls dels tipus:** {#detalls-dels-tipus:}

- Els tamanys dels tipus primitius són: int 4 bytes, char 1 byte i bool 1 byte.  
- Les llistes són de mida dinàmica. Es creen amb un literal (\[1, 2, 3\]) o amb llista.append() i la manera d’accedir és l’usual, començant per 0 (llista\[i\]). Si es declara sense inicialitzar és una llista buida (p. ex. int\[\] l;). En quant a les matrius, com hem dit són llistes de llistes, i les files poden tenir longituds diferents.   
  Evidentment, accedir a una posició fora de rang produeix un error d’execució; els índexs negatius no estan permesos.  
  El literal buit \[\] únicament és vàlid quan el context determina el tipus (p. ex. int\[\] l \= \[\];). En qualsevol altre context, és un error semàntic.

## **Declaracions, constants i àmbits** {#declaracions,-constants-i-àmbits}

- **Declaració:** \[const\] tipus ID \[= expr\]; (tipatge explícit, els camps entre \[ \] són opcionals).  
- **Constants:** Una constant (const) ha de tenir obligatòriament un inicialitzador; reassignar-hi un valor posteriorment constitueix un error semàntic.  
- **Identificadors:** Lletra o ‘\_’, seguida de lletres, dígits o ‘\_’.  
- **Àmbits:** Cada bloc de codi delimitat per {} defineix el seu propi àmbit. No es permet fer ombrat d’una variable visible d’un bloc exterior, i els paràmetres d’un subprograma pertanyen al mateix àmbit que el cos de la funció, per tant, declarar dins del cos una variable amb el nom d’un paràmetre és un error semàntic.  
- **Reutilització de noms:** És vàlid declarar el mateix nom d’identificador en blocs germans o en un bloc intern un cop tancat l’àmbit anterior.

## **Expressions i operadors** {#expressions-i-operadors}

| Nivell | Operadors | Associativitat |
| :---- | :---- | :---- |
| 1 | \|\| | Esquerra |
| 2 | && | Esquerra |
| 3 | \==, \!= | No associatiu |
| 4 | \<, \>, \<=, \>= | No associatiu |
| 5 | \+, \- | Esquerra |
| 6 | \*, /, % | Esquerra |
| 7 | \!, \- (unaris) | Dreta |
| 8 | Indexació a\[i\], crida f(x), crida a mètode a.f(x), parèntesis () | Esquerra |

## **Instruccions i sentències** {#instruccions-i-sentències}

- **Declaració i assignació:** x \= expr;, x \+= expr;, x \-= expr;, llista\[2\] \= 10;, matriu\[1\]\[1\] \= 20; La declaració sense inicialitzar, tindrà valor per defecte (int → 0, bool → false, char → '\\0', str → "", lista → \[\]).  
- **Increments i decrements:** i++; i--;++i; \--i;  
- **Estructura condicional:** if cond { ... } else { ... } (sense parèntesis en la condició, claudàtors obligatoris i bloc else opcional).  
- **Bucle iteratiu:** while cond { ... }  
- **Bucle per elements:** for tipus ID in llista { ... } (la variable de control ha de ser del mateix tipus que els elements de la llista).  
- **Crida a subprograma:** helper(3, 4);  
- **Retorn:** return expr; en funcions (comprovant que es retorna el tipus que toca) i return; en procediments. A més, ha de retornar un valor en tots els camins d’execució. Si es pot arribar al final del cos de la funció sense return, serà un error semàntic.  
- **Sintaxi:** Totes les instruccions simples finalitzen amb punt i coma (;).

## **Subprogrames** {#subprogrames}

- **Procediments:** def nom(tipus p1, tipus p2) { ... }  
- **Funcions:** def tipus nom(tipus p1, tipus p2) { ... return expr; }  
- **Pas de paràmetres:** El pas de paràmetres es realitza sempre per valor en el cas de les variables primitives junt amb els str. En canvi, les llistes es passen per referència: la funció treballa sobre la mateixa llista que el cridador. De la mateixa manera, l’assignació entre llistes (b \= a) fa que les dues variables apuntin a la mateixa llista, no es crea una còpia.  
- **Ús en expressions:** Les funcions es poden avaluar directament en expressions (p. ex., int r \= helper(3, 4);).

## **Funcions i mètodes predefinits** {#funcions-i-mètodes-predefinits}

- print(e1, e2, ...): Imprimeix per pantalla cada expressió adaptada al seu tipus.  
- println(e1,e2,...): Imprimeix per pantalla afegint un salt de línia.  
- readInt(), readBool(), readChar(), readStr(): Expressions que llegeixen de l’entrada estàndard i retornen un valor del tipus corresponent.  
- llista.len(): Retorna el nombre d’elements d’una llista.  
- llista.append(x): Afegeix l’element x al final de la llista. Aquest element ha de ser del mateix tipus de la llista, i no retorna cap valor aquesta funció.

## **Especificació lèxica** {#especificació-lèxica}

- **Comentaris:** Comentaris d’una sola línia utilitzant //.  
- **Separadors:** Els espais, tabulacions i salts de línia s’ignoren (el salt de línia s’utilitza per comptar línies).  
- **Seqüències d’escapament** (dins de cadenes i caràcters): \\n, \\t, \\", \\', \\\\, \\0. Qualsevol altra seqüència és un error lèxic.  
- **Errors lèxics:** Caràcters il·legals (p. ex., @, \#,\$), cadenes sense tancar o literals de caràcter mal formats, a més de com hem dit abans seqüències d’escapament mal formades. L’analitzador anotarà els errors, però continuarà analitzant, per poder detectar la major quantitat d’errors en una sola passada. Un literal enter que supera el valor màxim representable (2.147.483.647) és un error lèxic.  
- **Paraules reservades**: def, if, else, while, for, in, return, const, int, bool, char, str, true, false, print, println, readInt, readBool, readChar, readStr, len, append.  
- **Posició:** Tots els tokens mantenen la informació de línia i columna per facilitar la gestió d’errors.

## 

## **Exemple de Codi:** {#exemple-de-codi:}

def main() {  
    // Declaracions i constants  
    int x \= 10 \- 3;  
    bool y \= true;  
    const char z \= 'A';  
    str text \= "Hola, món\!";  
    int\[\] llista \= \[1, 2, 3, 4, 5\];  
    int\[\]\[\] matriu \= \[\[1, 2\], \[3, 4\], \[5, 6\]\];

    // Condicional i entrada de dades  
    if x \> 5 {  
        print("Introdueix un enter: ");  
        int s \= readInt();  
        println("Has escrit: ", s);  
    } else {  
        println("x no és major que 5");  
    }

    // Bucle while amb curtcircuit  
    int s \= 0;  
    while y \== true \|\| s \== 40 {  
        println("Dins del bucle iteratiu");  
        s++;  
        if s \>= 3 {  
            y \= false;  
        }  
    }

    // Bucle for-in i operacions amb llistes  
    for int j in llista {  
        println("Element: ", j);  
    }  
    llista.append(6);  
    llista\[2\] \= 10;  
    matriu\[1\]\[1\] \= 20;  
    matriu.append(\[7, 8\]);  
    println("Mida de la llista: ", llista.len());  
    println("Mida de la primera fila: ", matriu\[0\].len());

    // Crides a subprogrames  
    int r \= helper(3, 4);  
    println("Resultat de helper: ", r);  
    println("Factorial de 5: ", factorial(5));  
    afegeixDoble(llista, 7);  
    println("Mida després d'afegir: ", llista.len());

    // Operadors addicionals  
    x \-= 1;  
    x--;  
    if r \!= 0 && \!y {  
        println("Residu: ", r % 3, " Quocient: ", r / 3);  
    }  
    println("Constant: ", z, " ", text);  
    println("Ell va dir \\"hola\\"\\tfi");  
}

def int helper(int a, int b) {  
    // Subprograma auxiliar  
    int sum \= a \+ b;  
    sum++;  
    sum \+= b;  
    sum \-= 1;  
    if sum \> 20 && sum \< 30 {  
        println("La suma és entre 20 i 30");  
    } else {  
        println("La suma no pertany a l'interval (20, 30)");  
    }  
    return sum;  
}

def int factorial(int n) {  
    if n \<= 1 {  
        return 1;  
    }  
    return n \* factorial(n \- 1);  
}

def afegeixDoble(int\[\] l, int v) {  
    // Les llistes es passen per referència  
    l.append(v);  
    l.append(v \* 2);  
}

## **Característiques opcionals incloses**

Hem afegit les següents característiques opcionals del llenguatge, extretes de l’enunciat:

- **Cadena de caràcters (str):** tipus primitiu amb literals entre cometes dobles.  
- **Operadors especials:** pre/post-increment (++) i pre/post-decrement (--), i les assignacions compostes \+= i \-=.  
- **println:** variant de print que afegeix un salt de línia al final.  
- **Llistes dinàmiques:** les llistes (tipus\[\]) tenen mida variable en temps d’execució, amb operacions llista.len() i llista.append(x). 

# 

# **Anàlisi Lèxic** {#anàlisi-lèxic}

| Tipus | Tokens | Patrons |
| :---- | :---- | :---- |
| Paraules reservades | DEF, IF, ELSE, WHILE, FOR, IN, RETURN, CONST, INT, BOOL, CHAR, STR, TRUE, FALSE, PRINT, PRINTLN, READINT, READBOOL, READCHAR, READSTR, LEN, APPEND | def, if, else, while, for, in, return, const, int, bool, char, str, true, false, print, println, readInt, readBool, readChar, readStr, len, append (tal com s’escriuen, distingeix majúscules) |
| Identificador | ID | lletra (lletra \| digit)\* |
| Literals | INT\_LIT CHAR\_LIT STR\_LIT | digit\+  'car\_caracter' "car\_cadena\*" |
| Aritmètics | PLUS MINUS TIMES DIV MOD | \+ \- \* / % |
| Relacionals | EQ NEQ LT GT LE GE | \== \!= \< \> \<= \>= |
| Lògics | AND OR NOT | && \|\| \! |
| Assignació | ASSIGN PLUSEQ MINUSEQ INC DEC | \= \+= \-= \++ \-- |
| Separadors | LPAREN RPAREN LBRACE RBRACE LBRACKET RBRACKET SEMI COMMA DOT | ( ) { } \[ \] ; , . |
| S’ignora espais | (cap token)  | \[ \\t\\r\\n\]\+  |
| S’ignora comentaris | (cap token) | "//"\[^\\r\\n\]\* |

## **Patrons Auxiliars:**

| Patró auxiliar | Expressió | Descripció |
| :---- | :---- | :---- |
| digit | \[0-9\] | Un dígit |
| lletra | \[A-Za-z\_\]  | Lletra o guió baix (sense dígits) |
| escapament | \\\\\[nt"'\\\\0\] | Barra invertida seguida de n, t, ", ', \\ o 0 |
| car\_cadena | \[^"\\\\\\n\\r\] \| escapament | Caràcter permès dins d’una cadena |
| car\_caracter | \[^'\\\\\\n\\r\] \| escapament | Caràcter permès dins d’un literal de caràcter |

## 

## **Taula d’errors lèxics:**

## 

| Error | Patró | Missatge |
| :---- | :---- | :---- |
| Caràcter illegal  | . (qualsevol altre caracter, incloent & o \| aïllats, @, \#, \$) | Caràcter ilegal  |
| Cadena sense tancar o amb escapament desconegut | “ \[^”\\r\\n\]\* “? | Cadena mal formada |
| Literal de caràcter mal format | ‘ \[^’\\r\\n\]\* ‘? | Literal de caràcter mal format |
| Enter fora de rang | digit+ amb valor superior a 2147483647 | Enter fora de rang |

## 

# **Anàlisi Sintàctic** {#anàlisi-sintàctic}

# 

# **Anàlisi Semàntic** {#anàlisi-semàntic}

# 

# **Generació de Codi Intermig** {#generació-de-codi-intermig}

# 

# **Optimització** {#optimització}

# 

# **Generació de Codi Assemblador** {#generació-de-codi-assemblador}

