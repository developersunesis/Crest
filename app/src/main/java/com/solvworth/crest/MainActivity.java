package com.solvworth.crest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends Activity {

    PrefManager pref;
    DatabaseManager databaseManager;
    ListView listView;
    ImageView sound;
    TextView text;
    Button play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = new PrefManager(this);

        databaseManager = new DatabaseManager(this);
        databaseManager.open();
        listView = (ListView) findViewById(R.id.scores);
        text = (TextView) findViewById(R.id.none);
        play = (Button) findViewById(R.id.play);
        sound = (ImageView) findViewById(R.id.sound);

        if(pref.getSoundState()){
            sound.setImageDrawable(getResources().getDrawable(R.drawable.sound_on));
        } else {
            sound.setImageDrawable(getResources().getDrawable(R.drawable.sound_off));
        }

        listView.setClickable(false);

        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pref.getSoundState()){
                pref.setSoundState(false);
                sound.setImageDrawable(getResources().getDrawable(R.drawable.sound_off));
            } else {
                pref.setSoundState(true);
                sound.setImageDrawable(getResources().getDrawable(R.drawable.sound_on));
            }

            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputName();
            }
        });
        populateDatabase();

    }

    public void inputName(){
        final AlertDialog alert = new AlertDialog.Builder(this).create();
        LayoutInflater factory = LayoutInflater.from(this);
        View dialog = factory.inflate(R.layout.dialog, null);
        alert.setView(dialog);
        alert.setTitle("ENTER YOUR NAME PLEASE.");
        final AutoCompleteTextView name = (AutoCompleteTextView) dialog.findViewById(R.id.name);
        Button proceed = (Button) dialog.findViewById(R.id.proceed);
        Cursor cursor = databaseManager.fetchAllScores();
        startManagingCursor(cursor);
        ArrayList arr =new ArrayList();
        while(cursor.moveToNext()){
            arr.add(cursor.getString(cursor.getColumnIndex("name")));
        }

        ArrayAdapter adapter = new ArrayAdapter
                (this,android.R.layout.select_dialog_item, arr);
        name.setThreshold(1);
        name.setAdapter(adapter);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!name.getText().toString().isEmpty()) {
                    Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                    intent.putExtra("name", name.getText().toString());
                    alert.dismiss();
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Please input a name.", Toast.LENGTH_LONG).show();
                }
            }
        });
        alert.show();
    }

    private void populateDatabase() {
        Cursor cursor = databaseManager.fetchAllScores();
        startManagingCursor(cursor);
        String[] fromDatabase = {"name", "score"};
        int[] to = {R.id.name, R.id.score};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.item, cursor, fromDatabase, to);
        if (cursor.getCount() != 0){
            listView.setAdapter(adapter);
            listView.setClickable(false);
            registerForContextMenu(listView);
            text.setVisibility(View.GONE);
        } else {
            text.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }

    }

    public void onResume(){
        super.onResume();
        if(pref.getSoundState()){
            sound.setImageDrawable(getResources().getDrawable(R.drawable.sound_on));
        } else {
            sound.setImageDrawable(getResources().getDrawable(R.drawable.sound_off));
        }
        populateDatabase();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.list_longpress, menu);
    }

    @Override
    public boolean onContextItemSelected( final MenuItem item) {
        switch(item.getItemId()) {
            case R.id.deleteTable:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setIcon(R.drawable.alert);
                alertDialog.setTitle("Delete");
                alertDialog.setMessage("Do you really want to delete this data?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                        databaseManager.deleteScore(info.id);
                        populateDatabase();
                        Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alert = alertDialog.create();
                alert.show();
        }
        return super.onContextItemSelected(item);
    }

}
