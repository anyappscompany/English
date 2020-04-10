package ua.com.anyapps.english;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SeekDialogListAdapter  extends BaseAdapter {
    Context context;
    LayoutInflater lInflater;
    ArrayList<SeekListItem> objects;
    private static final String TAG = "debapp";

    SeekDialogListAdapter(Context context, ArrayList<SeekListItem> objects) {
        this.context = context;
        this.objects = objects;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setList(Context context, ArrayList<SeekListItem> objects){
        this.context = context;
        this.objects = objects;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int i) {
        try{
            return objects.get(i);
        }catch (Exception ex){
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // используем созданные, но не используемые view
        View v = view;
        if (v == null) {
            v = lInflater.inflate(R.layout.seek_dialog_list_item, viewGroup, false);
        }
        TextView tvSubtitle = (TextView) v.findViewById(R.id.tvSubtitle);

        //tvSubtitle.setText("123");
        SeekListItem jsonObject = getProduct(i);
        if(jsonObject==null) return null;

        ArrayList<String> subsArray;
        try{
            subsArray = jsonObject.subtitles;
            Integer start = jsonObject.start;

            String fullSubtitle = "";
            for(int h = 0; h < subsArray.size(); h ++) {
                fullSubtitle += subsArray.get(h).toString() + " ";
            }

            //DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.US);
            //formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            //String text = formatter.format(new Date(start));

            //tvSubtitle.setText(text + " - " + fullSubtitle.trim());
            tvSubtitle.setText(fullSubtitle.trim());
            tvSubtitle.setTag(start);


        }catch (Exception ex){
            return null;
        }



        return v;
    }

    SeekListItem getProduct(int position) {
        return ((SeekListItem) getItem(position));
    }
}
