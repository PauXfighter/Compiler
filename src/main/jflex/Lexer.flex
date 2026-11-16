package compilador;

import java.io.*;

import java_cup.runtime.*;
import java_cup.runtime.ComplexSymbolFactory.ComplexSymbol;

import compiler.sintactic.ParserSym;

%%

%cup

%public
%class Lexer
%unicode

%line
%column
%char

%eofval{
  return symbol(ParserSym.EOF);
%eofval}

%%

[^] { }