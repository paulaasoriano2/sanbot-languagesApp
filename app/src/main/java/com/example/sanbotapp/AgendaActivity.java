package com.example.sanbotapp;


import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.bumptech.glide.Glide;

public class AgendaActivity extends TopBaseActivity {


    public Boolean reconocimientoFacial = false;
    private ImageButton addButton;
    private Button reproducir;

    private CustomAdapter adapter;
    
    private FaceRecognitionControl faceRecognitionControl;
    private SpeechManager speechManager;
    private MediaManager mediaManager;
    private SystemManager systemManager;
    private HandMotionManager handMotionManager;
    private WheelMotionManager wheelMotionManager;
    private HeadMotionManager headMotionManager;
    private HardWareManager hardwareManager;
    List<Pictograma> pictogramas = new ArrayList<>();
    private LinearLayout containerLayout;


    @Override
    protected void onMainServiceConnected() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        onMainServiceConnected();
        setContentView(R.layout.activity_agenda);


        // Creación de la lista de elementos seleccionados vacía
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        ArrayList<Pictograma> datos = new ArrayList<>();

        adapter = new CustomAdapter(datos);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        recyclerView.setAdapter(adapter);



        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        mediaManager = (MediaManager) getUnitManager(FuncConstant.MEDIA_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        hardwareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);


        faceRecognitionControl = new FaceRecognitionControl(speechManager, mediaManager);

        reproducir = findViewById(R.id.reproducir);
        containerLayout = findViewById(R.id.containerLayout);



        faceRecognitionControl.stopFaceRecognition();

        setonClicks();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        PictogramasDbAdapter db = getPictogramasDbAdapter();
// Leer
        Cursor cursor = db.fetchAllPictogramas();

        if (cursor.moveToFirst()) {
            do {
                String nombre = cursor.getString(2);
                String imagen = cursor.getString(3);

                pictogramas.add(new Pictograma(nombre, imagen));
                // palabras.add(nombre);
                Log.d("DATABWWWWWW", nombre);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        mostrarPictogramas();


    }

    private void mostrarPictogramas() {

        LinearLayout pictogramasLayout =
                findViewById(R.id.pictogramasLayout);

        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(50);
        speakOption.setIntonation(50);

        for (Pictograma pictograma : pictogramas) {

            // Layout vertical
            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams itemParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            itemParams.setMargins(16,16,16,16);

            itemLayout.setLayoutParams(itemParams);

            // Imagen
            ImageView imageView = new ImageView(this);

            LinearLayout.LayoutParams imageParams =
                    new LinearLayout.LayoutParams(200,200);

            imageView.setLayoutParams(imageParams);

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            imageView.setClickable(true);
            imageView.setFocusable(true);

            // Obtener drawable desde nombre
            int imageResource = getResources().getIdentifier(
                    pictograma.getImagen(),
                    "drawable",
                    getPackageName()
            );

            imageView.setImageResource(imageResource);

            // Texto
            android.widget.TextView textView =
                    new android.widget.TextView(this);

            textView.setText(pictograma.getNombre());
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            // CLICK EN LA IMAGEN
            imageView.setOnClickListener(v -> {

                Log.d("CLICK", "Pulsado: " + pictograma.getNombre());

                // Añadir al RecyclerView
                adapter.addItem(pictograma);

                // Hablar
                speechManager.startSpeak(
                        pictograma.getNombre(),
                        speakOption
                );
            });

            // Añadir vistas
            itemLayout.addView(imageView);
            //itemLayout.addView(textView); POR SI SE QUIERE MOSTRAR EL TEXTO DE LOS PICTOGRAMAS

            pictogramasLayout.addView(itemLayout);
        }
    }
    private PictogramasDbAdapter getPictogramasDbAdapter() {
        PictogramasDbAdapter db = new PictogramasDbAdapter(this);
        db.open();
        return db;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    public void setAllButtonsClickable(boolean clickable) {
        reproducir.setClickable(clickable);
    }

    public void setonClicks() {

        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(50);
        speakOption.setIntonation(50);


        reproducir.setOnClickListener(new View.OnClickListener() {
            private boolean isProcessing = false; // Bandera para evitar múltiples clics

            @Override
            public void onClick(View v) {
                if (isProcessing) return; // Si ya está procesando, ignorar el clic
                isProcessing = true;

                // Desactivar todos los botones
                setAllButtonsClickable(false);

                new Thread(() -> {
                    try {

                        ArrayList<Pictograma> localDataSet = adapter.getDataSet();
                        if(localDataSet.isEmpty()){
                            Thread.sleep(100);
                            speechManager.startSpeak("Please, select one or more pictograms to build your agenda", speakOption);

                        }
                        else{
                            for (int i = 0; i < localDataSet.size(); i++) {
                                Pictograma elemento = localDataSet.get(i);
                                System.out.println(elemento.getNombre());
                                Thread.sleep(100);
                                speechManager.startSpeak(localDataSet.get(i).getNombre(), speakOption);
                                Thread.sleep(1000);

                            }
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


                String[] frases = {
                        "What if we remember what you did yesterday? I'm very curious. Click on the actions you did"
                };
                Random rand = new Random();
                int randomIndex = rand.nextInt(frases.length);
                speechManager.startSpeak(frases[randomIndex], speakOption);

               /* try {
                    getPictogramas();
                } catch (IOException | JSONException e) {
                    throw new RuntimeException(e);
                }
*/
               /* RelativeAngleWheelMotion movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_LEFT, 5, 360);
                wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);*/

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


        return super.onOptionsItemSelected(item);
    }


}
