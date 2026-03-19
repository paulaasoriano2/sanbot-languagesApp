package com.example.sanbotapp;


import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanbotapp.robotControl.FaceRecognitionControl;
import com.example.sanbotapp.robotControl.HardwareControl;
import com.example.sanbotapp.robotControl.SpeechControl;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.EmotionsType;
import com.qihancloud.opensdk.function.beans.LED;
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.beans.handmotion.AbsoluteAngleHandMotion;
import com.qihancloud.opensdk.function.beans.headmotion.AbsoluteAngleHeadMotion;
import com.qihancloud.opensdk.function.beans.headmotion.RelativeAngleHeadMotion;
import com.qihancloud.opensdk.function.beans.wheelmotion.RelativeAngleWheelMotion;
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.HardWareManager;
import com.qihancloud.opensdk.function.unit.HeadMotionManager;
import com.qihancloud.opensdk.function.unit.MediaManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;
import com.qihancloud.opensdk.function.unit.WheelMotionManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class MainActivity extends TopBaseActivity {


    public Boolean reconocimientoFacial = false;
    private Button btnAsociacion;
    private Button btnAgenda;
    private Button btnColores;
    private Button btnConversacion;
    private Button btnEmociones;


    private ImageButton feliz;
    private ImageButton triste;
    private ImageButton enfadado;
    private ImageButton conver;
    private ImageButton emociones;

    private FaceRecognitionControl faceRecognitionControl;
    private SpeechManager speechManager;
    private MediaManager mediaManager;
    private SystemManager systemManager;
    private HandMotionManager handMotionManager;
    private WheelMotionManager wheelMotionManager;
    private HeadMotionManager headMotionManager;
    private HardWareManager hardwareManager;


    @Override
    protected void onMainServiceConnected() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        onMainServiceConnected();
        setContentView(R.layout.activity_main);

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        mediaManager = (MediaManager) getUnitManager(FuncConstant.MEDIA_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        hardwareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);


        faceRecognitionControl = new FaceRecognitionControl(speechManager, mediaManager);

        btnAsociacion = findViewById(R.id.asociacionimagenpalabra);
        btnAgenda = findViewById(R.id.agenda);
        btnColores = findViewById(R.id.colores);
        btnConversacion = findViewById(R.id.conversacion);
        btnEmociones = findViewById(R.id.emociones);


        feliz = findViewById(R.id.imgasociacionimagenpalabra);
        triste = findViewById(R.id.imgagenda);
        enfadado = findViewById(R.id.imgcolores);
        conver = findViewById(R.id.imgconversacion);
        emociones = findViewById(R.id.emocionesimg);


        faceRecognitionControl.stopFaceRecognition();

        setonClicks();
    }

    public void setAllButtonsClickable(boolean clickable) {
        btnAgenda.setClickable(clickable);
        btnColores.setClickable(clickable);
        btnAsociacion.setClickable(clickable);
        btnConversacion.setClickable(clickable);
        btnEmociones.setClickable(clickable);

        triste.setClickable(clickable);
        enfadado.setClickable(clickable);
        feliz.setClickable(clickable);
        conver.setClickable(clickable);
        emociones.setClickable(clickable);
    }

    public void setonClicks() {

        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(50);
        speakOption.setIntonation(50);


        btnAsociacion.setOnClickListener(new View.OnClickListener() {
            private boolean isProcessing = false; // Bandera para evitar múltiples clics

            @Override
            public void onClick(View v) {

                // Desactivar todos los botones

                /*new Thread(() -> {
                    try {
                        // Simula un pequeño retraso inicial
                        Thread.sleep(100);

                        // Mostrar emoción y encender LEDs
                        systemManager.showEmotion(EmotionsType.SMILE);
                        hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_YELLOW));

                        // Generar frases aleatorias
                        /*String[] frases = {
                                "Hoy estoy muy feliz, ¡Gracias por jugar conmigo!",
                                "Estoy contenta de que estés aquí",
                                "Estoy muy feliz de verte, espero que tú también lo estés"
                        };
                        Random rand = new Random();
                        int randomIndex = rand.nextInt(frases.length);
                        speechManager.startSpeak(frases[randomIndex], speakOption);

                        String frase = "Vamos a jugar al juego asociación imagen palabra." +
                                "Yo te diré una palabra en inglés y tú tendrás que seleccionar la imagen asociada. ¿Empezamos?";
                        speechManager.startSpeak(frase, speakOption);

                        // Simula la duración de la frase
                        Thread.sleep(5000);

                        // Apagar luces
                        hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_CLOSE));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        // Reactivar todos los botones
                        runOnUiThread(() -> {
                            setAllButtonsClickable(true);
                            isProcessing = false; // Liberar bandera
                        });
                    }
                }).start();*/

                Intent intent = new Intent(MainActivity.this, AsociacionimagenPalabraMainActivity.class);
                startActivity(intent);
            }

        });


        feliz.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Desactivar todos los botones

                Intent intent = new Intent(MainActivity.this, AsociacionimagenPalabraMainActivity.class);
                startActivity(intent);
            }

        });



        btnConversacion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Desactivar todos los botones

                /*new Thread(() -> {
                    try {
                        // Simula un pequeño retraso inicial
                        Thread.sleep(100);

                        // Mostrar emoción y encender LEDs
                        systemManager.showEmotion(EmotionsType.SMILE);
                        hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_YELLOW));

                        // Generar frases aleatorias
                        /*String[] frases = {
                                "Hoy estoy muy feliz, ¡Gracias por jugar conmigo!",
                                "Estoy contenta de que estés aquí",
                                "Estoy muy feliz de verte, espero que tú también lo estés"
                        };
                        Random rand = new Random();
                        int randomIndex = rand.nextInt(frases.length);
                        speechManager.startSpeak(frases[randomIndex], speakOption);

                        String frase = "Vamos a jugar al juego asociación imagen palabra." +
                                "Yo te diré una palabra en inglés y tú tendrás que seleccionar la imagen asociada. ¿Empezamos?";
                        speechManager.startSpeak(frase, speakOption);

                        // Simula la duración de la frase
                        Thread.sleep(5000);

                        // Apagar luces
                        hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_CLOSE));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        // Reactivar todos los botones
                        runOnUiThread(() -> {
                            setAllButtonsClickable(true);
                            isProcessing = false; // Liberar bandera
                        });
                    }
                }).start();*/

                Intent intent = new Intent(MainActivity.this, ConversacionActivity.class);
                startActivity(intent);
            }

        });


        conver.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Desactivar todos los botones

                Intent intent = new Intent(MainActivity.this, ConversacionActivity.class);
                startActivity(intent);
            }

        });

        btnAgenda.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Desactivar todos los botones
                setAllButtonsClickable(false);

                Intent intent = new Intent(MainActivity.this, AgendaActivity.class);
                startActivity(intent);

            }
        });

        triste.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Desactivar todos los botones

                Intent intent = new Intent(MainActivity.this, AgendaActivity.class);
                startActivity(intent);
            }
        });

        btnColores.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Desactivar todos los botones
                setAllButtonsClickable(false);

                Intent intent = new Intent(MainActivity.this, ColoresActivity.class);
                startActivity(intent);
            }
        });

        enfadado.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Desactivar todos los botones


                Intent intent = new Intent(MainActivity.this, ColoresActivity.class);
                startActivity(intent);
            }
        });

        btnEmociones.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                faceRecognitionControl.stopFaceRecognition();
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.example.emocionesIngles", "com.example.sanbotapp.MainActivity"));
                startActivity(intent);
            }

        });

        emociones.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                faceRecognitionControl.stopFaceRecognition();
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.example.emocionesIngles", "com.example.sanbotapp.MainActivity"));
                startActivity(intent);
            }

        });



    }

    @Override
    public void onResume() {
        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(50);
        speakOption.setIntonation(50);
        super.onResume();
        // Inicializamos el sistema
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                systemManager.showEmotion(EmotionsType.SMILE);
                AbsoluteAngleHandMotion absoluteAngleHandMotion =
                        new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH,20,0);
                handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                speechManager.startSpeak("Hi! Ready for a word adventure? Switching to super-learning mode!", speakOption);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                absoluteAngleHandMotion =
                        new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH,20,180);
                handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);


            }
        }, 200);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }


}
