package com.raphaelmarco.vianderito.activity;

import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.databinding.ActivityHomeBinding;
import com.raphaelmarco.vianderito.fragment.CartFragment;
import com.raphaelmarco.vianderito.fragment.StoreFragment;

public class HomeActivity extends AppCompatActivity {

    private Fragment storeFragment, cartFragment, active;

    private FragmentManager fm;

    private UiData ui = new UiData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityHomeBinding binding = DataBindingUtil.setContentView(
                this, R.layout.activity_home);

        binding.setUi(ui);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new NavigationItemSelectedListener());

        int frameId = R.id.home_frame;
        fm = getSupportFragmentManager();

        storeFragment = new StoreFragment();
        cartFragment = new CartFragment();

        active = storeFragment;

        fm.beginTransaction().add(frameId, cartFragment, "2").hide(cartFragment).commit();
        fm.beginTransaction().add(frameId, storeFragment, "1").commit();

        setPageTitle(R.string.store);
    }

    private void setPageTitle(int resId) {
        ui.pageTitle.set(getResources().getString(resId));
    }

    private class NavigationItemSelectedListener implements
            BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.store:
                    fm.beginTransaction().hide(active).show(storeFragment).commit();

                    setPageTitle(R.string.store);

                    active = storeFragment;

                    break;

                case R.id.cart:
                    fm.beginTransaction().hide(active).show(cartFragment).commit();

                    setPageTitle(R.string.cart);

                    active = cartFragment;

                    break;
            }

            return true;
        }
    }

    public class UiData extends BaseObservable {

        public ObservableField<String> pageTitle = new ObservableField<>();

    }
}

