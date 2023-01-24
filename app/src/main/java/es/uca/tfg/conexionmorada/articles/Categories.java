package es.uca.tfg.conexionmorada.articles;

public enum Categories {
    VIOLENCIA_DE_GENERO("Violencia de genero"),
    VIOLENCIA_SEXUAL("Violencia sexual"),
    IGUALDAD("igualdad");

    private String name;

    Categories(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
