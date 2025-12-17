package graduate.itdreams.android.utils;

import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.LeadingMarginSpan;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import org.xml.sax.XMLReader;

public class BindingAdapters {
    @BindingAdapter("htmlText")
    public static void setHtmlText(TextView view, String html) {
        if (html == null) return;
        // Giữ lại 4 space đầu dòng bằng cách đổi thành &nbsp;&nbsp;&nbsp;&nbsp;
        html = html.replace("\n    ", "<br>&nbsp;&nbsp;&nbsp;&nbsp;");
        html = html.replace("\n   ", "<br>&nbsp;&nbsp;&nbsp;");
        html = html.replace("\n  ", "<br>&nbsp;&nbsp;");
        html = html.replace("\n", "<br>");

        Spanned spanned = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY, null, new Html.TagHandler() {
            @Override
            public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
                if (tag.equalsIgnoreCase("blockquote")) {
                    int len = output.length();
                    if (opening) {
                        // Thêm khoảng trống trước block
                        output.setSpan(new LeadingMarginSpan.Standard(50, 50), len, len, Spannable.SPAN_MARK_MARK);
                    } else {
                        int start = output.getSpanStart(output.getSpans(0, output.length(), LeadingMarginSpan.Standard.class)[0]);
                        output.setSpan(new LeadingMarginSpan.Standard(50, 50), start, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        });


        view.setText(spanned);
        view.setMovementMethod(LinkMovementMethod.getInstance()); // nếu có <a>
        view.setLinksClickable(true);

        view.setFocusable(false);
        view.setClickable(true);
        view.setLongClickable(true);
    }
}
