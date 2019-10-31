package com.softbankrobotics.interactionsample.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.softbankrobotics.interactionsample.R;

/*
 * A simple fragment that just shows an image, specified by the Resource ID and a color filter.
 */
public class SplashImageFragment extends Fragment {
    private int resourceId;
    private int color;

    public SplashImageFragment(int resourceId, int color) {
        this.resourceId = resourceId;
        this.color = color;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.splash_image_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ImageView splashImage = view.findViewById(R.id.splashImage);
        splashImage.setImageResource(resourceId);
        splashImage.setColorFilter(color);
        splashImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
