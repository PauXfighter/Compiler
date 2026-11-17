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

/**
 * Classe principal del compilador.
 * Processa el fitxer font subministrat per argument en UTF-8,
 * extreu els tókens mitjançant el Lexer, i genera els fitxers de sortida
 * de tókens i d'errors requerits per l'enunciat de la pràctica.
 */
public class Compilador {

    public static void main(String[] args) {
        // 1. Validació de paràmetres de línia de comandes
        if (args.length < 1) {
            System.err.println("Ús: java compilador.Compilador <fitxer_font>");
            return;
        }

        File fitxerEntrada = new File(args[0]);
        if (!fitxerEntrada.exists() || !fitxerEntrada.isFile()) {
            System.err.println("Error: El fitxer d'entrada '" + args[0] + "' no existeix o no és vàlid.");
            return;
        }

        // Determinam el nom base per als fitxers de sortida (ex: programa.tokens.txt)
        String nomBase = fitxerEntrada.getName();
        int puntIndex = nomBase.lastIndexOf('.');
        if (puntIndex > 0) {
            nomBase = nomBase.substring(0, puntIndex);
        }

        String fitxerSortidaTokens = nomBase + ".tokens.txt";
        String fitxerSortidaErrors = nomBase + ".errors.txt";

        GestorErrors gestorErrors = new GestorErrors();
        List<String> registreTokens = new ArrayList<>();

        // 2. Lectura en UTF-8 i execució de l'anàlisi lèxic
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(fitxerEntrada), StandardCharsets.UTF_8))) {

            Lexer lexer = new Lexer(reader, gestorErrors);

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

            // 3. Generació del fitxer de tókens (en UTF-8)
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(fitxerSortidaTokens), StandardCharsets.UTF_8))) {
                for (String linia : registreTokens) {
                    writer.write(linia);
                    writer.newLine();
                }
            }
            System.out.println("Fitxer de tókens generat amb èxit: " + fitxerSortidaTokens);

            // 4. Gestió i generació del fitxer d'errors
            if (gestorErrors.teErrors()) {
                System.err.println("\nS'han detectat " + gestorErrors.numErrors() + " error(s) lèxic(s):");
                gestorErrors.imprimirErrors();
                gestorErrors.generarFitxerErrors(fitxerSortidaErrors);
                System.err.println("Fitxer d'errors generat: " + fitxerSortidaErrors);
            } else {
                System.out.println("No s'ha detectat cap error lèxic.");
            }

        } catch (Exception e) {
            // Control total d'excepcions per evitar caigudes no controlades
            System.err.println("Error inesperat durant el processament: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}