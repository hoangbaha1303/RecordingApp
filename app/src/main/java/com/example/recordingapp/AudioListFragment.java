package com.example.recordingapp;

import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;


import java.io.File;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class AudioListFragment extends Fragment  implements  AudioListAdapter.onnItemListClick{

    private RecyclerView audioList;
    private File[] allFiles;

    private AudioListAdapter audioListAdapter;

    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;
    private  File fileToPlay = null;

    //Phần giao diện người dùng
    private ImageButton playerBtn;
    private TextView playerHeader;
    private TextView playerFilename;
    private ImageButton backwardBtn;

    private SeekBar playerSeekbar;
    private Handler seekbarHandler;
    private Runnable updateSeekbar;

    public AudioListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_list, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle saveInstanceState){
        super.onViewCreated(view, saveInstanceState);

        audioList = view.findViewById(R.id.audio_list_view);

        playerBtn = view.findViewById(R.id.player_play_btn);
        playerHeader =  view.findViewById(R.id.player_header_title);
        playerFilename= view.findViewById(R.id.player_filename);


        playerSeekbar = view.findViewById(R.id.player_seekbar);

        String path = getActivity().getExternalFilesDir("/").getAbsolutePath();
        File directory =  new File(path);
        allFiles =  directory.listFiles();

        audioListAdapter = new AudioListAdapter(allFiles, this);

        audioList.setHasFixedSize(true);
        audioList.setLayoutManager(new LinearLayoutManager(getContext()));
        audioList.setAdapter(audioListAdapter);


        playerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying){
                    pauseAudio();
                }else {
                    if (fileToPlay != null) {
                        resumeAudio();
                    }
                }
            }
        });

        playerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (fileToPlay != null) {
                    pauseAudio();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (fileToPlay != null) {
                    int progress = seekBar.getProgress();
                    mediaPlayer.seekTo(progress);
                    resumeAudio();
                }
            }
        });
    }

    @Override
    public void onClickListener(File file, int position) {
        fileToPlay = file;
        if (isPlaying){
            stopAudio();
            playAudio(fileToPlay);
        } else {
            playAudio(fileToPlay);
        }
    }
    private void pauseAudio(){
        mediaPlayer.pause();
        playerBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.play, null));
        isPlaying = false;
        seekbarHandler.removeCallbacks(updateSeekbar);
    }
    private void resumeAudio(){
        mediaPlayer.start();
        playerBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.pause_button, null));
        isPlaying = true;

        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);
    }

    private void stopAudio() {
        //Dùng để tắt âm thanh đang phát
        playerBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.play, null));
        playerHeader.setText("Stopped");
        isPlaying= false;
        mediaPlayer.stop();
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    private void playAudio(File fileToPlay) {

        mediaPlayer =new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e){
            e.printStackTrace();
        }
        playerBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.pause_button, null));
        playerFilename.setText(fileToPlay.getName());
        playerHeader.setText("Playing");
        //Dùng để chạy âm thanh
        isPlaying= true;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopAudio();
                playerHeader.setText("Finished");
            }
        });

        playerSeekbar.setMax(mediaPlayer.getDuration());

        seekbarHandler = new Handler();
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);
    }

    private void updateRunnable() {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                playerSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                seekbarHandler.postDelayed(this, 500);
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isPlaying) {
            stopAudio();
        }
    }
}