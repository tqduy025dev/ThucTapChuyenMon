package com.tranquangduy.fragments;

import com.tranquangduy.notifications.MyResponse;
import com.tranquangduy.notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA3I4loaY:APA91bGgiMwJmcef_opaAz8rfsF4Ykm1m999FUp0HgrwYVJvFx8EFf73L9cl2pCy_QTW0KgWQM0igD-RCbKP2j7UJoNsj2bxHkiWYbZ7NJBnbSkRG1iqPuTd7qnJB3Mf6RB3JA7E5z5f"

            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body );
}
