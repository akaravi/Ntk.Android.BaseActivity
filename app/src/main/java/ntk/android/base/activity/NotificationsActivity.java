package ntk.android.base.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;


import ntk.android.base.NTKBASEApplication;
import ntk.android.base.R;
import ntk.android.base.adapter.AdInbox;
import ntk.android.base.event.notificationEvent;
import ntk.android.base.model.NotificationModel;
import ntk.android.base.room.RoomDb;

public class NotificationsActivity extends AppCompatActivity {

    RecyclerView Rv;

    private ArrayList<NotificationModel> notifies = new ArrayList<>();
    private AdInbox adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_inbox);

        initView();
        init();
    }

    private void initView() {
        Rv = findViewById(R.id.recyclerInbox);
        findViewById(R.id.imgBackActInbox).setOnClickListener(v -> ClickBack());
    }

    private void init() {
        Rv.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        Rv.setLayoutManager(manager);
        notifies.addAll(RoomDb.getRoomDb(this).NotificationDoa().All());
        if (notifies.size() == 0) {
            Toast.makeText(this, "پیامی برای نمایش وجود ندارد", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            adapter = new AdInbox(this, notifies);
            Rv.post(() -> {
                Rv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        NTKBASEApplication.Inbox = true;
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        NTKBASEApplication.Inbox = false;
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void SetDataChange(notificationEvent event) {
        if (event.DataChange()) {
            notifies.clear();
            notifies.addAll(RoomDb.getRoomDb(NotificationsActivity.this).NotificationDoa().All());
            Rv.post(() -> adapter.notifyDataSetChanged());
        }
    }

    public void ClickBack() {
        finish();
    }
}
