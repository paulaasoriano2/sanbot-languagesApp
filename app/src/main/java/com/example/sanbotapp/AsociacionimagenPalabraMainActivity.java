package com.example.sanbotapp;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

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
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class AsociacionimagenPalabraMainActivity extends TopBaseActivity {


    public Boolean reconocimientoFacial = false;

    private ImageButton acierto;
    private ImageButton fallo1;
    private ImageButton fallo2;
    private ImageButton fallo3;
    private ImageView imageDialog;

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
    private  boolean isFirstScreen;
    List<String> titulos = new ArrayList<>();
    List<String> categorias = Arrays.asList("comida", "animales");
    List<String> subCategorias = Arrays.asList("frutas", "carbohidratos");
    List<String[]> palabras = new ArrayList<>();
    private Integer i = 0; // Contador de categorias
    private Integer j = 0; // Contador de palabras
    private Integer h = 0; // Contador de sub categorias


    int indiceActual = 0;
    private Integer contador;
    private Boolean correcto;
    ArrayList<String> palabrasUsadas = new ArrayList<>();
    List<ImageButton> imagenes = new ArrayList<>();
    int indiceCorrecto;
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
        isFirstScreen = true;
        //titulos.add("APPLE");
      //  imageDialog = dialogView.findViewById(R.id.dialogImage);


        setContentView(R.layout.activity_asociacionimagenpalabra);


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

        imagenes.add(acierto);
        imagenes.add(fallo1);
        imagenes.add(fallo2);
        imagenes.add(fallo3);

        acierto.setImageDrawable(null);
        fallo1.setImageDrawable(null);
        fallo2.setImageDrawable(null);
        fallo3.setImageDrawable(null);

        faceRecognitionControl.stopFaceRecognition();

        setonClicks();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        VocabularioDbAdapter db = getVocabularioDbAdapter();
// Leer
        Cursor cursor = db.fetchAllVocabulario();

        if (cursor.moveToFirst()) {
            do {
                String nombre = cursor.getString(3);
                String categoria = cursor.getString(1);
                String subCategoria = cursor.getString(2);

                palabras.add(new String[]{nombre, categoria, subCategoria});
               // palabras.add(nombre);
                Log.d("DATABWWWWWW", nombre);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }

    private VocabularioDbAdapter getVocabularioDbAdapter() {
        VocabularioDbAdapter db = new VocabularioDbAdapter(this);
        db.open();
        return db;
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

    public void setonClicks() {

        // Desactivar todos los botones
        setAllButtonsClickable(false);

        for (int k = 0; k < imagenes.size(); k++) {

            int index = k;
            imagenes.get(k).setVisibility(View.VISIBLE);
            imagenes.get(k).setOnClickListener(v -> {
                try {
                    comprobarRespuesta(index);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

    }

    void comprobarRespuesta(int imagenPulsada) throws InterruptedException {
        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(50);
        speakOption.setIntonation(50);

        if(imagenPulsada == indiceCorrecto){
            // Mostrar emoción y encender LEDs
            systemManager.showEmotion(EmotionsType.PRISE);
            hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_GREEN));

            AtomicReference<AbsoluteAngleHandMotion> absoluteAngleHandMotion =
                    new AtomicReference<>(new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH, 20, 0));
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion.get());

            // Generar frases aleatorias
            String[] frases = {
                    "Ta-da! Your intelligence shines like an LED.",
                    "Well done! Your brain is in linguist mode.",
                    "Amazing! Your effort is paying off."
            };
            Random rand = new Random();
            int randomIndex = rand.nextInt(frases.length);
            speechManager.startSpeak(frases[randomIndex], speakOption);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();

            View dialogView = inflater.inflate(R.layout.dialog_feedbackasociacion, null);
            builder.setView(dialogView);

            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
            Button btnAcceptar = dialogView.findViewById(R.id.btnAccept);
            Button btnCancelar = dialogView.findViewById(R.id.btnCancel);


            btnAcceptar.setOnClickListener(v -> {
                dialog.dismiss();

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                absoluteAngleHandMotion.set(new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH, 20, 180));
                handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion.get());

                // Cambiar imagen
                indiceActual++;
                contador ++;

                runOnUiThread(() -> {
                    actualizarImagen();
                    actualizarTitulo();
                });
                correcto = false;
            /*int resId = getResources().getIdentifier(
                    nombreImagen,
                    "drawable",
                    getPackageName()
            );*/


                // apagar luces
                hardwareManager.setLED(new LED(LED.PART_ALL, LED.MODE_CLOSE));
                headMotionManager.doAbsoluteAngleMotion(new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL,30));

                if (contador>=4) {
                    indiceActual = 0;
                    finJuego();
                    Intent intent = new Intent(AsociacionimagenPalabraMainActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });

            btnCancelar.setOnClickListener(v -> {
                dialog.dismiss();
                finJuego();
                finish();
            });


        } else {
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

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();

            View dialogView = inflater.inflate(R.layout.dialog_pista, null);
            builder.setView(dialogView);

            AlertDialog dialog = builder.create();
            dialog.show();


            imagenes.get(imagenPulsada).setVisibility(View.GONE);

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

            speechManager.startSpeak(titulos.get(contador), speakOption);

        }

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
        String categoria;
        String subCategoria;
        Integer indice = 0;
        List<String> palabrasAMostrar = new ArrayList<String>();
        palabrasAMostrar.clear();


        boolean palabraDisponible = false;
        for (String[] fruta : palabras) {
            if (!palabrasUsadas.contains(fruta[0])) {
                palabraDisponible = true;
                break;
            }
        }

        if (!palabraDisponible) {
            Log.d("INFO", "No quedan palabras disponibles");
            finJuego();
            return; // salir del método
        }


        while (i < categorias.size()) { // Se recorren todas las categorias que hay
            categoria = categorias.get(i);
            if(Objects.equals(categoria, "comida")){
                while(h<subCategorias.size()){
                        for (String[] fruta : palabras) {
                            String nombre = fruta[0];
                            String categoriaPalabra = fruta[1];
                            String subcategoria = fruta[2];

                            Log.d("PALABRA", nombre + " " + categoria + " " + subcategoria);

                            if (!palabrasUsadas.contains(nombre) && Objects.equals(subCategorias.get(h), subcategoria) && Objects.equals(categoriaPalabra, categoria)) {
                                Log.d("PALABRA AÑADIDA", nombre + " " + categoria + " " + subcategoria);
                                palabrasAMostrar.add(nombre);
                                palabrasUsadas.add(nombre);
                                indice++;
                            }
                            if(indice == 4){
                                mostrarPalabras(palabrasAMostrar);
                                indice = 0;
                                return;
                            }
                        }

                    h++;
                }

            }
            else{
                    for (String[] fruta : palabras) {
                        String nombre = fruta[0];
                        String categoriaPalabra = fruta[1];
                        String subcategoria = fruta[2];

                        Log.d("PALABRA", nombre + " " + categoria + " " + subcategoria);

                        if (!palabrasUsadas.contains(nombre) && Objects.equals(categoriaPalabra, categoria)) {
                            Log.d("PALABRA AÑADIDA", nombre + " " + categoria + " " + subcategoria);
                            palabrasAMostrar.add(nombre);
                            palabrasUsadas.add(nombre);
                            indice++;
                        }
                        if(indice == 4){
                            mostrarPalabras(palabrasAMostrar);
                            indice = 0;
                            return;
                        }
                    }

                    j++;

            }

            i++;
        }
    }

    void mostrarPalabras(List<String> palabrasAMostrar){

        Random rand = new Random();
        indiceCorrecto = rand.nextInt(4); // genera 0,1,2 o 3
        titulos.add(palabrasAMostrar.get(indiceCorrecto).toUpperCase());

        //String nombreImagen1 = palabrasAMostrar.get(0);
        for (int k = 0; k < 4; k++) {

            String nombreImagen = palabrasAMostrar.get(k);

            int resId = getResources().getIdentifier(
                    nombreImagen,
                    "drawable",
                    getPackageName()
            );

            imagenes.get(k).setImageResource(resId);
        }

/*
        int resId1 = getResources().getIdentifier(
                nombreImagen1,
                "drawable",
                getPackageName()
        );

        acierto.setImageResource(resId1);

        indiceActual = indiceActual+1;

        String nombreImagen2 = palabrasAMostrar.get(1);

        int resId2 = getResources().getIdentifier(
                nombreImagen2,
                "drawable",
                getPackageName()
        );
        fallo1.setImageResource(resId2);

        indiceActual = indiceActual+1;

        String nombreImagen3 = palabrasAMostrar.get(2);

        int resId3 = getResources().getIdentifier(
                nombreImagen3,
                "drawable",
                getPackageName()
        );
        fallo2.setImageResource(resId3);

        indiceActual = indiceActual+1;

        String nombreImagen4 = palabrasAMostrar.get(3);

        int resId4 = getResources().getIdentifier(
                nombreImagen4,
                "drawable",
                getPackageName()
        );
        fallo3.setImageResource(resId4);*/


    }
    private void actualizarTitulo() {

        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(50);
        speakOption.setIntonation(50);
        String palabra = titulos.get(contador);

        if(isFirstScreen){
            speechManager.startSpeak("Get ready because the words are coming!", speakOption);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            speechManager.startSpeak("The first word is", speakOption);

            isFirstScreen = false;
        }
        else{
            // Generar frases aleatorias
            String[] frases = {
                    "Let's move on to the next word.",
                    "That was incredible. I am sure you will know the next one too. ",
                    "Here it is the next word."
            };
            Random rand = new Random();
            int randomIndex = rand.nextInt(frases.length);
            speechManager.startSpeak(frases[randomIndex], speakOption);
        }

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // speakOption.setLanguageType(SpeakOption.LAG_ENGLISH_US);
        speechManager.startSpeak(palabra, speakOption);


        titulo.setText(palabra);


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
// limpiar imágenes primero
               /* for (ImageButton img : imagenes) {
                    img.setImageDrawable(null);
                }*/
                actualizarImagen();
                actualizarTitulo();
                //titulo.setText(titulos.get(contador));



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
