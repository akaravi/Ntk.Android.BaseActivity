package ntk.android.base.view.swicherview;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.IdRes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ntk.android.base.R;


/**
 * Base class Helper for switching content in Activity or Fragment
 * its contains array of ContentViews ErrorViews EmptyViews ProgressView @note that Array cna not be NULL
 * <p>
 * Created by m.parishani on 09/03/2017.
 */

public class Switcher {
    boolean forDialog;
    private List<View> contentViews = new ArrayList<>();
    private List<View> errorViews = new ArrayList<>();
    private List<View> emptyViews = new ArrayList<>();
    private List<View> progressViews = new ArrayList<>();
    private View toolbarProgress;
    private View loadingMoreView;
    int dialogResId = R.layout.dialog_load;
    Dialog dialog;
//    private List<View> reqestViews = new ArrayList<>();

    private Integer errorLabel;//for customization of Error
    private int errorButton;//for customization of Error
    private TextView progressLabel;//for customization of loading
    private int animDuration = 2000;
    private boolean errorShown;

    private List<Animations.FadeListener> runningAnimators = new ArrayList<>();
    private int emyptyTextId;

    private Switcher() {

    }

    private void addContentView(View contentView) {
        this.contentViews.add(contentView);
    }

    private void addErrorView(View errorView) {
        this.errorViews.add(errorView);
    }

    private void addEmptyView(View emptyView) {
        this.emptyViews.add(emptyView);
    }


    private void addProgressView(View progressView) {
        this.progressViews.add(progressView);
    }

    private void setErrorLabelID(@IdRes int errorLabel) {
        this.errorLabel = errorLabel;
    }

    private void setProgressLabel(TextView progressLabel) {
        this.progressLabel = progressLabel;
    }

    private void setAnimationDuration(int duration) {
        animDuration = duration;
    }

    public void showLoadMore() {
        if (loadingMoreView != null) {
            Animations.FadeListener animator = Animations.fadeIn(loadingMoreView, animDuration);
            if (animator != null) runningAnimators.add(animator);
        }
    }

    public void hideLoadMore() {
        if (loadingMoreView != null) {
            Animations.FadeListener animator = Animations.fadeOut(forDialog, loadingMoreView, animDuration);
            if (animator != null) runningAnimators.add(animator);
        }
    }

    public void showLoadDialog(Context context, boolean cancleable) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogResId);
        dialog.setCanceledOnTouchOutside(cancleable);

        dialog.show();
    }

    public void hideLoadDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        dialog = null;
    }

    public void hideToolbarProgress() {

        if (toolbarProgress != null) {
            Log.d(Switcher.class.getSimpleName(), "hideToolbarProgress: ");
            toolbarProgress.setVisibility(View.GONE);
            Animations.FadeListener animator = Animations.fadeOut(false, toolbarProgress, animDuration);
            if (animator != null) runningAnimators.add(animator);
        }
    }

    public void replaceContentView(View rv) {
        contentViews.clear();
        contentViews.add(rv);
    }

    public void setLoadMore(View viewById) {
        loadingMoreView = viewById;
    }

    public static class Builder {

        private Switcher switcher;
        private Context context;
        private boolean indialog;

        public Builder(Context context) {
            this.context = context;
            switcher = new Switcher();
            switcher.setAnimationDuration(context.getResources().getInteger(android.R.integer.config_shortAnimTime));
        }

        public Builder forDialog() {
            switcher.forDialog = true;
            return this;
        }

        public Builder(Switcher switcher) {
            this.switcher = switcher;
        }

        public Builder addContentView(View contentView) {
            checkNotNull(contentView, "Provided content view is null");
            switcher.addContentView(contentView);
            return this;
        }

        public Builder addErrorView(View errorView) {
            checkNotNull(errorView, "Provided error view is null");
            switcher.addErrorView(errorView);
            return this;
        }

        public Builder addEmptyView(View emptyView) {
            checkNotNull(emptyView, "Provided empty view is null");
            switcher.addEmptyView(emptyView);
            return this;
        }

        public Builder addProgressView(View progressView) {
            checkNotNull(progressView, "Provided progress view is null");
            switcher.addProgressView(progressView);
            return this;
        }

        public Builder setProgressLabel(TextView progressLabel) {
            checkNotNull(progressLabel, "Provided progress label is null");
            switcher.setProgressLabel(progressLabel);
            return this;
        }

        public Builder setErrorLabelId(Integer errorLabel) {
            checkNotNull(errorLabel, "Provided error label is null");
            switcher.setErrorLabelID(errorLabel);
            return this;
        }

        public Builder setErrorButtonId(int errorButton) {
            checkNotNull(errorButton, "Provided error label is null");
            switcher.setErrorButtonId(errorButton);
            return this;
        }

        public Builder setEmptyLabelId(int errorButton) {
            checkNotNull(errorButton, "Provided error label is null");
            switcher.setEmptyId(errorButton);
            return this;
        }

        public Switcher build() {
            switcher.setupViews();

            return switcher;
        }

        public void addTitleProgress(View view) {
            if (view != null)
                switcher.setToolbarProgress(view);
        }


//        //next version
//        public void addWaitView(View view) {
//            switcher.reqestViews.add(view);
//        }
    }

    private void setEmptyId(int emptyID) {
        this.emyptyTextId = emptyID;
    }

    private void setErrorButtonId(int errorButton) {
        this.errorButton = errorButton;
    }

    private void setToolbarProgress(View view) {
        toolbarProgress = view;
    }

    private void setupViews() {
        for (View contentView : contentViews) {
            contentView.setVisibility(View.VISIBLE);
        }

        for (View errorView : errorViews) {
            errorView.setVisibility(forDialog ? View.INVISIBLE : View.GONE);
        }

        for (View progressView : progressViews) {
            progressView.setVisibility(forDialog ? View.INVISIBLE : View.GONE);
        }

        for (View emptyView : emptyViews) {
            emptyView.setVisibility(forDialog ? View.INVISIBLE : View.GONE);
        }
//        //next version
//        for (View reqview : reqestViews) {
//            reqview.setVisibility(View.GONE);
//        }
    }

    private List<View> membersExcludingGroup(List<View> excludedGroup) {
        List<View> members = new ArrayList<>();
        members.addAll(contentViews);
        members.addAll(errorViews);
        members.addAll(progressViews);
        members.addAll(emptyViews);
        //next version
//        members.addAll(reqestViews);

        if (excludedGroup != null && excludedGroup.size() > 0) {
            members.removeAll(excludedGroup);
        }

        return members;
    }

    private boolean isViewPartOfSwitcher(View potentialMember) {
        List<View> members = new ArrayList<>();
        members.addAll(contentViews);
        members.addAll(errorViews);
        members.addAll(progressViews);
        members.addAll(emptyViews);
        //next version
//        members.addAll(reqestViews);
//        for (View view : members) {
//            if (view.equals(potentialMember)) return true;
//        }
        return false;
    }

    private void showGroup(List<View> groupToShow, boolean immediately) {
        if (groupToShow.equals(errorViews)) errorShown = true;
        else errorShown = false;

        cancelCurrentAnimators();

        for (View view : groupToShow) {
            if (immediately) {
                view.setVisibility(View.VISIBLE);
                view.setAlpha(1);
            } else {
                Animations.FadeListener animator = Animations.fadeIn(view, animDuration);
                if (animator != null) runningAnimators.add(animator);
            }
        }

        List<View> viewsToHide = membersExcludingGroup(groupToShow);
        for (View view : viewsToHide) {
            if (immediately) {
                view.setAlpha(0);
                view.setVisibility(forDialog ? View.INVISIBLE : View.GONE);
            } else {
                Animations.FadeListener animator = Animations.fadeOut(forDialog, view, animDuration);
                if (animator != null) runningAnimators.add(animator);
            }
        }
    }

    public void showContentView() {
        Log.i(Switcher.class.getSimpleName(), "showContentView");
        showGroup(contentViews, false);
    }

    public void showContentViewImmediately() {
        Log.i(Switcher.class.getSimpleName(), "showContentViewImmediately");
        showGroup(contentViews, true);
    }


    public void showProgressView() {
        hideToolbarProgress();
        Log.i(Switcher.class.getSimpleName(), "showProgressView");
        showGroup(progressViews, false);
        /*
        for vitrin frist time to load showing progress problem i put this code

         */
//        if (progressViews != null && progressViews.size() > 0 && progressViews.get(0).findViewById(R.id.BaseLoading_ProgressView) != null)
//            ((ProgressView) progressViews.get(0).findViewById(R.id.BaseLoading_ProgressView)).start();
    }

    public void showProgressViewImmediately() {
        hideToolbarProgress();
        Log.i(Switcher.class.getSimpleName(), "showProgressViewImmediately");
        showGroup(progressViews, true);
    }

    public void showProgressView(String progressMessage) {
        if (errorLabel == null) {
            throw new NullPointerException("You have to build ViewSwitcher using withProgressLabel() method");
        }

        progressLabel.setText(progressMessage);
        showProgressView();
    }

    public void showErrorView() {
        hideToolbarProgress();
        Log.i(Switcher.class.getSimpleName(), "showErrorView");
        showGroup(errorViews, false);
    }

    public void showErrorViewImmediately() {
        hideToolbarProgress();
        Log.i(Switcher.class.getSimpleName(), "showErrorView");
        showGroup(errorViews, true);
    }

    private void showErrorView(String errorMessage) {
        if (errorViews.size() == 0 || errorLabel == null || errorMessage == null) {
            throw new NullPointerException(" build ViewSwitcher showErrorView(String errorMessage)");
        }
        if (errorViews.get(0).findViewById(errorLabel) != null)
            ((TextView) errorViews.get(0).findViewById(errorLabel)).setText(errorMessage);
        else
            throw new NullPointerException("build ViewSwitcher showErrorView(String errorMessage) VIEW NULL");
    }

    private void showErrorView(Runnable runner) {
        for (View errorView : errorViews) {
//            errorView.setClickable(true);
            errorView.setAlpha(1);
//            errorView.setEnabled(true);
            errorView.findViewById(errorButton)
                    .setOnClickListener((view) -> {
                        view.setOnTouchListener(null);
                        runner.run();
                    });

        }
    }

    public void showErrorView(String errorMessage, Runnable runner) {
        if (errorLabel == null) {
            throw new NullPointerException("You have to build ViewSwitcher using withErrorLabel() method");
        }

        showErrorView(errorMessage);

        showErrorView();
        showErrorView(runner);
    }

    public void showEmptyView() {
        hideToolbarProgress();
        Log.i(Switcher.class.getSimpleName(), "showEmptyView");
        showGroup(emptyViews, false);
    }

    public void showEmptyViewImmediately() {
        Log.i(Switcher.class.getSimpleName(), "showEmptyView");
        showGroup(emptyViews, true);
    }

//    //next version
//    public void WaitUntilResponse() {
//        Log.i(ViewSwitcher.class.getSimpleName(), "showRequestView");
//        for (View v : reqestViews) {
//            v.setEnabled(false);
//        }
//    }

//    public void ResponseReady() {
//        Log.i(ViewSwitcher.class.getSimpleName(), "hideRequestView");
//        for (View v : reqestViews) {
//            v.setActivated(true);
//        }
//    }

    private void cancelCurrentAnimators() {
        Iterator<Animations.FadeListener> it = runningAnimators.iterator();
        while (it.hasNext()) {
            Animations.FadeListener animator = it.next();
            animator.onAnimationEnd();
            it.remove();
        }
    }

    public boolean isShowingError() {
        return errorShown;
    }

    public static <T> T checkNotNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }
}