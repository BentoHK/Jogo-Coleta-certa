package com.reciclagame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import java.util.HashMap;
import java.util.Map;

public class Trash {
    private Rectangle bounds;
    private TrashType type;
    private float speed;
    private Texture texture;
    private static Map<TrashType, Texture> trashTextures;
    private float rotation;
    private float rotationSpeed;

    public Trash(float x, float width, float speed) {
        bounds = new Rectangle();
        bounds.width = width;
        bounds.height = width;
        bounds.x = x;
        bounds.y = 720;

        if (MathUtils.random(1, 10) == 1) {
            type = TrashType.VIDA;
        } else {
            TrashType[] types = TrashType.values();
            type = types[MathUtils.random(types.length - 2)];
        }

        if (trashTextures == null) {
            loadTextures();
        }

        this.speed = speed;
        this.texture = trashTextures.get(type);
        this.rotation = 0;
        this.rotationSpeed = (type != TrashType.VIDA) ? MathUtils.random(50f, 150f) : 0;
    }

    private void loadTextures() {
        trashTextures = new HashMap<>();
        trashTextures.put(TrashType.PLASTICO, new Texture("trash_plastic.png"));
        trashTextures.put(TrashType.METAL, new Texture("trash_metal.png"));
        trashTextures.put(TrashType.PAPEL, new Texture("trash_paper.png"));
        trashTextures.put(TrashType.ORGANICO, new Texture("trash_organic.png"));
        trashTextures.put(TrashType.VIDRO, new Texture("trash_glass.png"));
        trashTextures.put(TrashType.VIDA, new Texture("life_item.png"));
    }

    public void update(float delta) {
        bounds.y -= speed * delta;
        rotation += rotationSpeed * delta;
        if (rotation > 360) rotation -= 360;
    }

    public void draw(SpriteBatch batch) {
        if (type == TrashType.VIDA) {
            batch.draw(texture,
                bounds.x, bounds.y,
                bounds.width/2, bounds.height/2,
                bounds.width, bounds.height,
                1.5f, 1.5f,
                0,
                0, 0,
                texture.getWidth(), texture.getHeight(),
                false, false);
        } else {
            batch.draw(texture,
                bounds.x, bounds.y,
                bounds.width/2, bounds.height/2,
                bounds.width, bounds.height,
                1.5f, 1.5f,
                rotation,
                0, 0,
                texture.getWidth(), texture.getHeight(),
                false, false);
        }
    }

    public Rectangle getBounds() { return bounds; }
    public TrashType getType() { return type; }
    public boolean isOutOfScreen() { return bounds.y + bounds.height < 0; }
    public void setSpeed(float newSpeed) { this.speed = newSpeed; }

    public static void disposeTextures() {
        if (trashTextures != null) {
            for (Texture texture : trashTextures.values()) {
                texture.dispose();
            }
        }
    }
}
