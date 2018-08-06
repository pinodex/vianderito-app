package com.raphaelmarco.vianderito;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.raphaelmarco.vianderito.transformer.ZoomOutPageTransformer;

public class WelcomeActivity extends AppCompatActivity implements
        LoginFragment.OnFragmentInteractionListener,
        SignupFragment.OnFragmentInteractionListener {

    private Button btnGetStarted;

    private View viewGetStarted;

    private ViewPager viewPager;

    private Fragment loginFragment, registerFragment;

    private boolean isGetStartedScreenOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        loginFragment = new LoginFragment();
        registerFragment = new SignupFragment();

        btnGetStarted = findViewById(R.id.button_get_started);
        viewGetStarted = findViewById(R.id.view_get_started);
        viewPager = findViewById(R.id.action_viewpager);

        viewPager.setAdapter(new WelcomeFragmentPagerAdapter(getSupportFragmentManager()));
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleGetStartedScreen();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() > 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);

            return;
        }

        if (isGetStartedScreenOpened) {
            toggleGetStartedScreen();

            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onCreateAccountLinkClick() {
        viewPager.setCurrentItem(1);
    }

    private void toggleGetStartedScreen() {
        int cx, cy;
        float startRadius, finalRadius;

        cx = Math.round(btnGetStarted.getX() + btnGetStarted.getWidth() / 2);
        cy = Math.round(btnGetStarted.getY() + btnGetStarted.getHeight() / 2);

        startRadius = 0;
        finalRadius = (float) Math.hypot(cx, cy);

        if (isGetStartedScreenOpened) {
            startRadius = finalRadius;
            finalRadius = 0;
        }

        Animator anim = ViewAnimationUtils.createCircularReveal(viewGetStarted, cx, cy,
                startRadius, finalRadius);

        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setDuration(500);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if (!isGetStartedScreenOpened)
                    viewGetStarted.setVisibility(View.INVISIBLE);
            }
        });

        viewGetStarted.setVisibility(View.VISIBLE);

        isGetStartedScreenOpened = !isGetStartedScreenOpened;

        anim.start();
    }

    private class WelcomeFragmentPagerAdapter extends FragmentPagerAdapter {

        public WelcomeFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return loginFragment;

                case 1:
                    return registerFragment;
            }

            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
