package com.reciclagame;

import com.badlogic.gdx.graphics.Color;

public enum TrashType {
    PLASTICO(1, Color.RED, "Plástico"),
    METAL(2, Color.YELLOW, "Metal"),
    PAPEL(3, Color.BLUE, "Papel"),
    ORGANICO(4, new Color(0.65f, 0.16f, 0.16f, 1), "Orgânico"),
    VIDRO(5, Color.GREEN, "Vidro"),
    VIDA(6, new Color(1, 0.75f, 0.79f, 1), "Vida");

    private final int key;
    private final Color color;
    private final String name;

    TrashType(int key, Color color, String name) {
        this.key = key;
        this.color = color;
        this.name = name;
    }

    public int getKey() { return key; }
    public Color getColor() { return color; }
    public String getName() { return name; }

    public static TrashType getByKey(int key) {
        for (TrashType type : values()) {
            if (type.getKey() == key) return type;
        }
        return PLASTICO;
    }
}
