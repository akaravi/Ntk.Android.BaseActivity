package ntk.android.base.activity.common;


import android.widget.TextView;

import androidx.annotation.LayoutRes;

import ntk.android.base.BaseNtkApplication;
import ntk.android.base.NTKApplication;
import ntk.android.base.R;
import ntk.android.base.activity.abstraction.AbstractSplashActivity;
import ntk.android.base.styles.BaseModuleStyle;
import ntk.android.base.styles.UnitStyleEnum;
import ntk.android.base.utill.FontManager;
import ntk.android.base.view.ThemeNameEnum;

public abstract class BaseSplashActivity extends AbstractSplashActivity {


    @Override
    protected void onCreated() {
        SplashViewController splashViewController;
        ThemeNameEnum theme;
        //create splash base on theme
        BaseModuleStyle moduleStyle = NTKApplication.getApplicationStyle().getModule(UnitStyleEnum.Splash);
        if (moduleStyle != null) {
            theme = ThemeNameEnum.get(moduleStyle);
        } else {
            theme = NTKApplication.getApplicationStyle().getTheme();
        }
        if (theme == ThemeNameEnum.THEME1 || theme == ThemeNameEnum.DEFAULT)
            splashViewController = SplashViewController1();
        else
            splashViewController = SplashViewController2();
        setContentView(splashViewController.getLayout());
        splashViewController.buildUi();
    }


    public SplashViewController SplashViewController1() {
        return new SplashViewController() {
            @Override
            public int getLayout() {
                return R.layout.common_splash_activity;
            }

            @Override
            public void buildUi() {
                TextView Lb2 = findViewById(R.id.lblWorkActSplash);
                TextView Lbl = findViewById(R.id.lblVersionActSplash);
                findViewById(R.id.splash_debugView).setOnClickListener(BaseSplashActivity.this::showDebugView);
                if (findViewById(R.id.activity_BaseError) != null)
                    findViewById(R.id.activity_BaseError).setOnClickListener(BaseSplashActivity.this::showDebugView);
                if (Lb2 != null)
                    Lb2.setTypeface(FontManager.T1_Typeface(BaseSplashActivity.this));
                Lbl.setTypeface(FontManager.T1_Typeface(BaseSplashActivity.this));
                Lbl.setText(getString(R.string.version_number) + "  " + (BaseNtkApplication.get().getApplicationParameter().VERSION_NAME())
                        + "    " + getString(R.string.build_number) + "  " + BaseNtkApplication.get().getApplicationParameter().VERSION_CODE());
            }
        };
    }

    public SplashViewController SplashViewController2() {
        return new SplashViewController() {
            @Override
            public int getLayout() {
                return R.layout.common_splash_activity_2;
            }

            @Override
            public void buildUi() {
                TextView Lb2 = findViewById(R.id.lblWorkActSplash);
                TextView Lbl = findViewById(R.id.lblVersionActSplash);
                findViewById(R.id.splash_debugView).setOnClickListener(BaseSplashActivity.this::showDebugView);
                if (findViewById(R.id.activity_BaseError) != null)
                    findViewById(R.id.activity_BaseError).setOnClickListener(BaseSplashActivity.this::showDebugView);
                if (Lb2 != null)
                    Lb2.setTypeface(FontManager.T1_Typeface(BaseSplashActivity.this));
                Lbl.setTypeface(FontManager.T1_Typeface(BaseSplashActivity.this));
                Lbl.setText(getString(R.string.version_number) + "  " + (BaseNtkApplication.get().getApplicationParameter().VERSION_NAME())
                        + "    " + getString(R.string.build_number) + "  " + BaseNtkApplication.get().getApplicationParameter().VERSION_CODE());
            }
        };
    }

    protected interface SplashViewController {
        @LayoutRes
        int getLayout();

        void buildUi();
    }


}
