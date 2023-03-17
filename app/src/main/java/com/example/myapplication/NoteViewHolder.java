package com.example.myapplication;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class NoteViewHolder extends RecyclerView.ViewHolder{

    public TextView nom;
    public TextView contenu;
    public ImageView image;



    public NoteViewHolder(View view) {
        super(view);
        findViews(view);
        image =  view.findViewById(R.id.image);
    }
    private void findViews(View view) {
        nom =  view.findViewById(R.id.nom);
        contenu =  view.findViewById(R.id.contenu);
        image =  view.findViewById(R.id.image);
    }
    public void setItem(final Item note) {
        nom.setText(note.nom);
        contenu.setText(note.contenu);
        image.setImageResource(note.image);
    }


}
