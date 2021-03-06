package in.co.ashclan.mirchithunder.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;

import in.co.ashclan.mirchithunder.ParticipantsLogin;
import in.co.ashclan.mirchithunder.model.ImagesModel;
import in.co.ashclan.mirchithunder.model.ParticipantModel;
import in.co.ashclan.mirchithunder.model.VolunteerModel;

public class util {
    public static ParticipantModel currentParticipant;
    public static VolunteerModel currentvolunteer;
    public static ImagesModel CurrentimagesModel;
    public static final int PICK_IMAGE_REQUEST = 71;
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";

    public static boolean isConnectedToInterNet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null)
        {
            NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
            if(infos != null)
            {
                for(int i = 0;i<infos.length;i++)
                {
                    if(infos[i].getState()==NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
}