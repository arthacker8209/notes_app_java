package com.example.hellonotes.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hellonotes.Listeners.NotesListener;
import com.example.hellonotes.R;
import com.example.hellonotes.entities.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Note_CardAdapter extends RecyclerView.Adapter<Note_CardAdapter.NoteViewHolder> {

    private List<Note> notes;
    private NotesListener notesListener;
    private Timer timer;
    private List<Note> noteSource;
    public Note_CardAdapter(List<Note> notes , NotesListener notesListener ) {
        this.notes = notes;
        this.notesListener=notesListener;
        this.noteSource=notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_card_notes,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, final int position) {

        holder.setNote(notes.get(position));
        holder.noteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notesListener.OnNoteClicked(notes.get(position),position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position ;
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder{

        TextView textTitle ,textSubtitle,textTime;
        LinearLayout noteLayout;
        ImageView NotesImage;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle=(itemView).findViewById(R.id.textTitle);
            textSubtitle=(itemView).findViewById(R.id.textSubtitle);
            textTime=(itemView).findViewById(R.id.textTime);
            noteLayout=(itemView).findViewById(R.id.layout_note);
            NotesImage=(itemView).findViewById(R.id.NotesImage);
        }

       public void setNote(Note note){
            textTitle.setText(note.getTitle());
            if (note.getSubtitle().trim().isEmpty()){
                textSubtitle.setVisibility(View.GONE);
            }
            else{
                textSubtitle.setText(note.getSubtitle());
            }
            textTime.setText(note.getDateTime());

            GradientDrawable gradientDrawable = (GradientDrawable) noteLayout.getBackground();
            if(note.getColor()!=null){
                gradientDrawable.setColor(Color.parseColor(note.getColor()));
            }
            else{
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }

            if (note.getImagePath()!=null){
                Bitmap bitmap = BitmapFactory.decodeFile(note.getImagePath());
                NotesImage.setImageBitmap(bitmap);
                NotesImage.setVisibility(View.VISIBLE);
            }
            else{
                NotesImage.setVisibility(View.GONE);
                }
        }
    }
    public  void searchNotes(final String searchKeyword){

        timer= new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()){
                    notes=noteSource;
                }
                else {
                    ArrayList<Note> temp = new ArrayList<>();
                    for (Note note : noteSource){
                        if (note.getTitle().toLowerCase().contains(searchKeyword.toLowerCase()) ||

                           note.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase())

                        || note.getNoteText().toLowerCase().contains(searchKeyword.toLowerCase()) ||
                        note.getDateTime().toLowerCase().contains(searchKeyword.toLowerCase())){

                            temp.add(note);
                        }
                    }
                    notes= temp;
                }
               new Handler(Looper.getMainLooper()).post(new Runnable() {
                   @Override
                   public void run() {
                       notifyDataSetChanged();
                   }
               });
            }

        } ,500);

    }
    public void cancelTimer(){
        if (timer!=null){
            timer.cancel();
        }
    }
}
