package ntk.android.base.event;

public class HtmlBodyEvent {

    private final String Html;

    public HtmlBodyEvent(String m) {
        this.Html = m;
    }

    public String GetMessage() {
        return Html;
    }
}
