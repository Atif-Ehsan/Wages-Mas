package com.example.wages.Notification;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAawJrAIQ:APA91bH46vbOveQyuyK55LF7RXKlUnT4SXxb5ARQO2g9oa9aKMoDKXatI21T6k1KuMDQJiZAGLjTzoEZi_zl_PZw8XSb0IxoGZgfVZdCNIag_gWNb_Ks9Pdj-fpkQj4hm6L4BC1tph2M"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
