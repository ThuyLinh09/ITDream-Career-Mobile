package graduate.itdreams.android.data.remote;

import graduate.itdreams.android.data.model.api.ResponseWrapper;
import graduate.itdreams.android.data.model.api.request.achievement.UploadCertificateRequest;
import graduate.itdreams.android.data.model.api.request.task.TaskQuestionProgressRequest;
import graduate.itdreams.android.data.model.api.response.file.UploadResponse;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UploadApiService {

    @GET("v1/file/download{file}")
    Observable<ResponseBody> loadFile(@Path(value = "file", encoded = true) String file);
    @Multipart
    @POST("v1/file/upload")
    Observable<ResponseWrapper<UploadResponse>> uploadFile(
            @Part("type") RequestBody type,
            @Part MultipartBody.Part file
    );
    @POST("v1/file/upload-certificate")
    Observable<ResponseWrapper<UploadResponse>> uploadCertificate(
            @Body UploadCertificateRequest request
    );
}
