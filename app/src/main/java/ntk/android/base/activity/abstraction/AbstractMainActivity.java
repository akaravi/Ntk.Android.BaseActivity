package ntk.android.base.activity.abstraction;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.gson.Gson;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import ntk.android.base.ApplicationParameter;
import ntk.android.base.BaseNtkApplication;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.activity.common.IntroActivity;
import ntk.android.base.api.core.entity.CoreMain;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.dtomodel.application.ApplicationScoreDtoModel;
import ntk.android.base.entitymodel.base.ErrorExceptionBase;
import ntk.android.base.services.application.ApplicationAppService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;
import ntk.android.base.utill.prefrense.Preferences;

public class AbstractMainActivity extends BaseActivity {
    private long lastPressedTime;
    private static final int PERIOD = 2000;

    @Override
    protected void onStart() {
        super.onStart();
        CheckUpdate();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            switch (event.getAction()) {
                case KeyEvent.ACTION_DOWN:
                    if (event.getDownTime() - lastPressedTime < PERIOD) {
                        finish();
                    } else {
                        Toasty.warning(getApplicationContext(), "برای خروج مجددا کلید بازگشت را فشار دهید",
                                Toast.LENGTH_SHORT, true).show();
                        lastPressedTime = event.getEventTime();
                    }
                    return true;
            }
        }
        return false;
    }

    /**
     * check new version availability
     */
    protected void CheckUpdate() {
        String st = Preferences.with(this).appVariableInfo().configapp();
        CoreMain mcr = new Gson().fromJson(st, CoreMain.class);
        ApplicationParameter AppParams = BaseNtkApplication.get().getApplicationParameter();
        if (mcr.AppVersion > AppParams.VERSION_CODE() && !AppParams.APPLICATION_ID().contains(".APPNTK")) {
            if (mcr.AppForceUpdate) {
                UpdateFore();
            } else {
                Update();
            }
        }
    }

    /**
     * optional update if user want
     */
    private void Update() {
        String st = Preferences.with(this).appVariableInfo().configapp();
        CoreMain mcr = new Gson().fromJson(st, CoreMain.class);
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.dialog_permission);
        ((TextView) dialog.findViewById(R.id.lbl1PernissionDialog)).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        ((TextView) dialog.findViewById(R.id.lbl1PernissionDialog)).setText("توجه");
        ((TextView) dialog.findViewById(R.id.lbl2PernissionDialog)).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        ((TextView) dialog.findViewById(R.id.lbl2PernissionDialog)).setText("نسخه جدید اپلیکیشن اومده دوست داری آبدیت بشه؟؟");
        Button Ok = dialog.findViewById(R.id.btnOkPermissionDialog);
        Ok.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Ok.setOnClickListener(view1 -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(mcr.AppUrl));
            startActivity(i);
            dialog.dismiss();
        });
        Button Cancel = dialog.findViewById(R.id.btnCancelPermissionDialog);
        Cancel.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Cancel.setOnClickListener(view12 -> dialog.dismiss());
        dialog.show();
    }

    /**
     * force update app
     */
    private void UpdateFore() {
        String st = Preferences.with(this).appVariableInfo().configapp();
        CoreMain mcr = new Gson().fromJson(st, CoreMain.class);
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.dialog_update);
        ((TextView) dialog.findViewById(R.id.lbl1PernissionDialogUpdate)).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        ((TextView) dialog.findViewById(R.id.lbl1PernissionDialogUpdate)).setText("توجه");
        ((TextView) dialog.findViewById(R.id.lbl2PernissionDialogUpdate)).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        ((TextView) dialog.findViewById(R.id.lbl2PernissionDialogUpdate)).setText("نسخه جدید اپلیکیشن اومده حتما باید آبدیت بشه");
        Button Ok = dialog.findViewById(R.id.btnOkPermissionDialogUpdate);
        Ok.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Ok.setOnClickListener(view1 -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(mcr.AppUrl));
            startActivity(i);
            dialog.dismiss();
        });
        dialog.setOnKeyListener((dialog1, keyCode, event) -> {
            switch (event.getAction()) {
                case KeyEvent.ACTION_DOWN:
                    finish();
            }
            return true;
        });
        dialog.show();
    }

    protected void onInviteMethod() {
        String st = Preferences.with(this).appVariableInfo().configapp();
        CoreMain mcr = new Gson().fromJson(st, CoreMain.class);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.dialog_qrcode);
        dialog.show();
        TextView Lbl = dialog.findViewById(R.id.lblTitleDialogQRCode);
        Lbl.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));

        QRGEncoder qrgEncoder = new QRGEncoder(mcr.AppUrl, null, QRGContents.Type.TEXT, 300);
        try {
            Bitmap bitmap = qrgEncoder.encodeAsBitmap();
            ImageView img = dialog.findViewById(R.id.qrCodeDialogQRCode);
            img.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Toasty.warning(this, e.getMessage(), Toast.LENGTH_LONG, true).show();
        }

        Button Btn = dialog.findViewById(R.id.btnDialogQRCode);
        Btn.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Btn.setOnClickListener(v -> {
            dialog.dismiss();
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, this.getString(R.string.app_name) + "\n" + "لینک دانلود:" + "\n" + mcr.AppUrl);
            shareIntent.setType("text/txt");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            this.startActivity(Intent.createChooser(shareIntent, "به اشتراک گزاری با...."));
        });
    }

    protected void onFeedbackClick(){
        ApplicationScoreDtoModel request = new ApplicationScoreDtoModel();
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.dialog_comment);
        dialog.show();
        TextView Lbl = dialog.findViewById(R.id.lblTitleDialogComment);
        Lbl.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        final EditText Txt = dialog.findViewById(R.id.txtDialogComment);
        Txt.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
//        Txt.setText(EasyPreference.with(this).getString("RateMessage", ""));
        final MaterialRatingBar Rate = dialog.findViewById(R.id.rateDialogComment);
//        Rate.setRating(EasyPreference.with(this).getInt("Rate", 0));
        Rate.setOnRatingChangeListener((ratingBar, rating) -> {
            request.ScorePercent = (int) rating;
            //برای تبدیل به درصد
            request.ScorePercent = request.ScorePercent * 17;
            if (request.ScorePercent > 100)
                request.ScorePercent = 100;
        });
        Button Btn = dialog.findViewById(R.id.btnDialogComment);
        Btn.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Btn.setOnClickListener(v -> {
            if (Txt.getText().toString().isEmpty()) {
                Toasty.error(this, "لطفا نظر خود را وارد نمایید", Toasty.LENGTH_LONG, true).show();
            } else {
                if (AppUtill.isNetworkAvailable(this)) {
                    request.ScoreComment = Txt.getText().toString();
                    //todo show loading

                    new ApplicationAppService(this).submitAppScore(request)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new NtkObserver<ErrorExceptionBase>() {
                                @Override
                                public void onNext(@NonNull ErrorExceptionBase response) {
                                    if (response.IsSuccess)
                                        Toasty.success(AbstractMainActivity.this, "با موفقیت ثبت شد", Toast.LENGTH_LONG, true).show();
                                    else {
                                        Toasty.warning(AbstractMainActivity.this, "خظا در دریافت اطلاعات", Toast.LENGTH_LONG, true).show();
                                    }
                                }

                                @Override
                                public void onError(@NonNull Throwable e) {
                                    Toasty.warning(AbstractMainActivity.this, "خظا در اتصال به مرکز", Toast.LENGTH_LONG, true).show();

                                }
                            });
                } else {
                    Toasty.error(this, "عدم دسترسی به اینترنت", Toasty.LENGTH_LONG, true).show();
                }
                dialog.dismiss();
            }
        });
    }

    public void onMainIntro() {
        Intent intent = new Intent(this, IntroActivity.class);
        intent.putExtra(IntroActivity.ExtraComeFromMain, true);
        this.startActivity(intent);
    }
}
