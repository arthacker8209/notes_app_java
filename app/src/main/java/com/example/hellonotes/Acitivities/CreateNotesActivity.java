package com.example.hellonotes.Acitivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hellonotes.Adapters.Note_CardAdapter;
import com.example.hellonotes.Database.NoteDataBase;
import com.example.hellonotes.R;
import com.example.hellonotes.entities.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNotesActivity extends AppCompatActivity {
    private ImageView ImageBack;
    private EditText inputNoteTitle , inputNoteSubtitle, inputNote;
    private TextView textDatetime;
    private ImageView saveImage;
    private String selectNoteColor;
    private View colorSubtitleIndicator;
    private static final int REQUEST_CODE_STORAGE_PERMISSION=1;
    private static final int REQUEST_CODE_SELECT_IMAGE=2;
    private ImageView noteImage;
    private String selectedImagePath;
    private TextView Url;
    private LinearLayout AddWebUrl ;
    private AlertDialog AddUrlDialog ,deleteNoteDialog;
    private Note alreadyAvailableNote;
    private ImageView imageDeleteImage, image_urlDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notes);
        ImageBack=findViewById(R.id.ImageBack);
        inputNoteTitle=findViewById(R.id.inputNoteTitle);
        inputNoteSubtitle=findViewById(R.id.inputNoteSubitle);
        inputNote=findViewById(R.id.inputNote);
        textDatetime=findViewById(R.id.textDateTime);
        saveImage=findViewById(R.id.ImageSave);
        colorSubtitleIndicator=findViewById(R.id.viewSubtitleIndicator);
        noteImage=findViewById(R.id.imageNote);
        Url=findViewById(R.id.Url);
        AddWebUrl=findViewById(R.id.layout_web_url);
        image_urlDelete=findViewById(R.id.image_urlRemove);
        imageDeleteImage=findViewById(R.id.imageDeleteImage);

        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });
        textDatetime.setText(new SimpleDateFormat("EEEE, dd MMMM YYYY HH:mm a", Locale.getDefault()).format(new Date()));
        ImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        selectNoteColor="#333333";
        selectedImagePath="";
        Url.setTextColor(Color.parseColor(selectNoteColor));
        setSubtitleIndicator();
        if (getIntent().getBooleanExtra("isViewOrUpdate",false)){
            alreadyAvailableNote=(Note)getIntent().getSerializableExtra("note");
            setViewOrUpdate();
        }
        if (getIntent().getBooleanExtra("isFromQuickAction",false)) {
            String type = getIntent().getStringExtra("quickActionType");
            if (type != null) {
                if (type.equals("image")) {
                    selectedImagePath = getIntent().getStringExtra("imagePath");
                    noteImage.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
                    noteImage.setVisibility(View.VISIBLE);
                    imageDeleteImage.setVisibility(View.VISIBLE);

                } else if (type.equals("URL")) {
                    Url.setText(getIntent().getStringExtra("URL"));
                    AddWebUrl.setVisibility(View.VISIBLE);
                    image_urlDelete.setVisibility(View.VISIBLE);
                }
            }
            }
        image_urlDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Url.setText(null);
                Url.setVisibility(View.GONE);
                image_urlDelete.setVisibility(View.GONE);


            }
        });

        imageDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteImage.setImageBitmap(null);
                noteImage.setVisibility(View.GONE);
                imageDeleteImage.setVisibility(View.GONE);
                selectedImagePath="";
            }
        });

        initMiscellenous();
    }

    private  void setViewOrUpdate(){
            inputNoteTitle.setText(alreadyAvailableNote.getTitle());
            inputNoteSubtitle.setText(alreadyAvailableNote.getSubtitle());
            inputNote.setText(alreadyAvailableNote.getNoteText());
            textDatetime.setText(alreadyAvailableNote.getDateTime());

            if(alreadyAvailableNote.getImagePath()!=null && !alreadyAvailableNote.getImagePath().trim().isEmpty()){
                noteImage.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImagePath()));
                noteImage.setVisibility(View.VISIBLE);
                imageDeleteImage.setVisibility(View.VISIBLE);
                selectedImagePath=alreadyAvailableNote.getImagePath();
            }

            if (alreadyAvailableNote.getWebLink()!=null && !alreadyAvailableNote.getWebLink().trim().isEmpty()){
                Url.setText(alreadyAvailableNote.getWebLink());
                AddWebUrl.setVisibility(View.VISIBLE);
                image_urlDelete.setVisibility(View.VISIBLE);

            }


    }


    private void saveNote(){
        if(inputNoteTitle.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Title Can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }else if(inputNote.getText().toString().trim().isEmpty() &&
                inputNoteSubtitle.getText().toString().trim().isEmpty()){

            Toast.makeText(this, "Note Can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        final Note note = new Note();
        note.setTitle(inputNoteTitle.getText().toString());
        note.setSubtitle(inputNoteSubtitle.getText().toString());
        note.setNoteText(inputNote.getText().toString());
        note.setDateTime(textDatetime.getText().toString());
        note.setColor(selectNoteColor);
        note.setImagePath(selectedImagePath);
        if (AddWebUrl.getVisibility() == View.VISIBLE){
            note.setWebLink(Url.getText().toString());
        }

        if(alreadyAvailableNote!=null){
            note.setId(alreadyAvailableNote.getId());
        }

        @SuppressLint("StaticFieldLeak")
        class saveNoteTask extends AsyncTask<Void , Void ,Void>{

            @Override
            protected Void doInBackground(Void... voids) {
                NoteDataBase.getNoteDataBase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent =new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        }

        new saveNoteTask().execute();


    }

    public void initMiscellenous(){
        final LinearLayout layoutMiscellenous =findViewById(R.id.layoutMiscellenous);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellenous);
        layoutMiscellenous.findViewById(R.id.layoutMiscellenous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState()!= BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else{
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });


        final ImageView imageColor1 = layoutMiscellenous.findViewById(R.id.Color1);
        final ImageView imageColor2 = layoutMiscellenous.findViewById(R.id.Color2);
        final ImageView imageColor3 = layoutMiscellenous.findViewById(R.id.Color3);
        final ImageView imageColor4 = layoutMiscellenous.findViewById(R.id.Color4);
        final ImageView imageColor5 = layoutMiscellenous.findViewById(R.id.Color5);
        final ImageView imageColor6 = layoutMiscellenous.findViewById(R.id.Color6);
        final ImageView imageColor7 = layoutMiscellenous.findViewById(R.id.Color7);

        imageColor1.setImageResource(R.drawable.ic_done);
        imageColor2.setImageResource(0);
        imageColor3.setImageResource(0);
        imageColor4.setImageResource(0);
        imageColor5.setImageResource(0);
        imageColor6.setImageResource(0);
        imageColor7.setImageResource(0);

        layoutMiscellenous.findViewById(R.id.view_color1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNoteColor="#333333";
                imageColor1.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                imageColor6.setImageResource(0);
                imageColor7.setImageResource(0);
                setSubtitleIndicator();

            }
        });

        layoutMiscellenous.findViewById(R.id.view_color2).setOnClickListener(new View.OnClickListener() {
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

        layoutMiscellenous.findViewById(R.id.view_color3).setOnClickListener(new View.OnClickListener() {
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

        layoutMiscellenous.findViewById(R.id.view_color4).setOnClickListener(new View.OnClickListener() {
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

        layoutMiscellenous.findViewById(R.id.view_color5).setOnClickListener(new View.OnClickListener() {
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

        layoutMiscellenous.findViewById(R.id.view_color6).setOnClickListener(new View.OnClickListener() {
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

        layoutMiscellenous.findViewById(R.id.view_color7).setOnClickListener(new View.OnClickListener() {
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

        if (alreadyAvailableNote!=null&& alreadyAvailableNote.getColor()!=null && !alreadyAvailableNote.getColor().trim().isEmpty()){
            switch (alreadyAvailableNote.getColor()) {
                case "#3700B3":
                    layoutMiscellenous.findViewById(R.id.view_color2).performClick();
                    break;
                case "#6200EE":
                    layoutMiscellenous.findViewById(R.id.view_color3).performClick();
                    break;

                case "#ea384d":
                    layoutMiscellenous.findViewById(R.id.view_color4).performClick();
                    break;

                case "#fcbd38":
                    layoutMiscellenous.findViewById(R.id.view_color5).performClick();
                    break;

                case "#f7931e":
                    layoutMiscellenous.findViewById(R.id.view_color6).performClick();
                    break;

                case "#5fe36a":
                    layoutMiscellenous.findViewById(R.id.view_color7).performClick();

            }
        }
        layoutMiscellenous.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if(ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(
                            CreateNotesActivity.this,
                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_STORAGE_PERMISSION
                    );
                }
                else{
                    selectImage();
                } }
        });
        //------------------------------------------Add URL ---------------------------------------------------------------------//
        layoutMiscellenous.findViewById(R.id.layoutAddUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showDialog();
            }
        });


     //---------------------------------------Delete Note ----------------------------------------------//
        if (alreadyAvailableNote!=null){

            layoutMiscellenous.findViewById(R.id.layoutDeleteNote).setVisibility(View.VISIBLE);
            layoutMiscellenous.findViewById(R.id.layoutDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    deleteDialog();
                }
            });

        }

    }
//---------------------------------------------------------------------------------------------------------------------------------------------------------//
    //----------------------------------DialogBox for DeleteNote------------------------------------------------------------------------------------------//


    private void deleteDialog(){
        if(deleteNoteDialog==null){
            AlertDialog.Builder alertDialogDeleteNote= new AlertDialog.Builder(CreateNotesActivity.this);
            View view=LayoutInflater.from(this).inflate(R.layout.layout_delete,
                    (ViewGroup)findViewById(R.id.DeleteContainer));
            alertDialogDeleteNote.setView(view);
            alertDialogDeleteNote.setCancelable(false);
            deleteNoteDialog=alertDialogDeleteNote.create();
            if (deleteNoteDialog.getWindow()!=null){

                deleteNoteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.Delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    @SuppressLint("StaticFieldLeak")
                    class DeleteNoteTask extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... voids) {
                            NoteDataBase.getNoteDataBase(getApplicationContext()).noteDao().deleteNote(alreadyAvailableNote);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Intent intent = new Intent();
                            intent.putExtra("isNotDeleted" ,true);
                            setResult(RESULT_OK,intent);
                            finish();

                        }
                    }
                    new DeleteNoteTask().execute();
                }
            });

            view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteNoteDialog.dismiss();
                }
            });
        }
        deleteNoteDialog.show();
    }



    //---------------------------------------------------------------------------------------------------------------------------------------//


    public void setSubtitleIndicator(){
        GradientDrawable gradientDrawable = (GradientDrawable) colorSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectNoteColor));

    }

    private void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,REQUEST_CODE_SELECT_IMAGE);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE_STORAGE_PERMISSION && grantResults.length>0){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                selectImage();
            }
            else{
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){

            if (data != null) {

                Uri selectedImageUri = data.getData();
                if(selectedImageUri!=null){
                    try{
                        InputStream inputStream =getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        noteImage.setImageBitmap(bitmap);
                        noteImage.setVisibility(View.VISIBLE);
                        imageDeleteImage.setVisibility(View.VISIBLE);
                        selectedImagePath=getPathfromUri(selectedImageUri);

                    }catch (Exception e){
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            }

        }
        private String getPathfromUri(Uri contentUri){

         String filePath;
            Cursor cursor= getContentResolver().query(
                    contentUri ,null,null,null,null
            );
            if (cursor== null){
                filePath=contentUri.getPath();
            }
            else {
                cursor.moveToFirst();
                int index = cursor.getColumnIndex("_data");
                filePath=cursor.getString(index);
                cursor.close();
            }
            return filePath;
        }

       private void showDialog(){
        if (AddUrlDialog== null){
            final AlertDialog.Builder alertDialog= new AlertDialog.Builder(
              CreateNotesActivity.this
              );
            View view= LayoutInflater.from(this).inflate(R.layout.layout_url,
                    (ViewGroup)findViewById(R.id.AddUrlContainer));

            alertDialog.setView(view);
            alertDialog.setCancelable(false);
            AddUrlDialog=alertDialog.create();
            if (AddUrlDialog.getWindow()!=null){

                AddUrlDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            }

            final EditText inputUrl = view.findViewById(R.id.inputUrl);
            inputUrl.requestFocus();
            view.findViewById(R.id.textAddUrl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (inputUrl.getText().toString().trim().isEmpty()){
                        Toast.makeText(CreateNotesActivity.this, "Enter Url", Toast.LENGTH_SHORT).show();
                    }
                    else if (!Patterns.WEB_URL.matcher(inputUrl.getText().toString()).matches()){

                        Toast.makeText(CreateNotesActivity.this, "Enter Valid Url", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Url.setText(inputUrl.getText().toString());
                        AddWebUrl.setVisibility(View.VISIBLE);
                        image_urlDelete.setVisibility(View.VISIBLE);
                        AddUrlDialog.dismiss();
                    }
                }
            });

            view.findViewById(R.id.cancelUrl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddUrlDialog.dismiss();
                }
            });
        }
            AddUrlDialog.show();
    }



}

