package com.reciclagame;

import com.badlogic.gdx.graphics.Color;

public enum TrashType {
    // Tipos de lixo com suas propriedades:
    PLASTICO(1, Color.RED, "Plástico"),       // Tipo 1 - Plástico (Vermelho)
    METAL(2, Color.YELLOW, "Metal"),          // Tipo 2 - Metal (Amarelo)
    PAPEL(3, Color.BLUE, "Papel"),            // Tipo 3 - Papel (Azul)
    ORGANICO(4, new Color(0.65f, 0.16f, 0.16f, 1), "Orgânico"), // Tipo 4 - Orgânico (Marrom)
    VIDRO(5, Color.GREEN, "Vidro"),           // Tipo 5 - Vidro (Verde)
    VIDA(6, new Color(1, 0.75f, 0.79f, 1), "Vida"); // Tipo 6 - Item de vida (Rosa)

    // Propriedades de cada tipo:
    private final int key;      // Identificador numérico
    private final Color color;  // Cor associada ao tipo
    private final String name;  // Nome amigável para exibição

    // Construtor do enum
    TrashType(int key, Color color, String name) {
        this.key = key;
        this.color = color;
        this.name = name;
    }

    // Métodos de acesso:
    public int getKey() { return key; }       // Retorna o identificador
    public Color getColor() { return color; } // Retorna a cor
    public String getName() { return name; }  // Retorna o nome

    // Método estático para obter um tipo pelo identificador
    public static TrashType getByKey(int key) {
        // Percorre todos os valores do enum
        for (TrashType type : values()) {
            if (type.getKey() == key) return type; // Retorna se encontrar
        }
        return PLASTICO; // Retorna plástico como padrão se não encontrar
    }
}
