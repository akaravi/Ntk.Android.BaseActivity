package ntk.android.base.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

import ntk.android.base.Extras;
import ntk.android.base.R;

public class DownloadFileDialog extends DialogFragment {
    List<String> urls;


    public static void SHOW_DIALOG(FragmentManager fm, List<String> linkFileIdsSrc) {
        DownloadFileDialog dialog = new DownloadFileDialog();
        Bundle b = new Bundle();
        ArrayList list = new ArrayList();
        list.addAll(linkFileIdsSrc);
        b.putStringArrayList(Extras.EXTRA_FIRST_ARG, list);
        dialog.setArguments(b);
        dialog.show(fm, "fragment_captcha");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        urls = getArguments().getStringArrayList(Extras.EXTRA_FIRST_ARG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_files_download, container);
        LinearLayout lin = v.findViewById(R.id.linear);
        int index = 0;
        for (String link :
                urls) {
            View innerV = inflater.inflate(R.layout.item_file_download, null, false);
            lin.addView(innerV);
            ((TextView) innerV.findViewById(R.id.tvFile)).setText("فایل شماره " + (++index));
            innerV.setOnClickListener(v1 -> showLink(link));
        }
        return v;
    }

    private void showLink(String link) {

    }


    public boolean isShow() {
        if (this.getDialog() != null && this.getDialog().isShowing() && !this.isRemoving()) {
            //dialog is showing so do something
            return true;
        } else
            return false;
    }

}
