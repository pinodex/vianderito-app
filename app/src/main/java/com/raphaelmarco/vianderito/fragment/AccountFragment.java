package com.raphaelmarco.vianderito.fragment;

import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.Util;
import com.raphaelmarco.vianderito.activity.DocumentActivity;
import com.raphaelmarco.vianderito.databinding.FragmentAccountBinding;
import com.raphaelmarco.vianderito.network.RetrofitClient;
import com.raphaelmarco.vianderito.network.model.auth.User;
import com.raphaelmarco.vianderito.network.service.AuthService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {

    private UiData ui = new UiData();

    private AuthService authService;

    private int[] menuItems = new int[] {
            R.id.item_my_account,
            R.id.item_payment_methods,
            R.id.item_order_history,
            R.id.item_terms_of_service,
            R.id.item_privacy_policy
    };

    public AccountFragment() {
        authService = RetrofitClient.getInstance().create(AuthService.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentAccountBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account,
                container, false);

        View view = binding.getRoot();

        binding.setUi(ui);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        for (int item: menuItems) {
            view.findViewById(item).setOnClickListener(MENU_ONCLICK_LISTENER);
        }

        authService.me().enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                ui.user.set(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public class UiData extends BaseObservable {

        public ObservableField<User> user = new ObservableField<>();

    }

    private final View.OnClickListener MENU_ONCLICK_LISTENER = new View.OnClickListener () {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_terms_of_service:
                    Util.openDocument(getContext(), "tos");

                    break;

                case R.id.item_privacy_policy:
                    Util.openDocument(getContext(), "privacy_policy");

                    break;
            }
        }
    };
}
