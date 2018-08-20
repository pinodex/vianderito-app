package com.raphaelmarco.vianderito.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
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
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.raphaelmarco.vianderito.fragment.AuthFragmentInteractionListener;
import com.raphaelmarco.vianderito.fragment.LoginFragment;
import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.fragment.SignupFragment;
import com.raphaelmarco.vianderito.network.RetrofitClient;
import com.raphaelmarco.vianderito.network.model.auth.User;
import com.raphaelmarco.vianderito.network.service.AuthService;
import com.raphaelmarco.vianderito.transformers.ZoomOutPageTransformer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WelcomeActivity extends AppCompatActivity implements AuthFragmentInteractionListener {

    private Button btnGetStarted;

    private View viewGetStarted;

    private ViewPager viewPager;

    private Fragment loginFragment, registerFragment;

    private ProgressBar progressBar;

    private AuthService authService;

    private boolean isGetStartedScreenOpened = false;

    public WelcomeActivity() {
        authService = RetrofitClient.getInstance().create(AuthService.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        loginFragment = new LoginFragment();
        registerFragment = new SignupFragment();

        btnGetStarted = findViewById(R.id.button_get_started);
        viewGetStarted = findViewById(R.id.view_get_started);
        viewPager = findViewById(R.id.action_viewpager);
        progressBar = findViewById(R.id.progress_bar);

        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setAdapter(new WelcomeFragmentPagerAdapter(getSupportFragmentManager()));

        btnGetStarted.setVisibility(View.INVISIBLE);

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleGetStartedScreen();
            }
        });

        //startActivity(new Intent(this, HomeActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkSession();
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
    public void onLoginCompleted(User user) {
        Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);

        startActivity(intent);

        finish();
    }

    @Override
    public void onCreateAccountLinkClick() {
        viewPager.setCurrentItem(1);
    }

    @Override
    public void onProgressStart() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProgressStop() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onRequireSmsVerification(User user) {
        String serializedUser = new Gson().toJson(user);
        Intent intent = new Intent(this, SmsVerifyActivity.class);

        intent.putExtra("user", serializedUser);

        startActivity(intent);
    }

    private void checkSession() {
        authService.me().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    onLoginCompleted(response.body());

                    return;
                }

                btnGetStarted.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();

                btnGetStarted.setVisibility(View.VISIBLE);
            }
        });
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

        WelcomeFragmentPagerAdapter(FragmentManager fm) {
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
