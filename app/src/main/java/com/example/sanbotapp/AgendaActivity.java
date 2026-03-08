package com.example.sanbotapp;


import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.sanbotapp.robotControl.FaceRecognitionControl;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.EmotionsType;
import com.qihancloud.opensdk.function.beans.LED;
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.beans.handmotion.AbsoluteAngleHandMotion;
import com.qihancloud.opensdk.function.beans.headmotion.AbsoluteAngleHeadMotion;
import com.qihancloud.opensdk.function.beans.wheelmotion.RelativeAngleWheelMotion;
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.HardWareManager;
import com.qihancloud.opensdk.function.unit.HeadMotionManager;
import com.qihancloud.opensdk.function.unit.MediaManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;
import com.qihancloud.opensdk.function.unit.WheelMotionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Random;

public class AgendaActivity extends TopBaseActivity {


    public Boolean reconocimientoFacial = false;
    private Button btnAsociacion;
    private Button btnAgenda;
    private Button btnColores;

    private ImageButton feliz;
    private ImageButton triste;
    private ImageButton enfadado;
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
        setContentView(R.layout.activity_agenda);

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        mediaManager = (MediaManager) getUnitManager(FuncConstant.MEDIA_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        hardwareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);


        faceRecognitionControl = new FaceRecognitionControl(speechManager, mediaManager);


        feliz = findViewById(R.id.imgasociacionimagenpalabra);
        triste = findViewById(R.id.imgagenda);
        enfadado = findViewById(R.id.imgcolores);

        faceRecognitionControl.stopFaceRecognition();

        setonClicks();
    }

    public void setAllButtonsClickable(boolean clickable) {
        btnAgenda.setClickable(clickable);
        btnColores.setClickable(clickable);
        btnAsociacion.setClickable(clickable);

        triste.setClickable(clickable);
        enfadado.setClickable(clickable);
        feliz.setClickable(clickable);
    }

    public void setonClicks() {

        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(50);
        speakOption.setIntonation(50);



        feliz.setOnClickListener(new View.OnClickListener() {
            private boolean isProcessing = false; // Bandera para evitar múltiples clics

            @Override
            public void onClick(View v) {
                if (isProcessing) return; // Si ya está procesando, ignorar el clic
                isProcessing = true;

                // Desactivar todos los botones
                setAllButtonsClickable(false);

                new Thread(() -> {
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
                        speechManager.startSpeak(frases[randomIndex], speakOption);*/



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
                }).start();
            }

        });



        triste.setOnClickListener(new View.OnClickListener() {
            private boolean isProcessing = false; // Bandera para evitar múltiples clics

            @Override
            public void onClick(View v) {
                if (isProcessing) return; // Si ya está procesando, ignorar el clic
                isProcessing = true;

                // Desactivar todos los botones
                setAllButtonsClickable(false);

                new Thread(() -> {
                    try {
                        // Simula un pequeño retraso inicial
                        Thread.sleep(100);

                        systemManager.showEmotion(EmotionsType.CRY);
                        hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_BLUE));

                        // saca frases aleatorias de tristeza rollo esto me pone triste, me siento triste, etc
                        String[] frases = {"A veces me pongo muy triste, y no puedo parar de llorar", "Cuando me pongo trise, no puedo ocultarlo", "Me siento muy triste, no se como parar de llorar"};
                        Random rand = new Random();
                        int randomIndex = rand.nextInt(frases.length);
                        speechManager.startSpeak(frases[randomIndex], speakOption);

                        AbsoluteAngleHeadMotion absoluteAngleHeadMotion =
                                new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL,7);
                        headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);

                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // apagar luces
                        hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_CLOSE));
                        headMotionManager.doAbsoluteAngleMotion(new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL,30));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        // Reactivar todos los botones
                        runOnUiThread(() -> {
                            setAllButtonsClickable(true);
                            isProcessing = false; // Liberar bandera
                        });
                    }
                }).start();

            }
        });

        enfadado.setOnClickListener(new View.OnClickListener() {
            private boolean isProcessing = false; // Bandera para evitar múltiples clics

            @Override
            public void onClick(View v) {
                if (isProcessing) return; // Si ya está procesando, ignorar el clic
                isProcessing = true;

                // Desactivar todos los botones
                setAllButtonsClickable(false);

                new Thread(() -> {
                    try {
                        // Simula un pequeño retraso inicial
                        Thread.sleep(100);

                        systemManager.showEmotion(EmotionsType.ANGRY);
                        hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_RED));

                        // saca frases aleatorias rollo cuando estoy enfadado me pongo muy nervioso, me enfada mucho, etc
                        String[] frases = {"Cuando estoy enfadada me pongo muy nerviosa", "No me gusta estar enfadada, pero a veces no puedo evitarlo", "No puedo evitar enfadarme cuando sucede una injusticia"};
                        Random rand = new Random();
                        int randomIndex = rand.nextInt(frases.length);
                        speechManager.startSpeak(frases[randomIndex], speakOption);

                        AbsoluteAngleHandMotion absoluteAngleHandMotion =
                                new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH,20,0);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        absoluteAngleHandMotion =
                                new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH,20,180);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                        // apagar luces
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
                }).start();

            }
        });














    }

    public void getPictogramas() throws IOException, JSONException {
        URL url = new URL("https://api.arasaac.org/v1/pictograms/es/search/water");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
        );

        StringBuilder result = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            result.append(line);
        }

        reader.close();

        String json = result.toString();

        JSONArray array = new JSONArray(json);
        JSONObject pictogram = array.getJSONObject(0);

        int id = pictogram.getInt("_id");

        String imageUrl = "https://static.arasaac.org/pictograms/"
                + id + "/" + id + "_500.png";

        /*Glide.with(this)
                .load(imageUrl)
                .into(imageView);*/

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


                String[] frases = {"¿Y si recordamos lo que hiciste ayer? Me da mucha curiosidad. Haz clic sobre las acciones que hiciste", "¡Vamos a planear tu día! Haz clic sobre las acciones que quieres hacer hoy"};
                Random rand = new Random();
                int randomIndex = rand.nextInt(frases.length);
                speechManager.startSpeak(frases[randomIndex], speakOption);

                try {
                    getPictogramas();
                } catch (IOException | JSONException e) {
                    throw new RuntimeException(e);
                }

                RelativeAngleWheelMotion movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_LEFT, 5, 360);
                wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                AbsoluteAngleHeadMotion absoluteAngleHeadMotion =
                        new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL,7);
                headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
