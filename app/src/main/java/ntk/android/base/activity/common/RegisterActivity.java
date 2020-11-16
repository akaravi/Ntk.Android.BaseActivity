package ntk.android.base.activity.common;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.NTKApplication;
import ntk.android.base.R;
import ntk.android.base.api.core.interfase.ICore;
import ntk.android.base.config.ConfigRestHeader;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.RetrofitManager;
import ntk.android.base.dtomodel.core.AuthUserSignUpModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.core.CoreUserModel;
import ntk.android.base.event.MessageEvent;
import ntk.android.base.services.core.CoreAuthService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.EasyPreference;
import ntk.android.base.utill.FontManager;

public class   RegisterActivity extends AppCompatActivity {

    ProgressBar Loading;
    EditText Txt;
    List<TextView> Lbls;

    private CountDownTimer Timer;
    private String PhoneNumber = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Boolean Registered = EasyPreference.with(RegisterActivity.this).getBoolean("Registered", false);
        if (Registered) {
            startActivity(new Intent(RegisterActivity.this, NTKApplication.getApplicationStyle().getMainActivity()));
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comon_register_activity);
        initView();
        init();
    }

    private void initView() {

        Lbls = new ArrayList() {
            {
                add(findViewById(R.id.lblVerificationActRegister));
                add(findViewById(R.id.lblNoPhoneActRegister));
                add(findViewById(R.id.lblCounterActRegister));
            }
        };

        Loading = findViewById(R.id.progressActRegister);
        Txt = findViewById(R.id.txtActRegister);
        findViewById(R.id.btnActRegister).setOnClickListener(v -> ClickBtn());
        findViewById(R.id.lblCounterActRegister).setOnClickListener(v -> ClickCounter());
        findViewById(R.id.RowNoPhoneActRegister).setOnClickListener(v -> ClickNoPhone());
    }

    private void init() {
        Loading.setVisibility(View.GONE);
//        Loading.getIndeterminateDrawable().setColorFilter(getResources().getColor(ColorUtils.FETCH_Attr_COLOR(this, R.attr.colorAccent)), PorterDuff.Mode.SRC_IN);

        Txt.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Lbls.get(0).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Lbls.get(1).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        ((Button) findViewById(R.id.btnActRegister)).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
    }

    public void ClickBtn() {
        if (((Button) findViewById(R.id.btnActRegister)).getText().equals("تایید شماره")) {
            if (Txt.getText().toString().isEmpty()) {
                Toast.makeText(this, "شماره موبایل خود را وارد نمایید", Toast.LENGTH_SHORT).show();
            } else {
                PhoneNumber = Txt.getText().toString();
                if (AppUtill.isNetworkAvailable(this)) {
                    if (CheckPermission()) {
                        Register();
                    } else {
                        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, 100);
                    }
                } else {
                    Toast.makeText(this, "عدم دسترسی به اینترنت", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            if (((Button) findViewById(R.id.btnActRegister)).getText().equals("ادامــه")) {
                if (Txt.getText().toString().isEmpty()) {
                    Toast.makeText(this, "کد اعتبار سنجی را وارد نمایید", Toast.LENGTH_SHORT).show();
                } else {
                    if (AppUtill.isNetworkAvailable(this)) {
                        Verify();
                    } else {
                        Toast.makeText(this, "عدم دسترسی به اینترنت", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void Verify() {
        if (AppUtill.isNetworkAvailable(this)) {
            Loading.setVisibility(View.VISIBLE);
            findViewById(R.id.cardActRegister).setVisibility(View.GONE);
            ICore iCore = new RetrofitManager(this).getCachedRetrofit().create(ICore.class);
            Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);

            AuthUserSignUpModel request = new AuthUserSignUpModel();
            request.Mobile = PhoneNumber;
//            request.Code = Txt.getText().toString(); todo

//            Observable<CoreUserResponse> observable = iCore.RegisterWithMobile(headers, request);
//            observable.observeOn(AndroidSchedulers.mainThread())
//                    .subscribeOn(Schedulers.io())
//                    .subscribe(new Observer<CoreUserResponse>() {
//                        @Override
//                        public void onSubscribe(Disposable d) {
//
//                        }
//
//                        @Override
//                        public void onNext(CoreUserResponse response) {
//                            Loading.setVisibility(View.GONE);
//                            if (!response.IsSuccess) {
//                                Toasty.warning(ntk.android.base.activity.common.RegisterActivity.this, response.ErrorMessage, Toasty.LENGTH_LONG, true).show();
//                                findViewById(R.id.cardActRegister).setVisibility(View.VISIBLE);
//                                return;
//                            }
//                            EasyPreference.with(ntk.android.base.activity.common.RegisterActivity.this).addLong("UserId", response.Item.UserId);
//                            EasyPreference.with(ntk.android.base.activity.common.RegisterActivity.this).addLong("MemberUserId", response.Item.MemberId);
//                            EasyPreference.with(ntk.android.base.activity.common.RegisterActivity.this).addLong("SiteId", response.Item.SiteId);
//                            EasyPreference.with(ntk.android.base.activity.common.RegisterActivity.this).addBoolean("Registered", true);
//
//                            startActivity(new Intent(ntk.android.base.activity.common.RegisterActivity.this, NTKApplication.getApplicationStyle().getMainActivity()));
//                            finish();
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            findViewById(R.id.cardActRegister).setVisibility(View.VISIBLE);
//                            Loading.setVisibility(View.GONE);
//                            Toasty.warning(ntk.android.base.activity.common.RegisterActivity.this, "خطای سامانه مجددا تلاش کنید", Toasty.LENGTH_LONG, true).show();
//
//                        }
//
//                        @Override
//                        public void onComplete() {
//
//                        }
//                    });
        } else {
            Loading.setVisibility(View.GONE);
            Toasty.warning(this, "عدم دسترسی به اینترنت", Toasty.LENGTH_LONG, true).show();
        }

    }

    private void Register() {
        if (AppUtill.isNetworkAvailable(this)) {
            Loading.setVisibility(View.VISIBLE);
            findViewById(R.id.cardActRegister).setVisibility(View.GONE);
            AuthUserSignUpModel request = new AuthUserSignUpModel();
            if (PhoneNumber.length() == 0) {
                PhoneNumber = Txt.getText().toString();
            }
            request.Mobile = PhoneNumber;
            new CoreAuthService(this).signUpUser(request)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new NtkObserver<ErrorException<CoreUserModel>>() {
                        @Override
                        public void onNext(@io.reactivex.annotations.NonNull ErrorException<CoreUserModel> response) {
                            Loading.setVisibility(View.GONE);
                            if (!response.IsSuccess) {
                                Toasty.warning(RegisterActivity.this, response.ErrorMessage, Toasty.LENGTH_LONG, true).show();
                                return;
                            }
                            findViewById(R.id.cardActRegister).setVisibility(View.VISIBLE);
                            InputFilter[] filterArray = new InputFilter[1];
                            filterArray[0] = new InputFilter.LengthFilter(4);
                            Txt.setFilters(filterArray);
                            Txt.setText("");
                            Txt.setHint("کد اعتبار سنجی");
                            ((Button) findViewById(R.id.btnActRegister)).setText("ادامــه");
                            Txt.setInputType(InputType.TYPE_CLASS_NUMBER);
                            Timer = new CountDownTimer(180000, 1000) {
                                @SuppressLint({"SetTextI18n", "DefaultLocale"})
                                @Override
                                public void onTick(long l) {
                                    int seconds = (int) (l / 1000) % 60;
                                    int minutes = (int) ((l / (1000 * 60)) % 60);
                                    Lbls.get(1).setClickable(false);
                                    Lbls.get(1).setText(" لطفا منتظر دریافت کد اعتبار سنجی بمانید " + String.format("%d:%d", minutes, seconds));
                                }

                                @Override
                                public void onFinish() {
                                    Lbls.get(1).setText("ارسال مجدد کد اعتبار سنجی ");
                                    Lbls.get(1).setClickable(true);
                                    Timer.cancel();
                                }
                            }.start();
                        }

                        @Override
                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                            Loading.setVisibility(View.GONE);
                            Toasty.warning(RegisterActivity.this, "خطای سامانه مجددا تلاش کنید", Toasty.LENGTH_LONG, true).show();
                            findViewById(R.id.cardActRegister).setVisibility(View.VISIBLE);

                        }
                    });
        } else {
            Loading.setVisibility(View.GONE);
            Toasty.warning(this, "عدم دسترسی به اینترنت", Toasty.LENGTH_LONG, true).show();
        }
    }


    public void ClickCounter() {
        Register();
    }


    public void ClickNoPhone() {
        EasyPreference.with(this).addBoolean("register_not_interested", true);
        startActivity(new Intent(this, NTKApplication.getApplicationStyle().getMainActivity()));
        finish();
    }

    private boolean CheckPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            return checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "عدم اجازه برای گرفتن پیامک کد اعتبار سنجی", Toast.LENGTH_SHORT).show();
                } else {
                    Register();
                }
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void SetMessage(MessageEvent event) {
        Txt.setText(event.GetMessage());
    }
}