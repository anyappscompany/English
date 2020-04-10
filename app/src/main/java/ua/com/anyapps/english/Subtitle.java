package ua.com.anyapps.english;

import android.util.Log;
import android.widget.TextView;

public class Subtitle implements Comparable<Subtitle>{
    TextView tvSubtitle;
    Integer top;
    Integer left;
    Integer bottom;
    Integer right;
    Integer id;
    private static final String TAG = "debapp";
    // выделенные субтитры по порядку
    @Override
    public int compareTo(Subtitle subtitle) {
        return (this.id < subtitle.id ? -1 :
                (this.id == subtitle.id ? 0 : 1));
        //return 0;
    }

}
