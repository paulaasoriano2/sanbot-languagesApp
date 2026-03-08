package com.example.sanbotapp;

import android.os.StrictMode;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModuloOpenAIChatCompletions {

    private Map<String, String> roleSystem = new HashMap<>();
    private List<Map<String, String>> messages = new ArrayList<>();
    private String respuestaChatGPT;

    // Constructor
    public ModuloOpenAIChatCompletions(){
    }

    // Función para añadir content RoleSystem
    public void anadirRoleSystem(String content){
        roleSystem.put("role", "system");
        roleSystem.put("content", content);
        messages.add(roleSystem);
    }

    // Función para vaciar content RoleSystem
    public void clearRoleSystem(){
        roleSystem.clear();
    }

    // Función para vaciar mensajes de la API de OpenAI
    public void clearConversacion(){
        messages.clear();
    }

    // Función para añadir content al RoleUser
    private void anadirRoleUser(String pregunta){
        Map<String, String> roleUser = new HashMap<>();
        roleUser.put("role", "user");
        roleUser.put("content", pregunta);
        messages.add(roleUser);
    }

    // Función para añadir content al RoleAssistant
    private void anadirRoleAssistant(String respuesta){
        Map<String, String> roleAssistant = new HashMap<>();
        roleAssistant.put("role", "assistant");
        roleAssistant.put("content", respuesta);
        messages.add(roleAssistant);
        respuestaChatGPT = respuesta;
    }

    // Función que va añadiendo el hilo de la conversación
    private JSONArray construirConversacion(){
        JSONArray conversacion = new JSONArray();

        for (Map<String, String> message : messages) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("role", message.get("role"));
                jsonObject.put("content", message.get("content"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            conversacion.put(jsonObject);
        }
        return conversacion;
    }

    // Función para realizar la consulta al endpoint Chat Completions de OpenAI
    public void consultaOpenAI(String pregunta){

        // ----------- DATOS PARA REALIZAR REQUESTS HTTP -------------

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final OkHttpClient client = new OkHttpClient();

        // ----------- DATOS PARA REALIZAR PETICIÓN A LA API DE OPENAI ---------

        anadirRoleUser(pregunta);

        JSONArray conversacion = construirConversacion();

        JSONObject request = new JSONObject();

        try {
            request.put("model", "gpt-4o-mini");
            request.put("messages", conversacion);
            request.put("max_tokens", 400);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("respuestaJSON", request.toString());

        RequestBody peticion = RequestBody.create(
                MediaType.parse("application/json"), String.valueOf(request));


        Request requestOpenAI = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .post(peticion)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer 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")
                .build();

        try (Response response = client.newCall(requestOpenAI).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);
            String respuesta = response.body().string();
            Log.d("Response", "He recibido: " + respuesta);
            JSONObject res = new JSONObject(respuesta);
            JSONArray choices = res.getJSONArray("choices");
            Log.d("Response", "Choices se compone de " + choices);
            String res2 = null;
            for (int i = 0; i < choices.length(); i++) {
                try {
                    JSONObject m = choices.getJSONObject(i);
                    Log.d("Response", "Mensajes se compone de " + m);
                    // Pulling items from the array
                    res2 = m.getString("message");
                } catch (JSONException e) {
                    // Oops
                }
            }
            JSONObject mess = new JSONObject(res2);
            Log.d("Response", "Messages es " + mess);
            String r = mess.getString("content");
            Log.d("Response", "La respuesta es " + r);
            anadirRoleAssistant(r);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // Función que obtiene la respuesta de la consulta al Chat Completions de la API de OpenAI
    public String getRespuestaGPT(){
        return respuestaChatGPT;
    }

}