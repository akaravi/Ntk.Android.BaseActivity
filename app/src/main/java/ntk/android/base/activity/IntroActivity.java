package ntk.android.base.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.NTKBASEApplication;
import ntk.android.base.R;
import ntk.android.base.R2;
import ntk.android.base.config.ConfigRestHeader;
import ntk.android.base.config.ConfigStaticValue;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.EasyPreference;
import ntk.android.base.utill.FontManager;
import ntk.base.api.application.interfase.IApplication;
import ntk.base.api.application.model.ApplicationIntroRequest;
import ntk.base.api.application.model.ApplicationIntroResponse;
import ntk.base.api.utill.RetrofitManager;

public class IntroActivity extends BaseActivity {

    @BindViews({R2.id.lblTitleActIntro, R2.id.lblDescriptionActIntro, R2.id.lblBtnAfterActIntro})
    List<TextView> Lbls;

    @BindView(R2.id.imgPhotoActIntro)
    ImageView Img;

    private ApplicationIntroResponse Intro = new ApplicationIntroResponse();
    private int CountIntro = 0;
    private Handler handler = new Handler();
    long startTime;
    public int Help = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_intro);
        ButterKnife.bind(this);
        startTime = System.currentTimeMillis();
        init();
    }

    private void init() {
        Bundle bundle = getIntent().getBundleExtra("Help");
        if (bundle != null) {
            Help = bundle.getInt("Help");
        }

        Lbls.get(0).setTypeface(FontManager.GetTypeface(this, FontManager.IranSansBold));
        Lbls.get(1).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Lbls.get(2).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        getdata();
    }

    private void getdata() {
        if (AppUtill.isNetworkAvailable(this)) {
            switcher.showProgressView();
            RetrofitManager manager = new RetrofitManager(this);
            IApplication iApplication = manager.getCachedRetrofit(new ConfigStaticValue(this).GetApiBaseUrl()).create(IApplication.class);
            Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);
            ApplicationIntroRequest request = new ApplicationIntroRequest();
            Observable<ApplicationIntroResponse> Call = iApplication.GetApplicationIntro(headers, request);
            Call.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<ApplicationIntroResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ApplicationIntroResponse response) {
                            if (response.IsSuccess) {
                                if (Intro.ListItems != null && response.ListItems.size() != 0) {
                                    Intro.ListItems = response.ListItems;
                                    HandelIntro();
                                } else {
                                    if (Help != 0) {
                                        startActivity(new Intent(ntk.android.base.activity.IntroActivity.this, NTKBASEApplication.getApplicationStyle().getMainActivity()));
                                        finish();
                                    } else {
                                        EasyPreference.with(ntk.android.base.activity.IntroActivity.this).addBoolean("Intro", true);

                                        if (EasyPreference.with(ntk.android.base.activity.IntroActivity.this).getBoolean("Registered", false)) {
                                            new Handler().postDelayed(() -> {
                                                startActivity(new Intent(ntk.android.base.activity.IntroActivity.this, NTKBASEApplication.getApplicationStyle().getMainActivity()));
                                                finish();
                                            }, System.currentTimeMillis() - startTime >= 3000 ? 100 : 3000 - System.currentTimeMillis() - startTime);
                                        } else {
                                            new Handler().postDelayed(() -> {
                                                startActivity(new Intent(ntk.android.base.activity.IntroActivity.this, ntk.android.base.activity.RegisterActivity.class));
                                                finish();
                                            }, System.currentTimeMillis() - startTime >= 3000 ? 100 : 3000 - System.currentTimeMillis() - startTime);
                                        }
                                    }
                                }
                            } else {
                                switcher.showErrorView();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toasty.error(ntk.android.base.activity.IntroActivity.this, "خطا در اتصال به مرکز", Toasty.LENGTH_LONG, true).show();
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
        ImageLoader.getInstance().displayImage(Intro.ListItems.get(CountIntro).MainImageSrc, Img);
        Lbls.get(0).setText(Intro.ListItems.get(CountIntro).Title);
        Lbls.get(1).setText(Intro.ListItems.get(CountIntro).Description);
        switcher.showContentView();
    }

    @OnClick(R2.id.btnBeforeActIntro)
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

    @OnClick(R2.id.btnAfterActIntro)
    public void ClickAfter() {
        if (Intro.ListItems == null)
            return;
        if (CountIntro < (Intro.ListItems.size() - 1)) {
            CountIntro = CountIntro + 1;
            findViewById(R.id.btnBeforeActIntro).setVisibility(View.VISIBLE);
            HandelIntro();
            if (CountIntro == Intro.ListItems.size()) {
                Lbls.get(2).setText("شروع");
            }
        } else {
            handler.removeCallbacksAndMessages(null);
            if (Help == 0) {
                EasyPreference.with(this).addBoolean("Intro", true);
                startActivity(new Intent(ntk.android.base.activity.IntroActivity.this, ntk.android.base.activity.RegisterActivity.class));
                finish();
            } else {
                finish();
            }
        }
    }
}
