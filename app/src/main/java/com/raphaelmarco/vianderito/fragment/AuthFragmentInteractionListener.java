package com.raphaelmarco.vianderito.fragment;

import com.raphaelmarco.vianderito.network.model.auth.User;

public interface AuthFragmentInteractionListener {
    void onProgressStart();

    void onProgressStop();

    void onCreateAccountLinkClick();

    void onRequireSmsVerification(User user);

    void onLoginCompleted(User user);
}
