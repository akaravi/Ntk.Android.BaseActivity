package ntk.android.base.dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import es.dmoral.toasty.Toasty;
import ntk.android.base.Extras;
import ntk.android.base.R;

public class DownloadFileDialog extends DialogFragment {
    List<String> urls;


    public static void SHOW_DIALOG(Context c, FragmentManager fm, List<String> linkFileIdsSrc) {
        if (linkFileIdsSrc != null && linkFileIdsSrc.size() > 0) {
            DownloadFileDialog dialog = new DownloadFileDialog();
            Bundle b = new Bundle();
            ArrayList list = new ArrayList();
            list.addAll(linkFileIdsSrc);
            b.putStringArrayList(Extras.EXTRA_FIRST_ARG, list);
            dialog.setCancelable(false);
            dialog.setArguments(b);
            dialog.show(fm, "fragment_captcha");
        } else
            Toasty.error(c, R.string.download_url_not_exist, Toasty.LENGTH_LONG, true).show();
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
        v.findViewById(R.id.btnDialogClose).setOnClickListener(v1 -> dismiss());
        v.findViewById(R.id.dialog_close).setOnClickListener(v1 -> dismiss());
        int index = 0;
        for (String link :
                urls) {
            View innerV = inflater.inflate(R.layout.item_file_download, null, false);
            lin.addView(innerV);
            ((TextView) innerV.findViewById(R.id.tvFile)).setText(getString(R.string.file_number) + (++index)  );
            innerV.setOnClickListener(v1 -> showLink(link));
        }
        return v;
    }

    private void showLink(String link) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(link));
        startActivity(i);
    }


    public boolean isShow() {
        if (this.getDialog() != null && this.getDialog().isShowing() && !this.isRemoving()) {
            //dialog is showing so do something
            return true;
        } else
            return false;
    }

}
