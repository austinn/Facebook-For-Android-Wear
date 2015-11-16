package com.refect.facebookforwear.fragments;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.DialogFragment;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.DelayedConfirmationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.refect.facebookforwear.R;

/**
 * Created by anelson on 5/18/15.
 */
public class ConfirmStatusDialog extends DialogFragment implements
        DelayedConfirmationView.DelayedConfirmationListener {

    private static final int SPEECH_REQUEST_CODE = 0;

    private ScrollView scrollView;
    private DelayedConfirmationView mDelayedView;
    private String status;
    private TextView tvStatus;
    private ValueAnimator realSmoothScrollAnimation;

    public ConfirmStatusDialog() {
        // Empty constructor required for DialogFragment
    }

    public ConfirmStatusDialog(String status) {
        this.status = status;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_NoActionBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirm_status, container);

        tvStatus = (TextView) view.findViewById(R.id.tv_status);
        tvStatus.setText(getActivity().getResources().getString(R.string.sample_status));

        mDelayedView =
                (DelayedConfirmationView) view.findViewById(R.id.delayed_confirm);
        mDelayedView.setListener(this);

        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        ViewTreeObserver vto = scrollView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                scrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int height = scrollView.getMeasuredHeight();
                startRealSmoothAnimation(height);
            }
        });

        return view;
    }

    /**
     *
     * @param height
     */
    public void startRealSmoothAnimation(int height) {
        realSmoothScrollAnimation =
                ValueAnimator.ofInt(scrollView.getScrollY(), height);
        realSmoothScrollAnimation.setStartDelay(700);
        realSmoothScrollAnimation.setDuration(2000);
        realSmoothScrollAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        realSmoothScrollAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int scrollTo = (Integer) animation.getAnimatedValue();
                scrollView.scrollTo(0, scrollTo);
            }
        });

        realSmoothScrollAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mDelayedView.setTotalTimeMs(2000);
                mDelayedView.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        realSmoothScrollAnimation.start();
    }

    @Override
    public void onTimerFinished(View view) {
        // User didn't cancel, perform the action
        Intent intent = new Intent(getActivity(), ConfirmationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.SUCCESS_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                "Status Sent");
        startActivity(intent);

        dismiss();
        getActivity().finish();
    }

    @Override
    public void onTimerSelected(View view) {
        mDelayedView.reset();
        realSmoothScrollAnimation.cancel();
        dismiss();
    }
}
