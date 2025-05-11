package com.reciclagame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ReciclaGame extends Game {
    // SpriteBatch é o objeto principal para renderização 2D no LibGDX
    // É usado para desenhar texturas, sprites, textos, etc.
    public SpriteBatch batch;

    @Override
    public void create() {
        // Método chamado quando o jogo é iniciado

        // Inicializa o SpriteBatch que será usado por todas as telas do jogo
        batch = new SpriteBatch();

        // Carrega todos os recursos de áudio do jogo
        SoundManager.load();

        // Define a tela inicial do jogo como a tela de menu principal
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        // Método chamado quando o jogo é fechado
        // Responsável por liberar todos os recursos

        // Libera os recursos do SpriteBatch
        batch.dispose();

        // Libera os recursos de áudio carregados
        SoundManager.dispose();
    }
}
