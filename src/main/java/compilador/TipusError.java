package compilador;

/*
 Tipus d'errors contemplats pel compilador segons l'enunciat de la pràctica.
*/
public enum TipusError {
    LEXIC("Lèxic"),
    SINTACTIC("Sintàctic"),
    SEMANTIC("Semàntic");

    private final String nom;

    TipusError(String nom) {
        this.nom = nom;
    }

    @Override
    public String toString() {
        return nom;
    }
}
