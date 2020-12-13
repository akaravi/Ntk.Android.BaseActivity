package ntk.android.base.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ntk.android.base.NTKApplication;
import ntk.android.base.R;
import ntk.android.base.view.ViewController;
import ntk.android.base.view.swicherview.Switcher;

public abstract class baseFragmentDialog extends DialogFragment {
    protected Switcher switcher;
    int directLayout = -1;
    int stunLayout = -1;

    public abstract void onCreateFragment();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateFragment();
    }

    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (directLayout != -1) {
            //todo add direct view
            return null;
        } else {
            RelativeLayout rv = new RelativeLayout(getContext());
            rv.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            rv.setId(R.id.DialogStructView);
            View contentView = inflater.inflate(stunLayout, rv, false);
            rv.addView(contentView, 0);
            if (contentView.getId() == -1)
                contentView.setId(R.id.contentView);
            RelativeLayout exceptView = new RelativeLayout(getContext());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_TOP, contentView.getId());
            params.addRule(RelativeLayout.ALIGN_BOTTOM, contentView.getId());
            params.addRule(RelativeLayout.ALIGN_RIGHT, contentView.getId());
            params.addRule(RelativeLayout.ALIGN_LEFT, contentView.getId());
            exceptView.setLayoutParams(params);
            rv.addView(exceptView, 1);
            createSwitcher(rv, contentView.getId());
            return rv;
        }

    }

    private void createSwitcher(RelativeLayout parent, int contentId) {
        ViewController viewController = NTKApplication.getApplicationStyle().getViewController();
        inflateChild(parent, viewController.getEmpty_view(), R.id.activity_BaseEmpty);
        inflateChild(parent,  viewController.getError_view(), R.id.activity_BaseError);
        inflateChild(parent,  viewController.getLoading_view(), R.id.activity_BaseLoading);
        Switcher.Builder builder = new Switcher.Builder(getContext());
        builder.addEmptyView(parent.findViewById(R.id.activity_BaseEmpty))
                .addProgressView(parent.findViewById(R.id.activity_BaseLoading))
                .addContentView(parent.findViewById(contentId))
                .addErrorView(parent.findViewById(R.id.activity_BaseError)).setErrorLabelId((R.id.tvError));
        switcher = builder.build();

    }

    private void inflateChild( ViewGroup inflate, int childView, int ids) {
        View child = getLayoutInflater().inflate(childView, null);
        child.setId(ids);
        RelativeLayout.LayoutParams params =  new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        child.setLayoutParams(params);
        inflate.addView(child);
    }

    protected void setContentView(int layoutResID) {
        stunLayout = layoutResID;
    }

    public <T extends View> T findViewById(int id) {
        if (getView() == null)
            throw new RuntimeException("findviewById call befor view create");
        return getView().findViewById(id);
    }
}
