package com.example.quizapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapp.R;
import com.example.quizapp.model.Quiz;

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    private List<Quiz> quizList;
    private OnQuizClickListener listener;

    public interface OnQuizClickListener {
        void onQuizClick(int quizId);
    }

    public QuizAdapter(List<Quiz> quizList, OnQuizClickListener listener) {
        this.quizList = quizList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quiz_item, parent, false);
        return new QuizViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizList.get(position);
        
        holder.titleTextView.setText(quiz.getTitle());
        holder.descriptionTextView.setText(quiz.getDescription());
        
        int progress = (int) quiz.getProgressPercentage();
        holder.progressBar.setProgress(progress);
        
        holder.progressTextView.setText(quiz.getCompletedQuestions() + "/" + quiz.getTotalQuestions() + " questions");
        
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onQuizClick(quiz.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public class QuizViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView, descriptionTextView, progressTextView;
        public ProgressBar progressBar;

        public QuizViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.quizTitleTextView);
            descriptionTextView = view.findViewById(R.id.quizDescriptionTextView);
            progressBar = view.findViewById(R.id.quizProgressBar);
            progressTextView = view.findViewById(R.id.quizProgressTextView);
        }
    }
}
