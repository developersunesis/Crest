package com.solvworth.crest;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Splash extends Activity implements Animator.AnimatorListener {

    ImageView square;
    int state = 1;
    RelativeLayout layout;
    TextView t1, t2, t3;
    PrefManager pref;
    DatabaseManager databaseManager;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        mediaPlayer = MediaPlayer.create(this, R.raw.pop);
        pref = new PrefManager(this);
        databaseManager = new DatabaseManager(this);

        if(!pref.getDbState()){
            databaseManager.open();
            databaseManager.addScore("Sunesis", "1000000");
            databaseManager.close();
            pref.setDbState(true);
        }

        square = (ImageView) findViewById(R.id.square);
        layout = (RelativeLayout) findViewById(R.id.dad);
        t1 = (TextView) findViewById(R.id.textView);
        t2 = (TextView) findViewById(R.id.textView2);
        t3 = (TextView) findViewById(R.id.textView3);
        t1.setVisibility(View.GONE);
        t2.setVisibility(View.GONE);
        t3.setVisibility(View.GONE);
        translate(1000, getResources().getColor(R.color.first));
        mediaPlayer.start();
    }


    public void rotate(View v, float rotation, int startDelay, int stage){
        state = stage;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            v.animate()
                    .setDuration(500)
                    .rotation(rotation)
                    .setInterpolator(new LinearInterpolator())
                    .setStartDelay(startDelay);
        } else {
            v.animate()
                    .setDuration(500)
                    .rotation(rotation)
                    .setInterpolator(new LinearInterpolator());
        }
        v.clearAnimation();
    }

    public void translate(int startDelay, int color){

        ImageView v = new ImageView(this);
        v.setLayoutParams(new ViewGroup.LayoutParams(50,50));
        v.setMaxWidth(50);
        v.setMaxWidth(50);
        v.setX((getScreenWidth()/2)-25);
        v.setImageDrawable(Shape.drawBall(50, 50, color));
        layout.addView(v, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            v.animate()
                    .setDuration(1000)
                    .translationY(-10)
                    .translationYBy(getScreenHeight()-190)
                    .setInterpolator(new LinearInterpolator())
                    .setStartDelay(startDelay)
            .setListener(this);
        } else {
            v.animate()
                    .setDuration(1000)
                    .translationY(-10)
                    .translationYBy(getScreenHeight()-190)
                    .setInterpolator(new LinearInterpolator())
            .setListener(this);
        }
        v.clearAnimation();
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
        animation.removeAllListeners();
        if(state == 1) {
            translate(1000, getResources().getColor(R.color.second));
            mediaPlayer.start();
            rotate(square, state * 90, 500, 2);

        } else if(state == 2) {
            rotate(square, state * 90, 500, 3);
            mediaPlayer.start();
            translate(1000, getResources().getColor(R.color.third));
        } else if(state == 3) {
            rotate(square, state * 90, 500, 4);
            mediaPlayer.start();
            translate(1000, getResources().getColor(R.color.fourth));
        } else if(state == 4){
            mediaPlayer.stop();
            t1.setVisibility(View.VISIBLE);
            t2.setVisibility(View.VISIBLE);
            t3.setVisibility(View.VISIBLE);
            startScan();
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    public void startScan() {
        new Thread() {

            public void run() {

                try {
                    // This is just a tmp sleep so that we can emulate something loading
                    Thread.sleep(3000);
                    // Use this handler so than you can update the UI from a thread
                    Refresh.sendEmptyMessage(0);
                } catch (Exception e) {
                }
            }
        }.start();
    }

    // Refresh handler, necessary for updating the UI in a/the thread
    Handler Refresh = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case 0:
                    //start main
                    finish();
                    overridePendingTransition(0,0);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    break;

                default:
                    break;
            }
        }
    };


}
