package com.raphaelmarco.vianderito.binding;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;

public class LoginData extends BaseObservable {

    public ObservableField<String> id = new ObservableField<>();

    public ObservableField<String> password = new ObservableField<>();

}
