package app.viridis.chai.ui;

// Android imports for working with UI and fragments
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import app.viridis.chai.R;

import java.util.Random;

//First screen of the app with a writing prompt generator
public class WelcomeFragment extends Fragment {

    // Array of simple writing prompt ideas
    private String[] ideas = {
            "How are you feeling today?",
            "Who did you talk to today?",
            "What is the one thing you accomplished today?",
            "What picture did you take today and why?",
            "Did anything funny happen today?",
            "Don't forget to love yourself! Write something nice about yourself.",
            "Who or what made you laugh today?",
            "Compared to last month, what has changed?",
            "What would you say to yourself from 5 years ago?",
            "Do not write, just turn on the audio recording and rant.",
            "What are your dreams?",
            "What is an event you are waiting for right now?",
            "Let's get our mind off of serious things! Write down your best pickup lines and jokes!",
            "Make up a story about your life (Don't forget to mark it as fake;)",
            "What is the weirdest thing you've done recently?",
            "Write a letter to your future self.",
            "What made you smile today?"
    };

    // UI elements for displaying the idea and the button to generate a new one
    private TextView ideaText;
    private Button generateButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout XML file into a View object
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        // Connect layout elements to code
        ideaText = view.findViewById(R.id.ideaText);
        generateButton = view.findViewById(R.id.generateButton);
        // Set a click listener to show a random writing idea
        generateButton.setOnClickListener(v -> {
            int index = new Random().nextInt(ideas.length); // Pick a random index
            ideaText.setText(ideas[index]); // Show the selected idea
        });

        return view;
    }
}
