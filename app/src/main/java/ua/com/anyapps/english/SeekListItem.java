package ua.com.anyapps.english;

import java.util.ArrayList;

public class SeekListItem {
    public int start;
    public int duration;
    public ArrayList<String> subtitles;

    @Override
    public boolean equals(Object o){
        if(o instanceof SeekListItem){
            if (((SeekListItem) o).start == start && ((SeekListItem) o).duration == duration && ((SeekListItem) o).subtitles.toString().equals(subtitles.toString())){
                return true;
            }
        }
        return false;
    }
}
