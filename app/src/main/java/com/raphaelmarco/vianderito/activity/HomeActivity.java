package com.raphaelmarco.vianderito.activity;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

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

        fm = getSupportFragmentManager();

        initPages();
        switchPage(R.id.store);

        ((BottomNavigationView) findViewById(R.id.bottom_navigation))
                .setOnNavigationItemSelectedListener(new NavigationItemSelectedListener());

        findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((StoreFragment) storeFragment).toggleSearchMode();
            }
        });
    }

    private void initPages() {
        int frameId = R.id.home_frame;

        storeFragment = new StoreFragment();
        cartFragment = new CartFragment();
        accountFragment = new AccountFragment();

        fm.beginTransaction().add(frameId, accountFragment, "3").hide(accountFragment).commit();
        fm.beginTransaction().add(frameId, cartFragment, "2").hide(cartFragment).commit();
        fm.beginTransaction().add(frameId, storeFragment, "1").hide(storeFragment).commit();
    }

    private void switchPage(int pageId) {
        ui.activePage.set(pageId);

        FragmentTransaction ft = fm.beginTransaction()
                .setCustomAnimations(R.anim.page_fade_in, R.anim.page_fade_out);

        if (active != null)
            ft.hide(active);

        switch (pageId) {
            case R.id.store: active = storeFragment; break;
            case R.id.cart: active = cartFragment; break;
            case R.id.account: active = accountFragment; break;
        }

        ft.show(active).commit();
    }

    private class NavigationItemSelectedListener implements
            BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            ((StoreFragment) storeFragment).disableSearchMode();

            switchPage(menuItem.getItemId());

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

