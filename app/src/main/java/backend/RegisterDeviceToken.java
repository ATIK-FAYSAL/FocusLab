package backend;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by USER on 2/14/2018.
 */

public class RegisterDeviceToken
{
        /**when user successfully log in ,it will take device token for send notification
          *only first time take this token.
        */

        public static void registerToken(final String token,String phone)
        {
                String url = "http://app.focusdigitalcolorlab.com/registerToken.php";

                OkHttpClient client = new OkHttpClient();
                RequestBody body = new FormBody.Builder()
                        .add("token",token)
                        .add("userName",phone)
                        .build();

                final Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {}

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                                if(response.isSuccessful())
                                {
                                        String str = response.body().string();
                                        Log.d(TAG,"token is : "+str);
                                }
                        }
                });
        }
}
