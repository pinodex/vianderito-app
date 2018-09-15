package com.raphaelmarco.vianderito.network.service.password;

import com.raphaelmarco.vianderito.network.model.password.PasswordChange;
import com.raphaelmarco.vianderito.network.model.password.ResetResponse;
import com.raphaelmarco.vianderito.network.model.password.reset_request.EmailRequest;
import com.raphaelmarco.vianderito.network.model.password.reset_request.SmsRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ResetService {

    @POST("password/request_reset/sms/start")
    Call<ResponseBody> startSms(@Body SmsRequest smsRequest);

    @POST("password/request_reset/sms/token")
    Call<ResetResponse> getTokenFromSmsRequest(@Body SmsRequest smsRequest);

    @POST("password/request_reset/email/start")
    Call<ResetResponse> getTokenFromEmailRequest(@Body EmailRequest emailRequest);

    @POST("password/reset/{userId}")
    Call<ResponseBody> changePassword(
            @Path(value = "userId", encoded = true) String id,
            @Body PasswordChange passwordChange
    );

}
