package ntk.android.base.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.application.ApplicationIntroModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterModel;
import ntk.android.base.services.application.ApplicationIntroService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;
import ntk.android.base.utill.prefrense.Preferences;

public class IntroActivity extends BaseActivity {

    public final static String ExtraComeFromMain = "FromMain";
    List<TextView> Lbls;
    ImageView Img;
    long startTime;
    List<ApplicationIntroModel> IntroModels = new ArrayList<>();
    private int CountIntro = 0;
    private Handler handler = new Handler();
    private boolean startFromMain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preferences.with(this).appVariableInfo().setIntroSeen(true);
        setContentView(R.layout.common_intro_activty);
        if (getIntent() != null && getIntent().getExtras() != null)
            startFromMain = getIntent().getExtras().getBoolean(ExtraComeFromMain, false);
        startTime = System.currentTimeMillis();
        initView();
        init();
    }

    private void initView() {

        Lbls = new ArrayList() {{
            add(findViewById(R.id.lblTitleActIntro));
            add(findViewById(R.id.lblDescriptionActIntro));
            add(findViewById(R.id.lblBtnAfterActIntro));
        }};
        Img = findViewById(R.id.imgPhotoActIntro);

        findViewById(R.id.btnBeforeActIntro).setOnClickListener(v -> ClickBefore());
        findViewById(R.id.btnAfterActIntro).setOnClickListener(v -> ClickAfter());
    }

    private void init() {
        Lbls.get(0).setTypeface(FontManager.GetTypeface(this, FontManager.IranSansBold));
        Lbls.get(1).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Lbls.get(2).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        getdata();
    }

    private void getdata() {
        if (AppUtill.isNetworkAvailable(this)) {
            switcher.showProgressView();
            ServiceExecute.execute(new ApplicationIntroService(this).getAll(new FilterModel()))
                    .subscribe(new NtkObserver<ErrorException<ApplicationIntroModel>>() {
                        @Override
                        public void onNext(ErrorException<ApplicationIntroModel> response) {
                            if (response.IsSuccess) {
                                if (response.ListItems != null && response.ListItems.size() != 0) {
                                    IntroModels = response.ListItems;
                                    HandelIntro();
                                } else {
                                    if (!startFromMain)//only finish if come from main
                                        startActivity(new Intent(IntroActivity.this, AuthWithSmsActivity.class));
                                    finish();
                                }
                            } else {
                                switcher.showErrorView();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toasty.error(IntroActivity.this, "خطا در اتصال به مرکز", Toasty.LENGTH_LONG, true).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            switcher.showErrorView();
        }

    }

    private void HandelIntro() {
        ImageLoader.getInstance().displayImage(IntroModels.get(CountIntro).LinkMainImageIdSrc, Img);
        Lbls.get(0).setText(IntroModels.get(CountIntro).Title);
        Lbls.get(1).setText(IntroModels.get(CountIntro).Description);
        switcher.showContentView();
    }


    public void ClickBefore() {
        if (CountIntro > 0) {
            CountIntro = CountIntro - 1;
            if (CountIntro == 0) {
                findViewById(R.id.btnBeforeActIntro).setVisibility(View.INVISIBLE);
                HandelIntro();
            }
            Lbls.get(2).setText("بعدی");
        }
    }


    public void ClickAfter() {
        if (IntroModels == null)
            return;
        if (CountIntro < (IntroModels.size() - 1)) {
            CountIntro = CountIntro + 1;
            findViewById(R.id.btnBeforeActIntro).setVisibility(View.VISIBLE);
            HandelIntro();
            if (CountIntro == IntroModels.size()) {
                Lbls.get(2).setText("شروع");
            }
        } else {

            handler.removeCallbacksAndMessages(null);
            if (!startFromMain)//only finish if come from main
                startActivity(new Intent(IntroActivity.this, AuthWithSmsActivity.class));
            finish();
        }
    }
}
