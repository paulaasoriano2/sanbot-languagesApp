package com.example.sanbotapp;


import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.HardWareManager;
import com.qihancloud.opensdk.function.unit.HeadMotionManager;
import com.qihancloud.opensdk.function.unit.MediaManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;
import com.qihancloud.opensdk.function.unit.WheelMotionManager;

import java.io.File;
import java.util.Random;

public class ColoresActivity extends TopBaseActivity {


    public Boolean reconocimientoFacial = false;
    private Button btnRed;
    private Button btnBlue;
    private Button btnOrange;
    private Button btnYellow;

    private Button btnGreen;

    private FaceRecognitionControl faceRecognitionControl;
    private SpeechManager speechManager;
    private MediaManager mediaManager;
    private SystemManager systemManager;
    private HandMotionManager handMotionManager;
    private WheelMotionManager wheelMotionManager;
    private HeadMotionManager headMotionManager;
    private HardWareManager hardwareManager;
    private String colour;


    @Override
    protected void onMainServiceConnected() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        onMainServiceConnected();
        setContentView(R.layout.activity_colores);

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        mediaManager = (MediaManager) getUnitManager(FuncConstant.MEDIA_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        hardwareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);


        faceRecognitionControl = new FaceRecognitionControl(speechManager, mediaManager);

        btnRed = findViewById(R.id.red);
        btnBlue = findViewById(R.id.blue);
        btnYellow = findViewById(R.id.yellow);
        btnOrange = findViewById(R.id.orange);
        btnGreen = findViewById(R.id.green);



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
        btnRed.setClickable(clickable);
        btnBlue.setClickable(clickable);
        btnYellow.setClickable(clickable);
        btnOrange.setClickable(clickable);
        btnGreen.setClickable(clickable);


    }

    public void setonClicks() {

        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(50);
        speakOption.setIntonation(50);

        btnRed.setOnClickListener(new View.OnClickListener() {
            private boolean isProcessing = false; // Bandera para evitar múltiples clics

            @Override
            public void onClick(View v) {
                if (isProcessing) return; // Si ya está procesando, ignorar el clic
                isProcessing = true;

                // Desactivar todos los botones
                setAllButtonsClickable(false);

                Intent intent = new Intent(ColoresActivity.this, DetalleColorActivity.class);
                intent.putExtra("color", "red");
                startActivity(intent);
            }
        });
        btnBlue.setOnClickListener(new View.OnClickListener() {
            private boolean isProcessing = false; // Bandera para evitar múltiples clics

            @Override
            public void onClick(View v) {
                if (isProcessing) return; // Si ya está procesando, ignorar el clic
                isProcessing = true;

                // Desactivar todos los botones
                setAllButtonsClickable(false);

                Intent intent = new Intent(ColoresActivity.this, DetalleColorActivity.class);
                intent.putExtra("color", "blue");
                startActivity(intent);
            }
        });
        btnGreen.setOnClickListener(new View.OnClickListener() {
            private boolean isProcessing = false; // Bandera para evitar múltiples clics

            @Override
            public void onClick(View v) {
                if (isProcessing) return; // Si ya está procesando, ignorar el clic
                isProcessing = true;

                // Desactivar todos los botones
                setAllButtonsClickable(false);

                Intent intent = new Intent(ColoresActivity.this, DetalleColorActivity.class);
                intent.putExtra("color", "green");
                startActivity(intent);
            }
        });
        btnYellow.setOnClickListener(new View.OnClickListener() {
            private boolean isProcessing = false; // Bandera para evitar múltiples clics

            @Override
            public void onClick(View v) {
                if (isProcessing) return; // Si ya está procesando, ignorar el clic
                isProcessing = true;

                // Desactivar todos los botones
                setAllButtonsClickable(false);

                Intent intent = new Intent(ColoresActivity.this, DetalleColorActivity.class);
                intent.putExtra("color", "yellow");
                startActivity(intent);
            }
        });
        btnOrange.setOnClickListener(new View.OnClickListener() {
            private boolean isProcessing = false; // Bandera para evitar múltiples clics

            @Override
            public void onClick(View v) {
                if (isProcessing) return; // Si ya está procesando, ignorar el clic
                isProcessing = true;

                // Desactivar todos los botones
                setAllButtonsClickable(false);

                Intent intent = new Intent(ColoresActivity.this, DetalleColorActivity.class);
                intent.putExtra("color", "orange");
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

                String frase = "Vamos a reconocer colores. Yo te diré un color y tú me tendrás que enseñar un objeto de ese color.";
                speechManager.startSpeak(frase, speakOption);

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
