package com.raphaelmarco.vianderito.binding;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;

public class SignUpData extends BaseObservable {

    public ObservableField<String> name = new ObservableField<>();

    public ObservableField<String> username = new ObservableField<>();

    public ObservableField<String> password = new ObservableField<>();

    public ObservableField<String> phoneNumber = new ObservableField<>();

}
