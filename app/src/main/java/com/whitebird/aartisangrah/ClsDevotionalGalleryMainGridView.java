package com.whitebird.aartisangrah;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.whitebird.aartisangrah.MusicPlayer.GetMediaPlayer;

import java.util.ArrayList;

import static com.whitebird.aartisangrah.ClsFullFragmentAndPager.mediaPlayer;

public class ClsDevotionalGalleryMainGridView extends AppCompatActivity {


    PopToPlayAllSongs popToPlayAllSongs;
    //For Define List View Of Components
    GridView gridViewOfComponents;
    GetMediaPlayer getMediaPlayer;

    //Ads
    AdView adView;

    //Define Images Of list View
    static int[] imagesOfGodsModelCls = {
            R.drawable.hanuman,
            R.drawable.ganpati_bappa,
            R.drawable.gajanan_maharaj,
            R.drawable.durga_devi,
            R.drawable.mahalakshami_mata,
            R.drawable.mahadev,
            R.drawable.datta,
            R.drawable.vitthal
    };

    static String[] lyricsOfGod;
    //It define names in All three Language
    static String[] namesOfGod;
    //Define Shared preference to select language
    private SharedPreferences preferencesOflanguage;
    public static final String GALLERYFORLANG = "galleryforlang";
    public static final String SETLANG="setlang";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cls_main_view_hanuman_chalisa);
        popToPlayAllSongs = new PopToPlayAllSongs();
        getMediaPlayer =new GetMediaPlayer(this);


        adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("1720EC3DF18CC1184849510529D6A998")
                .build();
        adView.loadAd(adRequest);
        //Define the String As per Language Selected

        preferencesOflanguage = this.getSharedPreferences(GALLERYFORLANG,MODE_PRIVATE);
        String selectedLang = preferencesOflanguage.getString(SETLANG,"");

        switch (selectedLang)
        {
            case "मराठी":
                namesOfGod = getResources().getStringArray(R.array.names_in_marathi_string);
                break;
            case "English":
                namesOfGod = getResources().getStringArray(R.array.names_in_english_string);
                break;
            case "हिंदी":
                namesOfGod = getResources().getStringArray(R.array.names_in_hindi_string);
                break;
            default:
                namesOfGod = getResources().getStringArray(R.array.names_in_marathi_string);
                break;
        }

        switch (selectedLang)
        {
            case "मराठी":
                lyricsOfGod = getResources().getStringArray(R.array.lyrics_in_marathi_string);
                break;
            case "English":
                lyricsOfGod = getResources().getStringArray(R.array.lyrics_in_english_string);
                break;
            case "हिंदी":
                lyricsOfGod = getResources().getStringArray(R.array.lyrics_in_hindi_string);
                break;
            default:
                lyricsOfGod = getResources().getStringArray(R.array.lyrics_in_marathi_string);
                break;
        }


        //List View Of main components get and shown here
        gridViewOfComponents = (GridView) findViewById(R.id.mainIdGridViewOfComponents);

        final Adapter adapter = new Adapter(this, this.getGodsListComponent());
        gridViewOfComponents.setAdapter(adapter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer!=null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.setLooping(true); // Set looping
            }
        }
    }

    static ArrayList<ClsModelDataStoreRoom> getGodsListComponent() {
        ArrayList<ClsModelDataStoreRoom> gods = new ArrayList<ClsModelDataStoreRoom>();
        ClsModelDataStoreRoom g;

        for (int i = 0; i < namesOfGod.length; i++) {
            g = new ClsModelDataStoreRoom(namesOfGod[i], imagesOfGodsModelCls[i],lyricsOfGod[i]);
            gods.add(g);
        }
        return gods;
    }



    public class  Adapter extends BaseAdapter {

        Context c;
        MediaPlayer bell;
        ArrayList<ClsModelDataStoreRoom> gods;
        public Adapter(Context ctx,ArrayList<ClsModelDataStoreRoom> gods){
            this.c=ctx;
            this.gods=gods;
        }
        @Override
        public int getCount() {
            return gods.size();
        }

        @Override
        public Object getItem(int position) {
            return gods.get(position);
        }

        @Override
        public long getItemId(int position) {
            return gods.indexOf(getItem(position));
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.component_list_view_model, null);
            }
            TextView nameTxt = (TextView) convertView.findViewById(R.id.textViewPosition);
            ImageView img = (ImageView) convertView.findViewById(R.id.imageViewPosition);

            //Set Data

            nameTxt.setText(gods.get(position).getListOfStringComponent());
            img.setImageResource(gods.get(position).getImageOfComponents());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(bell!=null){
                        bell.reset();
                        bell.release();
                    }
                    bell = MediaPlayer.create(c,R.raw.temple_bell);
                    bell.start();
                    /**
                    Here We get Names of items
                    String stringText  = nameTxt.getText().toString();
                    Toast.makeText(getApplicationContext(),stringText,Toast.LENGTH_LONG).show();
                    Log.d("Song State Extra is", String.valueOf(position));
                    */
                    Intent intent = new Intent(getBaseContext(), ClsFullFragmentAndPager.class);
                    intent.putExtra("position",  position);
                    startActivity(intent);

                }
            });


            return convertView;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lang_selection_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_marathi_lang_set:
                SharedPreferences.Editor editor = preferencesOflanguage.edit();
                String selectedLang = "मराठी";
                editor.putString(SETLANG,selectedLang);
                Log.d("Song State Extra is",selectedLang);
                editor.commit();
                finish();
                Intent mainPageIntent = new Intent(this,ClsDevotionalGalleryMainGridView.class);
                startActivity(mainPageIntent);
                return true;
            case R.id.action_english_lang_set:
                editor = preferencesOflanguage.edit();
                selectedLang = "English";
                editor.putString(SETLANG,selectedLang);
                Log.d("Song State Extra is",selectedLang);
                editor.commit();
                finish();
                mainPageIntent = new Intent(this, ClsDevotionalGalleryMainGridView.class);
                startActivity(mainPageIntent);
                return true;
            case R.id.action_hindi_lang_set:
                editor = preferencesOflanguage.edit();
                selectedLang = "हिंदी";
                editor.putString(SETLANG,selectedLang);
                Log.d("Song State Extra is",selectedLang);
                editor.commit();
                finish();
                mainPageIntent = new Intent(this, ClsDevotionalGalleryMainGridView.class);
                startActivity(mainPageIntent);
                return true;
            case R.id.play_all:
                Intent intentPopUp = new Intent(getBaseContext(),PopToPlayAllSongs.class);
                startActivity(intentPopUp);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
