package graduate.itdreams.android.data;

import graduate.itdreams.android.data.local.prefs.PreferencesService;
import graduate.itdreams.android.data.local.room.RoomService;
import graduate.itdreams.android.data.remote.ApiService;
import graduate.itdreams.android.data.remote.UploadApiService;


public interface Repository {

    /**
     * ################################## Preference section ##################################
     */
    String getToken();
    void setToken(String token);

    PreferencesService getSharedPreferences();


    /**
     *  ################################## Remote api ##################################
     */
    ApiService getApiService();
    UploadApiService getUploadApiService();

    RoomService getRoomService();

}
