package com.example.sanbotapp;


import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
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
    private Button btnBlack;
    private Button btnWhite;

    private Button btnGreen;
    private Button btnPurple;
    private Button btnPink;
    private Button btnGrey;

    private ImageButton imgRed;
    private ImageButton imgBlue;
    private ImageButton imgBlack;
    private ImageButton imgWhite;
    private ImageButton imgGreen;
    private ImageButton imgPurple;
    private ImageButton imgPink;
    private ImageButton imgGrey;


    private FaceRecognitionControl faceRecognitionControl;
    private SpeechManager speechManager;
    private MediaManager mediaManager;
    private SystemManager systemManager;
    private HandMotionManager handMotionManager;
    private WheelMotionManager wheelMotionManager;
    private HeadMotionManager headMotionManager;
    private HardWareManager hardwareManager;
    private String colour;
    private ImageButton btnBack;


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
        btnPurple = findViewById(R.id.purple);
        btnWhite = findViewById(R.id.white);
        btnBlack = findViewById(R.id.black);
        btnGreen = findViewById(R.id.green);
        btnBack = findViewById(R.id.btnBack);
        btnPink = findViewById(R.id.pink);
        btnGrey = findViewById(R.id.grey);

        imgRed = findViewById(R.id.imgred);
        imgBlue = findViewById(R.id.imgblue);
        imgPurple = findViewById(R.id.imgpurple);
        imgWhite = findViewById(R.id.imgwhite);
        imgBlack = findViewById(R.id.imgblack);
        imgPink = findViewById(R.id.imgpink);
        imgGrey = findViewById(R.id.imggrey);
        imgGreen = findViewById(R.id.imggreen);


        setAllButtonsClickable(true);


        faceRecognitionControl.stopFaceRecognition();

        setonClicks();
        loadColorsFromDB();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    public void setAllButtonsClickable(boolean clickable) {
        btnRed.setClickable(clickable);
        btnBlue.setClickable(clickable);
        btnWhite.setClickable(clickable);
        btnBlack.setClickable(clickable);
        btnGreen.setClickable(clickable);
        btnBack.setClickable(clickable);
        btnPurple.setClickable(clickable);
        btnPink.setClickable(clickable);
        btnGrey.setClickable(clickable);

        imgRed.setClickable(clickable);
        imgBlue.setClickable(clickable);
        imgWhite.setClickable(clickable);
        imgBlack.setClickable(clickable);
        imgGreen.setClickable(clickable);
        imgPurple.setClickable(clickable);
        imgPink.setClickable(clickable);
        imgGrey.setClickable(clickable);

    }

    public void setonClicks() {

        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(50);
        speakOption.setIntonation(50);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String color = v.getTag().toString();

                btnWhite.setForeground(null);
                btnWhite.setAlpha(1f);
                Intent intent = new Intent(ColoresActivity.this, DetalleColorActivity.class);
                intent.putExtra("color", color);
                startActivity(intent);
            }
        };

        btnRed.setOnClickListener(listener);
        btnBlue.setOnClickListener(listener);
        btnGreen.setOnClickListener(listener);
        btnWhite.setOnClickListener(listener);
        btnBlack.setOnClickListener(listener);
        btnPink.setOnClickListener(listener);
        btnPurple.setOnClickListener(listener);
        btnGrey.setOnClickListener(listener);

        imgRed.setOnClickListener(listener);
        imgBlue.setOnClickListener(listener);
        imgGreen.setOnClickListener(listener);
        imgWhite.setOnClickListener(listener);
        imgBlack.setOnClickListener(listener);
        imgPink.setOnClickListener(listener);
        imgPurple.setOnClickListener(listener);
        imgGrey.setOnClickListener(listener);



        btnBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
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

                AbsoluteAngleHandMotion arm =
                        new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_LEFT, 20, 0);
                handMotionManager.doAbsoluteAngleMotion(arm);

                String frase = "Let's recognize colors. I will tell you a color and you will have to show me an object of that color.";
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

    private void loadColorsFromDB() {

        ColoresDbAdapter adapter = new ColoresDbAdapter(this);
        adapter.open();

        Cursor cursor = adapter.fetchAllColors();

        if (cursor != null) {
            while (cursor.moveToNext()) {

                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                int acierto = cursor.getInt(cursor.getColumnIndexOrThrow("acierto"));

                if (acierto == 1) {
                    setColorImage(nombre);
                }
            }
            cursor.close();
        }

        adapter.close();
    }

    private void setColorImage(String color) {

        int resId = getResources().getIdentifier(
                "img" + color,
                "drawable",
                getPackageName()
        );

        Drawable drawable;

        switch (color) {

            case "red":
                imgRed.setImageResource(resId);
                drawable = btnRed.getBackground().mutate();
                drawable.setTint(Color.parseColor("#E30613"));
                btnRed.setBackground(drawable);
                break;

            case "blue":
                imgBlue.setImageResource(resId);
                drawable = btnBlue.getBackground().mutate();
                drawable.setTint(Color.parseColor("#E30613"));
                btnBlue.setBackground(drawable);
                break;

            case "green":
                imgGreen.setImageResource(resId);
                drawable = btnGreen.getBackground().mutate();
                drawable.setTint(Color.parseColor("#3AAA35"));
                btnGreen.setBackground(drawable);
                break;

            case "purple":
                imgPurple.setImageResource(resId);
                drawable = btnPurple.getBackground().mutate();
                drawable.setTint(Color.parseColor("#3AAA35"));
                btnPurple.setBackground(drawable);
                break;

            case "black":
                imgBlack.setImageResource(resId);
                drawable = btnBlack.getBackground().mutate();
                drawable.setTint(Color.parseColor("#000000"));
                btnBlack.setBackground(drawable);
                break;

            case "pink":
                imgPink.setImageResource(resId);
                drawable = btnPink.getBackground().mutate();
                drawable.setTint(Color.parseColor("#E30613"));
                btnPink.setBackground(drawable);
                break;

            case "grey":
                imgGrey.setImageResource(resId);
                drawable = btnGrey.getBackground().mutate();
                drawable.setTint(Color.parseColor("#E30613"));
                btnGrey.setBackground(drawable);
                break;

            case "white":

                imgWhite.setImageResource(resId);

                GradientDrawable drawable2 = new GradientDrawable();
                drawable2.setShape(GradientDrawable.RECTANGLE);
                drawable2.setCornerRadius(30f);
                drawable2.setColor(Color.WHITE);
                drawable2.setStroke(4, Color.BLACK);

                btnWhite.setBackground(drawable2);


                btnWhite.setTextColor(Color.BLACK);

                break;
        }
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
