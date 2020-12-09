package ntk.android.base.activity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import ntk.android.base.NTKApplication;
import ntk.android.base.R;
import ntk.android.base.view.ViewController;
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

    public void setDirectContentView(int content) {
        super.setContentView(content);
    }

    public void setDirectContentViewWithSwicher(int layoutResID, int contentId) {
        super.setContentView(layoutResID);
        if (findViewById(contentId) == null)
            throw new RuntimeException("Id of your ContentView  not exist  ID: " + getResources().getResourceEntryName(contentId) +
                    "in layout:" + getResources().getResourceEntryName(layoutResID));
        if (!(findViewById(contentId).getParent() instanceof RelativeLayout))
            throw new RuntimeException("parent of  " + getResources().getResourceEntryName(contentId) + " should be relative layout");
        RelativeLayout frame = (RelativeLayout) findViewById(contentId).getParent();
        createSwitcher(frame, contentId);

    }

    @Override
    public final void setContentView(int layoutResID) {
        RelativeLayout rv = new RelativeLayout(this);
        rv.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rv.setId(R.id.StructView);
        super.setContentView(rv);
        View newView = getLayoutInflater().inflate(layoutResID, rv, false);
        rv.addView(newView, 0);
        if (newView.getId() == -1)
            newView.setId(R.id.contentView);
        createSwitcher(rv, newView.getId());
    }

    private void createSwitcher(RelativeLayout parent, int contentId) {
        ViewController viewController = NTKApplication.getApplicationStyle().getViewController();
        inflateChild(parent, contentId, viewController.getEmpty_view(), R.id.activity_BaseEmpty);
        inflateChild(parent, contentId, viewController.getError_view(), R.id.activity_BaseError);
        inflateChild(parent, contentId, viewController.getLoading_view(), R.id.activity_BaseLoading);
        Switcher.Builder builder = new Switcher.Builder(this);
        builder.addEmptyView(parent.findViewById(R.id.activity_BaseEmpty))
                .addProgressView(parent.findViewById(R.id.activity_BaseLoading))
                .addContentView(parent.findViewById(contentId))
                .addErrorView(parent.findViewById(R.id.activity_BaseError)).setErrorLabel((R.id.tvError));
        switcher = builder.build();

    }

    private void inflateChild(ViewGroup inflate, int contentId, int childView, int ids) {
        View child = getLayoutInflater().inflate(childView, inflate, false);
        child.setId(ids);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_TOP, contentId);
        params.addRule(RelativeLayout.ALIGN_BOTTOM, contentId);
        params.addRule(RelativeLayout.ALIGN_RIGHT, contentId);
        params.addRule(RelativeLayout.ALIGN_LEFT, contentId);
        child.setLayoutParams(params);
        inflate.addView(child);
    }


}

