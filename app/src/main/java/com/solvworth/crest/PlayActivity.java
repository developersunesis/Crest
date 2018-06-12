package com.solvworth.crest;

import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class PlayActivity extends Activity implements View.OnClickListener, Animator.AnimatorListener {

    String name;
    ImageView square;
    int state = 0, value, time = 4500, score, closed = 0;
    RelativeLayout layout;
    boolean fall = true;
    TextView points;
    DatabaseManager databaseManager;
    MediaPlayer mediaPlayer, lose;
    PrefManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Intent intent = getIntent();
        pref = new PrefManager(this);
        name = intent.getStringExtra("name");
        databaseManager = new DatabaseManager(this);
        databaseManager.open();

        if(databaseManager.fetchScore(name).getCount() == 0){
            databaseManager.addScore(name, ""+0);
        }

        points = (TextView) findViewById(R.id.points);
        layout = (RelativeLayout) findViewById(R.id.dad);
        square = (ImageView) findViewById(R.id.square);
        square.setOnClickListener(this);
        mediaPlayer = MediaPlayer.create(this, R.raw.pop);
        lose = MediaPlayer.create(this, R.raw.lose);
        initPoints(0);
        initGame();
    }

    private void initPoints(int i) {
        points.setText("POINTS: "+ i);
    }

    private void initGame() {
        Random rand = new Random();
        value = rand.nextInt(4);
        switch (value){
            case 0:
                translate(time, getResources().getColor(R.color.first));
                break;
            case 1:
                translate(time, getResources().getColor(R.color.second));
                break;
            case 2:
                translate(time, getResources().getColor(R.color.third));
                break;
            case 3:
                translate(time, getResources().getColor(R.color.fourth));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if(v == square){
             state++;
            rotate(square, state * 90, state++);
        }
    }

    public void rotate(View v, float rotation, int stage){
        state = stage;
            v.animate()
                    .setDuration(200)
                    .rotation(rotation)
                    .setInterpolator(new LinearInterpolator());
    }

    public void translate(int duration, int color){

        ImageView v = new ImageView(this);
        v.setLayoutParams(new ViewGroup.LayoutParams(50,50));
        v.setMaxWidth(50);
        v.setMaxWidth(50);
        v.setX((getScreenWidth()/2)-25);
        v.setImageDrawable(Shape.drawBall(50, 50, color));
        layout.addView(v, 0);
            v.animate()
                    .setDuration(duration)
                    .translationY(-10)
                    .translationYBy(getScreenHeight()-215)
                    .setInterpolator(new LinearInterpolator())
                    .setListener(this);
    }

    public int getScreenHeight(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public int getScreenWidth(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if(closed == 0){
            if (value != (state % 4)) {
                if(pref.getSoundState())
                    lose.start();
                huhFailed();
            } else {
                if(pref.getSoundState())
                    mediaPlayer.start();
                score = score + 100;
                initPoints(score);
                Cursor c = databaseManager.fetchScore(name);
                startManagingCursor(c);
                if (c.getInt(c.getColumnIndexOrThrow("score")) < score) {
                    databaseManager.updateScore(name, "" + score);
                }

                if (time > 1000) {
                    if (score == 1000)
                        time = time - 100;
                    else if (score == 1200)
                        time = time - 100;
                    else if (score == 1300)
                        time = time - 100;
                    else if (score == 1400)
                        time = time - 100;
                    else if (score == 1500)
                        time = time - 100;
                    else if (score == 1600)
                        time = time - 100;
                    else if (score == 1700)
                        time = time - 100;
                    else if (score == 2000)
                        time = time - 100;
                    else if (score == 3000)
                        time = time - 100;
                    else if (score == 4000)
                        time = time - 300;
                    else if (score == 5000)
                        time = time - 300;
                    else if (score == 6000)
                        time = time - 300;
                    else if (score == 6500)
                        time = time - 100;
                    else if (score == 7000)
                        time = time - 500;
                    else if (score == 7500)
                        time = time - 500;
                    else if (score == 10000)
                        time = time - 50;
                    else if (score == 15000)
                        time = time - 50;
                    else if (score == 20000)
                        time = time - 50;
                    else if (score == 25000)
                        time = time - 50;
                }
                if (fall) {
                    initGame();
                }
            }
        }
    }

    private void huhFailed() {
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle("Failed");
        alert.setMessage("Unfortunately you boxed the wrong ball. Do you want to try again?");
        alert.setCancelable(false);
        alert.setButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                score = 0;
                time = 4500;
                initPoints(score);
                initGame();
            }
        });
        alert.setButton2("No, I Quit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        alert.setButton3("Help", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "You just have to tap the box to match the falling ball's color", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        alert.show();
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    public void onBackPressed(){
        closed = 1;
        lose.stop();
        mediaPlayer.stop();
        databaseManager.close();
        super.onBackPressed();
        finish();
    }
}
