package com.example.media_player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SeekBar seekBar;
    MyCustomAdapter myCustomAdapter;
    ListView listView;
    MediaPlayer mediaPlayer;
    int seekValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBar=findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            seekValue=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            mediaPlayer.seekTo(seekValue);
            }
        });
        listView=findViewById(R.id.play_list);
        CheckUserPermsions();
        myCustomAdapter=new MyCustomAdapter(getAllSongs());
        listView.setAdapter(myCustomAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SongInfo songInfo=SongsList.get(position);
                mediaPlayer=new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(songInfo.path);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    seekBar.setMax(mediaPlayer.getDuration());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        mythread mythread=new mythread() ;
        mythread.start();
    }
    ArrayList<SongInfo>  SongsList =new ArrayList<SongInfo>();
    // online media
/*public ArrayList<SongInfo> getAllSongs() {
    SongsList.clear();
    SongsList.add(new SongInfo("http://server6.mp3quran.net/thubti/001.mp3","Fataha","bakar","quran"));
    SongsList.add(new SongInfo("http://server6.mp3quran.net/thubti/002.mp3","Bakara","bakar","quran"));
    SongsList.add(new SongInfo("http://server6.mp3quran.net/thubti/003.mp3","Al-Imran","bakar","quran"));
    SongsList.add(new SongInfo("http://server6.mp3quran.net/thubti/004.mp3","An-Nisa'","bakar","quran"));
    SongsList.add(new SongInfo("http://server6.mp3quran.net/thubti/005.mp3","Al-Ma'idah","bakar","quran"));
    SongsList.add(new SongInfo("http://server6.mp3quran.net/thubti/006.mp3","Al-An'am","bakar","quran"));
    SongsList.add(new SongInfo("http://server6.mp3quran.net/thubti/007.mp3","Al-A'raf","bakar","quran"));
    return SongsList;
}*/
    //local
    public ArrayList<SongInfo> getAllSongs() {
        Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        Cursor cursor = getContentResolver().query(allsongsuri, null, selection, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String    song_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String fullpath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String    album_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    String   artist_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    SongsList.add(new SongInfo(fullpath,song_name,album_name,album_name));

                } while (cursor.moveToNext());

            }
            cursor.close();

        }

        return SongsList;
    }
    class  mythread extends  Thread{
        public void run() {
            while(true){
                try {
                    Thread.sleep(1000);

                }  catch (Exception e) {}
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //seek bar   seekBar1.setProgress(mp .getCurrentPosition());
                        if(mediaPlayer!=null)
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());

                    }
                });



            }
        }}

    //display news list
    private class MyCustomAdapter extends BaseAdapter {
        public ArrayList<SongInfo>  fullSongData ;

        public MyCustomAdapter(ArrayList<SongInfo>  fullSongData) {
            this.fullSongData=fullSongData;
        }


        @Override
        public int getCount() {
            return fullSongData.size();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater mInflater = getLayoutInflater();
            View myView = mInflater.inflate(R.layout.item, null);

            final   SongInfo s = fullSongData.get(position);

            TextView songName=myView.findViewById(R.id.textViewName);
            songName.setText(s.song_name);
            TextView artistName=myView.findViewById(R.id.textViewArtistName);
            artistName.setText(s.artist_name);

            return myView;
        }

    }

    public void playButton(View view) {
    mediaPlayer.start();
    }

    public void stopButton(View view) {
    mediaPlayer.stop();
    }

    public void pauseButton(View view) {
    mediaPlayer.pause();
    }
    void LoadSng(){
        myCustomAdapter=new  MyCustomAdapter(getAllSongs());
        listView.setAdapter(myCustomAdapter);
    }
    void CheckUserPermsions(){
        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED  ){
                requestPermissions(new String[]{
                                android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return ;
            }
        }

        LoadSng();

    }
    //get acces to location permsion
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LoadSng();
                } else {
                    // Permission Denied
                    Toast.makeText( this,"denail" , Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}