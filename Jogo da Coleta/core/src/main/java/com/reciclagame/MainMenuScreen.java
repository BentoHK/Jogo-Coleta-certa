package com.reciclagame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class MainMenuScreen implements Screen {
    // Referência para o jogo principal
    private final ReciclaGame game;

    // Elementos gráficos
    private BitmapFont font; // Fonte para texto
    private Texture backgroundTexture; // Textura de fundo do menu
    private ShapeRenderer shapeRenderer; // Renderizador de formas (não utilizado no momento)

    // Botões do menu
    private Rectangle startButton; // Retângulo para o botão de iniciar
    private Rectangle exitButton; // Retângulo para o botão de sair

    // Controle de música
    private boolean musicStarted = false; // Flag para verificar se a música já começou

    public MainMenuScreen(ReciclaGame game) {
        this.game = game;
        font = new BitmapFont();
        backgroundTexture = new Texture("main_menu_background.png");
        shapeRenderer = new ShapeRenderer();

        // Definir dimensões dos botões (28% da largura e 13% da altura da tela)
        float buttonWidth = Gdx.graphics.getWidth() * 0.28f;
        float buttonHeight = Gdx.graphics.getHeight() * 0.13f;

        // Posiciona o botão de iniciar no meio da tela (ajustado para esquerda)
        startButton = new Rectangle(
            Gdx.graphics.getWidth() - buttonWidth - 565, // Posição X
            Gdx.graphics.getHeight() * 0.5f, // Posição Y (50% da altura)
            buttonWidth,
            buttonHeight
        );

        // Posiciona o botão de sair abaixo do botão de iniciar
        exitButton = new Rectangle(
            Gdx.graphics.getWidth() - buttonWidth - 565, // Mesma posição X
            Gdx.graphics.getHeight() * 0.32f, // Posição Y (32% da altura)
            buttonWidth,
            buttonHeight
        );
    }

    @Override
    public void render(float delta) {
        // Inicia a música de fundo se ainda não tiver começado
        if (!musicStarted) {
            SoundManager.backgroundMusic.play();
            musicStarted = true;
        }

        // Limpa a tela com cor preta
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Desenha o fundo do menu
        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.end();

        // Verifica toques na tela
        if (Gdx.input.justTouched()) {
            // Converte coordenadas do toque (o sistema de coordenadas do GDX é invertido no eixo Y)
            float x = Gdx.input.getX();
            float y = Gdx.graphics.getHeight() - Gdx.input.getY();

            // Verifica se o toque foi dentro do botão de iniciar
            if (startButton.contains(x, y)) {
                SoundManager.backgroundMusic.stop();
                game.setScreen(new GameScreen(game)); // Muda para a tela do jogo
            }
            // Verifica se o toque foi dentro do botão de sair
            else if (exitButton.contains(x, y)) {
                SoundManager.backgroundMusic.stop();
                Gdx.app.exit(); // Fecha o aplicativo
            }
        }
    }

    @Override
    public void dispose() {
        // Libera recursos quando a tela não é mais necessária
        font.dispose();
        backgroundTexture.dispose();
        shapeRenderer.dispose();
    }

    // Métodos não utilizados da interface Screen
    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
