package com.example.aluracursos.literalura.model;

public enum Tema {
    ACCION("Action"),
    ROMANCE("Romance"),
    COMEDIA("Comedy"),
    DRAMA("Drama"),
    CRIMEN("Crime"),
    FICCION_GOTICA("Gothic fiction"),
    CUENTOS_DE_HORROR("Horror tales"),
    MONSTRUOS("Monsters -- Fiction"),
    CIENCIA_FICCION("Science fiction"),
    CIENTIFICOS("Scientists -- Fiction"),

    DEFAULT("Unknown");  // Valor predeterminado

    private String temaOmdb;

    Tema(String categoriaOmdb) {
        this.temaOmdb = categoriaOmdb;
    }

    public static Tema fromString(String text) {
        if (text == null) {
            return Tema.DEFAULT;
        }

        // Limpieza del texto: eliminar paréntesis, comas y cualquier información adicional.
        String cleanedText = text.split("--")[0].trim();  // Tomar solo la primera parte antes de "--"

        // Depurar: Imprimir el texto que estamos procesando.
        System.out.println("Procesando texto de tema: " + cleanedText);

        for (Tema categoria : Tema.values()) {
            // Depurar: Verificar si coincide con el valor del enum
            System.out.println("Comparando con: " + categoria.temaOmdb);
            if (categoria.temaOmdb.equalsIgnoreCase(cleanedText)) {
                return categoria;
            }
        }
        return Tema.DEFAULT;  // Si no hay coincidencia, devolver el valor por defecto
    }

    public String getCategoriaOmdb() {
        return temaOmdb;
    }
}
