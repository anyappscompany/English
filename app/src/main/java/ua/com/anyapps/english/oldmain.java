/*
package ua.com.anyapps.english;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "debapp";
    VideoView vvPlayer;
    int Video_STATE_POSITION  = 0;

    TextView tvFirstWord;
    TextView tvSecondWord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Start");
        vvPlayer =(VideoView)findViewById(R.id.vvPlayer);
        tvFirstWord = (TextView)findViewById(R.id.tvFirstWord);
        tvSecondWord = (TextView)findViewById(R.id.tvSecondWord);

        MediaController mediaController= new MediaController(this);
        mediaController.setAnchorView(vvPlayer);
        mediaController.setVisibility(View.GONE);

        //specify the location of media file
        //Uri uri=Uri.parse("android.resource://ua.com.anyapps.english/"+R.raw.naomi);
        Uri uri=Uri.parse("http://anyapps.cf/1.mp4");

        //Setting MediaController and URI, then starting the videoView
        vvPlayer.setMediaController(mediaController);
        vvPlayer.setVideoURI(uri);
        vvPlayer.requestFocus();
        vvPlayer.start();

        tvFirstWord.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View view, MotionEvent motionEvent) {
                Log.d(TAG, "First hover11");
                return false;
            }
        });
        tvFirstWord.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.d(TAG, "First hover");
            }
        });
        tvSecondWord.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.d(TAG, "Second hover");
            }
        });

        tvFirstWord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(TAG, "HOVER");
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Log.d(TAG, "Down " + x + ":" +y );
                break;
            case MotionEvent.ACTION_MOVE:
                //Log.d(TAG, "Move " + x + ":" +y );
                break;
            case MotionEvent.ACTION_UP:
                //Log.d(TAG, "Up " + x + ":" +y );
                break;
        }
        return super.onTouchEvent(event);
    }

    public void seekClick(View v){

        vvPlayer.seekTo(145000);
        vvPlayer.start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("offset", Video_STATE_POSITION);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        vvPlayer.seekTo(savedInstanceState.getInt("offset"));
        vvPlayer.start();
    }

    @Override
    protected void onPause() {
        Video_STATE_POSITION = vvPlayer.getCurrentPosition();
        super.onPause();
    }
}

 */