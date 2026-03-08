package com.example.sanbotapp;

import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.sanbotapp.ByteArrayMediaDataSource;
import com.qihancloud.opensdk.function.unit.interfaces.speech.SpeakListener;

public class GestionMediaPlayer {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean finReproduccion = false;

    // Constructor
    public GestionMediaPlayer(){
    }

    // Función que indica si el media player se está reproduciendo o no
    public boolean isMediaPlayerReproduciendose(){
        return mediaPlayer.isPlaying();
    }

    // Función que reproduce el media player con la ristra de bytes que se pase como parámetro
    public void reproducirMediaPlayer(byte[] data){
        mediaPlayer.reset();
        // Llama a la clase ByteArrayMediaDataSource que permite reproducir
        // una ristra de bytes, en vez de archivos de audio
        MediaDataSource mediaDataSource = new ByteArrayMediaDataSource(data);
        mediaPlayer.setDataSource(mediaDataSource);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mediaPlayer.prepareAsync();
    }

    // Función que detiene la reproducción del media player
    public void pararMediaPlayer(){
        mediaPlayer.stop();
    }

    // Función que espera a que el media player ha dejado de reproducirse
    public boolean heAcabado(){
        finReproduccion = false;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            // Acción que se ejecuta al terminar la reproducción el mediaPlayer
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Si está en modo conversación automática
                finReproduccion = true;
            }
        });
        while(!finReproduccion){
        }
        return finReproduccion;
    }
}