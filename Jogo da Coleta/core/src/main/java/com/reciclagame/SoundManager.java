package com.reciclagame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {
    // Efeitos sonoros (sounds) - sons curtos que podem ser tocados simultaneamente
    public static Sound trashCollected;      // Som quando coleta lixo corretamente
    public static Sound powerUsed;          // Som ao usar poder especial
    public static Sound teleport;           // Som de teleporte (não utilizado no código principal)
    public static Sound damage;             // Som ao tomar dano/errar a coleta
    public static Sound lifeCollected;      // Som ao coletar item de vida
    public static Sound binChanged;         // Som ao mudar o tipo da lixeira
    public static Sound difficultyIncreased; // Som quando a dificuldade aumenta
    public static Sound cleanAllTrash;      // Som da habilidade Limpeza Total

    // Músicas (Music) - trilhas mais longas que geralmente tocam em loop
    public static Music backgroundMusic;    // Música de fundo principal
    public static Music ecoPauseMusic;     // Música durante a Pausa Ecológica
    public static Music frenzyMusic;       // Música durante a Coleta Desenfreada
    public static Music gameOverMusic;     // Música de Game Over

    // Carrega todos os recursos de áudio do jogo
    public static void load() {
        // Carrega todos os efeitos sonoros a partir dos arquivos na pasta sounds/
        trashCollected = Gdx.audio.newSound(Gdx.files.internal("sounds/collect.mp3"));
        powerUsed = Gdx.audio.newSound(Gdx.files.internal("sounds/power.mp3"));
        teleport = Gdx.audio.newSound(Gdx.files.internal("sounds/teleport.mp3"));
        damage = Gdx.audio.newSound(Gdx.files.internal("sounds/damage.mp3"));
        lifeCollected = Gdx.audio.newSound(Gdx.files.internal("sounds/life.mp3"));
        binChanged = Gdx.audio.newSound(Gdx.files.internal("sounds/bin_change.mp3"));
        difficultyIncreased = Gdx.audio.newSound(Gdx.files.internal("sounds/difficulty_up.mp3"));
        cleanAllTrash = Gdx.audio.newSound(Gdx.files.internal("sounds/clean_all.mp3"));

        // Carrega todas as músicas de fundo
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/background_music.mp3"));
        ecoPauseMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/eco_pause_music.mp3"));
        frenzyMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/frenzy_music.mp3"));
        gameOverMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/game_over.mp3"));

        // Configurações das músicas:
        backgroundMusic.setLooping(true);  // Toca em loop continuamente
        backgroundMusic.setVolume(0.5f);   // Volume reduzido para 50%

        ecoPauseMusic.setLooping(true);    // Toca em loop durante a pausa
        ecoPauseMusic.setVolume(0.6f);     // Volume em 60%

        frenzyMusic.setLooping(true);      // Toca em loop durante o frenesi
        frenzyMusic.setVolume(0.7f);       // Volume em 70%

        gameOverMusic.setLooping(false);   // Toca apenas uma vez
        gameOverMusic.setVolume(0.8f);     // Volume em 80%
    }

    // Libera todos os recursos de áudio quando o jogo é fechado
    public static void dispose() {
        // Descarta todos os efeitos sonoros
        trashCollected.dispose();
        powerUsed.dispose();
        teleport.dispose();
        damage.dispose();
        lifeCollected.dispose();
        binChanged.dispose();
        difficultyIncreased.dispose();
        cleanAllTrash.dispose();

        // Descarta todas as músicas
        backgroundMusic.dispose();
        ecoPauseMusic.dispose();
        frenzyMusic.dispose();
        gameOverMusic.dispose();
    }
}
