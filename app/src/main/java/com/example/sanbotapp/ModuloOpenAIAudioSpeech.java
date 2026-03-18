package com.example.sanbotapp;

import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModuloOpenAIAudioSpeech {
    private byte[] respuestaGPTVoz;

    // Constructor
    public ModuloOpenAIAudioSpeech(){
    }

    // Función para realizar la consulta al endpoint Audio Speech de OpenAI
    public void peticionVozOpenAI(String respuesta, String voz){

        // ----------- DATOS PARA REALIZAR REQUESTS HTTP -------------

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final OkHttpClient client = new OkHttpClient();

        // ----------- DATOS PARA REALIZAR PETICIÓN A LA API DE OPENAI ---------
        JSONObject request = new JSONObject();
        try{
            request.put("model", "tts-1");
            request.put("input", respuesta);
            request.put("voice", voz);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("respuestaJSON", request.toString());

        RequestBody peticion = RequestBody.create(
                MediaType.parse("application/json"), String.valueOf(request));

        Log.d("requestBody", peticion.toString());


        Request requestOpenAI = new Request.Builder()
                .url("https://api.openai.com/v1/audio/speech")
                .post(peticion)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer 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")
                .build();

        Log.d("requestBody", requestOpenAI.toString());

        try (Response response = client.newCall(requestOpenAI).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);
            // String respuesta = response.body().string();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                respuestaGPTVoz = response.body().bytes();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Función que obtiene la respuesta en formato ristra de bytes de la consulta al Audio Speech de la API de OpenAI
    public byte[] getGPTVoz(){
        return respuestaGPTVoz;
    }

    // Funcion que pasado un texto, lo reproduce con la voz seleccionada
    public void reproducirVoz(String respuesta, String voz) {
        peticionVozOpenAI(respuesta, voz);
    }


}