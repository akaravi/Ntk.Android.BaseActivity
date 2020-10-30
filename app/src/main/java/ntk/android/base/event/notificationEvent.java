package ntk.android.base.event;

public class notificationEvent {

    private boolean DataChange;

    public notificationEvent(boolean DC) {
        this.DataChange = DC;
    }

    public boolean DataChange() {
        return DataChange;
    }
}
