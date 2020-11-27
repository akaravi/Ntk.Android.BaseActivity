package ntk.android.base.activity;

import android.view.ViewStub;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import ntk.android.base.R;
import ntk.android.base.utill.FontManager;
import ntk.android.base.view.swicherview.Switcher;

public abstract class BaseActivity extends AppCompatActivity {

    protected Switcher switcher;

    protected Button btnTryAgain;

    @Override
    protected void onStart() {
        super.onStart();
        initBase();
    }


    protected void initBase() {
    }

    public void setDirectContentView(int layoutResID) {
        super.setContentView(layoutResID);
//        findViewById(R.id.swicher)
    }

    @Override
    public void setContentView(int layoutResID) {

        super.setContentView(R.layout.base_activity);
        ViewStub activity = (ViewStub) findViewById(R.id.activity_stub);
        activity.setLayoutResource(layoutResID);
        activity.inflate();
        Switcher.Builder builder = new Switcher.Builder(this);
        builder.addEmptyView(findViewById(R.id.activity_BaseEmpty))
                .addProgressView(findViewById(R.id.activity_BaseLoading))
                .addContentView(activity)
                .addErrorView(findViewById(R.id.activity_BaseError)).setErrorLabel((R.id.tvError));
//                .addProgressView(findViewById(R.id.sub_loading));
        switcher = builder.build();
        btnTryAgain = findViewById(R.id.btnTryAgain);
        btnTryAgain.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
    }


}

