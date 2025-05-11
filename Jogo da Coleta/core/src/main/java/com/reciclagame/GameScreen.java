package com.reciclagame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {
    // Referência para o jogo principal
    private final ReciclaGame game;

    // Objetos para renderização
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private Texture backgroundTexture;

    // Elementos do jogo
    private Bin bin; // Lixeira controlada pelo jogador
    private Array<Trash> trashArray; // Array de lixos na tela
    private long lastTrashTime; // Último tempo que um lixo foi gerado

    // Variáveis do jogo
    private int score, lives; // Pontuação e vidas
    private float trashSpeed; // Velocidade atual dos lixos
    private float baseTrashSpeed = 60f; // Velocidade base dos lixos
    private float lifeItemSpeed = 42f; // Velocidade dos itens de vida
    private int lastSpeedIncreaseScore = 0; // Pontuação do último aumento de velocidade
    private float spawnSpeed = 2000000000; // Intervalo de spawn dos lixos (em nanosegundos)

    // Constantes para controle de spawn
    private static final float MIN_SPAWN_SPEED = 500000000f; // Intervalo mínimo de spawn
    private static final float SPAWN_SPEED_DECREASE = 100000000f; // Quanto diminui o intervalo

    // Variáveis para controle de dificuldade
    private int lastSpawnIncreaseScore = 0; // Pontuação do último aumento de spawn

    // Sistema de Pausa Ecológica
    private boolean ecoPauseActive = false; // Se a pausa ecológica está ativa
    private float ecoPauseTimer = 0f; // Tempo restante da pausa
    private float ecoPauseCooldown = 0f; // Tempo de recarga
    private float ecoPauseDuration = 7f; // Duração da pausa
    private float ecoPauseMaxCooldown = 20f; // Tempo máximo de recarga
    private float ecoPauseSpeedMultiplier = 0.5f; // Multiplicador de velocidade durante a pausa
    private float ecoPulseTimer = 0f; // Timer para efeitos visuais
    private float prePauseSpeed; // Velocidade antes da pausa
    private Texture ecoPauseBackgroundTexture; // Textura de fundo durante a pausa

    // Sistema de Coleta Desenfreada
    private boolean frenzyCollectActive = false; // Se está ativo
    private float frenzyCollectTimer = 0f; // Tempo restante
    private float frenzyCollectCooldown = 0f; // Tempo de recarga
    private float frenzyCollectDuration = 10f; // Duração
    private float frenzyCollectMaxCooldown = 30f; // Tempo máximo de recarga
    private float originalTrashSpeed; // Velocidade original dos lixos

    // Sistema de Limpeza Total
    private boolean cleanAllActive = false; // Se está ativo
    private float cleanAllTimer = 0f; // Tempo restante
    private float cleanAllCooldown = 0f; // Tempo de recarga
    private float cleanAllDuration = 3f; // Duração
    private float cleanAllMaxCooldown = 25f; // Tempo máximo de recarga
    private Texture cleanAllEffectTexture; // Textura do efeito visual
    private float cleanAllEffectX = -Gdx.graphics.getWidth(); // Posição X do efeito
    private float cleanAllEffectSpeed = 1200f; // Velocidade do efeito
    private boolean cleanAllEffectActive = false; // Se o efeito está ativo
    private boolean cleanAllMusicStopping = false; // Se a música está parando
    private float cleanAllMusicStopTimer = 0f; // Timer para parar a música

    // Texturas para vidas
    private Texture lifeTexture; // Textura de vida cheia
    private Texture lifeTextureLost; // Textura de vida vazia

    // Controle de música
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
        lifeTexture = new Texture(Gdx.files.internal("heart.png"));
        lifeTextureLost = new Texture(Gdx.files.internal("empty_heart.png"));
    }

    @Override
    public void render(float delta) {
        // Inicia a música de fundo se não estiver tocando
        if (!musicPlaying) {
            SoundManager.backgroundMusic.play();
            musicPlaying = true;
        }

        // Limpa a tela
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Desenha o fundo
        game.batch.begin();
        Texture currentBackground = ecoPauseActive ? ecoPauseBackgroundTexture : backgroundTexture;
        game.batch.draw(currentBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.end();

        // Atualiza os sistemas de poder
        updateEcoPause(delta);
        updateFrenzyCollect(delta);
        updateCleanAll(delta);

        // Aumenta a frequência de spawn conforme a pontuação
        if (score - lastSpawnIncreaseScore >= 15) {
            lastSpawnIncreaseScore = score;
            if (spawnSpeed > MIN_SPAWN_SPEED) {
                spawnSpeed -= SPAWN_SPEED_DECREASE;
                if (spawnSpeed < MIN_SPAWN_SPEED) {
                    spawnSpeed = MIN_SPAWN_SPEED;
                }
            }
        }

        // Renderização de efeitos visuais
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (frenzyCollectActive) {
            float alpha = 0.3f + 0.2f * (float)Math.sin(frenzyCollectTimer * 10f);
            shapeRenderer.setColor(1f, 0.5f, 0.5f, alpha);
        }
        shapeRenderer.end();

        // Atualiza a lixeira
        bin.update(delta);

        // Gera novos lixos
        if (!cleanAllEffectActive && TimeUtils.nanoTime() - lastTrashTime > spawnSpeed) {
            spawnTrash();
            lastTrashTime = TimeUtils.nanoTime();
        }

        // Processa colisões e lógica dos lixos
        for (int i = 0; i < trashArray.size; i++) {
            Trash trash = trashArray.get(i);
            trash.update(delta);

            // Verifica colisão com a lixeira
            if (trash.getBounds().overlaps(bin.getBounds())) {
                if (trash.getType() == TrashType.VIDA) {
                    // Coleta item de vida
                    if (lives < 5) {
                        lives++;
                        SoundManager.lifeCollected.play(0.5f);
                    }
                    trashArray.removeIndex(i);
                    i--;
                    continue;
                }

                // Coleta durante frenesi
                if (frenzyCollectActive) {
                    score++;
                    SoundManager.trashCollected.play(0.5f);
                    trashArray.removeIndex(i);
                    i--;
                    continue;
                }

                // Coleta normal
                if (trash.getType() == bin.getCurrentType()) {
                    score++;
                    SoundManager.trashCollected.play(0.6f);
                    // Aumenta dificuldade a cada 10 pontos
                    if (score - lastSpeedIncreaseScore >= 10) {
                        lastSpeedIncreaseScore = score;
                        baseTrashSpeed += 20f;
                        if (!ecoPauseActive) {
                            trashSpeed = baseTrashSpeed;
                            updateAllTrashSpeeds();
                        }
                        SoundManager.difficultyIncreased.play(0.4f);
                    }
                } else {
                    // Errou o tipo - perde vida
                    lives--;
                    SoundManager.damage.play(0.3f);
                    if (lives <= 0) {
                        // Game over
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

            // Verifica se o lixo saiu da tela
            if (trash.isOutOfScreen()) {
                if (trash.getType() != TrashType.VIDA) {
                    lives--;
                    SoundManager.damage.play(0.3f);

                    if (lives <= 0) {
                        // Game over
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

        // Desenha os elementos do jogo
        game.batch.begin();
        bin.draw(game.batch);
        for (Trash trash : trashArray) {
            trash.draw(game.batch);
        }

        // Efeito visual da limpeza total
        if (cleanAllEffectActive) {
            game.batch.draw(cleanAllEffectTexture,
                cleanAllEffectX, -700,
                Gdx.graphics.getWidth() * 2.5f,
                Gdx.graphics.getHeight() * 2.5f);
        }
        game.batch.end();

        // Desenha o HUD
        drawHUD();

        // Para a música da limpeza total após um tempo
        if (cleanAllMusicStopping) {
            cleanAllMusicStopTimer -= delta;
            if (cleanAllMusicStopTimer <= 0) {
                SoundManager.cleanAllTrash.stop();
                cleanAllMusicStopping = false;
            }
        }
    }

    // Atualiza o estado da Limpeza Total
    private void updateCleanAll(float delta) {
        if (cleanAllActive) {
            cleanAllTimer -= delta;
            if (cleanAllTimer <= 0) {
                endCleanAll();
            }
        } else if (cleanAllCooldown > 0) {
            cleanAllCooldown -= delta;
        }

        // Ativa com a tecla W
        if (Gdx.input.isKeyJustPressed(Keys.W) && cleanAllCooldown <= 0 &&
            !ecoPauseActive && !frenzyCollectActive) {
            activateCleanAll();
        }

        // Atualiza o efeito visual
        if (cleanAllEffectActive) {
            cleanAllEffectX += cleanAllEffectSpeed * delta;

            if (cleanAllEffectX > Gdx.graphics.getWidth()) {
                cleanAllEffectActive = false;
                cleanAllMusicStopping = true;
                cleanAllMusicStopTimer = 1f;
            }
        }
    }

    // Ativa a Limpeza Total
    private void activateCleanAll() {
        cleanAllActive = true;
        cleanAllTimer = cleanAllDuration;
        cleanAllCooldown = cleanAllMaxCooldown;
        cleanAllEffectActive = true;
        cleanAllEffectX = -cleanAllEffectTexture.getWidth() * 2.5f;
        cleanAllMusicStopping = false;

        SoundManager.cleanAllTrash.play(0.5f);

        // Remove todos os lixos (exceto itens de vida)
        for (int i = trashArray.size - 1; i >= 0; i--) {
            Trash trash = trashArray.get(i);
            if (trash.getType() != TrashType.VIDA) {
                trashArray.removeIndex(i);
            }
        }
    }

    // Finaliza a Limpeza Total
    private void endCleanAll() {
        cleanAllActive = false;
    }

    // Atualiza a velocidade de todos os lixos
    private void updateAllTrashSpeeds() {
        for (Trash trash : trashArray) {
            if (trash.getType() != TrashType.VIDA) {
                trash.setSpeed(trashSpeed);
            }
        }
    }

    // Desenha a interface do usuário
    private void drawHUD() {
        game.batch.begin();

        font.getData().setScale(1.8f);
        font.setColor(0.2f, 0.2f, 0.2f, 1f);

        // Pontuação
        font.draw(game.batch, "Pontos: " + score,
            Gdx.graphics.getWidth()/2 - 60,
            Gdx.graphics.getHeight() - 30);

        // Vidas
        float lifeIconX = Gdx.graphics.getWidth() - 250;
        float lifeIconY = Gdx.graphics.getHeight() - 50;
        float lifeIconSize = 40;
        float lifeIconSpacing = 45;

        for (int i = 0; i < lives; i++) {
            game.batch.draw(lifeTexture,
                lifeIconX + (i * lifeIconSpacing),
                lifeIconY,
                lifeIconSize,
                lifeIconSize);
        }

        // Tipo atual da lixeira
        font.draw(game.batch, "Tipo: " + bin.getCurrentType().getName(),
            Gdx.graphics.getWidth()/2 - 60,
            50);

        // Vidas perdidas
        for (int i = lives; i < 5; i++) {
            game.batch.draw(lifeTextureLost,
                lifeIconX + (i * lifeIconSpacing),
                lifeIconY,
                lifeIconSize,
                lifeIconSize);
        }

        // Habilidades e seus tempos
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

        game.batch.end();
    }

    // Atualiza o estado da Pausa Ecológica
    private void updateEcoPause(float delta) {
        if (ecoPauseActive) {
            ecoPauseTimer -= delta;
            ecoPulseTimer += delta;
            if (ecoPauseTimer <= 0) endEcoPause();
        } else if (ecoPauseCooldown > 0) {
            ecoPauseCooldown -= delta;
        }

        // Ativa com a tecla Q
        if (Gdx.input.isKeyJustPressed(Keys.Q) && ecoPauseCooldown <= 0 && !frenzyCollectActive) {
            activateEcoPause();
        }
    }

    // Atualiza o estado da Coleta Desenfreada
    private void updateFrenzyCollect(float delta) {
        if (frenzyCollectActive) {
            frenzyCollectTimer -= delta;
            if (frenzyCollectTimer <= 0) endFrenzyCollect();
        } else if (frenzyCollectCooldown > 0) {
            frenzyCollectCooldown -= delta;
        }

        // Ativa com a tecla E
        if (Gdx.input.isKeyJustPressed(Keys.E) && frenzyCollectCooldown <= 0 && !ecoPauseActive) {
            activateFrenzyCollect();
        }
    }

    // Ativa a Pausa Ecológica
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

    // Finaliza a Pausa Ecológica
    private void endEcoPause() {
        ecoPauseActive = false;
        trashSpeed = prePauseSpeed;
        updateAllTrashSpeeds();

        SoundManager.ecoPauseMusic.stop();
        SoundManager.backgroundMusic.play();
    }

    // Ativa a Coleta Desenfreada
    private void activateFrenzyCollect() {
        frenzyCollectActive = true;
        frenzyCollectTimer = frenzyCollectDuration;
        frenzyCollectCooldown = frenzyCollectMaxCooldown;
        bin.setFrenzyActive(true);

        SoundManager.backgroundMusic.pause();
        SoundManager.frenzyMusic.play();
    }

    // Finaliza a Coleta Desenfreada
    private void endFrenzyCollect() {
        frenzyCollectActive = false;
        bin.setFrenzyActive(false);

        SoundManager.frenzyMusic.stop();
        SoundManager.backgroundMusic.play();
    }

    // Gera um novo lixo na tela
    private void spawnTrash() {
        float x = MathUtils.random(0, Gdx.graphics.getWidth() - 50);
        Trash newTrash = new Trash(x, 50,
            (MathUtils.random(1, 10) == 1 ? lifeItemSpeed : trashSpeed));
        trashArray.add(newTrash);
    }

    @Override
    public void dispose() {
        // Para todas as músicas e libera recursos
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
        lifeTexture.dispose();
        lifeTextureLost.dispose();
    }

    // Métodos não utilizados da interface Screen
    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
