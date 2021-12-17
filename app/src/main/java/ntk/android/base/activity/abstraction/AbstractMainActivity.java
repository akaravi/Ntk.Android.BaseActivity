package ntk.android.base.activity.abstraction;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.LinearLayoutCompat;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import es.dmoral.toasty.Toasty;
import io.reactivex.annotations.NonNull;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import ntk.android.base.ApplicationParameter;
import ntk.android.base.BaseNtkApplication;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.activity.common.IntroActivity;
import ntk.android.base.appclass.UpdateClass;
import ntk.android.base.config.GenericErrors;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
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
                        Toasty.warning(getApplicationContext(), R.string.per_push_back,
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
        UpdateClass updateClass = Preferences.with(this).appVariableInfo().updateInfo();
        ApplicationParameter AppParams = BaseNtkApplication.get().getApplicationParameter();
        if (!AppParams.APPLICATION_ID().contains(".APPNTK")) {
            if (updateClass.isForced) {
                if (updateClass.version != AppParams.VERSION_CODE())
                    UpdateFore(updateClass.url);
            } else {
                if (updateClass.version > AppParams.VERSION_CODE())
                    Update(updateClass.url);
            }
        }
    }

    /**
     * optional update if user want
     *
     * @param url
     */
    private void Update(String url) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.dialog_permission);
        ((TextView) dialog.findViewById(R.id.lbl1PernissionDialog)).setTypeface(FontManager.T1_Typeface(this));
        ((TextView) dialog.findViewById(R.id.lbl1PernissionDialog)).setText(R.string.per_notice);
        ((TextView) dialog.findViewById(R.id.lbl2PernissionDialog)).setTypeface(FontManager.T1_Typeface(this));
        ((TextView) dialog.findViewById(R.id.lbl2PernissionDialog)).setText(R.string.per_update_version);
        Button Ok = dialog.findViewById(R.id.btnOkPermissionDialog);
        Ok.setTypeface(FontManager.T1_Typeface(this));
        Ok.setOnClickListener(view1 -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            dialog.dismiss();
        });
        Button Cancel = dialog.findViewById(R.id.btnCancelPermissionDialog);
        Cancel.setTypeface(FontManager.T1_Typeface(this));
        Cancel.setOnClickListener(view12 -> dialog.dismiss());
        dialog.show();
    }

    /**
     * force update app
     *
     * @param url
     */
    private void UpdateFore(String url) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.dialog_update);
        ((TextView) dialog.findViewById(R.id.lbl1PernissionDialogUpdate)).setTypeface(FontManager.T1_Typeface(this));
        ((TextView) dialog.findViewById(R.id.lbl1PernissionDialogUpdate)).setText(R.string.per_notice);
        ((TextView) dialog.findViewById(R.id.lbl2PernissionDialogUpdate)).setTypeface(FontManager.T1_Typeface(this));
        ((TextView) dialog.findViewById(R.id.lbl2PernissionDialogUpdate)).setText(R.string.per_update_version2);
        Button Ok = dialog.findViewById(R.id.btnOkPermissionDialogUpdate);
        Ok.setTypeface(FontManager.T1_Typeface(this));
        Ok.setOnClickListener(view1 -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
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

    public void onInviteMethod() {
        UpdateClass updateInfo = Preferences.with(this).appVariableInfo().updateInfo();

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.dialog_qrcode);
        dialog.show();
        TextView Lbl = dialog.findViewById(R.id.lblTitleDialogQRCode);
        Lbl.setTypeface(FontManager.T1_Typeface(this));
        String qrCode = Preferences.with(this).appVariableInfo().qrCode();
        QRGEncoder qrgEncoder = new QRGEncoder(qrCode, null, QRGContents.Type.TEXT, 300);
        try {
            Bitmap bitmap = qrgEncoder.getBitmap();
            ImageView img = dialog.findViewById(R.id.qrCodeDialogQRCode);
            img.setImageBitmap(bitmap);
            if (bitmap==null)
                throw new Exception();
        } catch (Exception e) {
            String base64Image = qrCode.split(",")[1];
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ImageView img = dialog.findViewById(R.id.qrCodeDialogQRCode);
            img.setImageBitmap(decodedByte);
        }
        dialog.findViewById(R.id.qrCodeDialogQRCode).setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(getString(R.string.per_link_download), Preferences.with(v.getContext()).appVariableInfo().updateInfo().url);
            clipboard.setPrimaryClip(clip);
            Toasty.success(AbstractMainActivity.this, R.string.per_link_download2, Toast.LENGTH_LONG, true).show();
        });
        Button Btn = dialog.findViewById(R.id.btnDialogQRCode);
        Btn.setTypeface(FontManager.T1_Typeface(this));
        Btn.setOnClickListener(v -> {
            dialog.dismiss();
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, this.getString(R.string.app_name) + "\n" + getString(R.string.per_link) + "\n" + updateInfo.url);
            shareIntent.setType("text/txt");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            this.startActivity(Intent.createChooser(shareIntent, getString(R.string.per_shareto)));
        });
    }

    public void onFeedbackClick() {
//        if (Preferences.with(this).appVariableInfo().feedback() == null)
//            startActivity(new Intent(this, FeedbackListActivity.class));
//        else {
        ApplicationScoreDtoModel request = new ApplicationScoreDtoModel();
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.dialog_comment);
        dialog.show();
        TextView Lbl = dialog.findViewById(R.id.lblTitleDialogComment);
        Lbl.setTypeface(FontManager.T1_Typeface(this));
        final EditText Txt = dialog.findViewById(R.id.txtDialogComment);
        Txt.setTypeface(FontManager.T1_Typeface(this));
//        Txt.setText(EasyPreference.with(this).getString("RateMessage", ""));
        final MaterialRatingBar Rate = dialog.findViewById(R.id.rateDialogComment);
//        Rate.setRating(EasyPreference.with(this).getInt("Rate", 0));
        Rate.setOnRatingChangeListener((ratingBar, rating) -> {
            request.ScorePercent = (int) (rating * 20);
            if (request.ScorePercent > 100)
                request.ScorePercent = 100;
        });
        Button Btn = dialog.findViewById(R.id.btnDialogComment);
        Btn.setTypeface(FontManager.T1_Typeface(this));
        Btn.setOnClickListener(v -> {
            if (Txt.getText().toString().isEmpty()) {
                Toasty.error(this, R.string.per_insert_comment, Toasty.LENGTH_LONG, true).show();
            } else {
                if (AppUtill.isNetworkAvailable(this)) {
                    request.ScoreComment = Txt.getText().toString();
                    request.LinkApiId = Preferences.with(AbstractMainActivity.this).appVariableInfo().appId();
                    //todo show loading

                    ServiceExecute.execute(new ApplicationAppService(this).submitAppScore(request))
                            .subscribe(new NtkObserver<ErrorExceptionBase>() {
                                @Override
                                public void onNext(@NonNull ErrorExceptionBase response) {
                                    if (response.IsSuccess) {
                                        Preferences.with(AbstractMainActivity.this).appVariableInfo().setFeedback(request);
                                        Toasty.success(AbstractMainActivity.this,  R.string.per_success, Toast.LENGTH_LONG, true).show();
                                    } else {
                                        Toasty.warning(AbstractMainActivity.this, response.ErrorMessage, Toast.LENGTH_LONG, true).show();
                                    }
                                }

                                @Override
                                public void onError(@NonNull Throwable e) {
                                    new GenericErrors().throwableException((error, tryAgain) -> Toasty.warning(AbstractMainActivity.this, error, Toast.LENGTH_LONG, true).show(), e, () -> {
                                    });

                                }
                            });
                } else {
                    Toasty.error(this, R.string.per_no_net, Toasty.LENGTH_LONG, true).show();
                }
                dialog.dismiss();
            }
        });
//        }
    }

    public void onMainIntro() {
        Intent intent = new Intent(this, IntroActivity.class);
        intent.putExtra(IntroActivity.ExtraComeFromMain, true);
        this.startActivity(intent);
    }
}
