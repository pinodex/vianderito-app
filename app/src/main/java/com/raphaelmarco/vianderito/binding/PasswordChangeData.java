package com.raphaelmarco.vianderito.binding;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableField;

public class PasswordChangeData extends BaseObservable {

    public ObservableField<String> currentPassword = new ObservableField<>("");

    public ObservableField<String> newPassword = new ObservableField<>("");

    public ObservableField<String> confirmPassword = new ObservableField<>("");

    @Bindable({"newPassword", "confirmPassword"})
    public boolean isConfirmPasswordCorrect() {
        return newPassword.get().equals(confirmPassword.get());
    }

    @Bindable({"newPassword", "confirmPassword"})
    public boolean isSubmittable() {
        return newPassword.get() != null && !newPassword.get().isEmpty() &&
                confirmPassword.get() != null && !confirmPassword.get().isEmpty() &&
                        isConfirmPasswordCorrect();

    }

}
