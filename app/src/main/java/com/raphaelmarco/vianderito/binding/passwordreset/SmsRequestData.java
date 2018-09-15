package com.raphaelmarco.vianderito.binding.passwordreset;

import android.databinding.ObservableField;

import java.util.Observable;

public class SmsRequestData {

    public ObservableField<String> phoneNumber = new ObservableField<>();

    public ObservableField<String> code = new ObservableField<>();

}
