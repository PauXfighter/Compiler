package compilador;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 Gestor centralitzat d'errors del compilador.
 Registra els errors (lèxics, sintàctics o semàntics) sense avortar el procés
 per continuar l'anàlisi i poder detectar tants errors com sigui possible.
 Permet generar el fitxer d'errors demanat a l'enunciat.
*/
public class GestorErrors {
    private final List<ErrorCompilacio> errors = new ArrayList<>();

    public void afegirError(int linia, int columna, TipusError tipus, String missatge) {
        errors.add(new ErrorCompilacio(linia, columna, tipus, missatge));
    }

    public void afegirErrorLexic(int linia, int columna, String missatge) {
        afegirError(linia, columna, TipusError.LEXIC, missatge);
    }

    public void afegirErrorSintactic(int linia, int columna, String missatge) {
        afegirError(linia, columna, TipusError.SINTACTIC, missatge);
    }

    public void afegirErrorSemantic(int linia, int columna, String missatge) {
        afegirError(linia, columna, TipusError.SEMANTIC, missatge);
    }

    public boolean teErrors() {
        return !errors.isEmpty();
    }

    public int numErrors() {
        return errors.size();
    }

    public List<ErrorCompilacio> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public void imprimirErrors() {
        for (ErrorCompilacio err : errors) {
            System.err.println(err);
        }
    }

    /**
     * Genera el document amb els errors detectats si n'hi ha.
     *
     * @param rutaFitxer Ruta del fitxer de sortida.
     * @throws IOException Si falla l'escriptura.
     */
    public void generarFitxerErrors(String rutaFitxer) throws IOException {
        if (!teErrors()) {
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(rutaFitxer), StandardCharsets.UTF_8))) {
            for (ErrorCompilacio err : errors) {
                writer.write(err.toString());
                writer.newLine();
            }
        }
    }

    public void netejar() {
        errors.clear();
    }
}
