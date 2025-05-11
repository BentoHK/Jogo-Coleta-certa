package com.reciclagame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/**
 * Tela de Game Over que é exibida quando o jogador perde o jogo.
 * Mostra a pontuação final e opções para voltar ao menu ou sair do jogo.
 */
public class GameOverScreen implements Screen {
    // Referência para o jogo principal
    private final ReciclaGame game;

    // Pontuação final alcançada pelo jogador
    private final int finalScore;

    // Fonte para exibir texto
    private BitmapFont font;

    // Textura de fundo da tela
    private Texture backgroundTexture;

    // Área retangular do botão de menu
    private Rectangle menuButton;

    // Área retangular do botão de saída
    private Rectangle exitButton;

    // Flag para controlar se a música já começou
    private boolean musicStarted = false;

    // Renderizador de formas geométricas (não utilizado no código atual)
    private ShapeRenderer shapeRenderer;

    /**
     * Construtor da tela de Game Over.
     * @param game Referência para o jogo principal
     * @param finalScore Pontuação final do jogador
     */
    public GameOverScreen(ReciclaGame game, int finalScore) {
        this.game = game;
        this.finalScore = finalScore;

        // Inicializa recursos
        font = new BitmapFont();
        backgroundTexture = new Texture("game_over_background.png");
        shapeRenderer = new ShapeRenderer();

        // Configura tamanho dos botões (30% da largura e 15% da altura da tela)
        float buttonWidth = Gdx.graphics.getWidth() * 0.3f;
        float buttonHeight = Gdx.graphics.getHeight() * 0.15f;

        // Posiciona o botão de menu
        menuButton = new Rectangle(
            Gdx.graphics.getWidth() - buttonWidth - 565, // Posição X
            Gdx.graphics.getHeight() - 390,              // Posição Y
            buttonWidth,
            buttonHeight
        );

        // Posiciona o botão de saída abaixo do botão de menu
        exitButton = new Rectangle(
            Gdx.graphics.getWidth() - buttonWidth - 565,
            Gdx.graphics.getHeight() - 520,
            buttonWidth,
            buttonHeight
        );
    }

    /**
     * Método principal de renderização chamado a cada frame.
     * @param delta Tempo desde o último frame em segundos
     */
    @Override
    public void render(float delta) {
        // Toca a música de game over apenas uma vez
        if (!musicStarted) {
            SoundManager.gameOverMusic.play();
            musicStarted = true;
        }

        // Limpa a tela com cor preta
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Desenha o fundo
        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Exibe a pontuação final (com fonte aumentada)
        font.getData().setScale(1.8f);
        font.draw(game.batch, "Pontuação: " + finalScore,
            Gdx.graphics.getWidth()/2 - 100, // Posição X centralizada
            Gdx.graphics.getHeight() * 0.7f); // Posição Y a 70% da tela
        game.batch.end();

        // Verifica toques na tela
        if (Gdx.input.justTouched()) {
            float x = Gdx.input.getX();
            // Converte coordenada Y (o sistema do GDX tem Y invertido)
            float y = Gdx.graphics.getHeight() - Gdx.input.getY();

            // Verifica se tocou no botão de menu
            if (menuButton.contains(x, y)) {
                SoundManager.gameOverMusic.stop();
                game.setScreen(new MainMenuScreen(game)); // Volta para o menu principal
            }

            // Verifica se tocou no botão de saída
            if (exitButton.contains(x, y)) {
                SoundManager.gameOverMusic.stop();
                Gdx.app.exit(); // Fecha o aplicativo
            }
        }
    }

    /**
     * Libera os recursos da tela quando não são mais necessários.
     */
    @Override
    public void dispose() {
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
