package com.example.sanbotapp;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class ChatArrayAdapter extends ArrayAdapter<MensajeChat> {
    private TextView textoMensaje;
    private List<MensajeChat> listaMensajes = new ArrayList<MensajeChat>();
    private LinearLayout mensaje;

    @Override
    public void add(MensajeChat object) {
        listaMensajes.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public int getCount() {
        return this.listaMensajes.size();
    }

    public MensajeChat getItem(int index) {
        return listaMensajes.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.activity_chat_singlemessage, parent, false);
        }
        mensaje =  row.findViewById(R.id.singleMessageContainer);
        MensajeChat chatMessageObj = getItem(position);
        textoMensaje = row.findViewById(R.id.singleMessage);
        textoMensaje.setText(chatMessageObj.message);
        textoMensaje.setBackgroundResource(chatMessageObj.left ? R.drawable.incoming_speech_bubble : R.drawable.outcoming_speech_bubble);
        Log.d("chatbubble", "moviendo a la izquierda? " + chatMessageObj.left);
        mensaje.setGravity(chatMessageObj.left ? Gravity.LEFT : Gravity.RIGHT);
        return row;
    }

    public void clear(){
        listaMensajes.clear();
    }
}