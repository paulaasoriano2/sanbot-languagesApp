package com.example.sanbotapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;

import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.unit.ProjectorManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;

import java.util.ArrayList;

public class ElementosAgendaActivity extends TopBaseActivity {

    private SpeechManager speechManager;

    private ArrayList<Pictograma> elementos;

    private int currentIndex = 0;

    private Handler handler = new Handler();
    Boolean usarProyector;
    private ProjectorManager projectorManager;
    private ImageView image1, image2, image3;
    private TextView text1, text2, text3;

    @Override
    protected void onMainServiceConnected() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_elementos_agenda);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.arrow_button);
        }

        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);

        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);

        speechManager =
                (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);

        elementos =
                (ArrayList<Pictograma>) getIntent()
                        .getSerializableExtra("elements");
        usarProyector = getIntent().getBooleanExtra("proyector", false);

        if(usarProyector){
            projectorManager = (ProjectorManager) getUnitManager(FuncConstant.PROJECTOR_MANAGER);
        }

        if (elementos != null && !elementos.isEmpty()) {
            mostrarGrupo();
        }
    }

    private void mostrarGrupo() {

        ImageView[] images = {image1, image2, image3};
        TextView[] texts = {text1, text2, text3};

        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(50);
        speakOption.setIntonation(50);

        // limpiar UI
        for (int i = 0; i < 3; i++) {
            images[i].setImageDrawable(null);
            texts[i].setText("");
        }

        // llenar datos visibles
        int count = 0;

        for (int i = 0; i < 3; i++) {

            int pos = currentIndex + i;

            if (pos < elementos.size()) {

                Pictograma p = elementos.get(pos);

                texts[i].setText(p.getNombre());

                int imgRes = getResources().getIdentifier(
                        p.getImagen(),
                        "drawable",
                        getPackageName()
                );

                images[i].setImageResource(imgRes);

                count++;
            }
        }

        // hablar SECUENCIALMENTE (IMPORTANTE)
        handler.postDelayed(() -> {
            hablarSecuencial(0, speakOption);
        }, 600);
    }

    private void hablarSecuencial(int index, SpeakOption speakOption) {

        int pos = currentIndex + index;

        if (pos >= elementos.size() || index >= 3) {

            handler.postDelayed(() -> {

                currentIndex += 3;

                if (currentIndex < elementos.size()) {
                    mostrarGrupo();
                } else {
                    mostrarDialogoVerDeNuevo();
                }

            }, 1500);

            return;
        }

        Pictograma p = elementos.get(pos);

        speechManager.startSpeak(p.getNombre(), speakOption);

        handler.postDelayed(() -> {
            hablarSecuencial(index + 1, speakOption);
        }, 1200);
    }
    void mostrarDialogoVerDeNuevo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_reproducirdenuevo, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        dialog.show();
        Button btnAcceptar = dialogView.findViewById(R.id.btnAccept);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancel);

        btnAcceptar.setOnClickListener(v -> {
            dialog.dismiss();
            /*Intent intent = new Intent(ElementosAgendaActivity.this, ElementosAgendaActivity.class);
            intent.putExtra("elements", elementos);
            intent.putExtra("proyector", true);
            startActivity(intent);*/
            currentIndex = 0;
            image1.setImageDrawable(null);
            image2.setImageDrawable(null);
            image3.setImageDrawable(null);

            text1.setText("");
            text2.setText("");
            text3.setText("");
            mostrarGrupo();

        });

        btnCancelar.setOnClickListener(v -> {
            dialog.dismiss();
            /*Intent intent = new Intent(ElementosAgendaActivity.this, ElementosAgendaActivity.class);
            intent.putExtra("elements", elementos);
            intent.putExtra("proyector", false);
            startActivity(intent);*/
            finish();
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (usarProyector) {
            projectorManager.switchProjector(false);
        }
    }

}