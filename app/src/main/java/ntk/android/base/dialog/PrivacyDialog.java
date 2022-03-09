package ntk.android.base.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import ntk.android.base.Extras;
import ntk.android.base.R;
import ntk.android.base.adapter.BaseRecyclerAdapter;

public class PrivacyDialog extends DialogFragment {
    public WebView webViewBody;
    private String privacy;

    public static void showDialog(FragmentManager frg, String privacy) {
        PrivacyDialog fragment = new PrivacyDialog();
        Bundle args = new Bundle();
        args.putString(Extras.EXTRA_FIRST_ARG, privacy);
        fragment.setArguments(args);
        fragment.show(frg, "newFrag");
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        privacy = getArguments().getString(Extras.EXTRA_FIRST_ARG, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_privacy, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getLayoutParams().width = (BaseRecyclerAdapter.getScreenWidth() / 3) * 2;
        view.getLayoutParams().height = (BaseRecyclerAdapter.getScreenHeight() / 3) * 2;
        view.findViewById(R.id.ok).setOnClickListener(v -> close());
        view.findViewById(R.id.imgToolbarBack).setOnClickListener(v -> close());
        webViewBody = view.findViewById(R.id.webView);
        if (webViewBody != null) {
            webViewBody.getSettings().setJavaScriptEnabled(true);
            webViewBody.getSettings().setBuiltInZoomControls(true);
        }
        webViewBody.loadData("<html dir=\"rtl\" lang=\"\"><body>" + privacy + "</body></html>", "text/html; charset=utf-8", "UTF-8");

    }

    private void close() {
        if (isShow())
            dismiss();
    }

    public boolean isShow() {
        if (this.getDialog() != null && this.getDialog().isShowing() && !this.isRemoving()) {
            //dialog is showing so do something
            return true;
        } else
            return false;
    }

}

