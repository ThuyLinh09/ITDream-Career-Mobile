package graduate.itdreams.android.di.module;

import android.content.Context;

import androidx.core.util.Supplier;
import androidx.lifecycle.ViewModelProvider;

import graduate.itdreams.android.MVVMApplication;
import graduate.itdreams.android.ViewModelProviderFactory;
import graduate.itdreams.android.data.Repository;
import graduate.itdreams.android.di.scope.ActivityScope;
import graduate.itdreams.android.ui.base.activity.BaseActivity;
import graduate.itdreams.android.ui.main.MainViewModel;
import graduate.itdreams.android.ui.main.account.EditProfileViewModel;
import graduate.itdreams.android.ui.main.login.LoginViewModel;
import graduate.itdreams.android.ui.main.register.RegisterFlowViewModel;
import graduate.itdreams.android.ui.main.simulation.SimulationOverviewViewModel;
import graduate.itdreams.android.ui.main.taskdetail.PdfViewModel;
import graduate.itdreams.android.ui.main.taskdetail.TaskDetailViewModel;
import graduate.itdreams.android.utils.GetInfo;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    private BaseActivity<?, ?> activity;

    public ActivityModule(BaseActivity<?, ?> activity) {
        this.activity = activity;
    }

    @Named("access_token")
    @Provides
    @ActivityScope
    String provideToken(Repository repository){
        return repository.getToken();
    }

    @Named("device_id")
    @Provides
    @ActivityScope
    String provideDeviceId( Context applicationContext){
        return GetInfo.getAll(applicationContext);
    }


    @Provides
    @ActivityScope
    MainViewModel provideMainViewModel(Repository repository, Context application) {
        Supplier<MainViewModel> supplier = () -> new MainViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<MainViewModel> factory = new ViewModelProviderFactory<>(MainViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(MainViewModel.class);
    }

    @Provides
    @ActivityScope
    LoginViewModel provideLoginViewModel(Repository repository, Context application) {
        Supplier<LoginViewModel> supplier = () -> new LoginViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<LoginViewModel> factory = new ViewModelProviderFactory<>(LoginViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(LoginViewModel.class);
    }

    @Provides
    @ActivityScope
    RegisterFlowViewModel provideRegisterFlowViewModel(Repository repository, Context application) {
        Supplier<RegisterFlowViewModel> supplier = () -> new RegisterFlowViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<RegisterFlowViewModel> factory = new ViewModelProviderFactory<>(RegisterFlowViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(RegisterFlowViewModel.class);
    }

    @Provides
    @ActivityScope
    EditProfileViewModel provideEditProfileViewModel(Repository repository, Context application) {
        Supplier<EditProfileViewModel> supplier = () -> new EditProfileViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<EditProfileViewModel> factory = new ViewModelProviderFactory<>(EditProfileViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(EditProfileViewModel.class);
    }
    @Provides
    @ActivityScope
    SimulationOverviewViewModel provideSimulationOverviewViewModel(Repository repository, Context application) {
        Supplier<SimulationOverviewViewModel> supplier = () -> new SimulationOverviewViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<SimulationOverviewViewModel> factory = new ViewModelProviderFactory<>(SimulationOverviewViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(SimulationOverviewViewModel.class);
    }
    @Provides
    @ActivityScope
    TaskDetailViewModel provideTaskDetailViewModel(Repository repository, Context application) {
        Supplier<TaskDetailViewModel> supplier = () -> new TaskDetailViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<TaskDetailViewModel> factory = new ViewModelProviderFactory<>(TaskDetailViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(TaskDetailViewModel.class);
    }

    @Provides
    @ActivityScope
    PdfViewModel providePdfViewModel(Repository repository, Context application) {
        Supplier<PdfViewModel> supplier = () -> new PdfViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<PdfViewModel> factory = new ViewModelProviderFactory<>(PdfViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(PdfViewModel.class);
    }
}
