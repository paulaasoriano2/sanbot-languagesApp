package com.example.sanbotapp;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sanbotapp.robotControl.FaceRecognitionControl;
import com.example.sanbotapp.robotControl.SpeechControl;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.EmotionsType;
import com.qihancloud.opensdk.function.beans.LED;
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.beans.handmotion.AbsoluteAngleHandMotion;
import com.qihancloud.opensdk.function.beans.headmotion.AbsoluteAngleHeadMotion;
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.HardWareManager;
import com.qihancloud.opensdk.function.unit.HeadMotionManager;
import com.qihancloud.opensdk.function.unit.MediaManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;
import com.qihancloud.opensdk.function.unit.WheelMotionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class AsociacionimagenPalabraMainActivity extends TopBaseActivity {


    public Boolean reconocimientoFacial = false;
    private Button btnAsociacion;
    private Button btnAgenda;
    private Button btnColores;

    private ImageButton acierto;
    private ImageButton fallo1;
    private ImageButton fallo2;
    private ImageButton fallo3;

    private FaceRecognitionControl faceRecognitionControl;
    private SpeechManager speechManager;
    private MediaManager mediaManager;
    private SystemManager systemManager;
    private HandMotionManager handMotionManager;
    private WheelMotionManager wheelMotionManager;
    private HeadMotionManager headMotionManager;
    private HardWareManager hardwareManager;
    private SpeechControl speechControl;
    private TextView titulo;
    private ArrayList<Integer> tandas; //0 frutas, 1 animales, 2
    private Integer elementoRandom;
    private boolean[] tandasHechas = new boolean[4]; // true si ya se han hecho, false si no
    /*List<String> palabras = Arrays.asList("banana", "apple", "watermelon", "cherries",  "strawberry", "orange", "horse", "rabbit", //no funcionan: cherries strawberry orange shoes rabbit horse pear bread
            "hamburger", "pizza", "rice", "shoes");*/
    List<String> palabras = Arrays.asList("watermelon", "rice", "pizza", "hamburger",  "pizza", "pizza", "apple", "apple",
            "apple", "apple", "apple", "apple", "watermelon");
    List<String> titulos = Arrays.asList("APPLE", "RICE", "DOG", "SHOES");

    int indiceActual = 0;
    private Integer contador;
    private Boolean correcto;



    @Override
    protected void onMainServiceConnected() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        onMainServiceConnected();
        contador = 0;
        correcto = false;
        setContentView(R.layout.activity_asociacionimagenpalabra);


/*
        elementoRandom = ThreadLocalRandom.current().nextInt(0, 4);

        if(elementoRandom == 0){
            setContentView(R.layout.activity_asociacionimagenpalabra);

        }
        else if(elementoRandom == 1){
            setContentView(R.layout.activity_asociacionimagenpalabrauno);

        }
        else if(elementoRandom == 2){
            setContentView(R.layout.activity_asociacionimagenpalabrauno);

        }
        else{
            setContentView(R.layout.activity_asociacionimagenpalabrados);

        }

        tandasHechas[elementoRandom] = true;*/

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        speechControl = new SpeechControl(speechManager);
        mediaManager = (MediaManager) getUnitManager(FuncConstant.MEDIA_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        hardwareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);


        faceRecognitionControl = new FaceRecognitionControl(speechManager, mediaManager);


        acierto = findViewById(R.id.imgasociacionimagenpalabra);
        fallo1 = findViewById(R.id.imgagenda);
        fallo2 = findViewById(R.id.imgcolores);
        fallo3 = findViewById(R.id.imgcolores2);
        titulo = findViewById(R.id.actividad);

        faceRecognitionControl.stopFaceRecognition();

        setonClicks();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void setAllButtonsClickable(boolean clickable) {

        fallo1.setClickable(clickable);
        fallo2.setClickable(clickable);
        fallo3.setClickable(clickable);
        acierto.setClickable(clickable);
    }

    /*@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater.
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog.
        // Pass null as the parent view because it's going in the dialog layout.
        builder.setView(inflater.inflate(R.layout.dialog_pista, null))
                // Add action buttons
                .setPositiveButton(R.string.signin, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Sign in the user.
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LoginDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }*/
    public void setonClicks() {

        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(50);
        speakOption.setIntonation(50);


        acierto.setOnClickListener(new View.OnClickListener() {
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
                        if(contador == 0 || contador == 2){
                            correcto = true;
                        }

                        if(correcto){

                            // Mostrar emoción y encender LEDs
                            systemManager.showEmotion(EmotionsType.PRISE);
                            hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_GREEN));

                            AbsoluteAngleHandMotion absoluteAngleHandMotion =
                                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH,20,0);
                            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

                            // Generar frases aleatorias
                            String[] frases = {
                                    "Ta-da! Your intelligence shines like an LED.",
                                    "Well done! Your brain is in linguist mode.",
                                    "Amazing! Your effort is paying off."
                            };
                            Random rand = new Random();
                            int randomIndex = rand.nextInt(frases.length);
                            speechManager.startSpeak(frases[randomIndex], speakOption);

                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            absoluteAngleHandMotion =
                                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH,20,180);
                            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

                            // Cambiar imagen
                            indiceActual++;


                            runOnUiThread(() -> {
                                actualizarImagen();
                                actualizarTitulo();

                            });
                            correcto = false;
                            contador ++;

                            // apagar luces
                            hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_CLOSE));
                            headMotionManager.doAbsoluteAngleMotion(new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL,30));

                            if (indiceActual > titulos.size()) {
                                indiceActual = 0;
                                finJuego();
                                Intent intent = new Intent(AsociacionimagenPalabraMainActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                        else{
                            speechManager.startSpeak("Try again!", speakOption);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            systemManager.showEmotion(EmotionsType.QUESTION);
                            hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_YELLOW));

                            AbsoluteAngleHandMotion absoluteAngleHandMotion =
                                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_RIGHT, 20, 0);
                            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);


                            String[] frases = {"I can repeat the word again."};
                            Random rand = new Random();
                            int randomIndex = rand.nextInt(frases.length);
                            speechManager.startSpeak(frases[randomIndex], speakOption);

                            AbsoluteAngleHeadMotion absoluteAngleHeadMotion =
                                    new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL, 7);
                            headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);

                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            speechManager.startSpeak("The word is", speakOption);
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            speechManager.startSpeak(palabras.get(indiceActual), speakOption);
                        }



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



        fallo1.setOnClickListener(new View.OnClickListener() {
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
                        if(contador == 1){
                            correcto = true;
                        }

                        if(correcto){

                            // Mostrar emoción y encender LEDs
                            systemManager.showEmotion(EmotionsType.PRISE);
                            hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_GREEN));

                            AbsoluteAngleHandMotion absoluteAngleHandMotion =
                                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH,20,0);
                            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

                            // Generar frases aleatorias
                            String[] frases = {
                                    "Ta-da! Your intelligence shines like an LED.",
                                    "Well done! Your brain is in linguist mode.",
                                    "Amazing! Your effort is paying off."
                            };
                            Random rand = new Random();
                            int randomIndex = rand.nextInt(frases.length);
                            speechManager.startSpeak(frases[randomIndex], speakOption);

                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            absoluteAngleHandMotion =
                                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH,20,180);
                            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

                            // Cambiar imagen
                            indiceActual++;

                            runOnUiThread(() -> {
                                actualizarImagen();
                                actualizarTitulo();

                            });
                            correcto = false;
                            contador ++;

                            // apagar luces
                            hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_CLOSE));
                            headMotionManager.doAbsoluteAngleMotion(new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL,30));

                            if (indiceActual > titulos.size()) {
                                indiceActual = 0;
                                finJuego();
                                Intent intent = new Intent(AsociacionimagenPalabraMainActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                     else{
                        speechManager.startSpeak("Try again!", speakOption);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        systemManager.showEmotion(EmotionsType.QUESTION);
                        hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_YELLOW));

                        AbsoluteAngleHandMotion absoluteAngleHandMotion =
                                new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_RIGHT, 20, 0);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);


                        String[] frases = {"I can repeat the word again."};
                        Random rand = new Random();
                        int randomIndex = rand.nextInt(frases.length);
                        speechManager.startSpeak(frases[randomIndex], speakOption);

                        AbsoluteAngleHeadMotion absoluteAngleHeadMotion =
                                new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL, 7);
                        headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);

                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        speechManager.startSpeak("The word is", speakOption);
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        speechManager.startSpeak(titulos.get(indiceActual), speakOption);
                    }



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

        fallo2.setOnClickListener(new View.OnClickListener() {
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
                        /*if(contador == 1 || contador == 3){
                            correcto = true;
                        }*/
                        correcto = false;

                        if(correcto){

                            // Mostrar emoción y encender LEDs
                            systemManager.showEmotion(EmotionsType.PRISE);
                            hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_GREEN));

                            AbsoluteAngleHandMotion absoluteAngleHandMotion =
                                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH,20,0);
                            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

                            // Generar frases aleatorias
                            String[] frases = {
                                    "Ta-da! Your intelligence shines like an LED.",
                                    "Well done! Your brain is in linguist mode.",
                                    "Amazing! Your effort is paying off."
                            };
                            Random rand = new Random();
                            int randomIndex = rand.nextInt(frases.length);
                            speechManager.startSpeak(frases[randomIndex], speakOption);

                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            absoluteAngleHandMotion =
                                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH,20,180);
                            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

                            // Cambiar imagen
                            indiceActual++;


                            runOnUiThread(() -> {
                                actualizarImagen();
                                actualizarTitulo();

                            });
                            correcto = false;
                            contador ++;

                            // apagar luces
                            hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_CLOSE));
                            headMotionManager.doAbsoluteAngleMotion(new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL,30));

                            if (indiceActual > titulos.size()) {
                                indiceActual = 0;
                                finJuego();
                                Intent intent = new Intent(AsociacionimagenPalabraMainActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                        else{
                            speechManager.startSpeak("Try again!", speakOption);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            systemManager.showEmotion(EmotionsType.QUESTION);
                            hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_YELLOW));

                            AbsoluteAngleHandMotion absoluteAngleHandMotion =
                                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_RIGHT, 20, 0);
                            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);


                            String[] frases = {"I can repeat the word again."};
                            Random rand = new Random();
                            int randomIndex = rand.nextInt(frases.length);
                            speechManager.startSpeak(frases[randomIndex], speakOption);

                            AbsoluteAngleHeadMotion absoluteAngleHeadMotion =
                                    new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL, 7);
                            headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);

                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            speechManager.startSpeak("The word is", speakOption);
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            speechManager.startSpeak(palabras.get(indiceActual), speakOption);
                        }



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

        fallo3.setOnClickListener(new View.OnClickListener() {
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
                        if(contador == 3){
                            correcto = true;
                        }

                        if(correcto){

                            // Mostrar emoción y encender LEDs
                            systemManager.showEmotion(EmotionsType.PRISE);
                            hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_GREEN));

                            AbsoluteAngleHandMotion absoluteAngleHandMotion =
                                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH,20,0);
                            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

                            // Generar frases aleatorias
                            String[] frases = {
                                    "Ta-da! Your intelligence shines like an LED.",
                                    "Well done! Your brain is in linguist mode.",
                                    "Amazing! Your effort is paying off."
                            };
                            Random rand = new Random();
                            int randomIndex = rand.nextInt(frases.length);
                            speechManager.startSpeak(frases[randomIndex], speakOption);

                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            absoluteAngleHandMotion =
                                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH,20,180);
                            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

                            // Cambiar imagen
                            indiceActual++;

                            runOnUiThread(() -> {
                                actualizarImagen();
                                actualizarTitulo();
                            });
                            correcto = false;
                            contador ++;

                            // apagar luces
                            hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_CLOSE));
                            headMotionManager.doAbsoluteAngleMotion(new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL,30));

                            if (indiceActual > titulos.size()) {
                                indiceActual = 0;
                                finJuego();
                                Intent intent = new Intent(AsociacionimagenPalabraMainActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                        else{
                            speechManager.startSpeak("Try again!", speakOption);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            systemManager.showEmotion(EmotionsType.QUESTION);
                            hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_YELLOW));

                            AbsoluteAngleHandMotion absoluteAngleHandMotion =
                                    new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_RIGHT, 20, 0);
                            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);


                            String[] frases = {"I can repeat the word again."};
                            Random rand = new Random();
                            int randomIndex = rand.nextInt(frases.length);
                            speechManager.startSpeak(frases[randomIndex], speakOption);

                            AbsoluteAngleHeadMotion absoluteAngleHeadMotion =
                                    new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL, 7);
                            headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);

                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            speechManager.startSpeak("The word is", speakOption);
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            speechManager.startSpeak(palabras.get(indiceActual), speakOption);
                        }



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

    private void finJuego() {

        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(40);
        speakOption.setIntonation(50);

        speechManager.startSpeak("Amazing! You finished all the words!", speakOption);

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


    }

    private void actualizarImagen() {
        String nombreImagen1 = palabras.get(indiceActual);

        int valorIni = indiceActual;

        int resId1 = getResources().getIdentifier(
                nombreImagen1,
                "drawable",
                getPackageName()
        );

        acierto.setImageResource(resId1);

        indiceActual = indiceActual+1;

        String nombreImagen2 = palabras.get(indiceActual);

        int resId2 = getResources().getIdentifier(
                nombreImagen2,
                "drawable",
                getPackageName()
        );
        fallo1.setImageResource(resId2);

        indiceActual = indiceActual+1;

        String nombreImagen3 = palabras.get(indiceActual);

        int resId3 = getResources().getIdentifier(
                nombreImagen3,
                "drawable",
                getPackageName()
        );
        fallo2.setImageResource(resId3);

        indiceActual = indiceActual+1;

        String nombreImagen4 = palabras.get(indiceActual);

        int resId4 = getResources().getIdentifier(
                nombreImagen4,
                "drawable",
                getPackageName()
        );
        fallo3.setImageResource(resId4);

        indiceActual = valorIni;

    }
    private void actualizarTitulo() {
        String palabra = titulos.get(indiceActual);

        titulo.setText(palabra);

        if (indiceActual >= titulos.size()) {
            indiceActual = 0; // evitar out of bounds
        }
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

                String frase = "Let's play the image-word association game. I will say a word in English and you will have to select the corresponding image. Shall we start?";
                speechManager.startSpeak(frase, speakOption);

                try {
                    Thread.sleep(8000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
/*
                AbsoluteAngleHeadMotion absoluteAngleHeadMotion =
                        new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL,7);
                headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);
                AbsoluteAngleHeadMotion absoluteAngleHeadMotion2 =
                        new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL,30);
                headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion2);
*/
                /*try {
                    Thread.sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String frase2 = "Vamos con la primera palabra. Me pongo en modo lingüista.";*/
                speechManager.startSpeak("The first word is", speakOption);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // speakOption.setLanguageType(SpeakOption.LAG_ENGLISH_US);
                speechManager.startSpeak("Apple", speakOption);


            }
        }, 200);

    }

    private void registrarConsulta() throws IOException, InterruptedException {

        Log.d("prueba", "registrando consulta...");

        speechControl.setIdiomaIngles();
        final String[] respuesta = {""};

        // El robot se pone en modo escucha
        new Thread(new Runnable() {
            public void run(){
                respuesta[0] = speechControl.modoEscucha();
                while (respuesta[0].isEmpty()) {
                }
                Log.d("respuesta usuario", "el valor de respuesta es " + respuesta[0]);

            }
        }).start();
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
