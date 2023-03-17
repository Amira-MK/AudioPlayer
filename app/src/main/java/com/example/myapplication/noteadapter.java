package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class noteadapter extends RecyclerView.Adapter<NoteViewHolder>{
    ArrayList<Item> notes;
    Activity context;
    Calendar calendar = Calendar.getInstance();

    String dateTime = DateFormat.getDateTimeInstance().format(calendar.getTime());

    public noteadapter(ArrayList notes, Activity context) {
        this.notes=notes;
        this.context=context;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View View = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemnote, parent, false);
        return new NoteViewHolder(View);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, final int position) {
        holder.setItem(notes.get(position));
        final Item note = notes.get(position);

        String description = note.contenu;

        String[] words = description.split(" ");
        if (words.length > 3) {
            description = words[0] + " " + words[1] + " " + words[2] + "...";
        }
        holder.contenu.setText(description);

        holder.image.setImageResource(note.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity3.class);
                intent.putExtra("nom", note.nom);
                intent.putExtra("date", dateTime);

                intent.putExtra("contenu", note.contenu);
                intent.putExtra("image", note.image);
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder confirm = new AlertDialog.Builder(context);
                confirm.setTitle("Suppression");
                confirm.setMessage("Vous confirmez la suppression ?");
                confirm.setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int idBtn) {
                                int index= notes.indexOf(note);
                                notes.remove(index);
                                notifyItemRemoved(index);}
                        });
                confirm.setNegativeButton(android.R.string.no, null);
                confirm.show();
                return false;
            }
        });

    }

    public void notifyData(ArrayList newArray) {
        this.notes = newArray;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(notes == null) {
            return 0;
        }
        return notes.size();
    }


}
