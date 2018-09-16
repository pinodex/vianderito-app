package com.raphaelmarco.vianderito.fragment;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pixplicity.easyprefs.library.Prefs;
import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.Util;
import com.raphaelmarco.vianderito.Vianderito;
import com.raphaelmarco.vianderito.activity.DocumentActivity;
import com.raphaelmarco.vianderito.binding.SignUpData;
import com.raphaelmarco.vianderito.binding.ValidationErrorData;
import com.raphaelmarco.vianderito.databinding.FragmentSignupBinding;
import com.raphaelmarco.vianderito.network.model.ValidationError;
import com.raphaelmarco.vianderito.network.model.auth.User;
import com.raphaelmarco.vianderito.network.model.auth.UserCredentials;
import com.raphaelmarco.vianderito.network.model.auth.UserLogin;
import com.raphaelmarco.vianderito.network.RetrofitClient;
import com.raphaelmarco.vianderito.network.service.AuthService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupFragment extends Fragment {

    private AuthFragmentInteractionListener mListener;

    private ValidationErrorData validationError;

    private SignUpData user;

    private UiData ui;

    private AuthService authService;

    public SignupFragment() {
        user = new SignUpData();
        validationError = new ValidationErrorData();
        ui = new UiData();

        authService = RetrofitClient.getInstance().create(AuthService.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentSignupBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_signup, container, false);

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

        Button btnSignUp = view.findViewById(R.id.btn_signup);
        btnSignUp.setOnClickListener(new SignUpButtonAction());

        TextView signUpNoticeText = view.findViewById(R.id.signup_agreement_message);

        SpannableString signUpNotice = SpannableString.valueOf(
                getResources().getString(R.string.signup_agreement_message));

        Util.applySpan(signUpNotice, "Terms of Service", new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Util.openDocument(getContext(), "tos");
            }
        });

        Util.applySpan(signUpNotice, "Privacy Policy", new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Util.openDocument(getContext(), "privacy_policy");
            }
        });

        signUpNoticeText.setText(signUpNotice);
        signUpNoticeText.setMovementMethod(LinkMovementMethod.getInstance());
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

    private class SignUpButtonAction implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            enableLoadingState();

            final User data = new User(user);

            authService.register(data).enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    disableLoadingState();

                    if (response.code() == 422) {
                        validationError.fill(new ValidationError.Parser(response).parse());

                        return;
                    }

                    final User user = response.body();

                    UserCredentials credentials = new UserCredentials(
                            data.getUsername(), data.getPassword());

                    enableLoadingState();

                    authService.login(credentials).enqueue(new Callback<UserLogin>() {
                        @Override
                        public void onResponse(@NonNull Call<UserLogin> call, @NonNull Response<UserLogin> response) {
                            disableLoadingState();

                            if (response.isSuccessful()) {
                                Prefs.putString(Vianderito.JWT_TOKEN_ID,
                                        response.body().getAccessToken());

                                mListener.onRequireSmsVerification(user);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<UserLogin> call, @NonNull Throwable t) {
                            t.printStackTrace();

                            disableLoadingState();
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                    t.printStackTrace();

                    disableLoadingState();
                }
            });
        }
    }
}
