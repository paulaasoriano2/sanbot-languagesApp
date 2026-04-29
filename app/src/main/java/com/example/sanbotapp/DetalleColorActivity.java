package com.example.sanbotapp;

import okhttp3.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
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
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.EmotionsType;
import com.qihancloud.opensdk.function.beans.LED;
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.beans.headmotion.AbsoluteAngleHeadMotion;
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

public class DetalleColorActivity extends TopBaseActivity {


    public Boolean reconocimientoFacial = false;
    private Button btnAbrirCamara;
    private Button btnComprobarColor;
    private FaceRecognitionControl faceRecognitionControl;
    private SpeechManager speechManager;
    private MediaManager mediaManager;
    private SystemManager systemManager;
    private HandMotionManager handMotionManager;
    private WheelMotionManager wheelMotionManager;
    private HeadMotionManager headMotionManager;
    private HardWareManager hardwareManager;
    private String color;
    private String color_dominant;
    private double percent;
    private JSONArray allColors;
    private String imageUriString;



    @Override
    protected void onMainServiceConnected() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        onMainServiceConnected();

        setContentView(R.layout.activity_detalle_color);
        color = getIntent().getStringExtra("color");
        imageUriString = getIntent().getStringExtra("screenshot_uri");
        Log.d("Color elegido", color);
        TextView nombreColorEnPantalla = findViewById(R.id.nombreColor);
        nombreColorEnPantalla.setText(color);

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        mediaManager = (MediaManager) getUnitManager(FuncConstant.MEDIA_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        hardwareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);


        faceRecognitionControl = new FaceRecognitionControl(speechManager, mediaManager);

        btnAbrirCamara = findViewById(R.id.abrirCamara);
        btnComprobarColor = findViewById(R.id.comprobarColor);


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
        btnAbrirCamara.setClickable(clickable);
        btnComprobarColor.setClickable(clickable);
    }

    public void setonClicks() {

        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(50);
        speakOption.setIntonation(50);

        setAllButtonsClickable(true);



        btnAbrirCamara.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Se abre la cámara
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.example.languages", "com.example.sanbotapp.robotControl.MediaControlActivity"));
                intent.putExtra("nombre_actividad", "DetalleColorActivity");
                intent.putExtra("color", color);
                startActivityForResult(intent, 100);

                //startActivity(intent);

            }

        });

        btnComprobarColor.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {



                if (imageUriString != null) {
                    Uri imageUri = Uri.parse(imageUriString);
                    Log.d("Captura recibida", imageUriString);


                    File file = uriToFile(imageUri);
                    sendImageToServer(file);
                }
                else{
                    Log.d("Captura recibida", "Captura recibida pero es nula");

                }
/*                if(esColorCorrecto()){

                    String frase3 = "Good job! The object is red.";
                    speechManager.startSpeak(frase3, speakOption);
                    Log.d("Color elegido", color);
                }
                else{

                    String frase3 = "Try again, sometimes I don't see objects very well";
                    speechManager.startSpeak(frase3, speakOption);
                    Log.d("Color elegido", color);
                }*/
            }

        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {

            String imageUriString = data.getStringExtra("screenshot_uri");
            Log.d("imageUriString", imageUriString);


            if (imageUriString != null) {
                Uri imageUri = Uri.parse(imageUriString);

                File file = uriToFile(imageUri);
                sendImageToServer(file);
            }
        }
    }

    private File uriToFile(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File file = new File(getCacheDir(), "temp_image.jpg");
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            return file;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

   /* public void sendImageToServer(File imageFile){
        Log.d("Captura recibida", "Enviada al servidor");


        OkHttpClient client = new OkHttpClient();

        RequestBody fileBody = RequestBody.create(
                imageFile,
                MediaType.parse("image/jpeg")
        );

        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", imageFile.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url("https://opencv-pruebas.onrender.com/detect-color")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if(response.isSuccessful()){
                    String json = response.body().string();
                    System.out.println(json);
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(json);
                        color_dominant = obj.getString("dominant_color");
                        percent = obj.getDouble("percentage");
                        Log.d("Color dominante", color_dominant);

                        Log.d("Porcentaje", String.valueOf(percent));

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }*/

    public void sendImageToServer(File imageFile) {

        new Thread(() -> {

            try {
                //OkHttpClient client = new OkHttpClient();
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                        .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                        .build();

                RequestBody fileBody = RequestBody.create(
                        imageFile,
                        MediaType.parse("image/jpeg")
                );

                MultipartBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image", imageFile.getName(), fileBody)
                        .build();

                Request request = new Request.Builder()
                        .url("https://opencv-pruebas.onrender.com/detect-color")
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {

                    String json = response.body().string();

                    JSONObject obj = new JSONObject(json);

                    color_dominant = obj.getString("dominant_color");
                    percent = obj.getDouble("percentage");
                    allColors = obj.getJSONArray("all_colors");

                    for (int i = 0; i < allColors.length(); i++) {
                        JSONArray colorItem = allColors.getJSONArray(i);

                        String name = colorItem.getString(0);
                        double percentage = colorItem.getDouble(1);

                        Log.d("COLOR LIST", name + " -> " + percentage + "%");
                    }

                    Log.d("Color dominante", color_dominant);
                    Log.d("Porcentaje", String.valueOf(percent));

                    runOnUiThread(() -> {

                        SpeakOption speakOption = new SpeakOption();
                        speakOption.setSpeed(50);
                        speakOption.setIntonation(50);

                        if (esColorCorrecto()) {
                            speechManager.startSpeak(
                                    "Good job! The object is " + color_dominant,
                                    speakOption
                            );
                        } else {
                            speechManager.startSpeak(
                                    "Try again, I detected " + color_dominant,
                                    speakOption
                            );
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }

    public boolean esColorCorrecto(){
        return Objects.equals(color, color_dominant);
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


                AbsoluteAngleHeadMotion absoluteAngleHeadMotion =
                        new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_HORIZONTAL,7);
                headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);

                String frase2 = "Busquemos un objeto que sea de ese color!";
                speechManager.startSpeak(frase2, speakOption);

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                AbsoluteAngleHeadMotion absoluteAngleHeadMotion2 =
                        new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_HORIZONTAL,-7);
                headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion2);

                String frase3 = "When you find it, tap";
                speechManager.startSpeak(frase3, speakOption);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String frase = "Let's do it! The color is";
                speakOption.setLanguageType(SpeakOption.LAG_ENGLISH_US);
                speechManager.startSpeak(frase, speakOption);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                speechManager.startSpeak(color, speakOption);


                //speechManager.startSpeak(color, speakOption);

                AbsoluteAngleHeadMotion absoluteAngleHeadMotion3 =
                        new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_HORIZONTAL,90);
                headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion3);
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
