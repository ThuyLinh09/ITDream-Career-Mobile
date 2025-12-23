package graduate.itdreams.android.data.remote;

import java.util.List;

import graduate.itdreams.android.data.model.api.ResponseListObj;
import graduate.itdreams.android.data.model.api.request.achievement.UpdateCertificateRequest;
import graduate.itdreams.android.data.model.api.request.login.ChangePasswordRequest;
import graduate.itdreams.android.data.model.api.request.login.GoogleLoginRequest;
import graduate.itdreams.android.data.model.api.request.login.RequestForgetPasswordRequest;
import graduate.itdreams.android.data.model.api.request.review.ReviewSimulationRequest;
import graduate.itdreams.android.data.model.api.request.student.ResetOtpRequest;
import graduate.itdreams.android.data.model.api.request.student.StudentUpdateProfileRequest;
import graduate.itdreams.android.data.model.api.request.student.VerifyOtpRequest;
import graduate.itdreams.android.data.model.api.request.task.CompleteTaskRequest;
import graduate.itdreams.android.data.model.api.request.task.RestartTaskRequest;
import graduate.itdreams.android.data.model.api.request.task.TaskQuestionProgressRequest;
import graduate.itdreams.android.data.model.api.response.account.ForgetPasswordResponse;
import graduate.itdreams.android.data.model.api.response.feedback.FeedbackResponse;
import graduate.itdreams.android.data.model.api.response.notification.NotificationResponse;
import graduate.itdreams.android.data.model.api.response.question.TaskQuestionProgressResponse;
import graduate.itdreams.android.data.model.api.response.question.TaskQuestionResponse;
import graduate.itdreams.android.data.model.api.response.simulation.AchievementResponse;
import graduate.itdreams.android.data.model.api.response.simulation.RateResponse;
import graduate.itdreams.android.data.model.api.response.simulation.SimulationDetailResponse;
import graduate.itdreams.android.data.model.api.response.simulation.SimulationResponse;
import graduate.itdreams.android.data.model.api.response.student.SignUpResponse;
import graduate.itdreams.android.data.model.api.response.file.UploadResponse;
import graduate.itdreams.android.data.model.api.response.task.ListAnswerResponse;
import graduate.itdreams.android.data.model.api.response.task.SubTaskProgressResponse;
import graduate.itdreams.android.data.model.api.response.task.SubTaskResponse;
import graduate.itdreams.android.data.model.api.response.task.TaskResponse;
import io.reactivex.rxjava3.core.Observable;
import graduate.itdreams.android.data.model.api.ResponseWrapper;
import graduate.itdreams.android.data.model.api.request.student.StudentSignUpRequest;
import graduate.itdreams.android.data.model.api.request.login.CandidateLoginRequest;
import graduate.itdreams.android.data.model.api.response.account.AccountResponse;
import graduate.itdreams.android.data.model.api.response.account.ProfileAccountResponse;
import graduate.itdreams.android.data.model.api.response.login.AccessTokenResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("/api/token")
    @Headers({"UseBasicAuth: 1"})
    Observable<AccessTokenResponse> candidateLogin(@Body CandidateLoginRequest request);

    @POST("/v1/google/student-login")
    @Headers({"IgnoreAuth: 1"})
    Observable<ResponseWrapper<String>> googleLogin(@Body GoogleLoginRequest request);

//  STUDENT
    @POST("/v1/student/signup")
    @Headers({"UseBasicAuth: 1"})
    Observable<ResponseWrapper<SignUpResponse>> signUpCandidate(@Body StudentSignUpRequest request);

    @GET("/v1/student/profile")
    Observable<ResponseWrapper<AccountResponse>> getProfile();

    @PUT("/v1/student/client_update")
    Observable<ResponseWrapper> update(@Body StudentUpdateProfileRequest request);
    @POST("/v1/account/request_forget_password")
    @Headers({"UseBasicAuth: 1"})
    Observable<ResponseWrapper<ForgetPasswordResponse>> requestForgetPassword(@Body RequestForgetPasswordRequest request);
    @POST("/v1/account/forget_password")
    @Headers({"UseBasicAuth: 1"})
    Observable<ResponseWrapper> changePassword(@Body ChangePasswordRequest request);

//  IMAGE
    @GET("v1/file/download{file}")
    Observable<ResponseBody> loadFile(@Path(value = "file", encoded = true) String file);
    @Multipart
    @POST("v1/file/upload")
    Observable<ResponseWrapper<UploadResponse>> uploadImage(
            @Part("type") RequestBody type,
            @Part MultipartBody.Part file
    );

//  VERIFY OTP
    @POST("/v1/student/verify")
    @Headers({"UseBasicAuth: 1"})
    Observable<ResponseWrapper> verifyOtp(@Body VerifyOtpRequest request);

    @POST("/v1/student/resend-verify")
    @Headers({"UseBasicAuth: 1"})
    Observable<ResponseWrapper<SignUpResponse>> resetOtp(@Body ResetOtpRequest request);

//  SIMULATION
    @GET("/v1/simulation/student-list")
    Observable<ResponseWrapper<ResponseListObj<SimulationResponse>>> getSimulationList();

    @GET("/v1/simulation/student-get/{id}")
    Observable<ResponseWrapper<SimulationDetailResponse>> getSimulationDetail(@Path("id") Long id);
//  RATE
    @GET("/v1/review/client-list")
    Observable<ResponseWrapper<ResponseListObj<RateResponse>>> getRateList(@Query("simulationId") long simulationId);
//  TASK
    @GET("/v1/task/student-list")
    Observable<ResponseWrapper<ResponseListObj<TaskResponse>>> getTaskList(
            @Query("simulationId") long simulationId,
            @Query("size") int size
    );

//  SUBTASK
    @GET("/v1/task/student-get/{id}")
    Observable<ResponseWrapper<SubTaskResponse>> getSubTaskDetail(@Path("id") Long id);
    @GET("/v1/task-question-progress/student-list")
    Observable<ResponseWrapper<ResponseListObj<ListAnswerResponse>>> getAnswerList(
            @Query("studentSubTaskProgressId") long studentSubTaskProgressId,
            @Query("taskId") long taskId
    );
    @GET("/v1/subtask-progress/student-get/{id}")
    Observable<ResponseWrapper<SubTaskProgressResponse>> createSubTaskProgress(@Path("id") Long id);

    @PUT("/v1/subtask-progress/complete")
    Observable<ResponseWrapper> completeTask(@Body CompleteTaskRequest request);
    @POST("/v1/task-question-progress/create")
    Observable<ResponseWrapper> submitQuestion(@Body TaskQuestionProgressRequest request);
    @PUT("/v1/subtask-progress/restart")
    Observable<ResponseWrapper> restartQuestion(@Body RestartTaskRequest request);

//  TASK QUESTION
    @GET("/v1/task-question-progress/student-list")
    Observable<ResponseWrapper<ResponseListObj<TaskQuestionProgressResponse>>> getTaskQuestionProgressList(
            @Query("studentSubTaskProgressId") long studentSubTaskProgressId,
            @Query("taskId") long taskId
    );
    @GET("/v1/task-question/student-list")
    Observable<ResponseWrapper<ResponseListObj<TaskQuestionResponse>>> getTaskQuestionList(
            @Query("simulationId") long simulationId,
            @Query("taskId") long taskId
    );
//  REVIEW
    @POST("/v1/review/create")
    Observable<ResponseWrapper> submitReview(@Body ReviewSimulationRequest request);
//  ACHIEVEMENT
    @GET("/v1/achievement/student-list")
    Observable<ResponseWrapper<ResponseListObj<AchievementResponse>>> getAchievementList();
    @PUT("/v1/achievement/update")
    Observable<ResponseWrapper> updateAchievement(@Body UpdateCertificateRequest request);

//  NOTIFICATION
    @GET("/v1/notification/student-list")
    Observable<ResponseWrapper<List<NotificationResponse>>> getNotificationList();

//  FEEDBACK
    @GET("/v1/review-submission/student-get/{id}")
    Observable<ResponseWrapper<FeedbackResponse>> getFeedback(@Path("id") Long id);
}
