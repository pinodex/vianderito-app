package com.raphaelmarco.vianderito.binding;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.text.TextUtils;

import com.raphaelmarco.vianderito.model.ValidationError;

import java.util.HashMap;
import java.util.Map;

public class ValidationErrorData extends BaseObservable {

    public ObservableField<String> message = new ObservableField<>();

    public ObservableField<HashMap<String, String>> errors = new ObservableField<>();

    public static ValidationErrorData fromModel(ValidationError model) {
        ValidationErrorData validationErrorData = new ValidationErrorData();
        HashMap<String, String> errorMap = new HashMap<>();

        validationErrorData.message.set(model.getMessage());

        for (Map.Entry<String, String[]> entry: model.getErrors().entrySet()) {
            String errorMessages = TextUtils.join("\n", entry.getValue());

            errorMap.put(entry.getKey(), errorMessages);
        }

        validationErrorData.errors.set(errorMap);

        return validationErrorData;
    }

    public void fill(ValidationError model) {
        message.set(model.getMessage());

        HashMap<String, String> errorMap = new HashMap<>();

        for (Map.Entry<String, String[]> entry: model.getErrors().entrySet()) {
            String errorMessages = TextUtils.join("\n", entry.getValue());

            errorMap.put(entry.getKey(), errorMessages);
        }

        errors.set(errorMap);
    }

}
