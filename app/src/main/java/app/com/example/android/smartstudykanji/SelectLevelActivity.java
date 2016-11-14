package app.com.example.android.smartstudykanji;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.ThreadFactory;

import static android.view.View.VISIBLE;

public class SelectLevelActivity extends AppCompatActivity {
    private ScoreController mscoreCtrl = new ScoreController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_level);

        //Score FileのDefault値の作成(アプリを最初に利用するときだけ)
        mscoreCtrl.makeScoreDataFileAtFirstTimeOnce();

        //set OnClick Listener for N3
        TextView N3Category = (TextView) findViewById(R.id.category_N3);
        N3Category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent numbersIntent = new Intent(SelectLevelActivity.this, SelectUnitN3.class);
                startActivity(numbersIntent);
            }
        });

        //set OnClick Listener for N4
        TextView N4Category = (TextView) findViewById(R.id.category_N4);
        N4Category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent numbersIntent = new Intent(SelectLevelActivity.this, SelectUnitN4.class);
                startActivity(numbersIntent);
            }
        });

        //set OnClick Listener for N5
        TextView N5Category = (TextView) findViewById(R.id.category_N5);
        N5Category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LevelController.mlevel = LevelController.N5_level;
                Intent numbersIntent = new Intent(SelectLevelActivity.this, DisplayActivity.class);
                startActivity(numbersIntent);
            }
        });

        //set OnClick Listener for Progress
        TextView progressBar= (TextView) findViewById(R.id.progress);
        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent progress_activity = new Intent(SelectLevelActivity.this, ProgressActivity.class);
                startActivity(progress_activity);
            }
        });

        //set OnClick Listener for Feedback
        TextView feedBack= (TextView) findViewById(R.id.feedback);
        feedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent feedback_activity = new Intent(SelectLevelActivity.this, FeedBack.class);
                startActivity(feedback_activity);
            }
        });


        //set OnClick Listener for AboutUs
        TextView aboutUs= (TextView) findViewById(R.id.aboutus);
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aboutus_activity = new Intent(SelectLevelActivity.this, AboutUs.class);
                startActivity(aboutus_activity);
            }
        });
    }

    /* Display menu by clicking floating button*/

    public void displayMenu(View view) {
        TextView menu1 = (TextView) findViewById(R.id.progress);
        boolean menu1Visibility = menu1.isShown();
        if (menu1Visibility) {
            menu1.setVisibility(View.INVISIBLE);
        } else {
            menu1.setVisibility(VISIBLE);
        }
        TextView menu2 = (TextView) findViewById(R.id.feedback);
        boolean menu2Visibility = menu2.isShown();
        if (menu2Visibility) {
            menu2.setVisibility(View.INVISIBLE);
        } else {
            menu2.setVisibility(VISIBLE);
        }
        TextView menu3 = (TextView) findViewById(R.id.aboutus);
        boolean menu3Visibility = menu3.isShown();
        if (menu3Visibility) {
            menu3.setVisibility(View.INVISIBLE);
        } else {
            menu3.setVisibility(VISIBLE);
        }
    }
}
