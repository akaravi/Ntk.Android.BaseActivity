package ntk.android.base.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

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
    public void setDirectContentView(int content){
        super.setContentView(content);
    }
    public void setDirectContentViewWithSwicher(int layoutResID,int contentId) {
        super.setContentView(layoutResID);
        RelativeLayout frame = findViewById(R.id.StructView);
        inflatetoRelative(frame,contentId,R.layout.sub_base_empty,R.id.activity_BaseEmpty);
        inflatetoRelative(frame,contentId,R.layout.sub_base_error,R.id.activity_BaseError);
        inflatetoRelative(frame,contentId,R.layout.sub_base_loading,R.id.activity_BaseLoading);
        Switcher.Builder builder = new Switcher.Builder(this);
        builder.addEmptyView(findViewById(R.id.activity_BaseEmpty))
                .addProgressView(findViewById(R.id.activity_BaseLoading))
                .addContentView(findViewById(R.id.StructView).findViewById(contentId))
                .addErrorView(findViewById(R.id.activity_BaseError)).setErrorLabel((R.id.tvError));
//                .addProgressView(findViewById(R.id.sub_loading));
        switcher = builder.build();

    }
    private void inflatetoRelative( ViewGroup inflate,int conentId, int childView, int ids) {
        View child = getLayoutInflater().inflate(childView, null);
        child.setId(ids);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)inflate.findViewById(conentId).getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_TOP,conentId);
        params.addRule(RelativeLayout.ALIGN_BOTTOM,conentId);
        params.addRule(RelativeLayout.ALIGN_RIGHT,conentId);
        params.addRule(RelativeLayout.ALIGN_LEFT,conentId);
        child.setLayoutParams(params);
        inflate.addView(child);
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

