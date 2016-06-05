package com.github.michaelengland.fliptheswitch.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.github.michaelengland.fliptheswitch.Feature;
import com.github.michaelengland.fliptheswitch.FlipTheSwitch;

public class FeaturesActivity extends AppCompatActivity {
    private FlipTheSwitch flipTheSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flipTheSwitch = new FlipTheSwitch(this);
        setContentView(R.layout.activitiy_features);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getResetButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipTheSwitch.resetAllFeatures();
            }
        });

        FeaturesAdapter adapter = new FeaturesAdapter(getLayoutInflater(), FlipTheSwitch.getDefaultFeatures(),
                new FeaturesAdapter.OnFeatureToggledListener() {
                    @Override
                    public void onFeatureToggled(Feature feature, boolean enabled) {
                        flipTheSwitch.setFeatureEnabled(feature.getName(), enabled);
                    }
                });
        getListView().setAdapter(adapter);
    }

    private ListView getListView() {
        return (ListView) findViewById(R.id.activity_features_list_view);
    }

    private Button getResetButton() {
        return (Button) findViewById(R.id.toolbar_reset_button);
    }
}
