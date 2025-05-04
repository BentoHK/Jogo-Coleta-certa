package com.reciclagame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {
    private final ReciclaGame game;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private Texture backgroundTexture;
    private Bin bin;
    private Array<Trash> trashArray;
    private long lastTrashTime;
    private int score, lives;
    private float trashSpeed;
    private float baseTrashSpeed = 60f;
    private float lifeItemSpeed = 42f;
    private int lastSpeedIncreaseScore = 0;
    private float spawnSpeed = 2000000000;

    private static final float MIN_SPAWN_SPEED = 500000000f;
    private static final float SPAWN_SPEED_DECREASE = 100000000f;
    private int lastSpawnIncreaseScore = 0;

    private boolean ecoPauseActive = false;
    private float ecoPauseTimer = 0f;
    private float ecoPauseCooldown = 0f;
    private float ecoPauseDuration = 7f;
    private float ecoPauseMaxCooldown = 20f;
    private float ecoPauseSpeedMultiplier = 0.5f;
    private float ecoPulseTimer = 0f;
    private float prePauseSpeed;
    private Texture ecoPauseBackgroundTexture;

    private boolean frenzyCollectActive = false;
    private float frenzyCollectTimer = 0f;
    private float frenzyCollectCooldown = 0f;
    private float frenzyCollectDuration = 10f;
    private float frenzyCollectMaxCooldown = 30f;
    private float originalTrashSpeed;

    private boolean cleanAllActive = false;
    private float cleanAllTimer = 0f;
    private float cleanAllCooldown = 0f;
    private float cleanAllDuration = 3f;
    private float cleanAllMaxCooldown = 25f;
    private Texture cleanAllEffectTexture;
    private float cleanAllEffectX = -Gdx.graphics.getWidth();
    private float cleanAllEffectSpeed = 1200f;
    private boolean cleanAllEffectActive = false;
    private boolean cleanAllMusicStopping = false;
    private float cleanAllMusicStopTimer = 0f;
    private static final float CLEAN_ALL_MUSIC_STOP_DELAY = 1f;

    private boolean musicPlaying = false;

    public GameScreen(ReciclaGame game) {
        this.game = game;
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        backgroundTexture = new Texture(Gdx.files.internal("background_game.png"));
        bin = new Bin();
        trashArray = new Array<>();
        lastTrashTime = TimeUtils.nanoTime();
        score = 0;
        lives = 3;
        trashSpeed = baseTrashSpeed;
        originalTrashSpeed = trashSpeed;
        cleanAllEffectTexture = new Texture(Gdx.files.internal("clean_all_effect.png"));
        ecoPauseBackgroundTexture = new Texture(Gdx.files.internal("background_ecopause.png"));

    }

    @Override
    public void render(float delta) {
        if (!musicPlaying) {
            SoundManager.backgroundMusic.play();
            musicPlaying = true;
        }

        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();

        Texture currentBackground = ecoPauseActive ? ecoPauseBackgroundTexture : backgroundTexture;
        game.batch.draw(currentBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        game.batch.end();

        updateEcoPause(delta);
        updateFrenzyCollect(delta);
        updateCleanAll(delta);

        if (score - lastSpawnIncreaseScore >= 15) {
            lastSpawnIncreaseScore = score;
            if (spawnSpeed > MIN_SPAWN_SPEED) {
                spawnSpeed -= SPAWN_SPEED_DECREASE;
                if (spawnSpeed < MIN_SPAWN_SPEED) {
                    spawnSpeed = MIN_SPAWN_SPEED;
                }
            }
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (frenzyCollectActive) {
            float alpha = 0.3f + 0.2f * (float)Math.sin(frenzyCollectTimer * 10f);
            shapeRenderer.setColor(1f, 0.5f, 0.5f, alpha);
        }
        shapeRenderer.end();

        bin.update(delta);

        if (!cleanAllEffectActive && TimeUtils.nanoTime() - lastTrashTime > spawnSpeed) {
            spawnTrash();
            lastTrashTime = TimeUtils.nanoTime();
        }


        for (int i = 0; i < trashArray.size; i++) {
            Trash trash = trashArray.get(i);
            trash.update(delta);

            if (trash.getBounds().overlaps(bin.getBounds())) {
                if (trash.getType() == TrashType.VIDA) {
                    if (lives < 5) {
                        lives++;
                        SoundManager.lifeCollected.play(0.8f);
                    }
                    trashArray.removeIndex(i);
                    i--;
                    continue;
                }

                if (frenzyCollectActive) {
                    score++;
                    SoundManager.trashCollected.play(0.6f);
                    trashArray.removeIndex(i);
                    i--;
                    continue;
                }

                if (trash.getType() == bin.getCurrentType()) {
                    score++;
                    SoundManager.trashCollected.play(0.6f);
                    if (score - lastSpeedIncreaseScore >= 10) {
                        lastSpeedIncreaseScore = score;
                        baseTrashSpeed += 20f;
                        if (!ecoPauseActive) {
                            trashSpeed = baseTrashSpeed;
                            updateAllTrashSpeeds();
                        }
                        SoundManager.difficultyIncreased.play(0.3f);
                    }
                } else {
                    lives--;
                    SoundManager.damage.play(0.3f);
                    if (lives <= 0) {
                        SoundManager.backgroundMusic.stop();
                        SoundManager.ecoPauseMusic.stop();
                        SoundManager.frenzyMusic.stop();
                        SoundManager.gameOverMusic.play();
                        game.setScreen(new GameOverScreen(game, score));
                        return;
                    }
                }
                trashArray.removeIndex(i);
                i--;
                continue;
            }

            if (trash.isOutOfScreen()) {
                if (trash.getType() != TrashType.VIDA) {
                    lives--;
                    SoundManager.damage.play(0.3f);

                    if (lives <= 0) {
                        SoundManager.backgroundMusic.stop();
                        SoundManager.ecoPauseMusic.stop();
                        SoundManager.frenzyMusic.stop();
                        SoundManager.gameOverMusic.play();
                        game.setScreen(new GameOverScreen(game, score));
                        return;
                    }
                }
                trashArray.removeIndex(i);
                i--;
            }
        }

        game.batch.begin();
        bin.draw(game.batch);
        for (Trash trash : trashArray) {
            trash.draw(game.batch);
        }

        if (cleanAllEffectActive) {
            game.batch.draw(cleanAllEffectTexture,
                cleanAllEffectX, -700,
                Gdx.graphics.getWidth() * 2.5f,
                Gdx.graphics.getHeight() * 2.5f);
        }
        game.batch.end();

        drawHUD();

        if (cleanAllMusicStopping) {
            cleanAllMusicStopTimer -= delta;
            if (cleanAllMusicStopTimer <= 0) {
                SoundManager.cleanAllTrash.stop();
                cleanAllMusicStopping = false;
            }
        }
    }

    private void updateCleanAll(float delta) {
        if (cleanAllActive) {
            cleanAllTimer -= delta;
            if (cleanAllTimer <= 0) {
                endCleanAll();
            }
        } else if (cleanAllCooldown > 0) {
            cleanAllCooldown -= delta;
        }

        if (Gdx.input.isKeyJustPressed(Keys.W) && cleanAllCooldown <= 0 &&
            !ecoPauseActive && !frenzyCollectActive) {
            activateCleanAll();
        }

        if (cleanAllEffectActive) {
            cleanAllEffectX += cleanAllEffectSpeed * delta;

            if (cleanAllEffectX > Gdx.graphics.getWidth()) {
                cleanAllEffectActive = false;
                cleanAllMusicStopping = true;
                cleanAllMusicStopTimer = CLEAN_ALL_MUSIC_STOP_DELAY;
            }
        }
    }

    private void activateCleanAll() {
        cleanAllActive = true;
        cleanAllTimer = cleanAllDuration;
        cleanAllCooldown = cleanAllMaxCooldown;
        cleanAllEffectActive = true;
        cleanAllEffectX = -cleanAllEffectTexture.getWidth() * 2.5f;
        cleanAllMusicStopping = false;

        SoundManager.cleanAllTrash.play(0.5f);

        for (int i = trashArray.size - 1; i >= 0; i--) {
            Trash trash = trashArray.get(i);
            if (trash.getType() != TrashType.VIDA) {
                trashArray.removeIndex(i);
            }
        }
    }

    private void endCleanAll() {
        cleanAllActive = false;
    }

    private void updateAllTrashSpeeds() {
        for (Trash trash : trashArray) {
            if (trash.getType() != TrashType.VIDA) {
                trash.setSpeed(trashSpeed);
            }
        }
    }

    private void drawHUD() {
        game.batch.begin();

        // Configuração da fonte aumentada
        font.getData().setScale(1.1f); // Aumenta o tamanho da fonte para todos os elementos


        font.draw(game.batch, "Pontos: " + score,
            Gdx.graphics.getWidth()/2 - 60,
            Gdx.graphics.getHeight() - 30);

        // Vidas no canto superior direito
        font.draw(game.batch, "Vidas: " + lives,
            Gdx.graphics.getWidth() - 120,
            Gdx.graphics.getHeight() - 30);

        // Tipo de lixo no centro inferior (mantido)
        font.draw(game.batch, "Tipo: " + bin.getCurrentType().getName(),
            Gdx.graphics.getWidth()/2 - 60,
            50);

        // Poderes no canto superior esquerdo (agrupados verticalmente)
        float leftX = 20;
        float yPos = Gdx.graphics.getHeight() - 30;
        float spacing = 40;

        // Pausa Ecológica
        if (ecoPauseActive) {
            font.draw(game.batch, "Pausa Ecológica:" + (int)ecoPauseTimer + "s",
                leftX, yPos);
        } else if (ecoPauseCooldown > 0) {
            font.draw(game.batch, "Pausa Ecológica: " + (int)ecoPauseCooldown + "s",
                leftX, yPos);
        } else {
            font.draw(game.batch, "Q - Pausa Ecológica",
                leftX, yPos);
        }
        yPos -= spacing;

        // Coleta Desenfreada
        if (frenzyCollectActive) {
            font.draw(game.batch, "Coleta Desenfreada: " + (int)frenzyCollectTimer + "s",
                leftX, yPos);
        } else if (frenzyCollectCooldown > 0) {
            font.draw(game.batch, "Coleta Desenfreada: " + (int)frenzyCollectCooldown + "s",
                leftX, yPos);
        } else {
            font.draw(game.batch, "E - Coleta Desenfreada",
                leftX, yPos);
        }
        yPos -= spacing;

        // Limpeza Total
        if (cleanAllCooldown > 0) {
            font.draw(game.batch, "Limpeza Total: " + (int)cleanAllCooldown + "s",
                leftX, yPos);
        } else {
            font.draw(game.batch, "W - Limpeza Total",
                leftX, yPos);
        }

        // Restaura o tamanho original da fonte
        font.getData().setScale(1.0f);

        game.batch.end();
    }

    private void updateEcoPause(float delta) {
        if (ecoPauseActive) {
            ecoPauseTimer -= delta;
            ecoPulseTimer += delta;
            if (ecoPauseTimer <= 0) endEcoPause();
        } else if (ecoPauseCooldown > 0) {
            ecoPauseCooldown -= delta;
        }

        if (Gdx.input.isKeyJustPressed(Keys.Q) && ecoPauseCooldown <= 0 && !frenzyCollectActive) {
            activateEcoPause();
        }
    }

    private void updateFrenzyCollect(float delta) {
        if (frenzyCollectActive) {
            frenzyCollectTimer -= delta;
            if (frenzyCollectTimer <= 0) endFrenzyCollect();
        } else if (frenzyCollectCooldown > 0) {
            frenzyCollectCooldown -= delta;
        }

        if (Gdx.input.isKeyJustPressed(Keys.E) && frenzyCollectCooldown <= 0 && !ecoPauseActive) {
            activateFrenzyCollect();
        }
    }

    private void activateEcoPause() {
        ecoPauseActive = true;
        ecoPauseTimer = ecoPauseDuration;
        ecoPulseTimer = 0f;
        prePauseSpeed = trashSpeed;
        trashSpeed = originalTrashSpeed * ecoPauseSpeedMultiplier;
        updateAllTrashSpeeds();
        ecoPauseCooldown = ecoPauseMaxCooldown;

        SoundManager.backgroundMusic.pause();
        SoundManager.ecoPauseMusic.play();
    }

    private void endEcoPause() {
        ecoPauseActive = false;
        trashSpeed = prePauseSpeed;
        updateAllTrashSpeeds();

        SoundManager.ecoPauseMusic.stop();
        SoundManager.backgroundMusic.play();
    }

    private void activateFrenzyCollect() {
        frenzyCollectActive = true;
        frenzyCollectTimer = frenzyCollectDuration;
        frenzyCollectCooldown = frenzyCollectMaxCooldown;
        bin.setFrenzyActive(true);

        SoundManager.backgroundMusic.pause();
        SoundManager.frenzyMusic.play();
    }

    private void endFrenzyCollect() {
        frenzyCollectActive = false;
        bin.setFrenzyActive(false);

        SoundManager.frenzyMusic.stop();
        SoundManager.backgroundMusic.play();
    }

    private void spawnTrash() {
        float x = MathUtils.random(0, Gdx.graphics.getWidth() - 50);
        Trash newTrash = new Trash(x, 50,
            (MathUtils.random(1, 10) == 1 ? lifeItemSpeed : trashSpeed));
        trashArray.add(newTrash);
    }

    @Override
    public void dispose() {
        SoundManager.backgroundMusic.stop();
        SoundManager.ecoPauseMusic.stop();
        SoundManager.frenzyMusic.stop();
        SoundManager.cleanAllTrash.stop();
        shapeRenderer.dispose();
        font.dispose();
        backgroundTexture.dispose();
        bin.dispose();
        Trash.disposeTextures();
        cleanAllEffectTexture.dispose();
        ecoPauseBackgroundTexture.dispose();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
