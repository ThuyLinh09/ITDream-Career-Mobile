package graduate.itdreams.android.ui.main.taskdetail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import bolts.Task;
import graduate.itdreams.android.MVVMApplication;
import graduate.itdreams.android.data.Repository;
import graduate.itdreams.android.data.model.api.response.task.SubTaskResponse;
import graduate.itdreams.android.data.model.api.response.task.TaskResponse;
import graduate.itdreams.android.ui.base.activity.BaseViewModel;
import graduate.itdreams.android.utils.NetworkUtils;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;
import timber.log.Timber;

public class TaskDetailViewModel extends BaseViewModel {
    private final MutableLiveData<List<TaskResponse>> tasksLiveData = new MutableLiveData<>();
    public final MutableLiveData<String> simulationTitle = new MutableLiveData<>();
    public final MutableLiveData<Float> simulationPercent = new MutableLiveData<>();

    public TaskDetailViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }
    public void fetchSimulationDetail(Long id) {
        showLoading();
        compositeDisposable.add(repository.getApiService().getSimulationDetail(id)
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
                            simulationTitle.setValue(response.getData().getTitle());
                            simulationPercent.setValue(response.getData().getPercent());
                        }, throwable -> {
                            hideLoading();
                            Timber.e(throwable);
                            if (throwable instanceof HttpException && ((HttpException) throwable).code() == 400) {
                                HttpException httpException = (HttpException) throwable;
                                if (httpException.code() == 400) {
                                }
                            }
                        }));
    }
    public void fetchListTask(Long simulationId) {
        showLoading();
        compositeDisposable.add(repository.getApiService().getTaskList(simulationId)
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
                            List<TaskResponse> responseList = response.getData().getContent();
                            List<TaskResponse> taskList = responseList.stream()
                                    .filter(task -> task.getKind() == 1)
                                    .collect(Collectors.toList());

// Con = kind = 2
                            List<SubTaskResponse> subTaskList = responseList.stream()
                                    .filter(task -> task.getKind() == 2)
                                    .map(task -> {
                                        // Chuyển TaskResponse -> SubTaskResponse
                                        SubTaskResponse sub = new SubTaskResponse();
                                        sub.setId(task.getId());
                                        sub.setTitle(task.getTitle());
                                        sub.setName(task.getName());
                                        sub.setContent(task.getContent());
                                        sub.setDescription(task.getDescription());
                                        sub.setIntroduction(task.getIntroduction());
                                        sub.setKind(task.getKind());
                                        sub.setParent(task.getParent()); // vẫn giữ parent để map ngược
                                        return sub;
                                    })
                                    .collect(Collectors.toList());
                            for (TaskResponse task : taskList) {
                                List<SubTaskResponse> children = subTaskList.stream()
                                        .filter(sub -> sub.getParent().getId() != null && sub.getParent().getId().equals(task.getId()))
                                        .collect(Collectors.toList());

                                task.setSubTasks(children);
                            }
                            tasksLiveData.setValue(taskList);

                        }, throwable -> {
                            hideLoading();
                            Timber.e(throwable);
                            if (throwable instanceof HttpException && ((HttpException) throwable).code() == 400) {
                                HttpException httpException = (HttpException) throwable;
                                if (httpException.code() == 400) {
                                }
                            }
                        }));

    }
    private void loadMockData() {
        List<TaskResponse> mockTasks = new ArrayList<>();

//        mockTasks.add(new TaskResponse("1", "Lập trình Android", Arrays.asList(
//                new SubTaskResponse("1.1", "Activity & Fragment"),
//                new SubTaskResponse("1.2", "RecyclerView"),
//                new SubTaskResponse("1.3", "Navigation Drawer")
//        )));
//
//        mockTasks.add(new TaskResponse("2", "Thiết kế UI", Arrays.asList(
//                new SubTaskResponse("2.1", "ConstraintLayout"),
//                new SubTaskResponse("2.2", "Data Binding")
//        )));
//
//        mockTasks.add(new TaskResponse("3", "Kết nối API", Collections.emptyList()));
//
//        tasksLiveData.setValue(mockTasks);
    }

    public LiveData<List<TaskResponse>> getTasks() {
        return tasksLiveData;
    }
}
