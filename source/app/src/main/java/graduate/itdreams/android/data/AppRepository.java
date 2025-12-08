package graduate.itdreams.android.data;

import graduate.itdreams.android.data.local.prefs.PreferencesService;
import graduate.itdreams.android.data.local.room.RoomService;
import graduate.itdreams.android.data.remote.ApiService;
import graduate.itdreams.android.data.remote.UploadApiService;

import javax.inject.Inject;

public class AppRepository implements Repository {

    private final ApiService mApiService;
    private final UploadApiService mUploadApiService;
    private final PreferencesService mPreferencesHelper;
    private final RoomService roomService;

    @Inject
    public AppRepository(PreferencesService preferencesHelper, UploadApiService uploadApiService, ApiService apiService, RoomService roomService) {
        this.mPreferencesHelper = preferencesHelper;
        this.mUploadApiService = uploadApiService;
        this.mApiService = apiService;
        this.roomService = roomService;
    }

    /**
     * ################################## Preference section ##################################
     */
    @Override
    public String getToken() {
        return mPreferencesHelper.getToken();
    }

    @Override
    public void setToken(String token) {
        mPreferencesHelper.setToken(token);
    }

    @Override
    public PreferencesService getSharedPreferences(){
        return mPreferencesHelper;
    }



    /**
    *  ################################## Remote api ##################################
    */
    @Override
    public ApiService getApiService(){
        return mApiService;
    }

    @Override
    public UploadApiService getUploadApiService() {
        return mUploadApiService;
    }

    @Override
    public RoomService getRoomService() {
        return roomService;
    }
}
