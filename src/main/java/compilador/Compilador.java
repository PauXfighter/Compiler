package compilador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import java_cup.runtime.Symbol;

/*
 Classe principal del compilador.
 Processa el fitxer font subministrat per argument en UTF-8,
 executa l'anàlisi lèxic (generant .tokens.txt) i l'anàlisi sintàctic (CUP),
 i recull tots els errors detectats al fitxer .errors.txt.
 */
public class Compilador {

    public static void main(String[] args) {
        // Validació de paràmetres de línia de comandes
        if (args.length < 1) {
            System.err.println("Ús: java compilador.Compilador <fitxer_font>");
            return;
        }

        // Validació del fitxer d'entrada
        File fitxerEntrada = new File(args[0]);
        if (!fitxerEntrada.exists() || !fitxerEntrada.isFile()) {
            System.err.println("Error: El fitxer d'entrada '" + args[0] + "' no existeix o no és vàlid.");
            return;
        }

        // Determinam el nom base per als fitxers de sortida
        String nomBase = fitxerEntrada.getName();
        int puntIndex = nomBase.lastIndexOf('.');
        if (puntIndex > 0) {
            nomBase = nomBase.substring(0, puntIndex);
        }

        String fitxerSortidaTokens = nomBase + ".tokens.txt";
        String fitxerSortidaErrors = nomBase + ".errors.txt";

        GestorErrors gestorErrors = new GestorErrors();
        List<String> registreTokens = new ArrayList<>();

        try {
            // --- FASE 1: Anàlisi Lèxic ---
            try (BufferedReader readerLexic = new BufferedReader(
                    new InputStreamReader(new FileInputStream(fitxerEntrada), StandardCharsets.UTF_8))) {

                Lexer lexer = new Lexer(readerLexic, gestorErrors);

                System.out.println("=== Iniciant l'Anàlisi Lèxic de: " + fitxerEntrada.getName() + " ===\n");

                Symbol s;
                // Bucle d'extracció de tókens fins a arribar a fi de fitxer (EOF)
                while ((s = lexer.next_token()).sym != sym.EOF) {
                    String nomToken = sym.terminalNames[s.sym];
                    String liniaToken;
                    if (s.value != null) {
                        liniaToken = String.format("<%s, \"%s\"> [Línia: %d, Columna: %d]",
                                nomToken, s.value, s.left, s.right);
                    } else {
                        liniaToken = String.format("<%s> [Línia: %d, Columna: %d]",
                                nomToken, s.left, s.right);
                    }

                    registreTokens.add(liniaToken);
                    System.out.println(liniaToken);
                }

                System.out.println("\n=== Fi de l'Anàlisi Lèxic ===");

                // Generació del fitxer de tókens
                try (BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(fitxerSortidaTokens), StandardCharsets.UTF_8))) {
                    for (String linia : registreTokens) {
                        writer.write(linia);
                        writer.newLine();
                    }
                }
                System.out.println("Fitxer de tókens generat amb èxit: " + fitxerSortidaTokens);
            }

            // --- FASE 2: Anàlisi Sintàctic ---
            try (BufferedReader readerSintactic = new BufferedReader(
                    new InputStreamReader(new FileInputStream(fitxerEntrada), StandardCharsets.UTF_8))) {

                // Passem null com a gestor d'errors al Lexer per evitar duplicar errors lèxics
                Lexer lexerSintactic = new Lexer(readerSintactic, null);
                parser p = new parser(lexerSintactic, gestorErrors);

                System.out.println("\n=== Iniciant l'Anàlisi Sintàctic ===");
                p.parse();
                System.out.println("=== Fi de l'Anàlisi Sintàctic ===");
            }

            // --- Gestió i generació del fitxer d'errors ---
            if (gestorErrors.teErrors()) {
                System.err.println("\nS'han detectat " + gestorErrors.numErrors() + " error(s) de compilació:");
                gestorErrors.imprimirErrors();
                gestorErrors.generarFitxerErrors(fitxerSortidaErrors);
                System.err.println("Fitxer d'errors generat: " + fitxerSortidaErrors);
            } else {
                System.out.println("\nAnàlisi completat: no s'ha detectat cap error lèxic ni sintàctic.");
            }

        } catch (Exception e) {
            // Control total d'excepcions per evitar caigudes no controlades
            System.err.println("Error inesperat durant el processament: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}