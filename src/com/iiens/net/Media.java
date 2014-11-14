package com.iiens.net;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

public class Media extends Fragment implements SurfaceHolder.Callback, OnPreparedListener {
	
	private MediaPlayer mediaPlayer;
	private SurfaceHolder vidHolder;
	private SurfaceView vidSurface;
	private String url = "http://radioactive.iiens.net/uploads/cheap_cookies_2014_04_16.mp3"; 
	
//	static MediaPlayer mPlayer;
//	ImageButton buttonPlay;
//	ImageButton buttonStop;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.mediaplayer, container, false);
		vidSurface = (SurfaceView) view.findViewById(R.id.surfView);
		vidHolder = vidSurface.getHolder();
		vidHolder.addCallback(this);
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.mediaplayer);
//
//		buttonPlay = (ImageButton) findViewById(R.id.play);
//		buttonPlay.setOnClickListener(new OnClickListener() {
//
//			public void onClick(View v) {
//				mPlayer = new MediaPlayer();
//				mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//				try {
//					mPlayer.setDataSource(url);
//					//Toast.makeText(getApplicationContext(), mPlayer.getDuration(), Toast.LENGTH_LONG).show();
//				} catch (IllegalArgumentException e) {
//					Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
//				} catch (SecurityException e) {
//					Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
//				} catch (IllegalStateException e) {
//					Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				try {
//					mPlayer.prepare();
//				} catch (IllegalStateException e) {
//					Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
//				} catch (IOException e) {
//					Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
//				}
//				mPlayer.start();
//			}
//		});
//
//		buttonStop = (ImageButton) findViewById(R.id.stop);
//		buttonStop.setOnClickListener(new OnClickListener() {
//
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if(mPlayer!=null && mPlayer.isPlaying()){
//					mPlayer.stop();
//				}
//			}
//		});
		return view;
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		try {
		    mediaPlayer = new MediaPlayer();
		    mediaPlayer.setDisplay(vidHolder);
		    mediaPlayer.setDataSource(url);
		    mediaPlayer.prepare();
		    mediaPlayer.setOnPreparedListener(this);
		    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		} 
		catch(Exception e){
		    e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mediaPlayer.start();
	}

}