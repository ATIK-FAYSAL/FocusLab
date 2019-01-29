package services;

import backend.RegisterDeviceToken;
import com.example.atik_faysal.focuslab.AllSharedPreference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by USER on 2/14/2018.
 */

public class FirebaseInstanceIdServices extends FirebaseInstanceIdService
{
        private AllSharedPreference sharedPreferenceData;


        private static final String PREF_NAME = "session";

        @Override
        public void onTokenRefresh() {
                String token = FirebaseInstanceId.getInstance().getToken();

                sharedPreferenceData = new AllSharedPreference(this);


                String userName = sharedPreferenceData.getPreferencePhone(PREF_NAME);
                RegisterDeviceToken.registerToken(token,userName);
        }
}
