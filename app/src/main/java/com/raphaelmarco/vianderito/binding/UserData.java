package com.raphaelmarco.vianderito.binding;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;

import com.raphaelmarco.vianderito.network.model.auth.User;

public class UserData extends BaseObservable implements ModelFillable<User> {

    public ObservableField<String> id = new ObservableField<>();

    public ObservableField<String> name = new ObservableField<>();

    public ObservableField<String> username = new ObservableField<>();

    public ObservableField<String> emailAddress = new ObservableField<>();

    public ObservableField<String> phoneNumber = new ObservableField<>();

    public void fill(User model) {
        id.set(model.getId());

        name.set(model.getName());

        username.set(model.getUsername());

        emailAddress.set(model.getEmailAddress());

        phoneNumber.set(model.getPhoneNumber());
    }

}
