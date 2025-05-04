package com.reciclagame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class GameOverScreen implements Screen {
    private final ReciclaGame game;
    private final int finalScore;
    private BitmapFont font;
    private Texture backgroundTexture;
    private Rectangle menuButton;
    private Rectangle exitButton; // Novo botão de saída
    private boolean musicStarted = false;
    private ShapeRenderer shapeRenderer;

    public GameOverScreen(ReciclaGame game, int finalScore) {
        this.game = game;
        this.finalScore = finalScore;
        font = new BitmapFont();
        backgroundTexture = new Texture("game_over_background.png");
        shapeRenderer = new ShapeRenderer();

        float buttonWidth = Gdx.graphics.getWidth() * 0.3f;
        float buttonHeight = Gdx.graphics.getHeight() * 0.15f;

        menuButton = new Rectangle(
            Gdx.graphics.getWidth() - buttonWidth - 565,
            Gdx.graphics.getHeight() - 390,
            buttonWidth,
            buttonHeight
        );


        exitButton = new Rectangle(
            Gdx.graphics.getWidth() - buttonWidth - 565,
            Gdx.graphics.getHeight() - 520,
            buttonWidth,
            buttonHeight
        );
    }

    @Override
    public void render(float delta) {
        if (!musicStarted) {
            SoundManager.gameOverMusic.play();
            musicStarted = true;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Desenhar fundo
        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Mostrar pontuação
        font.getData().setScale(1.8f);
        font.draw(game.batch, "Pontuação: " + finalScore,
            Gdx.graphics.getWidth()/2 - 100,
            Gdx.graphics.getHeight() * 0.7f);
        game.batch.end();


        if (Gdx.input.justTouched()) {
            float x = Gdx.input.getX();
            float y = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (menuButton.contains(x, y)) {
                SoundManager.gameOverMusic.stop();
                game.setScreen(new MainMenuScreen(game));
            }

            if (exitButton.contains(x, y)) {
                SoundManager.gameOverMusic.stop();
                Gdx.app.exit(); // Fecha o jogo
            }
        }
    }

    @Override
    public void dispose() {
        font.dispose();
        backgroundTexture.dispose();
        shapeRenderer.dispose();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
