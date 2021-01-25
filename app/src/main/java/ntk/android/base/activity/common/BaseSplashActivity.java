package ntk.android.base.activity.common;


import android.widget.TextView;

import androidx.annotation.LayoutRes;

import ntk.android.base.BaseNtkApplication;
import ntk.android.base.R;
import ntk.android.base.activity.abstraction.AbstractSplashActivity;
import ntk.android.base.utill.FontManager;

public abstract class BaseSplashActivity extends AbstractSplashActivity {


    @Override
    protected void onCreated() {
        SplashViewController splashViewController = SplashViewController();
        setContentView(splashViewController.getLayout());
        splashViewController.buildUi();
    }


    public SplashViewController SplashViewController() {
        return new SplashViewController() {
            @Override
            public int getLayout() {
                return R.layout.common_splash_activity;
            }

            @Override
            public void buildUi() {
                TextView Lbl = findViewById(R.id.lblVersionActSplash);
                findViewById(R.id.splash_debugView).setOnClickListener(BaseSplashActivity.this::showDebugView);
                findViewById(R.id.activity_BaseError).findViewById(R.id.debugModeView).setOnClickListener(BaseSplashActivity.this::showDebugView);
                Lbl.setTypeface(FontManager.GetTypeface(BaseSplashActivity.this, FontManager.IranSans));
                Lbl.setText("نسخه  " + (int) Float.parseFloat(BaseNtkApplication.get().getApplicationParameter().VERSION_NAME())
                        + "." + BaseNtkApplication.get().getApplicationParameter().VERSION_CODE());
            }
        };
    }

    protected interface SplashViewController {
        @LayoutRes
        int getLayout();

        void buildUi();
    }


}
