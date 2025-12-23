package graduate.itdreams.android.ui.main.taskdetail.review;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import java.io.File;
import java.util.List;

import graduate.itdreams.android.MVVMApplication;
import graduate.itdreams.android.R;
import graduate.itdreams.android.data.Repository;
import graduate.itdreams.android.data.model.api.ResponseWrapper;
import graduate.itdreams.android.data.model.api.request.review.ReviewSimulationRequest;
import graduate.itdreams.android.data.model.api.request.task.CompleteTaskRequest;
import graduate.itdreams.android.data.model.api.request.task.TaskQuestionProgressRequest;
import graduate.itdreams.android.data.model.api.response.file.UploadResponse;
import graduate.itdreams.android.data.model.api.response.question.TaskQuestionProgressResponse;
import graduate.itdreams.android.data.model.api.response.question.TaskQuestionResponse;
import graduate.itdreams.android.data.model.api.response.task.SubTaskProgressResponse;
import graduate.itdreams.android.data.model.api.response.task.SubTaskResponse;
import graduate.itdreams.android.ui.base.fragment.BaseFragmentViewModel;
import graduate.itdreams.android.utils.NetworkUtils;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.HttpException;
import timber.log.Timber;

public class ReviewSimulationViewModel extends BaseFragmentViewModel {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    public ReviewSimulationViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }
    public void submitReview(ReviewSimulationRequest request) {
        showLoading();
        compositeDisposable.add(repository.getApiService().submitReview(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(throwable ->
                        throwable.flatMap((Function<Throwable, ObservableSource<?>>) throwable1 -> {
                            if (NetworkUtils.checkNetworkError(throwable1)) {
                                hideLoading();
                                return application.showDialogNoInternetAccess();
                            } else {
                                return Observable.error(throwable1);
                            }
                        })
                )
                .subscribe(
                        response -> {
                            hideLoading();
                            showNormalMessage("Gửi đánh giá thành công");

                            //taskQuestionLiveData.setValue(response.getData().getContent());
                        }, throwable -> {
                            hideLoading();
                            Timber.e(throwable);
                            if (throwable instanceof HttpException && ((HttpException) throwable).code() == 400) {
                                HttpException httpException = (HttpException) throwable;
                                try {
                                    String errorBody = httpException.response()
                                            .errorBody()
                                            .string();

                                    ResponseWrapper apiError = new Gson().fromJson(errorBody, ResponseWrapper.class);

                                    if ("REVIEW-ERROR-0001".equals(apiError.getCode())) {
                                        showNormalMessage("Bạn đã đánh giá bài này rồi");
                                    }else if ("REVIEW-ERROR-0003".equals(apiError.getCode())) {
                                        showNormalMessage("Bạn chưa hoàn thành khóa học");
                                    }

                                } catch (Exception e) {
                                    showNormalMessage("Có lỗi xảy ra, vui lòng thử lại");
                                }
                            }
                        }));

    }
}
