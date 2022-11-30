package ntk.android.base.activity;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import io.sentry.Sentry;
import ntk.android.base.NTKApplication;
import ntk.android.base.R;
import ntk.android.base.view.ViewController;
import ntk.android.base.view.swicherview.Switcher;

public abstract class BaseActivity extends AppCompatActivity {
    private ActivityResultCallback<ActivityResult> resultInterface;
    ActivityResultLauncher<Intent> someActivityResultLauncher;
    protected Switcher switcher;

    /**
     *
     * @param language
     */
    protected void setLanguage(String language) {
        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(language);
// Call this on the main thread as it may require Activity.restart()
        AppCompatDelegate.setApplicationLocales(appLocale);
    }
    @Override
    protected void onStart() {
        super.onStart();
        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (resultInterface != null)
                            resultInterface.onActivityResult(result);
                        else {
                            Sentry.captureMessage("resultInterface Is Null");
                        }
                    }
                }
        );
        initBase();
    }

    protected void onActivityResult(ActivityResult result) {

    }

    protected void initBase() {
    }

    public void setDirectContentView(int content) {
        super.setContentView(content);
    }

    /**
     * create view base on layoutRes as ContentView and create View Switcher dynamically
     * base of parent of id ContentId
     *
     * @param layoutResID
     * @param contentId
     */
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
                .addErrorView(parent.findViewById(R.id.activity_BaseError))
                .setErrorLabelId(viewController.getErrorLabel())
                .setErrorButtonId(viewController.getErrorButton())
                .setEmptyLabelId(viewController.getErrorButton());
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


    public void lunchActivityForResult(Intent intent, int readRequestCode) {
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        BaseActivityResult(result, readRequestCode);
                    }
                });
        someActivityResultLauncher.launch(intent);
    }

    public void lunchActivityForResult(Intent intent, ActivityResultCallback myInterface) {
        this.resultInterface = myInterface;
        someActivityResultLauncher.launch(intent);
    }

    private void BaseActivityResult(ActivityResult result, int readRequestCode) {

    }
}

