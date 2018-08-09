package com.raphaelmarco.vianderito.binding;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.text.TextUtils;

import com.raphaelmarco.vianderito.network.model.ValidationError;

import java.util.HashMap;
import java.util.Map;

public class ValidationErrorData extends BaseObservable
        implements ModelFillable<ValidationError> {

    public ObservableField<String> message = new ObservableField<>();

    public ObservableField<HashMap<String, String>> errors = new ObservableField<>();

    public void fill(ValidationError model) {
        message.set(model.getMessage());

        if (model.getErrors() != null) {
            HashMap<String, String> errorMap = new HashMap<>();

            for (Map.Entry<String, String[]> entry : model.getErrors().entrySet()) {
                String errorMessages = TextUtils.join("\n", entry.getValue());

                errorMap.put(entry.getKey(), errorMessages);
            }

            errors.set(errorMap);
        }
    }

}
