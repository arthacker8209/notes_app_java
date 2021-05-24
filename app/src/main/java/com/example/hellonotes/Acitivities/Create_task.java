package com.example.hellonotes.Acitivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.RoomDatabase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hellonotes.Database.TaskDataBase;
import com.example.hellonotes.R;
import com.example.hellonotes.entities.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class Create_task extends AppCompatActivity {
    private ImageView ImageBack , ImageSave;
    private EditText inputTaskTitle,inputTaskSubitle,inputTaskDescription;
    private String selectNoteColor;
    private Toast toast;
    private TextView text;
    private View colorSubtitleIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        ImageBack = findViewById(R.id.ImageBack);
        ImageSave= findViewById(R.id.ImageSave);
        inputTaskTitle=findViewById(R.id.inputTaskTitle);
        inputTaskSubitle=findViewById(R.id.inputTaskSubitle);
        inputTaskDescription=findViewById(R.id.inputTaskDescription);
        colorSubtitleIndicator= findViewById(R.id.viewSubtitleIndicator);
        selectNoteColor = "#333333";
        initMiscellenous();

        ImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Create_task.this, todo_activity.class);
                startActivity(intent);
            }
        });

        ImageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTasks();
            }
        });


    }

    private void saveTasks() {
        if (inputTaskTitle.getText().toString().trim().isEmpty()){
            toast = new Toast(getApplicationContext());
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_layout,
                    (ViewGroup) findViewById(R.id.custom_toast_container));
            text = (TextView) layout.findViewById(R.id.text);
            text.setText("Title can't be empty!");
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
            return;
        }
        else if (inputTaskDescription.getText().toString().trim().isEmpty()){
            toast = new Toast(getApplicationContext());
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_layout,
                    (ViewGroup) findViewById(R.id.custom_toast_container));
            text = (TextView) layout.findViewById(R.id.text);
            text.setText("Description can't be empty!");
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
            return;
        }

          final Task  task = new Task();
            task.setTitle(inputTaskTitle.getText().toString());
            task.setSubtitle(inputTaskSubitle.getText().toString());
            task.setDescription(inputTaskDescription.getText().toString());
            task.setColor(selectNoteColor);

            @SuppressLint("StaticFieldLeak")
            class saveTask extends AsyncTask<Void,Void,Void>{

                @Override
                protected Void doInBackground(Void... voids) {
                    TaskDataBase.getTaskDataBase(getApplicationContext()).taskDao().insertTask(task);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {

                    super.onPostExecute(aVoid);
                    Intent intent = new Intent();
                    setResult(RESULT_OK,intent);
                    finish();

                }
            }
            new saveTask().execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Create_task.this, todo_activity.class);
        startActivity(intent);
    }

    private void setSubtitleIndicator() {
        GradientDrawable gradientDrawable = (GradientDrawable) colorSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectNoteColor));
    }

    public void initMiscellenous() {
        final LinearLayout layoutMiscellenousTodo = findViewById(R.id.layoutMiscellenoustodo);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellenousTodo);
        layoutMiscellenousTodo.findViewById(R.id.layoutMiscellenoustodo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        final ImageView imageColor1 = layoutMiscellenousTodo.findViewById(R.id.Color1);
        final ImageView imageColor2 = layoutMiscellenousTodo.findViewById(R.id.Color2);
        final ImageView imageColor3 = layoutMiscellenousTodo.findViewById(R.id.Color3);
        final ImageView imageColor4 = layoutMiscellenousTodo.findViewById(R.id.Color4);
        final ImageView imageColor5 = layoutMiscellenousTodo.findViewById(R.id.Color5);
        final ImageView imageColor6 = layoutMiscellenousTodo.findViewById(R.id.Color6);
        final ImageView imageColor7 = layoutMiscellenousTodo.findViewById(R.id.Color7);

        imageColor1.setImageResource(R.drawable.ic_done);
        imageColor2.setImageResource(0);
        imageColor3.setImageResource(0);
        imageColor4.setImageResource(0);
        imageColor5.setImageResource(0);
        imageColor6.setImageResource(0);
        imageColor7.setImageResource(0);



        layoutMiscellenousTodo.findViewById(R.id.Color1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectNoteColor="#333333";
                imageColor1.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                imageColor6.setImageResource(0);
                imageColor7.setImageResource(0);
                setSubtitleIndicator();
            }});

        layoutMiscellenousTodo.findViewById(R.id.view_color2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNoteColor="#3700B3";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(R.drawable.ic_done);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                imageColor6.setImageResource(0);
                imageColor7.setImageResource(0);
                setSubtitleIndicator();

            }
        });

        layoutMiscellenousTodo.findViewById(R.id.view_color3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNoteColor="#6200EE";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(R.drawable.ic_done);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                imageColor6.setImageResource(0);
                imageColor7.setImageResource(0);
                setSubtitleIndicator();

            }
        });

        layoutMiscellenousTodo.findViewById(R.id.view_color4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNoteColor="#ea384d";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(R.drawable.ic_done);
                imageColor5.setImageResource(0);
                imageColor6.setImageResource(0);
                imageColor7.setImageResource(0);
                setSubtitleIndicator();

            }
        });

        layoutMiscellenousTodo.findViewById(R.id.view_color5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNoteColor="#fcbd38";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(R.drawable.ic_done);
                imageColor6.setImageResource(0);
                imageColor7.setImageResource(0);
                setSubtitleIndicator();

            }
        });

        layoutMiscellenousTodo.findViewById(R.id.view_color6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNoteColor="#f7931e";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                imageColor6.setImageResource(R.drawable.ic_done);
                imageColor7.setImageResource(0);
                setSubtitleIndicator();

            }
        });

        layoutMiscellenousTodo.findViewById(R.id.view_color7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNoteColor="#5fe36a";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                imageColor6.setImageResource(0);
                imageColor7.setImageResource(R.drawable.ic_done);
                setSubtitleIndicator();

            }
        });
    }


}