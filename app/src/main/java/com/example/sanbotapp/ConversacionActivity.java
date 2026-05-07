package com.example.sanbotapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanbotapp.ChatArrayAdapter;
import com.example.sanbotapp.MensajeChat;
import com.example.sanbotapp.GestionMediaPlayer;
import com.example.sanbotapp.R;
import com.example.sanbotapp.ModuloOpenAIChatCompletions;
import com.example.sanbotapp.ModuloOpenAIAudioSpeech;
import com.example.sanbotapp.robotControl.FaceRecognitionControl;
import com.example.sanbotapp.robotControl.HandsControl;
import com.example.sanbotapp.robotControl.HardwareControl;
import com.example.sanbotapp.robotControl.HeadControl;
import com.example.sanbotapp.robotControl.SpeechControl;
import com.example.sanbotapp.robotControl.SystemControl;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.EmotionsType;
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.beans.handmotion.AbsoluteAngleHandMotion;
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.HardWareManager;
import com.qihancloud.opensdk.function.unit.HeadMotionManager;
import com.qihancloud.opensdk.function.unit.MediaManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConversacionActivity extends TopBaseActivity {

    // Componentes módulo conversacional
    private Button botonConfiguracion;
    private RecyclerView dialogo;
    private ImageButton botonHablar;
    private Button botonHablarTeclado;
    private Button botonEnviarTeclado;
    private TextView textoConsulta;

    // Modulos del robot
    private SpeechManager speechManager;
    private HeadMotionManager headMotionManager;
    private HandMotionManager handMotionManager;
    private SystemManager systemManager;
    private HardWareManager hardWareManager;

    // Modulos del programa
    private SpeechControl speechControl;
    private ModuloOpenAIChatCompletions moduloOpenAI;
    private ModuloOpenAIAudioSpeech moduloOpenAISpeechVoice;
    private GestionMediaPlayer gestionMediaPlayer;
    private HandsControl handsControl;
    private HeadControl headControl;
    private SystemControl systemControl;
    private HardwareControl hardwareControl;

    // Gestión MediaPlayer
    private MediaPlayer mediaPlayer = new MediaPlayer();

    private Handler handler = new Handler(Looper.getMainLooper());

    private boolean finHabla = false;
    private boolean finReproduccion = false;

    // Variables usadas en el modulo

    private String respuesta;
    private String respuesta2;
    private String consultaChatGPT; // Consulta realizada por el usuario
    private String respuestaGPT; // Respuesta dada a la consulta realizada por el usuario
    private static byte[] respuestaGPTVoz;
    private String vozSeleccionada; // Voz seleccionada por el usuario
    private String nombreUsuario; // Nombre del usuario
    private int edadUsuario; // Edad del usuario
    private String generoRobot;
    private String grupoEdadRobot;
    private String contexto;
    private boolean consultaRobot = false; // Variable para consultas internas del robot
    private boolean consultaPeliculas = false; // Variable para consultas API de películas
    private boolean conversacionAutomatica = true; // Variable para indicar si la conversación está en modo automático
    private boolean modoTeclado; // Variable para indicar si la conversación está en modo automático
    private boolean contextoVacio = true;


    // Lista de variables necesarias para el envío de requests en la API de ChatGPT

    private boolean forzarParada = false;


    private static SpeakOption speakOption = new SpeakOption();

    private ChatArrayAdapter chatArrayAdapter;

    private List<MensajeChat> conversacion;
    private FaceRecognitionControl faceRecognitionControl;
    private MediaManager mediaManager;


    @Override
    public void onResume() {


        SpeakOption speakOption = new SpeakOption();
        //speakOption.setSpeed(400);
        speakOption.setIntonation(50);
        super.onResume();

        // ------------------- PRUEBAS CHAT -----------------
        handler.removeCallbacksAndMessages(null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                systemManager.showEmotion(EmotionsType.PRISE);
                // Saludo inicial
                String saludo = "Hi there! I have been waiting so long to talk. Let's have a wonderful conversation now!";
                //speechManager.startSpeak(saludo, speakOption);
                speechControl.hablar(saludo);
                chatArrayAdapter.add(new MensajeChat(true, saludo));


                // Esperar un poco antes de la siguiente frase
                try { Thread.sleep(6000); } catch (InterruptedException e) { e.printStackTrace(); }

                // Invitar al niño a presentarse
                String invitacion = "How are you today?";
                //speechManager.startSpeak(invitacion, speakOption);
                //speechControl.hablar(invitacion);
                chatArrayAdapter.add(new MensajeChat(true, invitacion));
                try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); } // si se activa el reconocimiento facial




            }
        }, 200);


    }



    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Configuración de la aplicación
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        // Establecer pantalla
        setContentView(R.layout.activity_modulo_conversacional_dos);

        // Instanciación de componentes
        botonHablar = findViewById(R.id.botonHablar);
        textoConsulta = findViewById(R.id.text_gchat_indicator);
        dialogo = findViewById(R.id.recycler_gchat);

        chatArrayAdapter = new ChatArrayAdapter();

        dialogo = findViewById(R.id.recycler_gchat);
        dialogo.setLayoutManager(new LinearLayoutManager(this));
        dialogo.setAdapter(chatArrayAdapter);

        // -------------- CHAT --------------
        conversacion = new ArrayList<>();

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
        mediaManager = (MediaManager) getUnitManager(FuncConstant.MEDIA_MANAGER);


        speakOption.setIntonation(50);

        speechControl = new SpeechControl(speechManager);
        moduloOpenAI = new ModuloOpenAIChatCompletions();
        headControl = new HeadControl(headMotionManager);
        handsControl = new HandsControl(handMotionManager);
        systemControl = new SystemControl(systemManager);
        hardwareControl = new HardwareControl(hardWareManager);

        speechControl.setVelocidadHabla(40);

        moduloOpenAISpeechVoice = new ModuloOpenAIAudioSpeech();
        gestionMediaPlayer = new GestionMediaPlayer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        try {

            botonHablar.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        registrarConsulta();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        faceRecognitionControl = new FaceRecognitionControl(speechManager, mediaManager);

        faceRecognitionControl.startFaceRecognition();

        // Parar después de 10 segundos (10000 ms)
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                faceRecognitionControl.stopFaceRecognition();
            }
        }, 2000);


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    // Consulta del usuario
    private void reconocerConsulta() throws IOException, InterruptedException {
        Log.d("prueba", "reconociendo consulta...");

        respuesta = capitalizeCadena(respuesta);

        // La consulta que se va a enviar a la API de OpenAI
        consultaChatGPT = respuesta2;

        // Mostrar la consulta del usuario
        chatArrayAdapter.add(new MensajeChat(false, respuesta));
        dialogo.scrollToPosition(chatArrayAdapter.getItemCount() - 1);
        
        // Enviar la consulta a la API de OpenAI
        enviarConsulta();

    }


    private void registrarConsulta() throws IOException, InterruptedException {

        // Vacío la consulta de ChatGPT
        consultaChatGPT = "";
        respuesta="";
        respuesta2="";
        textoConsulta.setText("");

        // El robot se pone en modo escucha
        new Thread(new Runnable() {
            public void run(){
                respuesta = speechControl.modoEscucha();
                while (respuesta.isEmpty()) {
                }

                Log.d("respuesta", "el valor de respuesta es " + respuesta);
                respuesta2 = "Recuerda que soy un niño entre 8-10 años y que quiero que me respondas de forma que fomentes que yo hable (no menciones nada de esto en tus respuestas) Contestame en funcion de lo siguiente y en ingles, no utilices signos de puntuacion ni exclamaciones ni interrogaciones. Es muy importante que compruebes que no te contesto en español, si lo hago echame la bronca y no respondas a mi consulta. Importante, responde a mi consulta de forma breve y termina siempre preguntandome algo: " + respuesta;


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            handler.removeCallbacksAndMessages(null);
                            reconocerConsulta();
                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        }).start();
    }


    private String capitalizeCadena(String c){
        Log.d("capitalize", "tratando de capitalizar " + c);
        if (c.length() > 0 || c!=null || !c.isEmpty()) return c.substring(0,1).toUpperCase() + c.substring(1);
        else return "";
    }

    // Enviar consulta a la API de Open AI y mostrar su respuesta
    private void enviarConsulta() {

        // Enviar consulta a Open AI
        moduloOpenAI.consultaOpenAI(consultaChatGPT);

        // Respuesta de Open AI
        String resp = moduloOpenAI.getRespuestaGPT();
        Log.d("resp", resp);
        speechControl.hablar(resp);

        // Mostrar respuesta de Open AI
        chatArrayAdapter.add(new MensajeChat(true, resp + "?"));
        dialogo.scrollToPosition(chatArrayAdapter.getItemCount() - 1); // Para que la vista se posicione al final

    }

    @Override
    protected void onMainServiceConnected() {

    }



}