package com.raphaelmarco.vianderito.fragment;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pixplicity.easyprefs.library.Prefs;
import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.Vianderito;
import com.raphaelmarco.vianderito.activity.password.PasswordResetActivity;
import com.raphaelmarco.vianderito.binding.LoginData;
import com.raphaelmarco.vianderito.binding.ValidationErrorData;
import com.raphaelmarco.vianderito.databinding.FragmentLoginBinding;
import com.raphaelmarco.vianderito.network.RetrofitClient;
import com.raphaelmarco.vianderito.network.model.ValidationError;
import com.raphaelmarco.vianderito.network.model.auth.User;
import com.raphaelmarco.vianderito.network.model.auth.UserCredentials;
import com.raphaelmarco.vianderito.network.model.auth.UserLogin;
import com.raphaelmarco.vianderito.network.service.AuthService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private AuthFragmentInteractionListener mListener;

    private UiData ui;

    private LoginData user;

    private ValidationErrorData validationError;

    private AuthService authService;

    public LoginFragment() {
        ui = new UiData();
        user = new LoginData();
        validationError = new ValidationErrorData();

        authService = RetrofitClient.getInstance().create(AuthService.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentLoginBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_login, container, false);

        View view = binding.getRoot();

        binding.setUser(user);
        binding.setValidationError(validationError);
        binding.setUi(ui);

        disableLoadingState();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.create_account_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onCreateAccountLinkClick();
                }
            }
        });

        view.findViewById(R.id.forgot_password_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PasswordResetActivity.class);

                startActivity(intent);
            }
        });

        view.findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableLoadingState();

                final UserCredentials data = new UserCredentials(user);

                authService.login(data).enqueue(new Callback<UserLogin>() {
                    @Override
                    public void onResponse(@NonNull Call<UserLogin> call, @NonNull Response<UserLogin> response) {
                        disableLoadingState();

                        if (!response.isSuccessful()) {
                            validationError.fill(new ValidationError.Parser(response).parse());

                            return;
                        }

                        UserLogin userLogin = response.body();
                        User user = userLogin.getUser();

                        Prefs.putString(Vianderito.JWT_TOKEN_ID, userLogin.getAccessToken());

                        if (!user.isVerified()) {
                            mListener.onRequireSmsVerification(user);

                            return;
                        }

                        mListener.onLoginCompleted(user);
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserLogin> call, @NonNull Throwable t) {
                        t.printStackTrace();

                        disableLoadingState();
                    }
                });
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AuthFragmentInteractionListener) {
            mListener = (AuthFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AuthFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void enableLoadingState() {
        mListener.onProgressStart();
        ui.isFormEnabled.set(false);
    }

    private void disableLoadingState() {
        mListener.onProgressStop();
        ui.isFormEnabled.set(true);
    }

    public class UiData extends BaseObservable {

        public ObservableBoolean isFormEnabled = new ObservableBoolean();

    }
}
