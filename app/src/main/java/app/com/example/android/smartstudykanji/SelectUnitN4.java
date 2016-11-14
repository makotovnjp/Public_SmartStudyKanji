package app.com.example.android.smartstudykanji;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.view.View.VISIBLE;

public class SelectUnitN4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_unit_n4);

        //N4 Unit 1 listener
        TextView N4_unit1 = (TextView) findViewById(R.id.N4_unit1);

        N4_unit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Levelを設定する */
                LevelController.mlevel = LevelController.N4_level_1;

                Intent numbersIntent = new Intent(SelectUnitN4.this, DisplayActivity.class);
                startActivity(numbersIntent);
            }
        });

        //N4 Unit 2 listener
        TextView N4_unit2 = (TextView) findViewById(R.id.N4_unit2);

        N4_unit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Levelを設定する */
                LevelController.mlevel = LevelController.N4_level_2;

                Intent numbersIntent = new Intent(SelectUnitN4.this, DisplayActivity.class);
                startActivity(numbersIntent);
            }
        });
        //set OnClick Listener for Progress
        TextView progressBar= (TextView) findViewById(R.id.progress);
        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent progress_activity = new Intent(SelectUnitN4.this, ProgressActivity.class);
                startActivity(progress_activity);
            }
        });

        //set OnClick Listener for Feedback
        TextView feedBack= (TextView) findViewById(R.id.feedback);
        feedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent feedback_activity = new Intent(SelectUnitN4.this, FeedBack.class);
                startActivity(feedback_activity);
            }
        });

        //set OnClick Listener for AboutUs
        TextView aboutUs= (TextView) findViewById(R.id.aboutus);
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aboutus_activity = new Intent(SelectUnitN4.this, AboutUs.class);
                startActivity(aboutus_activity);
            }
        });
    }
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
