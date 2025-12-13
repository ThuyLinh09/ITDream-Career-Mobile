package graduate.itdreams.android.utils;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

public class BindingAdapters {
    @BindingAdapter("htmlText")
    public static void setHtmlText(TextView view, String html) {
        if (html == null) return;
        // Giữ lại 4 space đầu dòng bằng cách đổi thành &nbsp;&nbsp;&nbsp;&nbsp;
        html = html.replace("\n    ", "<br>&nbsp;&nbsp;&nbsp;&nbsp;");
        html = html.replace("\n   ", "<br>&nbsp;&nbsp;&nbsp;");
        html = html.replace("\n  ", "<br>&nbsp;&nbsp;");
        html = html.replace("\n", "<br>");
        view.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        view.setMovementMethod(LinkMovementMethod.getInstance()); // nếu có <a>
        view.setLinksClickable(true);

        view.setFocusable(false);
        view.setClickable(true);
        view.setLongClickable(true);
    }
}
