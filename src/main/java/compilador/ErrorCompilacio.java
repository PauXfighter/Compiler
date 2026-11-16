package compilador;

/**
 * Representa un error detectat durant alguna de les fases de la compilació.
 * Guarda la línia, la columna, el tipus d'error i el missatge explicatiu.
 */
public class ErrorCompilacio {
    private final int linia;
    private final int columna;
    private final TipusError tipus;
    private final String missatge;

    public ErrorCompilacio(int linia, int columna, TipusError tipus, String missatge) {
        this.linia = linia;
        this.columna = columna;
        this.tipus = tipus;
        this.missatge = missatge;
    }

    public int getLinia() {
        return linia;
    }

    public int getColumna() {
        return columna;
    }

    public TipusError getTipus() {
        return tipus;
    }

    public String getMissatge() {
        return missatge;
    }

    @Override
    public String toString() {
        return String.format("[Línia %d, Columna %d] Error %s: %s", linia, columna, tipus, missatge);
    }
}
