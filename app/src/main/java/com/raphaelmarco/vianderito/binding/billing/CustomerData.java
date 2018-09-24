package com.raphaelmarco.vianderito.binding.billing;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;

public class CustomerData extends BaseObservable {

    public ObservableField<String> firstName = new ObservableField<>();

    public ObservableField<String> lastName = new ObservableField<>();

    public ObservableField<String> address = new ObservableField<>();

    public ObservableField<String> city = new ObservableField<>();

    public ObservableField<String> state = new ObservableField<>();

    public ObservableField<String> country = new ObservableField<>("Philippines");

    public ObservableField<String> postalCode = new ObservableField<>();

}
