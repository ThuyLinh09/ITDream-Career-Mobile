package graduate.itdreams.android.ui.main.taskdetail.question;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import graduate.itdreams.android.R;
import graduate.itdreams.android.data.model.api.response.question.OptionItem;
import graduate.itdreams.android.data.model.api.response.question.TaskQuestionResponse;
import graduate.itdreams.android.databinding.ItemQuestionQuizBinding;

public class QuestionQuizAdapter extends RecyclerView.Adapter<QuestionQuizAdapter.ViewHolder> {
    private List<TaskQuestionResponse> questionList = new ArrayList<>();
    private int currentIndex = 0;
    public interface OnAnswerSelectedListener {
        void onAnswerSelected(boolean isCorrect);
    }

    private OnAnswerSelectedListener answerListener;
    public QuestionQuizAdapter(List<TaskQuestionResponse> questionList, int currentIndex, OnAnswerSelectedListener answerListener) {
        this.questionList = questionList;
        this.currentIndex = currentIndex;
        this.answerListener = answerListener;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemQuestionQuizBinding binding;

        public ViewHolder(ItemQuestionQuizBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    public void setCurrentIndex(int index) {
        this.currentIndex = index;
    }

    @NonNull
    @Override
    public QuestionQuizAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemQuestionQuizBinding binding = ItemQuestionQuizBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(QuestionQuizAdapter.ViewHolder holder, int position) {
        TaskQuestionResponse item = questionList.get(currentIndex);
        holder.binding.numberQuestion.setText("Câu " + (currentIndex+1) + "/" + questionList.size());
        holder.binding.tvQuestion.setText(item.getQuestion());
        RadioGroup rg = holder.binding.rgOptions;
        rg.removeAllViews(); // clear nếu RecyclerView tái sử dụng
        String optionsJson = item.getOptions();
        Gson gson = new Gson();
        Type listType = new TypeToken<List<OptionItem>>(){}.getType();
        List<OptionItem> optionList = gson.fromJson(optionsJson, listType);


        // tạo RadioButton động
        for (OptionItem option : optionList) {
            RadioButton rb = new RadioButton(holder.itemView.getContext());
            rb.setText(option.getOption());
            rb.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
            rb.setTextColor(Color.BLACK);
            rb.setBackgroundResource(R.drawable.bg_white_stroke_gray);

            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            rb.setLayoutParams(params);
            rg.addView(rb);

            rb.setOnClickListener(v -> {
                //if (option.isAnswer()) return;

                option.setSelected(true);

                // set màu xanh/đỏ
                for (int i = 0; i < rg.getChildCount(); i++) {
                    RadioButton child = (RadioButton) rg.getChildAt(i);
                    OptionItem childOption = optionList.get(i);

                    if (childOption.isSelected()) {
                        child.setTextColor(childOption.isAnswer() ? Color.GREEN : Color.RED);
                        child.setBackgroundResource(childOption.isAnswer() ? R.drawable.bg_white_stroke_green : R.drawable.bg_white_stroke_red);
                    } else {
                        child.setTextColor(Color.BLACK);
                        child.setBackgroundResource(R.drawable.bg_white_stroke_gray);
                    }
                    child.setEnabled(false); // disable sau khi chọn

                }
                if (answerListener != null) {
                    answerListener.onAnswerSelected(option.isAnswer());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return questionList != null ? 1 : 0;
    }

}

