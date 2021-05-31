package ntk.android.base.activity.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;

import ntk.android.base.Extras;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;

public class VideoPlayerActivity extends BaseActivity {
    private SimpleExoPlayer player;
    private PlayerView playerView;
    final int podcast = 0;
    final int video = 1;

    int type = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoplayer_activity);
        findViewById(R.id.imgToolbarBack).setOnClickListener(v -> finish());
        playerView = findViewById(R.id.playerView);
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        initializePlayer();
        player.prepare();
        initializePlayer();

    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            initializePlayer();
        }
    }

    private void initializePlayer() {

        if (player != null) {
            MediaItem mediaItem = MediaItem.fromUri(getIntent().getExtras().getString(Extras.EXTRA_FIRST_ARG));
            player.setMediaItem(mediaItem);
            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer();
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        if (playerView != null) {
            playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }

    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }

    public static void VIDEO(Context c, String url) {
        Intent i = new Intent(c, VideoPlayerActivity.class);
        i.putExtra(Extras.EXTRA_FIRST_ARG, url);
        c.startActivity(i);
    }

    public static void PODCAST(Context c, String url) {
        Intent i = new Intent(c, VideoPlayerActivity.class);
        i.putExtra(Extras.EXTRA_FIRST_ARG, url);
        c.startActivity(i);
    }

}
