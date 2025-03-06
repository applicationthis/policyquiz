package quiz.policyquiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    private int currentQuestionIndex = 0;
    private int score = 0;
    private TextView questionTextView;
    private RadioGroup optionsRadioGroup;
    private Button nextButton;
    private List<Question> questionList;
    private String name; // User's name from registration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        questionTextView = findViewById(R.id.questionTextView);
        optionsRadioGroup = findViewById(R.id.optionsRadioGroup);
        nextButton = findViewById(R.id.nextButton);

        // Get user name from Firebase Auth
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            name = firebaseAuth.getCurrentUser().getDisplayName();
        }

        // Initialize quiz questions and answers
        String[] questions = getResources().getStringArray(R.array.quiz_questions);
        String[][] options = new String[][]{
                getResources().getStringArray(R.array.quiz_options_1),
                getResources().getStringArray(R.array.quiz_options_2),
                getResources().getStringArray(R.array.quiz_options_3),
                getResources().getStringArray(R.array.quiz_options_4),
                getResources().getStringArray(R.array.quiz_options_5),
                getResources().getStringArray(R.array.quiz_options_6),
                getResources().getStringArray(R.array.quiz_options_7),
                getResources().getStringArray(R.array.quiz_options_8),
                getResources().getStringArray(R.array.quiz_options_9),
                getResources().getStringArray(R.array.quiz_options_10),
                getResources().getStringArray(R.array.quiz_options_11),
                getResources().getStringArray(R.array.quiz_options_12),
                getResources().getStringArray(R.array.quiz_options_13)
        };
        String[] answers = getResources().getStringArray(R.array.quiz_answers);

        // Create a list of Question objects
        questionList = new ArrayList<>();
        for (int i = 0; i < questions.length; i++) {
            questionList.add(new Question(questions[i], options[i], answers[i]));
        }

        // Shuffle the list to randomize questions
        Collections.shuffle(questionList);

        loadQuestion();

        nextButton.setOnClickListener(view -> {
            int selectedOptionId = optionsRadioGroup.getCheckedRadioButtonId();
            if (selectedOptionId != -1) {
                RadioButton selectedRadioButton = findViewById(selectedOptionId);
                String selectedAnswer = selectedRadioButton.getText().toString();
                if (selectedAnswer.equals(questionList.get(currentQuestionIndex).getAnswer())) {
                    score++;
                }

                currentQuestionIndex++;
                if (currentQuestionIndex < questionList.size()) {
                    loadQuestion();
                } else {
                    finishQuiz();
                }
            } else {
                Toast.makeText(QuizActivity.this, "Please select an option", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadQuestion() {
        Question currentQuestion = questionList.get(currentQuestionIndex);
        questionTextView.setText(currentQuestion.getQuestion());
        optionsRadioGroup.clearCheck();
        for (int i = 0; i < optionsRadioGroup.getChildCount(); i++) {
            ((RadioButton) optionsRadioGroup.getChildAt(i)).setText(currentQuestion.getOptions()[i]);
        }
    }

    private void finishQuiz() {
        if (score >= 10) {
            // Save user name to Firebase database
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PassedUsers");
            databaseReference.child(name).setValue(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(QuizActivity.this, "Congratulations! You passed the quiz.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Failed to save data: ", task.getException());
                            Toast.makeText(QuizActivity.this, "Failed to save data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(QuizActivity.this, "Sorry, you failed the quiz.", Toast.LENGTH_SHORT).show();
        }

        // Navigate to the next activity or finish
        Intent intent = new Intent(QuizActivity.this, LoginActivity.class); // Replace with your next activity
        startActivity(intent);
        finish(); // Close QuizActivity to prevent returning on back press
    }

    private static class Question {
        private final String question;
        private final String[] options;
        private final String answer;

        public Question(String question, String[] options, String answer) {
            this.question = question;
            this.options = options;
            this.answer = answer;
        }

        public String getQuestion() {
            return question;
        }

        public String[] getOptions() {
            return options;
        }

        public String getAnswer() {
            return answer;
        }
    }
}
