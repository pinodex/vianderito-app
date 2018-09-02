package com.raphaelmarco.vianderito.activity;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.databinding.ActivityHomeBinding;
import com.raphaelmarco.vianderito.fragment.AccountFragment;
import com.raphaelmarco.vianderito.fragment.CartFragment;
import com.raphaelmarco.vianderito.fragment.StoreFragment;

public class HomeActivity extends AppCompatActivity {

    private Fragment storeFragment, cartFragment, accountFragment, active;

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
        accountFragment = new AccountFragment();

        // Show StoreFragment for initial page
        active = storeFragment;

        fm.beginTransaction().add(frameId, accountFragment, "3").hide(accountFragment).commit();
        fm.beginTransaction().add(frameId, cartFragment, "2").hide(cartFragment).commit();
        fm.beginTransaction().add(frameId, storeFragment, "1").commit();

        findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((StoreFragment) storeFragment).toggleSearchMode();
            }
        });


        ui.activePage.set(R.id.store);
    }

    private class NavigationItemSelectedListener implements
            BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            ((StoreFragment) storeFragment).disableSearchMode();

            switch (menuItem.getItemId()) {
                case R.id.store:
                    fm.beginTransaction().hide(active).show(storeFragment).commit();

                    active = storeFragment;

                    ui.activePage.set(R.id.store);

                    break;

                case R.id.cart:
                    fm.beginTransaction().hide(active).show(cartFragment).commit();

                    active = cartFragment;

                    ui.activePage.set(R.id.cart);

                    break;

                case R.id.account:
                    fm.beginTransaction().hide(active).show(accountFragment).commit();

                    active = accountFragment;

                    ui.activePage.set(R.id.account);

                    break;
            }

            return true;
        }
    }

    public class UiData extends BaseObservable {

        public ObservableField<Integer> activePage = new ObservableField<>();

        @Bindable({"activePage"})
        public String getPageTitle() {
            int stringId = 0;

            switch (activePage.get()) {
                case R.id.store:
                    stringId = R.string.store;
                    break;

                case R.id.cart:
                    stringId = R.string.cart;
                    break;

                case R.id.account:
                    stringId = R.string.account;
                    break;
            }

            return getResources().getString(stringId);
        }

        @Bindable({"activePage"})
        public Boolean getIsHome() {
            return R.id.store == activePage.get();
        }

        @Bindable({"activePage"})
        public Boolean getIsAccount() {
            return R.id.account == activePage.get();
        }
    }
}

