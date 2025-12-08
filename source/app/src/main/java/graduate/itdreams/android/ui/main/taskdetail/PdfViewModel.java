package graduate.itdreams.android.ui.main.taskdetail;

import static kotlin.io.ByteStreamsKt.readBytes;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import graduate.itdreams.android.MVVMApplication;
import graduate.itdreams.android.data.Repository;
import graduate.itdreams.android.data.model.api.response.task.SubTaskResponse;
import graduate.itdreams.android.data.model.api.response.task.TaskResponse;
import graduate.itdreams.android.ui.base.activity.BaseViewModel;
import graduate.itdreams.android.utils.NetworkUtils;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;
import timber.log.Timber;

public class PdfViewModel extends BaseViewModel {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<byte[]> pdfData = new MutableLiveData<>();
    public LiveData<byte[]> getPdfData() { return pdfData; }

    public PdfViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }
    public void loadDocument(String url){
        compositeDisposable.add(repository.getUploadApiService().loadFile(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( responseBody ->  {
                    InputStream inputStream = responseBody.byteStream();
                    byte[] bytes = readBytes(inputStream);
                    pdfData.postValue(bytes);
                }, throwable -> {
                    Log.e("ProfileViewModel", "Lỗi khi tải file: " + throwable.getMessage());
                })
        );

    }
}
