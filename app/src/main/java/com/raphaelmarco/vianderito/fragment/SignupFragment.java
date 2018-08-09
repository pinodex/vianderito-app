package com.raphaelmarco.vianderito.fragment;

import android.content.Context;
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
import android.widget.Button;

import com.pixplicity.easyprefs.library.Prefs;
import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.Vianderito;
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

    private OnFragmentInteractionListener mListener;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableLoadingState();

                final User data = new User(user);

                authService.register(data).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
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
                            public void onResponse(Call<UserLogin> call, Response<UserLogin> response) {
                                disableLoadingState();

                                if (response.isSuccessful()) {
                                    Prefs.putString(Vianderito.JWT_TOKEN_ID,
                                            response.body().getAccessToken());

                                    mListener.onRequireSmsVerification(user);
                                }
                            }

                            @Override
                            public void onFailure(Call<UserLogin> call, Throwable t) {
                                t.printStackTrace();

                                disableLoadingState();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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

    public interface OnFragmentInteractionListener {
        void onProgressStart();

        void onProgressStop();

        void onRequireSmsVerification(User user);
    }

    public class UiData extends BaseObservable {

        public ObservableField<Boolean> isFormEnabled = new ObservableField<>();

    }
}