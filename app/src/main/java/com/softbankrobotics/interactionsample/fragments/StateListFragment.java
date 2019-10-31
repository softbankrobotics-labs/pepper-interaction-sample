package com.softbankrobotics.interactionsample.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.softbankrobotics.interactionsample.MainActivity;
import com.softbankrobotics.interactionsample.R;
import com.softbankrobotics.interactionsample.statemachine.InteractionStateMachine;
import com.softbankrobotics.interactionsample.statemachine.common.StateStatus;

import java.util.List;
import java.util.Objects;

/*
 * A special fragment made to show a list of states, and highlight the active one.
 */
public class StateListFragment extends Fragment implements StateMachineWatcher {
    private String TAG = "StateListFragment";

    private LinearLayout stateList;

    private LinearLayout.LayoutParams stateLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1.0f
    );

    public static StateListFragment newInstance() {
        return new StateListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.state_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        stateList = view.findViewById(R.id.stateList);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.addStateMachineWatcher(this);
        }
    }

    @Override
    public void onStateMachineReady(InteractionStateMachine stateMachine) {
        stateMachine.state.observe(this, (state) -> Log.i(TAG, "Current state " + state));
        stateMachine.allStates.observe(this, this::showStateList);
    }

    /*
     * Repopulates it's stateList view with a text object for each state.
     */
    private void showStateList(List<StateStatus> states) {
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            stateList.removeAllViews();
            for (StateStatus stateStatus : states) {
                TextView stateText = new TextView(this.getContext());
                // Set layout
                stateText.setLayoutParams(stateLayoutParams);
                stateText.setTextSize(30.0f);
                stateText.setGravity(Gravity.CENTER);
                // Set label
                String label = stateStatus.name;
                if (!stateStatus.active) {
                    if (stateStatus.isNext) {
                        label = "➚" + label; // Prefix with upwards arrow
                    }
                    if (stateStatus.isPrevious) {
                        label = label + "⤵"; // Append downwards arrow
                    }
                }
                stateText.setText(label);
                // Set colors
                Context context = getContext();
                int activeColor = ContextCompat.getColor(context, R.color.colorActiveState);
                int previousColor = ContextCompat.getColor(context, R.color.colorPreviousState);
                if (stateStatus.active) {
                    stateText.setBackgroundColor(activeColor);
                    stateText.setTextColor(Color.WHITE);
                } else if (stateStatus.isPrevious) {
                    stateText.setBackgroundColor(previousColor);
                    stateText.setTextColor(Color.WHITE);
                } else if (stateStatus.isNext) {
                    stateText.setTextColor(activeColor);
                } else {
                    stateText.setTextColor(Color.DKGRAY);
                }
                stateList.addView(stateText);
            }
        });
    }
}
