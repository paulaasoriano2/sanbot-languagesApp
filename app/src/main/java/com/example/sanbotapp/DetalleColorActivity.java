package com.example.sanbotapp;


import android.content.ComponentName;
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

public class DetalleColorActivity extends TopBaseActivity {


    public Boolean reconocimientoFacial = false;
    private Button btnAbrirCamara;
    private FaceRecognitionControl faceRecognitionControl;
    private SpeechManager speechManager;
    private MediaManager mediaManager;
    private SystemManager systemManager;
    private HandMotionManager handMotionManager;
    private WheelMotionManager wheelMotionManager;
    private HeadMotionManager headMotionManager;
    private HardWareManager hardwareManager;
    private String color;


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
    }

    public void setonClicks() {

        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(50);
        speakOption.setIntonation(50);

        // TODO:


        btnAbrirCamara.setOnClickListener(new View.OnClickListener() {
            private boolean isProcessing = false; // Bandera para evitar múltiples clics

            @Override
            public void onClick(View v) {
                if (isProcessing) return; // Si ya está procesando, ignorar el clic
                isProcessing = true;

                // Desactivar todos los botones
                setAllButtonsClickable(false);

                // Se abre la cámara
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.example.camera", "com.example.sanbotapp.robotControl.MediaControlActivity"));
                intent.putExtra("nombre_actividad", "ColoresActivity");
                intent.putExtra("color", color);

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

                String frase3 = "Cuando lo encuentres, haz clic en el botón para enseñármelo";
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
                        new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_HORIZONTAL,0);
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
