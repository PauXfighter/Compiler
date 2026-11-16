# Declaració d'Ús d'Intel·ligència Artificial Generativa

Aquest document recull de manera breu les intervencions d'eines d'IA generativa en el desenvolupament del compilador, tal com requereix l'enunciat de l'assignatura Compiladors (21780, curs 2026-2027).

---

## Sessió 1: Verificació de l'entorn, revisió del projecte i Pas 2.5
- **Data:** 15/11/2026
- **Tasca realitzada:**
  - Detecció del sistema operatiu (Windows) i comprovació de l'entorn (JDK i Maven).
  - Configuració de l'entorn de treball: resolució de concurrència de versions de Java prioritzant Temurin JDK 25 LTS, instal·lació d'Apache Maven 3.9.9 i creació de `.gitignore`.
  - Verificació i acarament de la llista de 54 terminals i tipus associats de `Parser.cup` respecte a la taula de tokens del document de disseny.
  - Execució i validació completa del Pas 2.5 (`mvn generate-sources`) per comprovar la generació de codi amb CUP i JFlex.
- **Abast de la intervenció de la IA:**
  - Suport en el diagnòstic i configuració d'eines externes (Maven, Java, gitignore).
  - Validació de concordança 1:1 dels tokens de la gramàtica amb l'especificació lèxica.
  - Diagnòstic de la necessitat de declarar `package compilador;` a `Parser.cup` per al funcionament de `cup-maven-plugin`.
  - Diagnòstic de l'estructura mínima requerida per JFlex a la secció de regles de `Lexer.flex`.
  - Verificació satisfactòria de la generació de fonts amb `BUILD SUCCESS` (CUP s'executa primer i genera `sym.java` i `parser.java`; tot seguit JFlex genera `Lexer.java`).

---

## Sessió 2: Gestor d'errors del compilador (Pas 3.1)
- **Data:** 15/11/2026
- **Tasca realitzada:**
  - Creació de les classes de suport per a la gestió centralitzada d'errors del compilador: `TipusError.java`, `ErrorCompilacio.java` i `GestorErrors.java` a `src/main/java/compilador/`.
- **Abast de la intervenció de la IA:**
  - Estructuració del model d'errors segons l'enunciat (línia, columna, tipus d'error i missatge explicatiu) i suport a l'exportació en fitxer UTF-8. No interromp l'anàlisi per continuar la detecció.
