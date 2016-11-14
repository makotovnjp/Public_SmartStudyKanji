package app.com.example.android.smartstudykanji;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static android.content.Intent.ACTION_SENDTO;

public class FeedBack extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
    }

    public void sendFeedback(View view) {
        EditText edit_text;
        String content_feedback;

        //Get feedback content
        edit_text = (EditText) findViewById(R.id.edit_feed_back);
        content_feedback = edit_text.getText().toString();

        //Send to Email
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String []{"makotovnjp@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT,"Feedback for SmartStudyKanji");
        intent.putExtra(Intent.EXTRA_TEXT, content_feedback);

        try {
            startActivity(Intent.createChooser(intent, "Choose Email Client"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "client not found", Toast.LENGTH_LONG).show();
        }
    }
}
