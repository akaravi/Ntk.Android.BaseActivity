package ntk.android.base.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
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

import ntk.android.base.R;
import ntk.android.base.model.NotificationModel;
import ntk.android.base.room.RoomDb;
import ntk.android.base.utill.FontManager;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {

    private ArrayList<NotificationModel> arrayList;
    private Context context;

    public NotificationListAdapter(Context context, ArrayList<NotificationModel> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notif_item_recycler, viewGroup, false);
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
            ((TextView) dialog.findViewById(R.id.lbl1PernissionDialog)).setText(R.string.per_notice);
            ((TextView) dialog.findViewById(R.id.lbl2PernissionDialog)).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            ((TextView) dialog.findViewById(R.id.lbl2PernissionDialog)).setText(R.string.notice_delete_notification);
            Button Ok = dialog.findViewById(R.id.btnOkPermissionDialog);
            Ok.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Ok.setOnClickListener(view1 -> {
                RoomDb.getRoomDb(context).NotificationDoa().Delete(arrayList.get(position));
                Toast.makeText(context, R.string.delete_notification, Toast.LENGTH_SHORT).show();
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


        List<TextView> Lbls;
        ImageView ImgCancle;
        ImageView ImgInbox;
        List<ViewGroup> Root;

        public ViewHolder(View view) {
            super(view);
            Lbls = new ArrayList() {{
                add(view.findViewById(R.id.lblTitleInbox));
                add(view.findViewById(R.id.lblMessageInbox));
                add(view.findViewById(R.id.lblDetailInbox));

            }};
            ImgCancle = view.findViewById(R.id.imgRemoveInbox);
            ImgInbox = view.findViewById(R.id.imgInbox);
            ImgInbox = view.findViewById(R.id.imgInbox);
            Root = new ArrayList() {{
                add(view.findViewById(R.id.rootInbox));
                add(view.findViewById(R.id.rowDetailInbox));

            }};
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
