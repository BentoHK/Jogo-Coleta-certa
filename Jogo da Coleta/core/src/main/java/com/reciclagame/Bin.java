package com.reciclagame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe que representa a lixeira controlada pelo jogador no jogo.
 * Gerencia a movimentação, mudança de tipos, efeitos especiais e renderização da lixeira.
 */
public class Bin {
    // Retângulo que define a área de colisão e posição da lixeira
    private Rectangle bounds;

    // Tipo atual de lixo que a lixeira aceita
    private TrashType currentType;

    // Velocidade de movimento da lixeira
    private float speed = 400f;

    // Mapeamento de texturas para cada tipo de lixeira
    private Map<TrashType, Texture> binTextures;

    // Textura atual sendo exibida
    private Texture currentTexture;

    // Constantes e variáveis para o efeito de teletransporte
    private static final float TELEPORT_DISTANCE = 300f;
    private boolean canTeleport = true;
    private float teleportCooldown = 0f;
    private float teleportEffectTime = 0f;

    // Variáveis para o efeito de frenesi (mudança rápida de tipos)
    private float frenzyEffectTimer = 0f;
    private boolean isFrenzyActive = false;
    private float frenzySwitchInterval = 0.1f;

    /**
     * Construtor da lixeira.
     * Inicializa a posição, tamanho e carrega as texturas para cada tipo.
     */
    public Bin() {
        bounds = new Rectangle();
        bounds.width = 180f;
        bounds.height = 210f;
        bounds.x = Gdx.graphics.getWidth() / 2 - bounds.width / 2;
        bounds.y = 20f;

        // Carrega as texturas para cada tipo de lixeira
        binTextures = new HashMap<>();
        binTextures.put(TrashType.PLASTICO, new Texture("bin_plastic.png"));
        binTextures.put(TrashType.METAL, new Texture("bin_metal.png"));
        binTextures.put(TrashType.PAPEL, new Texture("bin_paper.png"));
        binTextures.put(TrashType.ORGANICO, new Texture("bin_organic.png"));
        binTextures.put(TrashType.VIDRO, new Texture("bin_glass.png"));

        // Define o tipo inicial como plástico
        currentType = TrashType.PLASTICO;
        currentTexture = binTextures.get(currentType);
    }

    /**
     * Atualiza o estado da lixeira a cada frame.
     * @param delta Tempo desde o último frame
     */
    public void update(float delta) {
        // Movimentação horizontal com as teclas LEFT e RIGHT
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            bounds.x -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            bounds.x += speed * delta;
        }

        // Limita a posição para não sair da tela
        bounds.x = Math.max(0, Math.min(bounds.x, Gdx.graphics.getWidth() - bounds.width));

        // Lógica de teletransporte com a tecla SPACE
        if (canTeleport && Gdx.input.isKeyPressed(Keys.SPACE)) {
            if (Gdx.input.isKeyPressed(Keys.LEFT)) {
                teleport(bounds.x - TELEPORT_DISTANCE);
            } else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
                teleport(bounds.x + TELEPORT_DISTANCE);
            }
        }

        // Efeito de frenesi - muda rapidamente entre tipos de lixeira
        if (isFrenzyActive) {
            frenzyEffectTimer += delta;
            if (frenzyEffectTimer >= frenzySwitchInterval) {
                frenzyEffectTimer = 0;
                TrashType[] types = TrashType.values();
                TrashType randomType = types[MathUtils.random(types.length - 2)];
                currentTexture = binTextures.get(randomType);
            }
        }
        // Volta para o tipo normal quando nenhuma tecla de mudança está pressionada
        else if (!Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.S) &&
            !Gdx.input.isKeyPressed(Keys.D) && !Gdx.input.isKeyPressed(Keys.F) &&
            !Gdx.input.isKeyPressed(Keys.G)) {
            currentTexture = binTextures.get(currentType);
        }

        // Muda o tipo da lixeira conforme teclas pressionadas
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

        // Controle do cooldown do teletransporte
        if (!canTeleport) {
            teleportCooldown -= delta;
            if (teleportCooldown <= 0) canTeleport = true;
        }
    }

    /**
     * Ativa/desativa o efeito de frenesi.
     * @param active true para ativar, false para desativar
     */
    public void setFrenzyActive(boolean active) {
        this.isFrenzyActive = active;
        if (!active) {
            currentTexture = binTextures.get(currentType);
        }
    }

    /**
     * Realiza o teletransporte da lixeira para uma nova posição.
     * @param newX Nova posição horizontal
     */
    private void teleport(float newX) {
        bounds.x = Math.max(0, Math.min(newX, Gdx.graphics.getWidth() - bounds.width));
        teleportEffectTime = 0.15f;
        activateCooldown();
        SoundManager.teleport.play(0.5f);
    }

    /**
     * Ativa o cooldown do teletransporte.
     */
    private void activateCooldown() {
        canTeleport = false;
        teleportCooldown = 1f;
    }

    /**
     * Desenha a lixeira na tela.
     * @param batch SpriteBatch usado para renderização
     */
    public void draw(SpriteBatch batch) {
        // Efeito visual durante o teletransporte (transparência)
        if (teleportEffectTime > 0) {
            batch.setColor(1, 1, 1, 0.7f);
            teleportEffectTime -= Gdx.graphics.getDeltaTime();
        }

        batch.draw(currentTexture, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.setColor(1, 1, 1, 1); // Restaura a cor normal
    }

    /**
     * Libera os recursos da lixeira.
     */
    public void dispose() {
        for (Texture texture : binTextures.values()) {
            texture.dispose();
        }
    }

    // Getters
    public Rectangle getBounds() { return bounds; }
    public TrashType getCurrentType() { return currentType; }
}
