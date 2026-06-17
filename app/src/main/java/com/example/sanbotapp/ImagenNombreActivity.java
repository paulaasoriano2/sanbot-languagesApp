package com.example.sanbotapp;


import static com.qihancloud.opensdk.function.beans.SpeakOption.LAG_ENGLISH_US;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.qihancloud.opensdk.function.beans.speech.Grammar;
import com.qihancloud.opensdk.function.beans.wheelmotion.RelativeAngleWheelMotion;
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.HardWareManager;
import com.qihancloud.opensdk.function.unit.HeadMotionManager;
import com.qihancloud.opensdk.function.unit.MediaManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;
import com.qihancloud.opensdk.function.unit.WheelMotionManager;
import com.qihancloud.opensdk.function.unit.interfaces.speech.RecognizeListener;
import com.qihancloud.opensdk.function.unit.interfaces.speech.SpeechListener;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class ImagenNombreActivity extends TopBaseActivity {


    public Boolean reconocimientoFacial = false;
    private Button btnImagen;
    Button btnSkip;

    private ImageButton imagen;

    private FaceRecognitionControl faceRecognitionControl;
    private SpeechManager speechManager;
    private MediaManager mediaManager;
    private SystemManager systemManager;
    private HandMotionManager handMotionManager;
    private WheelMotionManager wheelMotionManager;
    private HeadMotionManager headMotionManager;
    private HardWareManager hardwareManager;
    private SpeechControl speechControl;

    private Button sayitagain, skip;
    private ImageButton btnBack;
    private ImageButton btnHelp;
    private TextView textoDialogo;
    private TextView helpText;
    private LinearLayout loadingBox;
    private TextView title;

    int indiceActual = 0;
    private Boolean dejarDeJugar;
    List<String> palabras = new ArrayList<>();
    private String palabraActual;
    private int aciertosSeguidos;

    @Override
    protected void onMainServiceConnected() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        onMainServiceConnected();
        setContentView(R.layout.activity_imagennombre);
        dejarDeJugar = false;
        aciertosSeguidos = 0;
        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        mediaManager = (MediaManager) getUnitManager(FuncConstant.MEDIA_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        hardwareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);

        speechControl = new SpeechControl(speechManager);

        faceRecognitionControl = new FaceRecognitionControl(speechManager, mediaManager);

        btnImagen = findViewById(R.id.btnImagen);

        imagen = findViewById(R.id.imagen);
        btnSkip = findViewById(R.id.btnSkip);
        btnBack = findViewById(R.id.btnBack);
        title = findViewById(R.id.title);
        loadingBox = findViewById(R.id.loadingBox);
        sayitagain = findViewById(R.id.sayitagain);
        btnHelp = findViewById(R.id.btnHelp);
        skip = findViewById(R.id.skip);
        textoDialogo = findViewById(R.id.instruction);
        helpText = findViewById(R.id.helpText);

        faceRecognitionControl.stopFaceRecognition();

        VocabularioDbAdapter db = getVocabularioDbAdapter();

        Cursor cursor = db.fetchAllVocabulario();

        if (cursor.moveToFirst()) {
            do {

                String nombre = cursor.getString(3);

                if(nombre != null && !nombre.trim().isEmpty()){
                    palabras.add(nombre.toLowerCase());
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        if (!palabras.isEmpty()) {
            indiceActual = 0;
            palabraActual = palabras.get(0);
            actualizarImagen();
        }

        setonClicks();

        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(40);
        speakOption.setIntonation(50);




        speechManager.setOnSpeechListener(new RecognizeListener() {

            @Override
            public boolean onRecognizeResult(Grammar grammar) {

                // 4. Validar
                if (grammar == null || grammar.getText() == null || palabraActual == null) {
                    return false;
                }

                String said = grammar.getText().toLowerCase().trim();
                String target = palabraActual.toLowerCase().trim();

                boolean correcto = said.contains(target);

                runOnUiThread(() -> {
                    if (correcto) {

                        aciertosSeguidos++;

                        if(aciertosSeguidos == 2){
                            aciertosSeguidos = 0;

                            systemManager.showEmotion(EmotionsType.PRISE);
                            hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_RANDOM));

                            String[] frases = {
                                    "WOW! You are super intelligent",
                                    "Well done! You are the best!",
                            };
                            Random rand = new Random();
                            int randomIndex = rand.nextInt(frases.length);
                            speechManager.startSpeak(frases[randomIndex], speakOption);

                            // mover brazos, izquierdo para arriba derecho para abajo
                            AbsoluteAngleHandMotion absoluteAngleHandMotion =
                                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_LEFT,20,180);
                            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

                            absoluteAngleHandMotion =
                                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_RIGHT,20,20);
                            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            RelativeAngleWheelMotion movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_LEFT, 5, 360);
                            wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);

                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            // mover brazos, izquierdo para abajo derecho para arriba
                            absoluteAngleHandMotion =
                                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_LEFT,20,20);
                            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

                            absoluteAngleHandMotion =
                                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_RIGHT,20,180);
                            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        }
                        else{
                            AtomicReference<AbsoluteAngleHandMotion> absoluteAngleHandMotion =
                                    new AtomicReference<>(new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH, 20, 0));
                            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion.get());

                            String[] frases = {
                                    "Ta-da! Your intelligence shines like an LED.",
                                    "Well done! Your brain is in linguist mode.",
                                    "Amazing! Your effort is paying off."
                            };
                            Random rand = new Random();
                            int randomIndex = rand.nextInt(frases.length);
                            speechManager.startSpeak(frases[randomIndex], speakOption);

                            systemManager.showEmotion(EmotionsType.PRISE);
                            hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_GREEN));
                        }


                        mostrarDialogoAcierto();
                    } else {
                        /*speechManager.startSpeak("Try again!", speakOption);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        systemManager.showEmotion(EmotionsType.QUESTION);
                        hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_YELLOW));

                        AbsoluteAngleHandMotion absoluteAngleHandMotion =
                                new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_RIGHT,20,0);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);


                        String[] frases = {"Hey! Want a hint?", "Here comes a clue!",
                                "Let's help you!"};
                        Random rand = new Random();
                        int randomIndex = rand.nextInt(frases.length);
                        speechManager.startSpeak(frases[randomIndex], speakOption);

                        AbsoluteAngleHeadMotion absoluteAngleHeadMotion =
                                new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL,7);
                        headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);

                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        speechManager.startSpeak("Repeat after me", speakOption);
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        speechManager.startSpeak(palabras.get(indiceActual), speakOption);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }



                        absoluteAngleHandMotion =
                                new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_RIGHT,20,180);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_CLOSE));
                        headMotionManager.doAbsoluteAngleMotion(new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL,30));*/

                        aciertosSeguidos = 0;
                        mostrarDialogoPista();

                    }


                });

                return true;
            }

            @Override
            public void onRecognizeVolume(int i) {

            }

        });
    }

    private VocabularioDbAdapter getVocabularioDbAdapter() {
        VocabularioDbAdapter db = new VocabularioDbAdapter(this);
        db.open();
        return db;
    }

    private void mostrarDialogoPista() {

        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(50);
        speakOption.setIntonation(50);

        systemManager.showEmotion(EmotionsType.QUESTION);
        hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_YELLOW));

        AbsoluteAngleHandMotion arm =
                new AbsoluteAngleHandMotion(
                        AbsoluteAngleHandMotion.PART_RIGHT,
                        20,
                        0);

        handMotionManager.doAbsoluteAngleMotion(arm);

        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(this);

        View dialogView =
                getLayoutInflater().inflate(R.layout.dialog_pista, null);

        builder.setView(dialogView);

        androidx.appcompat.app.AlertDialog dialog =
                builder.create();

        dialog.show();

        speechManager.startSpeak(
                "Try again. Here comes a clue.",
                speakOption
        );

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            speechManager.startSpeak(
                    "Repeat after me.",
                    speakOption
            );
            new Handler(Looper.getMainLooper()).postDelayed(() -> {

                speechManager.startSpeak(
                        palabraActual,
                        speakOption
                );

            },2000);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {

                if(dialog.isShowing()){
                    dialog.dismiss();
                }

                resetRobotAfterFailure();

            },1000);

        },3500);
    }

    private void resetRobotAfterFailure() {

        hardwareManager.setLED(
                new LED(LED.PART_ALL, LED.MODE_CLOSE)
        );

        AbsoluteAngleHandMotion armsDown =
                new AbsoluteAngleHandMotion(
                        AbsoluteAngleHandMotion.PART_RIGHT,
                        20,
                        180
                );

        handMotionManager.doAbsoluteAngleMotion(armsDown);

        AbsoluteAngleHeadMotion headUp =
                new AbsoluteAngleHeadMotion(
                        AbsoluteAngleHeadMotion.ACTION_VERTICAL,
                        30
                );

        headMotionManager.doAbsoluteAngleMotion(headUp);
    }

    private void mostrarDialogoAcierto() {

        View view = getLayoutInflater().inflate(R.layout.dialog_feedbackasociacion, null);

        TextView respuestaCorrectaDialog =
                view.findViewById(R.id.textoRespuesta);

        ImageView imgDialog =
                view.findViewById(R.id.imgRespuesta);

        String nombreImagen = palabraActual;

        if(nombreImagen.equalsIgnoreCase("t-shirt")){
            nombreImagen = "tshirt";
        }
        // Obtener el drawable dinámicamente
        int resId = getResources().getIdentifier(
                nombreImagen,
                "drawable",
                getPackageName()
        );

        // Poner texto e imagen
        respuestaCorrectaDialog.setText(nombreImagen.toUpperCase());
        imgDialog.setImageResource(resId);


        Button btnYes = view.findViewById(R.id.btnAccept);
        Button btnNo = view.findViewById(R.id.btnCancel);

        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create();

        btnYes.setOnClickListener(v -> {
            hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_CLOSE));
            AbsoluteAngleHandMotion absoluteAngleHandMotion =
                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH,20,180);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
            dialog.dismiss();
            siguientePalabra();
        });

        btnNo.setOnClickListener(v -> {
            hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_CLOSE));
            dialog.dismiss();
            dejarDeJugar = true;
            finJuego();
            finish();
        });

        dialog.show();
    }

    private void siguientePalabra() {
        indiceActual++;

        if (indiceActual >= palabras.size()) {
            indiceActual = 0;
            finJuego();
            return;
        }

        actualizarImagen();
    }
    public void setonClicks() {



        btnImagen.setOnClickListener(v -> {

            new Thread(() -> {

                speechManager.doWakeUp();

            }).start();
        });

        btnSkip.setOnClickListener(v -> {

            indiceActual++;

            if (indiceActual >= palabras.size()) {
                indiceActual = 0;
                finJuego();
                return;
            }

            actualizarImagen();
        });

        btnBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnHelp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                textoDialogo.setText("Di la palabra que se corresponde con la imagen que ves.");
                sayitagain.setVisibility(View.GONE);
                btnHelp.setVisibility(View.GONE);
                helpText.setVisibility(View.GONE);


            }
        });

        skip.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SpeakOption speakOption = new SpeakOption();
                speakOption.setSpeed(50);
                speakOption.setIntonation(50);

                /*// Ocultar diálogo azul
                loadingBox.setVisibility(View.GONE);

                // Mostrar imágenes
                for (ImageButton img : imagenes) {
                    img.setVisibility(View.VISIBLE);
                }

                // Mostrar título
                titulo.setVisibility(View.VISIBLE);

                // Iniciar juego
                actualizarImagen();
                actualizarTitulo();

                // Desactivar botón skip después
                skip.setClickable(false);*/
                btnHelp.setVisibility(View.GONE);
                helpText.setVisibility(View.GONE);
                sayitagain.setVisibility(View.GONE);
                skip.setVisibility(View.GONE);
                title = findViewById(R.id.title);
                imagen = findViewById(R.id.imagen);
                btnImagen = findViewById(R.id.btnImagen);
                btnSkip = findViewById(R.id.btnSkip);

                // Hacer visibles
                title.setVisibility(View.VISIBLE);
                imagen.setVisibility(View.VISIBLE);
                btnImagen.setVisibility(View.VISIBLE);
                btnSkip.setVisibility(View.VISIBLE);
                loadingBox.setVisibility(View.GONE);


            }
        });

        sayitagain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SpeakOption speakOption = new SpeakOption();
                speakOption.setSpeed(50);
                speakOption.setIntonation(50);

                speechManager.startSpeak("Say the word which corresponds to the image you see. Tap the button HELP if you don't understand it.", speakOption);


            }
        });
    }

    private void actualizarImagen() {

        if (palabras.isEmpty()) return;

        palabraActual = palabras.get(indiceActual);

        String nombreImagen = palabraActual;

        if(nombreImagen.equalsIgnoreCase("t-shirt")){
            nombreImagen = "tshirt";
        }

        int resId = getResources().getIdentifier(
                nombreImagen,
                "drawable",
                getPackageName()
        );

        imagen.setImageResource(resId);
    }

    private void finJuego() {

        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(40);
        speakOption.setIntonation(50);

        if(dejarDeJugar){
            speechManager.startSpeak("Fine! Let's continue playing later!", speakOption);

        }
        else{
            speechManager.startSpeak("Amazing! You finished all the words!", speakOption);

        }

        systemManager.showEmotion(EmotionsType.SMILE);
        hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_BLUE));

        AbsoluteAngleHandMotion motion =
                new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH,20,0);
        handMotionManager.doAbsoluteAngleMotion(motion);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        motion =
                new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH,20,180);
        handMotionManager.doAbsoluteAngleMotion(motion);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_CLOSE));

        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.example.languages", "com.example.sanbotapp.MainActivity"));
        startActivity(intent);
        finish();

    }



    @Override
    public void onResume() {

        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(40);
        speakOption.setIntonation(50);


        super.onResume();
        // Inicializamos el sistema
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                speechManager.startSpeak("Say the word which corresponds to the image you see. Tap the button HELP if you don't understand it.", speakOption );

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


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

        return super.onOptionsItemSelected(item);
    }


}
