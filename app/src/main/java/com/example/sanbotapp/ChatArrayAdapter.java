package com.example.sanbotapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatArrayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MensajeChat> mensajes;

    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;

    public ChatArrayAdapter() {
        mensajes = new ArrayList<>();
    }

    public void add(MensajeChat mensaje) {
        mensajes.add(mensaje);
        notifyItemInserted(mensajes.size() - 1);
    }

    public int getItemCount() {
        return mensajes.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mensajes.get(position).isMe()) {
            return VIEW_TYPE_ME;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        if (viewType == VIEW_TYPE_ME) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_chat_my_messages, parent, false);
            return new MyMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_chat_other_messages, parent, false);
            return new OtherMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecyclerView.ViewHolder holder,
            int position
    ) {
        MensajeChat mensaje = mensajes.get(position);

        if (holder instanceof MyMessageViewHolder) {
            ((MyMessageViewHolder) holder).mensaje.setText(mensaje.message);
        } else {
            ((OtherMessageViewHolder) holder).mensaje.setText(mensaje.message);
        }
    }

    public static class MyMessageViewHolder extends RecyclerView.ViewHolder {
        TextView mensaje;

        public MyMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            mensaje = itemView.findViewById(R.id.text_gchat_message_me);
        }
    }

    public static class OtherMessageViewHolder extends RecyclerView.ViewHolder {
        TextView mensaje;

        public OtherMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            mensaje = itemView.findViewById(R.id.text_gchat_message_other);
        }
    }
}