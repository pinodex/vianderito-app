package com.raphaelmarco.vianderito.binding;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayMap;
import android.databinding.ObservableField;
import android.text.TextUtils;

import com.raphaelmarco.vianderito.network.model.ValidationError;

import java.util.HashMap;
import java.util.Map;

public class ValidationErrorData extends BaseObservable
        implements ModelFillable<ValidationError> {

    public ObservableField<String> message = new ObservableField<>();

    public ObservableArrayMap<String, String> errors = new ObservableArrayMap<>();

    @Bindable({"message"})
    public boolean isMessageAvailable() {
        return message.get() != null && !message.get().isEmpty();
    }

    public void clear() {
        message.set(null);

        errors.clear();
    }

    public void fill(ValidationError model) {
        message.set(model.getMessage());

        if (model.getErrors() != null) {
            errors.clear();

            for (Map.Entry<String, String[]> entry : model.getErrors().entrySet()) {
                String errorMessages = TextUtils.join("\n", entry.getValue());

                errors.put(entry.getKey(), errorMessages);
            }
        }
    }

}
