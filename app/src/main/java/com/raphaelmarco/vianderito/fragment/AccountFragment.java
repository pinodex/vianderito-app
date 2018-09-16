package com.raphaelmarco.vianderito.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.Util;
import com.raphaelmarco.vianderito.Vianderito;
import com.raphaelmarco.vianderito.activity.HomeActivity;
import com.raphaelmarco.vianderito.activity.password.ChangePasswordActivity;
import com.raphaelmarco.vianderito.databinding.FragmentAccountBinding;
import com.raphaelmarco.vianderito.network.EmptyCallback;
import com.raphaelmarco.vianderito.network.RetrofitClient;
import com.raphaelmarco.vianderito.network.model.GenericMessage;
import com.raphaelmarco.vianderito.network.model.auth.User;
import com.raphaelmarco.vianderito.network.service.AuthService;
import com.tapadoo.alerter.Alerter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {

    private static final int PASSWORD_CHANGE_REQUEST = 1001;

    private UiData ui = new UiData();

    private AuthService authService;

    private int[] menuItems = new int[] {
            R.id.item_my_account,
            R.id.item_change_password,
            R.id.item_payment_methods,
            R.id.item_order_history,
            R.id.item_terms_of_service,
            R.id.item_privacy_policy,
            R.id.item_logout
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

        updateUser();
    }

    public void updateUser() {
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

    private void logoutPrompt() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_confirm_message)
                .setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        logout();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create()
                .show();
    }

    private void logout() {
        authService.logout().enqueue(new EmptyCallback<GenericMessage>());

        Vianderito.removeToken();

        if (getActivity() != null) {
            ((HomeActivity) getActivity()).onLogout();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PASSWORD_CHANGE_REQUEST && resultCode == Activity.RESULT_OK) {
            Alerter.create(getActivity())
                    .setBackgroundColorRes(R.color.green)
                    .setText(R.string.password_updated)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public class UiData extends BaseObservable {

        public ObservableField<User> user = new ObservableField<>();

    }

    private final View.OnClickListener MENU_ONCLICK_LISTENER = new View.OnClickListener () {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_change_password:
                    Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);

                    startActivityForResult(intent, PASSWORD_CHANGE_REQUEST);

                    getActivity().overridePendingTransition(
                            R.anim.slide_from_right, R.anim.zoom_out);

                    break;

                case R.id.item_terms_of_service:
                    Util.openDocument(getContext(), "tos");

                    break;

                case R.id.item_privacy_policy:
                    Util.openDocument(getContext(), "privacy_policy");

                    break;

                case R.id.item_logout:
                    logoutPrompt();

                    break;
            }
        }
    };
}
