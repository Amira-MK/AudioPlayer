package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new Handler().postDelayed(new Runnable() {


            @Override

            public void run() {

                Intent i = new Intent(MainActivity.this, MainActivity2.class);

                startActivity(i);

                // close this activity

                finish();

            }

        }, 5*1000); // wait for 5 seconds

        ImageView image = findViewById(R.id.logo_id);
        Animation animation =
                AnimationUtils.loadAnimation(this, R.anim.rotation);
        image.startAnimation(animation);








    }
}