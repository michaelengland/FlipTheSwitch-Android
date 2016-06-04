package com.github.michaelengland.fliptheswitch.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.github.michaelengland.fliptheswitch.Feature;

import java.util.List;

public class FeaturesAdapter extends BaseAdapter {
    private final LayoutInflater layoutInflater;
    private final List<Feature> features;
    private final OnFeatureToggledListener onFeatureToggledListener;

    public FeaturesAdapter(LayoutInflater layoutInflater, List<Feature> features,
            OnFeatureToggledListener onFeatureToggledListener) {
        this.layoutInflater = layoutInflater;
        this.features = features;
        this.onFeatureToggledListener = onFeatureToggledListener;
    }

    @Override
    public int getCount() {
        return features.size();
    }

    @Override
    public Object getItem(final int position) {
        return features.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = createFeatureView(parent);
        } else {
            view = convertView;
        }
        updateFeatureView(view, features.get(position));
        return view;
    }

    private View createFeatureView(final ViewGroup parent) {
        return layoutInflater.inflate(R.layout.list_item_feature, parent, false);
    }

    private void updateFeatureView(final View view, final Feature feature) {
        TextView nameTextView = (TextView) view.findViewById(R.id.list_item_feature_name_view);
        TextView descriptionTextView = (TextView) view.findViewById(R.id.list_item_feature_description_view);
        Switch switchView = (Switch) view.findViewById(R.id.list_item_feature_switch);
        nameTextView.setText(feature.getName());
        descriptionTextView.setText(feature.getDescription());
        switchView.setChecked(feature.isEnabled());
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onFeatureToggledListener.onFeatureToggled(feature, isChecked);
            }
        });
    }

    public interface OnFeatureToggledListener {
        void onFeatureToggled(Feature feature, boolean enabled);
    }
}
