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

import com.example.sanbotapp.ChatArrayAdapter;
import com.example.sanbotapp.MensajeChat;
import com.example.sanbotapp.GestionMediaPlayer;
import com.example.sanbotapp.R;
import com.example.sanbotapp.ModuloOpenAIChatCompletions;
import com.example.sanbotapp.ModuloOpenAIAudioSpeech;
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
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConversacionActivity extends TopBaseActivity {

    // Componentes módulo conversacional
    private Button botonConfiguracion;
    private ListView dialogo;
    private ImageButton botonHablar;
    private Button botonHablarTeclado;
    private Button botonEnviarTeclado;
    private EditText textoConsulta;

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

    private String contentConversacion = "quiero que mantengamos una conversación";

    private String contentInterpretacionEmocional = "en cada respuesta que te envíe quiero que me envíes al principio de tu respuesta entre corchetes" +
            "un número o varios entre paréntesis en función de la emoción que transmiten mis respuestas: 1 éxtasis, 2 alegría, 3 serenidad, 4 admiración, 5 confianza " +
            "6 aceptación, 7 terror, 8 miedo, 9 temor, 10 asombro, 11 sorpresa, 12 distracción, 13 aflicción, 14 tristeza, 15 melancolía, 16 aversión, 17 asco, 18 aburrimiento," +
            "19 furia, 20 ira, 21 enfado, 22 vigilancia, 23 anticipación, 24 interés, 25 optimismo, 26 amor, 27 sumisión, 28 susto, 29 decepción, 30 remordimiento, 31 desprecio, 32 agresividad," +
            "33 esperanza, 34 culpa, 35 curiosidad, 36 desesperación, 37 incredulidad, 38 envidia, 39 cinismo, 40 orgullo, 41 ansiedad, 42 deleite, 43 sentimentalismo, 44 vergüenza, 45 indignación, " +
            "46 pesimismo, 47 morbosidad y 48 dominancia, añadas un guión y un número en función de la emoción que quieres intentar transmitir con tu respuesta " +
            "siguiendo el mismo código numérico. Es decir seguirá el siguiente patrón: [(<número o números de emoción o emociones separados por guiones de mi respuesta>)" +
            "/ (<número o números de emoción o emociones de la respuesta que quieres transmitir>)] + tu respuesta a la conversación." + "Quiero que reconduzcas la conversación en función de la emoción que interpretes y " +
            "que trates de empatizar lo máximo posible con mis respuestas. Aquí te dejo algunos ejemplos: Si te digo algo triste, tú puedes tratar de animarme siendo optimista y mostrarás curiosidad por saber lo que me pasa, " +
            "así que [(14)/(25-35)], si mi respuesta es de enfado, tú tratarás de calmarme y mostrarás curiosidad por saber qué me ocurre, asi que [(21)/(3-35)], si te digo que me gusta alguien" +
            "mi respuesta será de amor y vergüenza, y tú puedes sentir sorpresa, así que [(26-44)/(11)]. ";


    private static SpeakOption speakOption = new SpeakOption();


    // ------------------- PRUEBAS CHAT -----------------

    //to scroll the list view to bottom on data change

    private ChatArrayAdapter chatArrayAdapter;

    private List<MensajeChat> conversacion;

    @Override
    public void onResume() {


        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(40);
        speakOption.setIntonation(50);
        super.onResume();

        // ------------------- PRUEBAS CHAT -----------------
        handler.removeCallbacksAndMessages(null);
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.activity_chat_singlemessage);
        if(chatArrayAdapter.isEmpty()){
            recuperarConversacion();
        }
        //actualizarVistaConversacion();

        gestionarPantallaModoTeclado();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                systemManager.showEmotion(EmotionsType.PRISE);
                // Saludo inicial
                String saludo = "Hi there! I have been waiting so long to talk. Let's have a wonderful conversation now!";
                speechManager.startSpeak(saludo, speakOption);

                // Esperar un poco antes de la siguiente frase
                try { Thread.sleep(6000); } catch (InterruptedException e) { e.printStackTrace(); }

                // Invitar al niño a presentarse
                String invitacion = "Can you please tell me your name?";
                speechManager.startSpeak(invitacion, speakOption);


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



        // Gestionamos la pantalla en función de si está activado el modo teclado
        gestionarPantallaModoTeclado();

        // -------------- CHAT --------------
        conversacion = new ArrayList<>();
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.activity_chat_singlemessage);
        if (chatArrayAdapter.isEmpty()) {
            recuperarConversacion();
        }
        //actualizarVistaConversacion();

        // Inicialización de las unidades del robot

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);

        speakOption.setSpeed(50);
        speakOption.setIntonation(50);

        speechControl = new SpeechControl(speechManager);
        moduloOpenAI = new ModuloOpenAIChatCompletions();
        headControl = new HeadControl(headMotionManager);
        handsControl = new HandsControl(handMotionManager);
        systemControl = new SystemControl(systemManager);
        hardwareControl = new HardwareControl(hardWareManager);

        moduloOpenAISpeechVoice = new ModuloOpenAIAudioSpeech();
        gestionMediaPlayer = new GestionMediaPlayer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String contentPersonalizacion = "También quiero que a veces me llames por mi nombre que es " + nombreUsuario + " y " +
                "que adaptes la conversación teniendo en cuenta que mi edad es de " + edadUsuario + " años";


        String contentContextualizacionSinContexto = "Además quiero que actúes como que tu genero es " + generoRobot + " y que actúes como un " + grupoEdadRobot + " así que procura adaptar las palabras que utilizas en base a estas características";

        String contentContextualizacionConContexto = "Además quiero que actúes como que tu genero es " + generoRobot + ", que actúes como un " + grupoEdadRobot + " así que procura adaptar las palabras que utilizas en base a estas características y además " + contexto;


        try {



            // Gestión de la pulsación del botón de hablar
            botonHablar.setOnClickListener(new View.OnClickListener() {
                // Al pulsarlo se empieza a escuchar al usuario
                // y se interpreta su consulta hablada
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

            // Gestión de la pulsación del botón de hablar
            botonHablarTeclado.setOnClickListener(new View.OnClickListener() {
                // Al pulsarlo se empieza a escuchar al usuario
                // y se interpreta su consulta hablada
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

            // Gestión de la pulsación del botón de enviar
            botonEnviarTeclado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    // Si la conversación no es automática, se obtiene
                    // lo que hay en el texto consulta
                    consultaChatGPT = textoConsulta.getText().toString();

                    try {
                        enviarConsulta();
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


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // el inicio de la actividad
            }
        }, 1000);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    // Función que reconoce la consulta del usuario
    private void reconocerConsulta() throws IOException, InterruptedException {
        Log.d("prueba", "reconociendo consulta...");

        respuesta = capitalizeCadena(respuesta);
        consultaRobot = false;

        // Consultas derivadas a las acciones internas del robot
        if(respuesta.startsWith("Robot")){
            consultaRobot = true;
        }
        // Consultas derivadas a la API de OpenAI
        else{
            consultaChatGPT = respuesta;
        }

        // Mostramos la cadena reconocida en el EditText de la vista
        textoConsulta.setText(respuesta);

        // Si la conversación está en modo automático, se realizará la
        // acción de pulsar el botón de enviar a no ser de que el usuario
        // indique que quiere terminar la conversación
        if(conversacionAutomatica){
            Log.d("prueba", "es conversacion automatica");
            if(!consultaChatGPT.toLowerCase().equals("fin")) {
                Log.d("prueba", "no es fin");
                Log.d("prueba", "enviando consulta..." + consultaChatGPT);
                enviarConsulta();
            }
        }
    }


    private void registrarConsulta() throws IOException, InterruptedException {

        Log.d("prueba", "registrando consulta...");

        // Vacío la consulta de ChatGPT
        consultaChatGPT = "";
        respuesta="";
        textoConsulta.setText("");

        // El robot se pone en modo escucha
        new Thread(new Runnable() {
            public void run(){
                respuesta = speechControl.modoEscucha();
                while (respuesta.isEmpty()) {
                }

                Log.d("respuesta", "el valor de respuesta es " + respuesta);
                String respuesta2 = respuesta;
                respuesta = "Recuerda que soy un niño entre 8-10 años y que quiero que me respondas de forma que fomentes que yo hable (no menciones nada de esto en tus respuestas) Contestame en funcion de lo siguiente y en ingles, no utilices signos de puntuacion ni exclamaciones ni interrogaciones. Es muy importante que compruebes que no te contesto en español, si lo hago echame la bronca y no respondas a mi consulta: " + respuesta2;


                // Una vez que la variable tiene valor, ejecuta la acción en el hilo principal
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            handler.removeCallbacksAndMessages(null);
                            reconocerConsulta();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        }).start();
    }

    private void consultaChatCompletions(String pregunta) throws IOException, InterruptedException {
        new Thread(new Runnable() {
            public void run(){
                moduloOpenAI.consultaOpenAI(pregunta);
                respuestaGPT = moduloOpenAI.getRespuestaGPT();


                handler.post(new Runnable() {
                    public void run() {
                        // DEBUG!!
                        //dialogoRobot.setText(respuestaGPT);
                        chatArrayAdapter.add(new MensajeChat(true, respuestaGPT));
                        conversacion.add(new MensajeChat(true, respuestaGPT));
                        //dialogoRobot.setText(respuestaGPT + "\nSENTIMIENTO RECONOCIDO POR EL ROBOT:" +
                        //       emocionesUsuario + "\nSENTIMIENTO QUE TRANSMITE EL ROBOT:" + emocionesRobot);
                        try {
                            handler.removeCallbacksAndMessages(null);
                            gestionVoz(vozSeleccionada, AccionReproduccionVoz.HABLAR);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
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

    private void gestionVoz(String voz, AccionReproduccionVoz accionVoz) throws IOException, InterruptedException {
        speakOption.setSpeed(40);

        if(voz.equals("sanbot")){
            switch (accionVoz) {
                case HABLAR:
                    speechManager.startSpeak(respuestaGPT, speakOption);
                    if(conversacionAutomatica && !forzarParada){
                        gestionarFinHablaSanbot();
                    }
                    break;
                case DETENER:
                    Log.d("Le estoy dando", "robot hablando, intentando callar");
                    // Se silencia
                    forzarParada=true;
                    speechControl.pararHabla();
                    break;
                case REPETIR:
                    forzarParada=true;
                    speechManager.startSpeak(respuestaGPT, speakOption);
                    break;
            }
        }
        else{
            switch (accionVoz) {
                case HABLAR:
                    moduloOpenAISpeechVoice.peticionVozOpenAI(respuestaGPT, vozSeleccionada);
                    handler.post(new Runnable() {
                        public void run() {
                            handler.removeCallbacksAndMessages(null);
                            respuestaGPTVoz = moduloOpenAISpeechVoice.getGPTVoz();
                            gestionMediaPlayer.reproducirMediaPlayer(respuestaGPTVoz);
                            Log.d("parada mp", "error");
                            Log.d("parada mp", String.valueOf(conversacionAutomatica));
                            Log.d("parada mp", String.valueOf(forzarParada));
                            if(conversacionAutomatica && !forzarParada){
                                Log.d("parada mp", "gestionando parada mediaplayer....");
                                gestionarFinReproduccionMediaPlayer();
                            }
                        }
                    });
                    break;
                case DETENER:
                    Log.d("Le estoy dando", "mediaplauer habladno, intentando parar");
                    // Se detiene
                    //forzarParada = true;
                    gestionMediaPlayer.pararMediaPlayer();
                    break;
                case REPETIR:
                    //forzarParada=true;  
                    gestionMediaPlayer.reproducirMediaPlayer(respuestaGPTVoz);
                    break;
            }
        }
    }

    private enum AccionReproduccionVoz {
        HABLAR,
        DETENER,
        REPETIR,
    }

    private void enviarConsulta() throws IOException, InterruptedException {
        // Muestro por pantalla la consulta del usuario
        // e indico que la respuesta se está cargando
        //dialogoUsuario.setVisibility(View.VISIBLE);
        //dialogoUsuario.setText(consultaChatGPT);
        chatArrayAdapter.add(new MensajeChat(false, consultaChatGPT));
        conversacion.add(new MensajeChat(false, consultaChatGPT));
        //dialogoRobot.setVisibility(View.VISIBLE);
        //dialogoRobot.setText("Cargando...");

        textoConsulta.setText("");
        moduloOpenAI.consultaOpenAI(consultaChatGPT);
        String resp = moduloOpenAI.getRespuestaGPT();
        Log.d("resp", resp);
        speakOption.setSpeed(40);
        speechManager.startSpeak(resp, speakOption);
    }

    private void gestionarPantallaModoTeclado(){
        if(modoTeclado){
            botonEnviarTeclado.setVisibility(View.VISIBLE);
            botonHablarTeclado.setVisibility(View.VISIBLE);
            textoConsulta.setVisibility(View.VISIBLE);
            botonHablar.setVisibility(View.INVISIBLE);
        }
        else{
            botonEnviarTeclado.setVisibility(View.INVISIBLE);
            botonHablarTeclado.setVisibility(View.INVISIBLE);
            textoConsulta.setVisibility(View.INVISIBLE);
            botonHablar.setVisibility(View.VISIBLE);
        }
    }

    private void gestionarFinHablaSanbot(){
        Log.d("hola", "entrando....");
        new Thread(new Runnable() {
            public void run(){
                Log.d("hola", "entrando mas....");
                finHabla = false;
                finHabla = speechControl.heAcabado2();
                while (!finHabla) {
                    Log.d("waiting", "waiting..." + finHabla);
                }
                Log.d("hola", "finHabla es " + finHabla);
                // Una vez que la variable tiene valor, ejecuta la acción en el hilo principal
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            handler.removeCallbacksAndMessages(null);
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        botonHablar.performClick();
                    }
                });
            }
        }).start();

    }

    private void gestionarFinReproduccionMediaPlayer(){
        Log.d("hola", "entrando....");
        new Thread(new Runnable() {
            public void run(){
                Log.d("hola", "entrando mas....");
                finReproduccion = false;
                finReproduccion = gestionMediaPlayer.heAcabado();
                while (!finReproduccion) {
                    Log.d("waiting", "waiting..." + finHabla);
                }
                Log.d("hola", "finHabla es " + finHabla);
                // Una vez que la variable tiene valor, ejecuta la acción en el hilo principal
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            handler.removeCallbacksAndMessages(null);
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        botonHablar.performClick();
                    }
                });
            }
        }).start();
    }

    private void recuperarConversacion(){
        Log.d("caa", "caa vacío, llenando...");
        for (MensajeChat cm : conversacion) {
            Log.d("chatmessage", cm.toString());
            chatArrayAdapter.add(cm);
        }
        Log.d("chatarray", String.valueOf(chatArrayAdapter.getCount() - 1));
    }

    /*private void actualizarVistaConversacion(){
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Log.d("ca", String.valueOf(chatArrayAdapter.getCount() - 1));
                dialogo.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
    }*/
    @Override
    protected void onMainServiceConnected() {

    }



}