package ntk.android.base.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ntk.android.base.R;
import ntk.android.base.view.swicherview.Switcher;

public abstract class BaseFragment extends Fragment {

    protected Switcher switcher;
    int directLayout = -1;
    int stunLayout = -1;

    public abstract void onCreateFragment();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateFragment();
        onCreated();
    }

    public void onCreated() {

    }

    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (directLayout != -1) {
            //todo add direct view
            return null;
        } else {
            View inflate = inflater.inflate(R.layout.base_fragmnet, container, false);
            ViewStub activity = (ViewStub) inflate.findViewById(R.id.base_frag_stub);
            activity.setLayoutResource(stunLayout);
            activity.inflate();
            Switcher.Builder builder = new Switcher.Builder(getContext());
            builder.addEmptyView(inflate.findViewById(R.id.base_frag_baseEmpty))
                    .addContentView(activity)
                    .addErrorView(inflate.findViewById(R.id.base_frag_baseError)).setErrorLabel((R.id.tvError))
                    .addProgressView(inflate.findViewById(R.id.base_frag_baseLoading));
            switcher = builder.build();
            return inflate;
        }

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
