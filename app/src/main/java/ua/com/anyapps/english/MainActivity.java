package ua.com.anyapps.english;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "debapp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void firstVideoClick(View v)
    {
        Intent i = new Intent(this, PlayerActivity.class);
        i.putExtra("video", 1);
        startActivity(i);
    }
}
