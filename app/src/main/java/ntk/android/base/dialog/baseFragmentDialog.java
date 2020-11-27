package ntk.android.base.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ntk.android.base.R;
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
            ViewGroup inflate = (ViewGroup) inflater.inflate(R.layout.base_fragmnet_dialog, null);
//            View activity = inflate.findViewById(R.id.fragment_stub);
            RelativeLayout activity = inflate.findViewById(R.id.dialog_view_stub);
            activity.addView(inflater.inflate(stunLayout, null));
            inflatetoRelative(inflater,inflate,R.layout.sub_base_empty, R.id.dialog_frag_baseEmpty);
            inflatetoRelative(inflater, inflate, R.layout.sub_base_error, R.id.dialog_frag_baseError);
            inflatetoRelative(inflater, inflate, R.layout.sub_base_loading, R.id.dialog_frag_baseLoading);

            Switcher.Builder builder = new Switcher.Builder(getContext());
            builder.forDialog()//for dialog styling
                    .addEmptyView(inflate.findViewById(R.id.dialog_frag_baseEmpty))
                    .addProgressView(inflate.findViewById(R.id.dialog_frag_baseLoading))
                    .addContentView(activity)
                    .addErrorView(inflate.findViewById(R.id.dialog_frag_baseError)).setErrorLabel((R.id.tvError));
//                    .addProgressView(inflate.findViewById(R.id.sub_loading));
            switcher = builder.build();
            return inflate;
        }

    }

    private void inflatetoRelative(LayoutInflater inflater, ViewGroup inflate, int childView, int ids) {
        View child = inflater.inflate(childView, null);
        child.setId(ids);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)inflate.findViewById(R.id.frame).getLayoutParams();
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
