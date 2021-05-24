package com.example.hellonotes.Acitivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hellonotes.Acitivities.CreateNotesActivity;
import com.example.hellonotes.Adapters.Note_CardAdapter;
import com.example.hellonotes.Database.NoteDataBase;
import com.example.hellonotes.Listeners.NotesListener;
import com.example.hellonotes.R;
import com.example.hellonotes.entities.Note;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesListener, androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener {
    private ImageView ImageAddNote ,imageViewList,imageWebLink,imageAddPhoto;
    private static final int REQUEST_CODE_ADD_NOTE=1;
    private static final int REQUEST_CODE_UPDATE_NOTE=2;
    private static final int REQUEST_CODE_SHOW_NOTE=3;
    private static final int REQUEST_CODE_STORAGE_PERMISSION=4;
    private static final int REQUEST_CODE_SELECT_IMAGE=5;
    private AlertDialog addURLDialog;
    private RecyclerView notes_recyclerView;
    private List<Note> notesList;
    private EditText inputSearch;
    private Note_CardAdapter adapter;
    private int NoteClickPosition=-1;
    public int SpanCount=2;
    private LinearLayout linearLayout;
    private TextView nothing_to_show;
    private ConstraintLayout todoApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageAddNote=findViewById(R.id.imageAddNote);
        inputSearch=findViewById(R.id.inputSearch);
        imageViewList=findViewById(R.id.imageViewList);
        imageWebLink=findViewById(R.id.imageWebLink);
        imageAddPhoto=findViewById(R.id.imageAddPhoto);
        linearLayout=findViewById(R.id.layout_note);
        todoApp=findViewById(R.id.todoApp);
        ImageAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getApplicationContext(), CreateNotesActivity.class),
                        REQUEST_CODE_ADD_NOTE
                );
            }
        });
        notes_recyclerView=findViewById(R.id.notesRecyclerView);
        notesList=new ArrayList<>();
        adapter=new Note_CardAdapter(notesList,this);
        notes_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(SpanCount, StaggeredGridLayoutManager.VERTICAL));
        notes_recyclerView.setAdapter(adapter);
        getNotes(REQUEST_CODE_SHOW_NOTE,false);

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.cancelTimer(); }

            @Override
            public void afterTextChanged(Editable s) {
                if (notesList.size()!=0){
                    adapter.searchNotes(s.toString());
                }

            }});

        imageViewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this,v);
                popupMenu.setOnMenuItemClickListener(MainActivity.this);
                popupMenu.inflate(R.menu.view_menu);
                popupMenu.show();
            }
        });
        imageAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION); }
                else{
                    selectImage();
                }
            }
        }
                );

        imageWebLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_URL_dialog();
            }
        });

        // TODO: 28-10-2020
        todoApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,todo_activity.class);
                startActivity(intent);
                finish();
            }
        });
    }
        private void show_URL_dialog(){
        if (addURLDialog==null){
            AlertDialog.Builder  alertDialog= new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(getApplicationContext()).inflate
                    (R.layout.layout_url,
                            (ViewGroup)findViewById(R.id.AddUrlContainer));

            alertDialog.setView(view);
            alertDialog.setCancelable(false);
            addURLDialog=alertDialog.create();
            if (addURLDialog.getWindow()!=null){
                addURLDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final EditText inputText = view.findViewById(R.id.inputUrl);
            inputText.requestFocus();

            view.findViewById(R.id.textAddUrl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (inputText.getText().toString().trim().isEmpty()){
                        Toast.makeText(MainActivity.this, "Enter URL !", Toast.LENGTH_SHORT).show();
                    }
                    else if (!Patterns.WEB_URL.matcher(inputText.getText().toString()).matches()){
                        Toast.makeText(MainActivity.this, "Enter valid URL!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        addURLDialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(),CreateNotesActivity.class);
                        intent.putExtra("isFromQuickAction",true);
                        intent.putExtra("quickActionType","URL");
                        intent.putExtra("URL",inputText.getText().toString());
                        startActivityForResult(intent,REQUEST_CODE_ADD_NOTE);
                    }
                }
            });
            view.findViewById(R.id.cancelUrl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addURLDialog.dismiss();
                }
            });

        }

        addURLDialog.show();
    }

    private void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_CODE_STORAGE_PERMISSION && grantResults.length>0 ){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                selectImage();
            }
            else{
                Toast.makeText(this, "Permission Denied !", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private  String getPathFromUri(Uri contentUri){
        String filepath;
        Cursor cursor = getContentResolver().query(contentUri,null,null,null,null);
        if (cursor==null){
            filepath=contentUri.getPath();
        }
        else {
            cursor.moveToFirst();
            int index= cursor.getColumnIndex("_data");
            filepath=cursor.getString(index);
            cursor.close();
        }
        return filepath ;
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.defaultView:
                notes_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                return true;
            case R.id.LargeView:
                notes_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
                return true;
            case R.id.SmallView:
                notes_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
                return true;

                case R.id.listView:
                SpanCount=1;
                linearLayout.findViewById(R.id.NotesImage).setVisibility(View.GONE);
                linearLayout.findViewById(R.id.textSubtitle).setVisibility(View.GONE);
                linearLayout.findViewById(R.id.textTime).setVisibility(View.GONE);
                return true;

            default:
                return false;
        }
    }
    @Override
    public void OnNoteClicked(Note note, int position) {
        NoteClickPosition=position;
        Intent intent = new Intent(getApplicationContext(), CreateNotesActivity.class);
        intent.putExtra("isViewOrUpdate",true);
        intent.putExtra("note",note);
        startActivityForResult(intent,REQUEST_CODE_UPDATE_NOTE);
    }

    private void getNotes(final int requestCode , final boolean isNoteDeleted ){

        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask <Void , Void , List<Note>>{


            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NoteDataBase.getNoteDataBase(
                        getApplicationContext()).noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);

                if (requestCode==REQUEST_CODE_SHOW_NOTE){
                    notesList.addAll(notes);
                    adapter.notifyDataSetChanged();
                }
                else if(requestCode==REQUEST_CODE_ADD_NOTE){
                    notesList.add(0,notes.get(0));
                    adapter.notifyItemInserted(0);
                    notes_recyclerView.smoothScrollToPosition(0);
                }

                else if (requestCode==REQUEST_CODE_UPDATE_NOTE) {

                    if (isNoteDeleted){
                        notesList.remove(NoteClickPosition);
                        adapter.notifyItemRemoved(NoteClickPosition);
                    }

                    else {
                        notesList.remove(NoteClickPosition);
                        notesList.add(NoteClickPosition,notes.get(NoteClickPosition));
                        adapter.notifyItemChanged(NoteClickPosition);
                    }
                     }

            }
            }

        new GetNotesTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_ADD_NOTE && resultCode==RESULT_OK){
            getNotes(REQUEST_CODE_ADD_NOTE,false);
        }
        else if
             (requestCode==REQUEST_CODE_UPDATE_NOTE && resultCode==RESULT_OK ){
             if(data!=null)
                getNotes(REQUEST_CODE_UPDATE_NOTE,data.getBooleanExtra("isNoteDeleted",false));
            }

        else if (requestCode==REQUEST_CODE_SELECT_IMAGE &&  resultCode==RESULT_OK){

            if (data!=null){
                Uri selectedImageUri= data.getData();
                if (selectedImageUri!=null){
                    try {
                        String selectedImagePath = getPathFromUri(selectedImageUri);
                        Intent intent = new Intent(getApplicationContext(),CreateNotesActivity.class);
                        intent.putExtra("isFromQuickAction",true);
                        intent.putExtra("quickActionType","image");
                        intent.putExtra("imagePath",selectedImagePath);
                        startActivityForResult(intent,REQUEST_CODE_ADD_NOTE);
                    }catch (Exception e){

                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    } } } }
    }
}

