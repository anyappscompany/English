package ua.com.anyapps.english;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.LoginFilter;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.com.anyapps.english.Server.ServerConnect;
import ua.com.anyapps.english.Server.Translation.TranslationResult;

//ffmpeg.exe -i "C:\WorkSpace\play_video_test.mp4" -c:v libx264 -preset superfast -x264opts keyint=2 -acodec copy -f mp4 "C:\WorkSpace\play_video_test_key_frame.mp4"
//https://www.dev2qa.com/how-to-make-android-videoview-seekto-method-consistent/
public class PlayerActivity extends AppCompatActivity {
    private static final String TAG = "debapp";
    VideoView vvPlayer;
    MediaController mediaController;
    Handler handler;
    FlexboxLayout flSubtitles;
    JSONArray jsonSubtitlesArray;
    ArrayList<Subtitle> tvSubtitlesArray = new ArrayList<>();
    ArrayList<Subtitle> tvSelectedSubtitlesArray = new ArrayList<>();

    // индекс последних показанных субтитров
    static int latSubtitleIndex =-1;
    // показаны субтитры или нет
    static int skipVideo = 0;


    //AlertDialog.Builder seekDialog;
    SeekDialogListAdapter seekDialogListAdapter;

    int playerStopPos = 0;
    Uri uri;
    //https://www.youtube.com/watch?v=iCX4XwH8dnU&t=145s
    String subtitlesSource = "";

    ArrayList<ArrayList<String>> subtitles = new ArrayList<>();
    ArrayList<ArrayList<Integer>> times = new ArrayList<>();

    AlertDialog.Builder dialogBuilder;
    LayoutInflater inflater;
    View dialogView;
    TextView tvText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        flSubtitles = (FlexboxLayout)findViewById(R.id.flSubtitles);
        handler = new Handler();


        Bundle b = getIntent().getExtras();
        int id = b.getInt("video");

        //субтитры из ресурса
        try {
            Resources res = getResources();
            InputStream in_s = res.openRawResource(R.raw.sub1);

            byte[] b1 = new byte[in_s.available()];
            in_s.read(b1);
            subtitlesSource = new String(b1);
        } catch (Exception e) {
            Log.d(TAG, "Error read subtitle from raw resourse");
        }
        //Log.d(TAG, "SSSSSSSSSSSSSS: "+subtitlesSource);

        vvPlayer =(VideoView)findViewById(R.id.vvPlayer);
        mediaController= new MediaController(this);
        mediaController.setAnchorView(vvPlayer);
        mediaController.setVisibility(View.GONE);

        //specify the location of media file
        //Uri uri=Uri.parse("android.resource://ua.com.anyapps.english/"+R.raw.naomi);
        //uri=Uri.parse("http://anyapps.cf/english/videos/"+id+".mp4");
        uri=Uri.parse("android.resource://" + getPackageName()+ "/" + R.raw.vid1);
        //Setting MediaController and URI, then starting the videoView
        vvPlayer.setMediaController(mediaController);
        vvPlayer.setVideoURI(uri);
        vvPlayer.requestFocus();

        vvPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //mp.setVolume(0f, 0f);
                mp.setLooping(false);
            }
        });
        vvPlayer.start();



        vvPlayer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int x = (int)motionEvent.getX();
                int y = (int)motionEvent.getY();

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN://Ставить на паузу и останавливать счетчик
                        handler.removeCallbacks(runnableCode);
                        Log.d(TAG, "ACTION_DOWN");
                        vvPlayer.pause();
                        tvSelectedSubtitlesArray.clear();
                        //Log.d(TAG, "total subs " + tvSubtitlesArray.size());
                        for(int m=0; m<tvSubtitlesArray.size();m++){
                            //Log.d(TAG, "m" + m);
                            //Rect rectf = new Rect();
                            //tvSubtitlesArray.get(m).tvSubtitle.getLocalVisibleRect(rectf);
                            //tvSubtitlesArray.get(m).tvSubtitle.getGlobalVisibleRect(rectf);
                            tvSubtitlesArray.get(m).top = tvSubtitlesArray.get(m).tvSubtitle.getTop();
                            tvSubtitlesArray.get(m).left = tvSubtitlesArray.get(m).tvSubtitle.getLeft();
                            tvSubtitlesArray.get(m).bottom = tvSubtitlesArray.get(m).tvSubtitle.getBottom();
                            tvSubtitlesArray.get(m).right = tvSubtitlesArray.get(m).tvSubtitle.getRight();
                            //Log.d(TAG, "Sub" + m + " top-"+rectf.top + " left-"+rectf.left + " bottom-"+rectf.bottom + " right-"+rectf.right);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //Log.d(TAG, "Total sabs " + tvSubtitlesArray.size());

                        /*Rect rectf = new Rect();
                        tvFirstWord.getLocalVisibleRect(rectf);
                        tvFirstWord.getGlobalVisibleRect(rectf);
                        Log.d(TAG, "top - " + rectf.top + " left - " + rectf.left + " bottom - " + rectf.bottom + " right - " + rectf.right);
                        if((x>=rectf.left && x<=rectf.right) && (y>=rectf.top && y<=rectf.bottom)){
                            Log.d(TAG, "1111111111111");
                        }
                        //Log.d(TAG, "Move " + x + ":" +y );
                        */
                        boolean naiden = false;
                        for(int m=0; m<tvSubtitlesArray.size();m++){
                            //Log.d(TAG, "x: " +x + " y: " + y + " left: " +tvSubtitlesArray.get(m).left + " right: " + tvSubtitlesArray.get(m).right + " top: " + tvSubtitlesArray.get(m).top + " bottom: " + tvSubtitlesArray.get(m).bottom);

                            if ((x >= tvSubtitlesArray.get(m).left && x <= tvSubtitlesArray.get(m).right) && (y >= tvSubtitlesArray.get(m).top && y <= tvSubtitlesArray.get(m).bottom)) {
                            //if ((x >= tvSubtitlesArray.get(m).tvSubtitle.getLeft() && x <= tvSubtitlesArray.get(m).tvSubtitle.getRight()) && (y >= tvSubtitlesArray.get(m).tvSubtitle.getTop() && y <= tvSubtitlesArray.get(m).tvSubtitle.getBottom())) {
                                //Log.d(TAG, "Cross - " + m);
                                tvSubtitlesArray.get(m).tvSubtitle.setBackgroundResource(R.color.subtitleSelectedBackgrouns);

                                Log.d(TAG, "SELECTED TEXT: " + tvSubtitlesArray.get(m).tvSubtitle.getText());

                                for(Integer f=0; f<tvSelectedSubtitlesArray.size();f++){
                                    if(tvSubtitlesArray.get(m).id ==tvSelectedSubtitlesArray.get(f).id ) naiden = true;
                                }
                                if(!naiden){
                                tvSelectedSubtitlesArray.add(tvSubtitlesArray.get(m));}

                            }
                        }


                        // выделенные субтитры без дубликатов
                        /*HashSet<Subtitle> hashSet = new HashSet(tvSelectedSubtitlesArray);
                        ArrayList<Subtitle> tvTmpArray = new ArrayList<>();
                        for (Subtitle employee : hashSet) {
                            tvTmpArray.add(employee);
                        }
                        tvSelectedSubtitlesArray = tvTmpArray;*/

                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "ACTION_UP");
                        Log.d(TAG, "Selected subtitles " + tvSelectedSubtitlesArray.size());
                        String selectedSubtitlesLine = "";



                        Collections.sort(tvSelectedSubtitlesArray);

                        for(int g=0;g<tvSelectedSubtitlesArray.size();g++){
                            selectedSubtitlesLine += tvSelectedSubtitlesArray.get(g).tvSubtitle.getText()+" ";
                        }
                        selectedSubtitlesLine.trim();

                        handler.post(runnableCode);

                        if(tvSelectedSubtitlesArray.size()==0) {
                            vvPlayer.start();
                        }
                        if(tvSelectedSubtitlesArray.size()>0) {
                            vvPlayer.pause();

                            dialogBuilder = new AlertDialog.Builder(PlayerActivity.this);
// ...Irrelevant code for customizing the buttons and title
                            inflater = getLayoutInflater();
                            dialogView = inflater.inflate(R.layout.translate_dialog, null);
                            dialogBuilder.setView(dialogView);

                            tvText = (TextView) dialogView.findViewById(R.id.tvText);
                            tvText.setText(getResources().getString(R.string.translate_result));
                            AlertDialog alertDialog = dialogBuilder.create();

                            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                public void onCancel(DialogInterface dialog) {
                                    vvPlayer.start();
                                }
                            });
                            alertDialog.show();

                            //Toast.makeText(PlayerActivity.this, selectedSubtitlesLine, Toast.LENGTH_LONG).show();


                            String searchString = "";
                            try {
                                //searchString = URLEncoder.encode(new String(Base64.encodeToString(selectedSubtitlesLine.getBytes(), Base64.DEFAULT)), "UTF-8");
                                searchString = URLEncoder.encode(new String(Base64.encodeToString(selectedSubtitlesLine.trim().getBytes(), Base64.DEFAULT)).trim(), "UTF-8");

                                Log.d(TAG, "1SELECTED SUBS: " + selectedSubtitlesLine.trim());
                                Log.d(TAG, "2SELECTED SUBS: " + new String(Base64.encodeToString(selectedSubtitlesLine.getBytes(), Base64.DEFAULT)).trim());
                                Log.d(TAG, "3SELECTED SUBS: " + URLEncoder.encode(new String(Base64.encodeToString(selectedSubtitlesLine.getBytes(), Base64.DEFAULT)).trim(), "UTF-8"));
                                //Log.d(TAG, "4SELECTED SUBS: " + searchString);
                                //Log.d(TAG, "5SELECTED SUBS: " + searchString);
                            } catch (UnsupportedEncodingException e) {
                                Log.d(TAG, e.getMessage());
                            }

                            //Log.d(TAG, "SELECTED SUBS: " + searchString);
                            //Log.d(TAG, new String(Base64.encodeToString(selectedSubtitlesLine.getBytes(), Base64.DEFAULT)));
                            ServerConnect.getInstance()
                                    .getJSONApi()
                                    .getText(searchString)
                                    .enqueue(new Callback<TranslationResult>() {
                                        @Override
                                        public void onResponse(Call<ua.com.anyapps.english.Server.Translation.TranslationResult> call, Response<ua.com.anyapps.english.Server.Translation.TranslationResult> response) {

                                            //Log.d(TAG, "Сервер успешно вернул данные во время получения ключа доступа: " + response.code() + " RESPONSE: " + response.toString() + " BODY: " + response.body().toString());
                                            //if(true) return;
                                            if (response.isSuccessful()) {
                                                ua.com.anyapps.english.Server.Translation.TranslationResult accessKeyInfo = response.body();
                                                // сохранение ключа доступа в настройках и переход на гланое актиивити
                                                Log.d(TAG, "Во время перевода, сервер успешно вернул данные: " + response.code() + " RESPONSE: " + response.toString() + " BODY: " + response.body().toString());
                                                //Toast.makeText(PlayerActivity.this, "" + accessKeyInfo.getResult(), Toast.LENGTH_SHORT).show();

                                                try {
                                                    Log.d(TAG, "Возвращенный текст: " + accessKeyInfo.getResult());
                                                    Log.d(TAG, "Возвращенный текст: " + URLDecoder.decode(accessKeyInfo.getResult(), "UTF-8"));
                                                    Log.d(TAG, "Возвращенный текст: " + Base64.decode(URLDecoder.decode(accessKeyInfo.getResult(), "UTF-8"), Base64.DEFAULT));
                                                    Log.d(TAG, "Возвращенный текст: " + new String(Base64.decode(URLDecoder.decode(accessKeyInfo.getResult(), "UTF-8"), Base64.DEFAULT), "UTF-8").trim());
                                                    tvText.setText(     new String(Base64.decode(URLDecoder.decode(accessKeyInfo.getResult(), "UTF-8"), Base64.DEFAULT), "UTF-8").trim()   );
                                                    Log.d(TAG, new String(Base64.decode(URLDecoder.decode(accessKeyInfo.getResult(), "UTF-8"), Base64.DEFAULT), "UTF-8").trim());
                                                } catch (UnsupportedEncodingException e) {
                                                    Log.d(TAG, "Error decode " + e.getMessage());
                                                }

                                            } else {
                                                Log.e(TAG, "Во время перевода, сервер вернул ошибку " + response.code() + " " + response.toString());
                                                tvText.setText(response.code());
                                                //Toast.makeText(PlayerActivity.this, ""+response.code(), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ua.com.anyapps.english.Server.Translation.TranslationResult> call, Throwable t) {

                                            Log.e(TAG, "Отказ. Не удалось подключиться к серверу во время перевода - " + t);
                                            //Toast.makeText(PlayerActivity.this, "" + t, Toast.LENGTH_SHORT).show();
                                            tvText.setText(""+t);
                                        }
                                    });
                        }

                        tvSubtitlesArray.clear();
                        tvSelectedSubtitlesArray.clear();


                        break;
                }
                return true;
            }
        });
        /*
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Current position: " + vvPlayer.getCurrentPosition());
                handler.postDelayed(this, 1000);
            }
        }, 1000);*/

        // ArrayList<ArrayList<String>> subtitles = new ArrayList<>();
        // ArrayList<ArrayList<Integer>> times;
        try {

            // напослнение списка с субтитрами
            JSONObject obj = new JSONObject(subtitlesSource);
            JSONArray subs = obj.getJSONArray("subtitles");
            JSONObject tims = obj.getJSONObject("times");

            for(int i=0; i<subs.length(); i++){
                JSONArray ja = subs.getJSONArray(i);
                ArrayList<String> subt = new ArrayList();
                for(int u=0; u<ja.length(); u++){
                    subt.add(u, ja.getString(u));
                }
                subtitles.add(subt);
            }

            // наполнение списка со временем
            // поиск максимального индекса
            Integer maxIndex = 0;
            Iterator<String> keys = tims.keys();
            Iterator<String> keys2 = tims.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                //Log.d(TAG, key + tims.get(key));
                JSONArray tim = tims.getJSONArray(key);
                //Log.d(TAG, tim.length() + "");

                maxIndex = Integer.valueOf(key)+tim.getInt(1)-1;
                if (tims.get(key) instanceof JSONObject) {
                    // do something with jsonObject here
                }

            }

            // заполнение пустыми массивами
            //times = new ArrayList<>();
            ArrayList<Integer> tmp = new ArrayList<>();
            for(int h=0; h<(maxIndex+1);h++){
                times.add(tmp);
            }

            // заполнение массива со временем показа
            while(keys2.hasNext()) {
                String key = keys2.next();
                //Log.d(TAG, key + tims.get(key));
                ArrayList<Integer> tmp2 = new ArrayList<>();
                JSONArray tim = tims.getJSONArray(key);
                tmp2.add(tim.getInt(0));
                tmp2.add(tim.getInt(1));
                tmp2.add(tim.getInt(2));

                times.set(Integer.valueOf(key), tmp2);

                //maxIndex = Integer.valueOf(key)+tim.getInt(1)-1;
                if (tims.get(key) instanceof JSONObject) {
                    // do something with jsonObject here
                }

            }

            //Log.d(TAG, "maxIndex " + maxIndex + " tot " + times.size() + " aaaa " + times.get(9).get(0));

            //Integer startst = jsonObject.getInt("startst");
            //Integer endst = jsonObject.getInt("endst");
            //Integer start = jsonObject.getInt("start");
            //JSONArray jsonArr = subs.getJSONArray(0);

        }catch (Exception ex){
            Log.d(TAG, "1Json parse error " + ex.getMessage());
        }
        //
    }

    @Override
    protected void onResume() {
        super.onResume();
        vvPlayer.seekTo(playerStopPos);
        vvPlayer.start();
        handler.post(runnableCode);

        //vvPlayer.pause();
    }

    @Override
    protected void onPause() {
        super.onPause();
        vvPlayer.pause();
        playerStopPos = vvPlayer.getCurrentPosition(); //stopPosition is an int
        handler.removeCallbacks(runnableCode);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.player_control_buttons, menu);
        return super.onCreateOptionsMenu(menu);
    }
    ArrayList<SeekListItem> jsonSubtitlesArray2 = new ArrayList<>();
    AlertDialog alertDialog;
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_play:
                Log.d(TAG, "Play");
                vvPlayer.start();
                return true;
            case R.id.action_pause:
                Log.d(TAG, "Pause");
                vvPlayer.pause();
                return true;
            case R.id.action_stop:
                Log.d(TAG, "Stop");
                latSubtitleIndex =-1;
                vvPlayer.seekTo(0);
                vvPlayer.pause();

                return true;
            case R.id.action_subtitles_list:
                Integer currentPos = vvPlayer.getCurrentPosition();
                Integer index = (int)Math.round(currentPos/1000);
                //ArrayList<Integer> tim = times.get(index);
                //ArrayList<String> subs = subtitles.get(tim.get(2));
                //Log.d(TAG, currentPos + " tim2: " + subs.size());

                // по 5 субтитров вокруг активного
                jsonSubtitlesArray2 = new ArrayList<>();
                ArrayList<SeekListItem> prevSubt = new ArrayList<>();
                ArrayList<SeekListItem> midSubt = new ArrayList<>();
                ArrayList<SeekListItem> newSubt = new ArrayList<>();


                // предыдушие субтитры
                for(int m=1; m<times.size(); m++){
                    //Log.d(TAG, "Subs search m=" + m + " index=" + index);

                    if(jsonSubtitlesArray2.size()>4) break;
                    try{
                        SeekListItem al = new SeekListItem();
                        al.start = times.get(index-m).get(0);
                        al.duration = times.get(index-m).get(1);
                        al.subtitles = subtitles.get(times.get(index-m).get(2));
                        if(!jsonSubtitlesArray2.contains(al)) {
                            jsonSubtitlesArray2.add(al);
                        }
                    }catch (Exception ex){
                        //
                    }

                    /*try{ // назад без текущего
                        SeekListItem al = new SeekListItem();
                        al.start = times.get(start-(1+m)).get(0);
                        al.duration = times.get(start-(1+m)).get(1);
                        al.subtitles = subtitles.get(times.get(start-(1+m)).get(2));

                        if(!jsonSubtitlesArray2.contains(al)) {
                            jsonSubtitlesArray2.add(al);
                        }
                    }catch (Exception ex){
                        //
                    }*/
                }
                Collections.reverse(jsonSubtitlesArray2);

                // текущий субтитр
                try{
                    SeekListItem al = new SeekListItem();
                    al.start = times.get(index).get(0);
                    al.duration = times.get(index).get(1);
                    al.subtitles = subtitles.get(times.get(index).get(2));
                    if(!jsonSubtitlesArray2.contains(al)) {
                        jsonSubtitlesArray2.add(al);
                    }
                }catch (Exception ex){
                    //
                }

                // следующие субтитры
                for(int m=1; m<times.size(); m++){
                    //Log.d(TAG, "Subs search m=" + m + " index=" + index);

                    if(jsonSubtitlesArray2.size()>9) break;
                    try{
                        SeekListItem al = new SeekListItem();
                        al.start = times.get(index+m).get(0);
                        al.duration = times.get(index+m).get(1);
                        al.subtitles = subtitles.get(times.get(index+m).get(2));
                        if(!jsonSubtitlesArray2.contains(al)) {
                            jsonSubtitlesArray2.add(al);
                        }
                    }catch (Exception ex){
                        //
                    }

                    /*try{ // назад без текущего
                        SeekListItem al = new SeekListItem();
                        al.start = times.get(start-(1+m)).get(0);
                        al.duration = times.get(start-(1+m)).get(1);
                        al.subtitles = subtitles.get(times.get(start-(1+m)).get(2));

                        if(!jsonSubtitlesArray2.contains(al)) {
                            jsonSubtitlesArray2.add(al);
                        }
                    }catch (Exception ex){
                        //
                    }*/
                }


                //jsonSubtitlesArray2.addAll(prevSubt);
                //jsonSubtitlesArray2.addAll(midSubt);
                //Log.d(TAG, "TOTSUB: " + jsonSubtitlesArray2.size());

                AlertDialog.Builder seekDialogBuilder = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.seek_dialog, null);
                seekDialogBuilder.setView(dialogView);

                ListView lv = (ListView) dialogView.findViewById(R.id.lvSubList);

                seekDialogListAdapter = new SeekDialogListAdapter(this, jsonSubtitlesArray2);
                lv.setAdapter(seekDialogListAdapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        try {
                            Log.d(TAG,  i + "");
                            //Log.d(TAG, view.getTag().toString() + "111111111");
                            Log.d(TAG, "TAAG " + view.findViewById(R.id.tvSubtitle).getTag());
                            Log.d(TAG, "From object: " + jsonSubtitlesArray2.get(i).start);

                            //SeekListItem jsonObject2 = jsonSubtitlesArray2.get(i);
                            //Integer start = jsonObject2.getInt("start");
                            vvPlayer.seekTo(jsonSubtitlesArray2.get(i).start*1000);

                            alertDialog.dismiss();
                            //vvPlayer.seekTo(11000);
                        }catch (Exception ex){
                            //
                        }
                    }
                });

                //EditText editText = (EditText) dialogView.findViewById(R.id.label_field);
                //editText.setText("test label");
                alertDialog = seekDialogBuilder.create();
                //alertDialog.setCancelable(false);
                alertDialog.show();

                /*Integer currentPos = vvPlayer.getCurrentPosition();
                for(int i = 0; i < jsonSubtitlesArray.length(); i ++) {
                    try {
                        JSONObject jsonObject = jsonSubtitlesArray.getJSONObject(i);
                        Integer startst = jsonObject.getInt("startst");
                        Integer endst = jsonObject.getInt("endst");
                        Integer start = jsonObject.getInt("start");
                        Integer end = jsonObject.getInt("end");

                        if (currentPos >= start && currentPos <= end) {
                            Log.d(TAG, "Клин на субтитрах " + i);
                        }
                    }catch (Exception ex){
                        Log.d(TAG, "SeekTo error " + ex.toString());
                    }
                }*/
                //Log.d(TAG, "Back");
                /*vvPlayer.pause();
                Integer currentPos = vvPlayer.getCurrentPosition();

                for(int u = 0; u <= jsonSubtitlesArray.length(); u++) {
                    try {
                        JSONObject jsonObject = jsonSubtitlesArray.getJSONObject(u);
                        Integer startst = jsonObject.getInt("startst");
                        Integer endst = jsonObject.getInt("endst");

                        //Log.d(TAG, startst + " - " + currentPos + " - " + endst);
                        //if (currentPos>=startst && currentPos<=endst) {
                        if (currentPos<=startst) {
                            // массив слов через пробел
                            JSONArray subsArray = jsonObject.getJSONArray("st");
                            //Log.d(TAG, "PREWINDEX : " + i);
                            //перейти на конец предыдущего

                            JSONObject jsonObject2 = jsonSubtitlesArray.getJSONObject(u);
                            vvPlayer.seekTo(jsonObject2.getInt("startst"));
                            Log.d(TAG, "CurrentIndex: " + u + ", PrevIndex: " + (u) + " " + jsonObject2.getJSONArray("st").getString(0) + " Startsst: " + jsonObject2.getInt("startst"));

                            vvPlayer.start();
                            return true;
                        }
                    } catch (Exception ex) {
                        Log.d(TAG, "Ошибка при определении текущей позиции");
                    }
                }*/
                return true;
            /*case R.id.action_forward:
                Log.d(TAG, "Forward");
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // отображение субтитров
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            //vvPlayer.pause();
            Integer currentPos = vvPlayer.getCurrentPosition();
            // очистка блока субтитров
            flSubtitles.removeAllViews();

            try {
                Integer index = (int)Math.round(currentPos/1000);
                ArrayList<Integer> tim = times.get(index);
                ArrayList<String> subs = subtitles.get(tim.get(2));
                //Log.d(TAG, currentPos + " tim: " + subs.size());
                String stringSubtuitles = "";
                for(int k = 0 ; k < subs.size(); k ++) {
                    TextView rowTextView = new TextView(PlayerActivity.this);
                    rowTextView.setText("" + subs.get(k));
                    FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(10,10,10,10);
                    rowTextView.setLayoutParams(params);
                    rowTextView.setPadding(5 ,5,5,5);
                    rowTextView.setBackgroundResource(R.color.subtitleBackgrouns);
                    rowTextView.setTextColor(getResources().getColor(R.color.subtitleColor));


                    flSubtitles.addView(rowTextView);

                    Subtitle sTitle = new Subtitle();
                    sTitle.tvSubtitle = rowTextView;
                    stringSubtuitles += subs.get(k);
                    sTitle.id = k;

                    tvSubtitlesArray.add(sTitle);
                }
            }catch (Exception ex){
                //
            }
            /*for(int i = 0; i < jsonSubtitlesArray.length(); i ++) {
                try {
                    JSONObject jsonObject = jsonSubtitlesArray.getJSONObject(i);
                    Integer startst = jsonObject.getInt("startst");
                    Integer endst = jsonObject.getInt("endst");
                    Integer start = jsonObject.getInt("start");
                    Integer end = jsonObject.getInt("end");


                    //Log.d(TAG, startst + " - " + currentPos + " - " + endst);
                    if(currentPos>=startst && currentPos<=endst){
                        latSubtitleIndex = i;
                        // массив слов через пробел
                        JSONArray subsArray = jsonObject.getJSONArray("st");
                        //Log.d(TAG, "cur: " + startst + " arrlength " + subsArray.length());
                        //Log.d(TAG, "Вывод сабов " + subsArray.length());

                        // динамическое создание строки сабтитров
                        //if(tvsWords.size()>0)
                        tvSubtitlesArray.clear();
                        String stringSubtuitles = "";
                        for(int k = 0 ; k < subsArray.length(); k ++) {
                            TextView rowTextView = new TextView(PlayerActivity.this);
                            rowTextView.setText("" + subsArray.get(k));
                            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(10,10,10,10);
                            rowTextView.setLayoutParams(params);
                            rowTextView.setPadding(5 ,5,5,5);
                            rowTextView.setBackgroundResource(R.color.subtitleBackgrouns);
                            rowTextView.setTextColor(getResources().getColor(R.color.subtitleColor));


                            flSubtitles.addView(rowTextView);

                            Subtitle sTitle = new Subtitle();
                            sTitle.tvSubtitle = rowTextView;
                            stringSubtuitles += subsArray.get(k);
                            sTitle.id = k;

                            tvSubtitlesArray.add(sTitle);
                        }
                        //Log.d(TAG, "Для части " + i + " субтитры: " + stringSubtuitles);
                        break;
                        //Log.d(TAG, "tvSubtitlesArray size: " + tvSubtitlesArray.size());

                    }else{
                        //flSubtitles.removeAllViews();
                    }
                    //Log.d(TAG, "Str: " + jsonObject.getInt("startst"));
                }catch (Exception ex){
                    Log.d(TAG, "2Json parse error " + ex.toString());
                }
            }*/
            //vvPlayer.start();
            // Repeat this the same runnable code block again another 2 seconds
            // 'this' is referencing the Runnable object
            handler.postDelayed(this, 1000);
        }
    };
}
