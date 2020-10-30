package ntk.android.base.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import ntk.android.base.R;
import ntk.android.base.R2;
import ntk.android.base.model.NotificationModel;
import ntk.android.base.room.RoomDb;
import ntk.android.base.utill.FontManager;

public class AdInbox extends RecyclerView.Adapter<ntk.android.base.adapter.AdInbox.ViewHolder> {

    private ArrayList<NotificationModel> arrayList;
    private Context context;

    public AdInbox(Context context, ArrayList<NotificationModel> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycler_inbox, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.Lbls.get(0).setText(arrayList.get(position).Title);
        holder.Lbls.get(1).setText(arrayList.get(position).Content);
        if (arrayList.get(position).IsRead == 0) {
            holder.Lbls.get(0).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans), Typeface.BOLD);
        }
        if (arrayList.get(position).BigImageSrc != null) {
            ImageLoader.getInstance().displayImage(arrayList.get(position).BigImageSrc, holder.ImgInbox);
        }
        holder.Root.get(0).setOnClickListener(view -> {
            RoomDb.getRoomDb(context).NotificationDoa().Update(arrayList.get(position));
            if (arrayList.get(position).Content.length() > 15) {
                if (holder.Root.get(1).getVisibility() == View.GONE) {
                    holder.Lbls.get(2).setText(arrayList.get(position).Content);
                    holder.Lbls.get(1).setVisibility(View.GONE);
                    holder.Root.get(1).setVisibility(View.VISIBLE);
                    holder.Lbls.get(0).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans), Typeface.NORMAL);
                    arrayList.get(position).IsRead = 1;
                    RoomDb.getRoomDb(context).NotificationDoa().Update(arrayList.get(position));
                } else {
                    holder.Root.get(1).setVisibility(View.GONE);
                    holder.Lbls.get(2).setText("");
                    holder.Lbls.get(1).setVisibility(View.VISIBLE);
                    holder.Lbls.get(0).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans), Typeface.NORMAL);
                }
            } else {
                holder.Lbls.get(0).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans), Typeface.NORMAL);
            }
        });

        holder.ImgCancle.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(true);
            Window window = dialog.getWindow();
            window.setLayout(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            dialog.setContentView(R.layout.dialog_permission);
            ((TextView) dialog.findViewById(R.id.lbl1PernissionDialog)).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            ((TextView) dialog.findViewById(R.id.lbl1PernissionDialog)).setText("توجه");
            ((TextView) dialog.findViewById(R.id.lbl2PernissionDialog)).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            ((TextView) dialog.findViewById(R.id.lbl2PernissionDialog)).setText("پیام مورد نظر حذف گردد؟");
            Button Ok = dialog.findViewById(R.id.btnOkPermissionDialog);
            Ok.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Ok.setOnClickListener(view1 -> {
                RoomDb.getRoomDb(context).NotificationDoa().Delete(arrayList.get(position));
                Toast.makeText(context, "پیام مورد نظر حذف گردید", Toast.LENGTH_SHORT).show();
                arrayList.remove(position);
                notifyDataSetChanged();
                dialog.dismiss();
            });
            Button Cancel = dialog.findViewById(R.id.btnCancelPermissionDialog);
            Cancel.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Cancel.setOnClickListener(view12 -> dialog.dismiss());
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindViews({R2.id.lblTitleInbox, R2.id.lblMessageInbox, R2.id.lblDetailInbox})
        List<TextView> Lbls;
        @BindView(R2.id.imgRemoveInbox)
        ImageView ImgCancle;
        @BindView(R2.id.imgInbox)
        ImageView ImgInbox;
        @BindViews({R2.id.rootInbox, R2.id.rowDetailInbox})
        List<ViewGroup> Root;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            Lbls.get(0).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Lbls.get(1).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Lbls.get(2).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //todo
//                Lbls.get(0).setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
//                Lbls.get(1).setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
//                Lbls.get(2).setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
            }
        }
    }
}
