package com.reciclagame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import java.util.HashMap;
import java.util.Map;

public class Trash {
    // Área de colisão e posicionamento do lixo
    private Rectangle bounds;

    // Tipo do lixo (enum TrashType)
    private TrashType type;

    // Velocidade de queda
    private float speed;

    // Textura atual do lixo
    private Texture texture;

    // Mapa estático que armazena todas as texturas (compartilhado entre todas as instâncias)
    private static Map<TrashType, Texture> trashTextures;

    // Rotação atual (para efeito visual)
    private float rotation;

    // Velocidade de rotação
    private float rotationSpeed;

    // Construtor - cria um novo lixo na posição especificada
    public Trash(float x, float width, float speed) {
        bounds = new Rectangle();
        bounds.width = width;  // Largura do lixo
        bounds.height = width; // Altura igual à largura (quadrado)
        bounds.x = x;         // Posição X inicial
        bounds.y = 720;       // Posição Y inicial (topo da tela)

        // 10% de chance de ser um item de vida, senão um tipo de lixo aleatório
        if (MathUtils.random(1, 10) == 1) {
            type = TrashType.VIDA;
        } else {
            TrashType[] types = TrashType.values();
            type = types[MathUtils.random(types.length - 2)]; // Exclui VIDA do sorteio
        }

        // Carrega as texturas se for a primeira instância
        if (trashTextures == null) {
            loadTextures();
        }

        this.speed = speed;
        this.texture = trashTextures.get(type);
        this.rotation = 0;
        // Itens de vida não rotacionam, outros têm rotação aleatória
        this.rotationSpeed = (type != TrashType.VIDA) ? MathUtils.random(50f, 150f) : 0;
    }

    // Carrega todas as texturas dos lixos (executado apenas uma vez)
    private void loadTextures() {
        trashTextures = new HashMap<>();
        trashTextures.put(TrashType.PLASTICO, new Texture("trash_plastic.png"));
        trashTextures.put(TrashType.METAL, new Texture("trash_metal.png"));
        trashTextures.put(TrashType.PAPEL, new Texture("trash_paper.png"));
        trashTextures.put(TrashType.ORGANICO, new Texture("trash_organic.png"));
        trashTextures.put(TrashType.VIDRO, new Texture("trash_glass.png"));
        trashTextures.put(TrashType.VIDA, new Texture("life_item.png"));
    }

    // Atualiza a posição e rotação do lixo
    public void update(float delta) {
        bounds.y -= speed * delta; // Move para baixo
        rotation += rotationSpeed * delta; // Atualiza rotação

        // Mantém a rotação entre 0-360 graus
        if (rotation > 360) rotation -= 360;
    }

    // Desenha o lixo na tela
    public void draw(SpriteBatch batch) {
        // Desenho especial para itens de vida (sem rotação e com escala fixa)
        if (type == TrashType.VIDA) {
            batch.draw(texture,
                bounds.x, bounds.y,
                bounds.width/2, bounds.height/2, // Ponto de origem no centro
                bounds.width, bounds.height,
                1.5f, 1.5f, // Escala fixa
                0, // Sem rotação
                0, 0, // Região da textura
                texture.getWidth(), texture.getHeight(),
                false, false);
        } else {
            // Desenho normal para outros lixos (com rotação)
            batch.draw(texture,
                bounds.x, bounds.y,
                bounds.width/2, bounds.height/2, // Ponto de origem no centro
                bounds.width, bounds.height,
                1.5f, 1.5f, // Escala fixa
                rotation, // Aplica rotação
                0, 0, // Região da textura
                texture.getWidth(), texture.getHeight(),
                false, false);
        }
    }

    // Métodos de acesso:
    public Rectangle getBounds() { return bounds; } // Retorna área de colisão
    public TrashType getType() { return type; }    // Retorna tipo do lixo

    // Verifica se o lixo saiu da tela (pela parte inferior)
    public boolean isOutOfScreen() { return bounds.y + bounds.height < 0; }

    // Altera a velocidade de queda
    public void setSpeed(float newSpeed) { this.speed = newSpeed; }

    // Libera as texturas (chamado quando o jogo termina)
    public static void disposeTextures() {
        if (trashTextures != null) {
            for (Texture texture : trashTextures.values()) {
                texture.dispose(); // Libera recursos de cada textura
            }
        }
    }
}
