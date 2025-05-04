package com.reciclagame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import java.util.HashMap;
import java.util.Map;

public class Bin {
    private Rectangle bounds;
    private TrashType currentType;
    private float speed = 400f;
    private Map<TrashType, Texture> binTextures;
    private Texture currentTexture;

    private static final float TELEPORT_DISTANCE = 300f;
    private boolean canTeleport = true;
    private float teleportCooldown = 0f;
    private float teleportEffectTime = 0f;

    private float frenzyEffectTimer = 0f;
    private boolean isFrenzyActive = false;
    private float frenzySwitchInterval = 0.1f;

    public Bin() {
        bounds = new Rectangle();
        bounds.width = 180f;
        bounds.height = 210f;
        bounds.x = Gdx.graphics.getWidth() / 2 - bounds.width / 2;
        bounds.y = 20f;

        binTextures = new HashMap<>();
        binTextures.put(TrashType.PLASTICO, new Texture("bin_plastic.png"));
        binTextures.put(TrashType.METAL, new Texture("bin_metal.png"));
        binTextures.put(TrashType.PAPEL, new Texture("bin_paper.png"));
        binTextures.put(TrashType.ORGANICO, new Texture("bin_organic.png"));
        binTextures.put(TrashType.VIDRO, new Texture("bin_glass.png"));

        currentType = TrashType.PLASTICO;
        currentTexture = binTextures.get(currentType);
    }

    public void update(float delta) {
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            bounds.x -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            bounds.x += speed * delta;
        }

        bounds.x = Math.max(0, Math.min(bounds.x, Gdx.graphics.getWidth() - bounds.width));

        if (canTeleport && Gdx.input.isKeyPressed(Keys.SPACE)) {
            if (Gdx.input.isKeyPressed(Keys.LEFT)) {
                teleport(bounds.x - TELEPORT_DISTANCE);
            } else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
                teleport(bounds.x + TELEPORT_DISTANCE);
            }
        }

        if (isFrenzyActive) {
            frenzyEffectTimer += delta;
            if (frenzyEffectTimer >= frenzySwitchInterval) {
                frenzyEffectTimer = 0;
                TrashType[] types = TrashType.values();
                TrashType randomType = types[MathUtils.random(types.length - 2)];
                currentTexture = binTextures.get(randomType);
            }
        } else if (!Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.S) &&
            !Gdx.input.isKeyPressed(Keys.D) && !Gdx.input.isKeyPressed(Keys.F) &&
            !Gdx.input.isKeyPressed(Keys.G)) {
            currentTexture = binTextures.get(currentType);
        }

        if (Gdx.input.isKeyJustPressed(Keys.A)) {
            currentType = TrashType.PLASTICO;
            currentTexture = binTextures.get(currentType);
            SoundManager.binChanged.play(0.5f);
        } else if (Gdx.input.isKeyJustPressed(Keys.S)) {
            currentType = TrashType.METAL;
            currentTexture = binTextures.get(currentType);
            SoundManager.binChanged.play(0.5f);
        } else if (Gdx.input.isKeyJustPressed(Keys.D)) {
            currentType = TrashType.PAPEL;
            currentTexture = binTextures.get(currentType);
            SoundManager.binChanged.play(0.5f);
        } else if (Gdx.input.isKeyJustPressed(Keys.F)) {
            currentType = TrashType.ORGANICO;
            currentTexture = binTextures.get(currentType);
            SoundManager.binChanged.play(0.5f);
        } else if (Gdx.input.isKeyJustPressed(Keys.G)) {
            currentType = TrashType.VIDRO;
            currentTexture = binTextures.get(currentType);
            SoundManager.binChanged.play(0.5f);
        }

        if (!canTeleport) {
            teleportCooldown -= delta;
            if (teleportCooldown <= 0) canTeleport = true;
        }
    }

    public void setFrenzyActive(boolean active) {
        this.isFrenzyActive = active;
        if (!active) {
            currentTexture = binTextures.get(currentType);
        }
    }

    private void teleport(float newX) {
        bounds.x = Math.max(0, Math.min(newX, Gdx.graphics.getWidth() - bounds.width));
        teleportEffectTime = 0.15f;
        activateCooldown();
        SoundManager.teleport.play(0.5f);
    }

    private void activateCooldown() {
        canTeleport = false;
        teleportCooldown = 1f;
    }

    public void draw(SpriteBatch batch) {
        if (teleportEffectTime > 0) {
            batch.setColor(1, 1, 1, 0.7f);
            teleportEffectTime -= Gdx.graphics.getDeltaTime();
        }

        batch.draw(currentTexture, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.setColor(1, 1, 1, 1);
    }

    public void dispose() {
        for (Texture texture : binTextures.values()) {
            texture.dispose();
        }
    }

    public Rectangle getBounds() { return bounds; }
    public TrashType getCurrentType() { return currentType; }
}
