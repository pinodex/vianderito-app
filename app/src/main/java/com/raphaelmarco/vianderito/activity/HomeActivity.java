package com.raphaelmarco.vianderito.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.fragment.CartFragment;
import com.raphaelmarco.vianderito.fragment.StoreFragment;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private Fragment storeFragment, cartFragment, active;

    private FragmentManager fm;

    private int frameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new NavigationItemSelectedListener());

        fm = getSupportFragmentManager();
        frameId = R.id.home_frame;

        storeFragment = new StoreFragment();
        cartFragment = new CartFragment();

        active = storeFragment;

        fm.beginTransaction().add(frameId, cartFragment, "2").hide(cartFragment).commit();
        fm.beginTransaction().add(frameId, storeFragment, "1").commit();

    }

    private class NavigationItemSelectedListener implements
            BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.store:
                    fm.beginTransaction().hide(active).show(storeFragment).commit();
                    active = storeFragment;

                    break;

                case R.id.cart:
                    fm.beginTransaction().hide(active).show(cartFragment).commit();
                    active = cartFragment;

                    break;
            }

            return true;
        }
    }
}

