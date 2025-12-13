package graduate.itdreams.android.ui.main.home;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import graduate.itdreams.android.R;

public class FeedbackViewDialogFragment extends DialogFragment {

    private static final String ARG_CONTENT = "arg_content";

    public static FeedbackViewDialogFragment newInstance(String content) {
        FeedbackViewDialogFragment dialog = new FeedbackViewDialogFragment();
        Bundle args = new Bundle();
        args.putString("content", content);
        dialog.setArguments(args);
        return dialog;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.layout_feedback_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView tvContent = view.findViewById(R.id.tvFeedbackContent);
        Button btnClose = view.findViewById(R.id.btnClose);
        String content = getArguments().getString("content");
        if(content != null){
            tvContent.setText(content);
        }
        btnClose.setOnClickListener(v -> dismiss());
    }


    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85);

            getDialog().getWindow().setLayout(
                    width,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}

