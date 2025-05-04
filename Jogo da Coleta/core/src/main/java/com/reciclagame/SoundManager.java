package com.reciclagame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {
    public static Sound trashCollected;
    public static Sound powerUsed;
    public static Sound teleport;
    public static Sound damage;
    public static Sound lifeCollected;
    public static Sound binChanged;
    public static Sound difficultyIncreased;
    public static Sound cleanAllTrash;

    public static Music backgroundMusic;
    public static Music ecoPauseMusic;
    public static Music frenzyMusic;
    public static Music gameOverMusic;

    public static void load() {
        trashCollected = Gdx.audio.newSound(Gdx.files.internal("sounds/collect.mp3"));
        powerUsed = Gdx.audio.newSound(Gdx.files.internal("sounds/power.mp3"));
        teleport = Gdx.audio.newSound(Gdx.files.internal("sounds/teleport.mp3"));
        damage = Gdx.audio.newSound(Gdx.files.internal("sounds/damage.mp3"));
        lifeCollected = Gdx.audio.newSound(Gdx.files.internal("sounds/life.mp3"));
        binChanged = Gdx.audio.newSound(Gdx.files.internal("sounds/bin_change.mp3"));
        difficultyIncreased = Gdx.audio.newSound(Gdx.files.internal("sounds/difficulty_up.mp3"));
        cleanAllTrash = Gdx.audio.newSound(Gdx.files.internal("sounds/clean_all.mp3"));

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/background_music.mp3"));
        ecoPauseMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/eco_pause_music.mp3"));
        frenzyMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/frenzy_music.mp3"));
        gameOverMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/game_over.mp3"));

        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
        ecoPauseMusic.setLooping(true);
        ecoPauseMusic.setVolume(0.6f);
        frenzyMusic.setLooping(true);
        frenzyMusic.setVolume(0.7f);
        gameOverMusic.setLooping(false);
        gameOverMusic.setVolume(0.8f);
    }

    public static void dispose() {
        trashCollected.dispose();
        powerUsed.dispose();
        teleport.dispose();
        damage.dispose();
        lifeCollected.dispose();
        binChanged.dispose();
        difficultyIncreased.dispose();
        cleanAllTrash.dispose();

        backgroundMusic.dispose();
        ecoPauseMusic.dispose();
        frenzyMusic.dispose();
        gameOverMusic.dispose();
    }
}
