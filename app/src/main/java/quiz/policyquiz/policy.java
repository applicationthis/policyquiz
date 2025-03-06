package quiz.policyquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

public class policy extends AppCompatActivity {
    private Button startQuizButton;
    private ScrollView policyScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        startQuizButton = findViewById(R.id.startQuizButton);
        policyScrollView = findViewById(R.id.policyScrollView);

        // Initially hide the quiz button
//        startQuizButton.setVisibility(View.);

        // Listen for scroll changes to reveal the quiz button after reading
        policyScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (policyScrollView.getChildAt(0).getBottom() <= (policyScrollView.getHeight() + policyScrollView.getScrollY())) {
                // ScrollView is at the bottom
                startQuizButton.setVisibility(View.VISIBLE);
            }
        });

        startQuizButton.setOnClickListener(view -> {
            Intent intent = new Intent(policy.this, QuizActivity.class);
            startActivity(intent);
        });
    }
}
