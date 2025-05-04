package com.reciclagame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class MainMenuScreen implements Screen {
    private final ReciclaGame game;
    private BitmapFont font;
    private Texture backgroundTexture;
    private Rectangle startButton, exitButton;
    private boolean musicStarted = false;
    private ShapeRenderer shapeRenderer;

    public MainMenuScreen(ReciclaGame game) {
        this.game = game;
        font = new BitmapFont();
        backgroundTexture = new Texture("main_menu_background.png");
        shapeRenderer = new ShapeRenderer();

        // Definir botões (ajuste as posições conforme necessário)
        float buttonWidth = Gdx.graphics.getWidth() * 0.28f;
        float buttonHeight = Gdx.graphics.getHeight() * 0.13f;

        startButton = new Rectangle(
            Gdx.graphics.getWidth() - buttonWidth - 565,
            Gdx.graphics.getHeight() * 0.5f,
            buttonWidth,
            buttonHeight
        );

        exitButton = new Rectangle(
            Gdx.graphics.getWidth() - buttonWidth - 565,
            Gdx.graphics.getHeight() * 0.32f,
            buttonWidth,
            buttonHeight
        );
    }

    @Override
    public void render(float delta) {
        if (!musicStarted) {
            SoundManager.backgroundMusic.play();
            musicStarted = true;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Desenhar o fundo
        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.end();


        // Verificar clique
        if (Gdx.input.justTouched()) {
            float x = Gdx.input.getX();
            float y = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (startButton.contains(x, y)) {
                SoundManager.backgroundMusic.stop();
                game.setScreen(new GameScreen(game));
            } else if (exitButton.contains(x, y)) {
                SoundManager.backgroundMusic.stop();
                Gdx.app.exit();
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
