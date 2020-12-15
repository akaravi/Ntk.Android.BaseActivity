package ntk.android.base.activity.common;

import android.widget.TextView;

import ntk.android.base.BaseNtkApplication;
import ntk.android.base.R;
import ntk.android.base.activity.abstraction.AbstractSplashActivity;
import ntk.android.base.utill.FontManager;

public abstract class BaseSplashActivity extends AbstractSplashActivity {

    TextView Lbl;

    @Override
    protected void onCreated() {
        setContentView(R.layout.common_splash_activity);
        initView();
    }

    private void initView() {
        Lbl = findViewById(R.id.lblVersionActSplash);
        findViewById(R.id.btnTryAgain).setOnClickListener(v -> ClickRefresh());
        findViewById(R.id.debugModeView).setOnClickListener(this::showDebugView);
        findViewById(R.id.activity_BaseError).findViewById(R.id.debugModeView).setOnClickListener(this::showDebugView);
        Lbl.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Lbl.setText("نسخه  " + (int) Float.parseFloat(BaseNtkApplication.get().getApplicationParameter().VERSION_NAME())
                + "." + BaseNtkApplication.get().getApplicationParameter().VERSION_CODE());
    }




}
