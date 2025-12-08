package graduate.itdreams.android.ui.main.simulation.overview;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.InputStream;

import graduate.itdreams.android.MVVMApplication;
import graduate.itdreams.android.data.Repository;
import graduate.itdreams.android.data.model.api.response.simulation.SimulationDetailResponse;
import graduate.itdreams.android.ui.base.fragment.BaseFragmentViewModel;
import graduate.itdreams.android.utils.ImageUtils;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OverviewViewModel extends BaseFragmentViewModel {

    public OverviewViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

}
