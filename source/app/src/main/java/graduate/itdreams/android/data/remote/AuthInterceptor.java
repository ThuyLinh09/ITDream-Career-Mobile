package graduate.itdreams.android.data.remote;

import android.app.Application;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import graduate.itdreams.android.constant.Constants;
import graduate.itdreams.android.data.local.prefs.PreferencesService;
import graduate.itdreams.android.utils.LogService;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final PreferencesService appPreferences;
    private final Application application;

    public AuthInterceptor(PreferencesService appPreferences, Application application) {
        this.appPreferences = appPreferences;
        this.application = application;
    }
    @Override
    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder newRequest = originalRequest.newBuilder();

        // Bỏ qua xác thực nếu có header "IgnoreAuth: 1"
        if ("1".equals(originalRequest.header("IgnoreAuth"))) {
            newRequest.removeHeader("IgnoreAuth");
            newRequest.addHeader("X-tenant", "quanbui");
            return chain.proceed(newRequest.build());
        }

        // Dùng Basic Auth nếu có header "UseBasicAuth: 1"
        if ("1".equals(originalRequest.header("UseBasicAuth"))) {
            newRequest.removeHeader("UseBasicAuth");

            // Lấy từ cấu hình, hoặc hardcode nếu bạn chỉ cần cho 1 API
            String username = "abc_client";
            String password = "abc123";
            String credentials = username + ":" + password;

            String basicAuth = "Basic " + android.util.Base64.encodeToString(credentials.getBytes(), android.util.Base64.NO_WRAP);
            newRequest.addHeader("Authorization", basicAuth);
        } else {
            // Mặc định dùng Bearer Token
            String token = appPreferences.getToken();
            if (token != null && !token.isEmpty()) {
                newRequest.addHeader("Authorization", "Bearer " + token);
            }
        }

        // Thêm X-tenant (áp dụng cho tất cả)
        newRequest.addHeader("X-tenant", "quanbui");

        Response response = chain.proceed(newRequest.build());

        // Nếu token hết hạn
        if (response.code() == 401 || response.code() == 403) {
            LogService.i("Auth error code: " + response.code());
            appPreferences.removeKey(PreferencesService.KEY_BEARER_TOKEN);

            Intent intent = new Intent(Constants.ACTION_EXPIRED_TOKEN);
            LocalBroadcastManager.getInstance(application.getApplicationContext()).sendBroadcast(intent);
        }

        return response;
    }


//    @NotNull
//    @Override
//    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
//        String isIgnore = chain.request().header("IgnoreAuth");
//        if (isIgnore != null && isIgnore.equals("1")) {
//            Request.Builder newRequest = chain.request().newBuilder();
//            newRequest.removeHeader("IgnoreAuth");
//            newRequest.addHeader("X-tenant", "quanbui");
//            return chain.proceed(newRequest.build());
//        }
//
//        //Add Authentication
//        Request.Builder newRequest = chain.request().newBuilder();
//        String token = appPreferences.getToken();
//        if (token != null && !token.equals("")) {
//            newRequest.addHeader("Authorization", "Bearer " + token);
//        }
//        newRequest.addHeader("X-tenant", "quanbui");
//
//        Response origResponse = chain.proceed(newRequest.build());
//        if (origResponse.code() == 403 || origResponse.code() == 401) {
//            LogService.i("Error http =====================> code: "+origResponse.code());
//            appPreferences.removeKey(PreferencesService.KEY_BEARER_TOKEN);
//            Intent intent = new Intent();
//            intent.setAction(Constants.ACTION_EXPIRED_TOKEN);
//            LocalBroadcastManager.getInstance(application.getApplicationContext()).sendBroadcast(intent);
//        }
//        return origResponse;
//    }
}
