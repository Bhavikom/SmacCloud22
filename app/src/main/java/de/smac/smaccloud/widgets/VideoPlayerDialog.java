package de.smac.smaccloud.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;

import de.smac.smaccloud.R;

public class VideoPlayerDialog extends AlertDialog implements OnCompletionListener, OnErrorListener, OnInfoListener,
        OnPreparedListener, OnSeekCompleteListener, OnVideoSizeChangedListener,
        SurfaceHolder.Callback
{
    public final static String LOGTAG = "TEST";
    Context context;
    View content;
    Display currentDisplay;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    MediaPlayer mediaPlayer;
    AppCompatSeekBar seekBarVideo;
    Handler seekHandler;
    Runnable runVideo;
    int videoWidth = 0, videoHeight = 0;
    boolean readyToPlay = false;

    public VideoPlayerDialog(Context context, int fileId)
    {

        super(context);
        this.context = context;
        this.setCancelable(true);

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = Helper.getDeviceWidth(activity);
        lp.height = Helper.getDeviceHeight(activity);
        getWindow().setAttributes(lp);*/

        final File videoFile = new File("" + context.getFilesDir() + "/" + fileId);
        mediaPlayer = MediaPlayer.create(context, Uri.fromFile(videoFile));

        LayoutInflater li = LayoutInflater.from(context);
        content = li.inflate(R.layout.dialog_video_player, null);
        setView(content);

        /* VideoView*/

        LinearLayout layoutParentLinear = (LinearLayout) content.findViewById(R.id.layoutParentLinear);
        final VideoView videoView = (VideoView) content.findViewById(R.id.videoView);

        //Creating MediaController
        final MediaController mediaController = new MediaController(videoView.getContext());

        //specify the location of media file
        Uri uri = Uri.parse("" + context.getFilesDir() + "/" + fileId);

        //Setting MediaController and URI, then starting the videoView
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new OnPreparedListener()
        {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer)
            {
                videoView.start();
                mediaController.setKeepScreenOn(true);
                mediaController.setEnabled(true);
                mediaController.show(videoView.getDuration());
            }
        });
        //mediaController.setLayoutParams(new FrameLayout.LayoutParams(Helper.getDeviceWidth(activity), ViewGroup.LayoutParams.WRAP_CONTENT));
        mediaController.setAnchorView(videoView);


        /* End VideoView*/

        /*surfaceView = (SurfaceView) content.findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        seekBarVideo = (AppCompatSeekBar) content.findViewById(R.id.seekBarVideo);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnVideoSizeChangedListener(this);
        String filePath = Environment.getExternalStorageDirectory().getPath()
                + "/Test.m4v";

        try
        {
            mediaPlayer.setDataSource("" + context.getFilesDir() + "/" + fileId);
        }
        catch (Exception e)
        {
            Log.v(LOGTAG, e.getMessage());
            dismiss();
        }
        currentDisplay = getWindow().getWindowManager().getDefaultDisplay();


        seekHandler = new Handler();
        runVideo = new Runnable()
        {
            @Override
            public void run()
            {
                if (mediaPlayer != null && mediaPlayer.isPlaying())
                {
                    if (mediaPlayer.getCurrentPosition() < mediaPlayer.getDuration())
                    {
                        seekBarVideo.setProgress(mediaPlayer.getCurrentPosition());
                        seekHandler.postDelayed(this, 100);
                    }
                    else
                    {
                        if (mediaPlayer.isPlaying())
                        {
                            mediaPlayer.pause();
                            seekBarVideo.setProgress(0);

                        }
                    }
                }
                *//*else if (player != null && seekBarMusic.getProgress() <= player.getDuration())
                {
                    player.pause();
                    seekBarMusic.setProgress(0);
                    btn_play_pause_music.setImageResource(R.drawable.ic_play);
                }*//*
            }
        };

        seekHandler.postDelayed(runVideo, 100);*/

    }

    public void surfaceCreated(SurfaceHolder holder)
    {
        /*mediaPlayer.setDisplay(holder);

        try
        {
            mediaPlayer.prepare();
        }
        catch (Exception e)
        {
            dismiss();
        }*/
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    }

    public void surfaceDestroyed(SurfaceHolder holder)
    {
    }

    public void onCompletion(MediaPlayer mp)
    {
        //dismiss();
    }

    public boolean onError(MediaPlayer mp, int whatError, int extra)
    {
        /*Log.v(LOGTAG, "onError Called");

        if (whatError == MediaPlayer.MEDIA_ERROR_SERVER_DIED)
        {
            Log.v(LOGTAG, "Media Error, Server Died " + extra);
        }
        else if (whatError == MediaPlayer.MEDIA_ERROR_UNKNOWN)
        {
            Log.v(LOGTAG, "Media Error, Error Unknown " + extra);
        }*/

        return false;
    }

    public boolean onInfo(MediaPlayer mp, int whatInfo, int extra)
    {
        /*if (whatInfo == MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING)
        {
            Log.v(LOGTAG, "Media Info, Media Info Bad Interleaving " + extra);
        }
        else if (whatInfo == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE)
        {
            Log.v(LOGTAG, "Media Info, Media Info Not Seekable " + extra);
        }
        else if (whatInfo == MediaPlayer.MEDIA_INFO_UNKNOWN)
        {
            Log.v(LOGTAG, "Media Info, Media Info Unknown " + extra);
        }
        else if (whatInfo == MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING)
        {
            Log.v(LOGTAG, "MediaInfo, Media Info Video Track Lagging " + extra);
        }
        else if (whatInfo == MediaPlayer.MEDIA_INFO_METADATA_UPDATE)
        {
            Log.v(LOGTAG, "MediaInfo, Media Info Metadata Update " + extra);
        }*/
        return false;
    }

    public void onPrepared(MediaPlayer mp)
    {
        /*videoWidth = mp.getVideoWidth();
        videoHeight = mp.getVideoHeight();
        surfaceView.setLayoutParams(new LinearLayout.LayoutParams(videoWidth, videoHeight));
        mp.start();

        int duration = mp.getDuration() / 1000;
        int hours = duration / 3600;
        int minutes = (duration / 60) - (hours * 60);
        int seconds = duration - (hours * 3600) - (minutes * 60);
        String formatted = String.format("%d:%02d:%02d", hours, minutes, seconds);
        Toast.makeText(context, "duration is " + formatted, Toast.LENGTH_LONG).show();*/
    }

    public void onSeekComplete(MediaPlayer mp)
    {
    }

    public void onVideoSizeChanged(MediaPlayer mp, int width, int height)
    {
    }
}