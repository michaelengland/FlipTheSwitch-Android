package com.github.michaelengland.fliptheswitch.example;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.michaelengland.fliptheswitch.Features;

public class MainActivity extends AppCompatActivity {
    private Features features;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        features = new Features(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupView();
    }

    private void setupView() {
        setupFirstColor();
        setupSecondColor();
    }

    private void setupFirstColor() {
        if (features.isRedColorEnabled()) {
            setupColor(R.id.activity_main_first_color, android.R.color.holo_red_dark, R.string.red,
                    new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    features.disableRedColor();
                    setupView();
                }
            });
        } else {
            setupColor(R.id.activity_main_first_color, android.R.color.holo_orange_dark, R.string.orange,
                    new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    features.enableRedColor();
                    setupView();
                }
            });
        }
    }

    private void setupSecondColor() {
        if (features.isPurpleColorEnabled()) {
            setupColor(R.id.activity_main_second_color, android.R.color.holo_purple, R.string.purple,
                    new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    features.disablePurpleColor();
                    setupView();
                }
            });
        } else {
            setupColor(R.id.activity_main_second_color, android.R.color.holo_green_dark, R.string.green,
                    new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    features.enablePurpleColor();
                    setupView();
                }
            });
        }
    }

    private void setupColor(@IdRes int layoutId, @ColorRes int colorId, @StringRes int colorStringId,
            View.OnClickListener onClickListener) {
        View layout = findViewById(layoutId);
        layout.setBackgroundColor(getResources().getColor(colorId));

        Button button = (Button) layout.findViewById(R.id.color_button);
        button.setOnClickListener(onClickListener);

        TextView descriptionView = (TextView) layout.findViewById(R.id.color_description);
        descriptionView.setText(getString(R.string.color_description, getString(colorStringId)));
    }
}
