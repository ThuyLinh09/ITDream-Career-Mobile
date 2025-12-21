package graduate.itdreams.android.di.module;

import android.content.Context;

import androidx.core.util.Supplier;
import androidx.lifecycle.ViewModelProvider;

import graduate.itdreams.android.MVVMApplication;
import graduate.itdreams.android.ViewModelProviderFactory;
import graduate.itdreams.android.data.Repository;
import graduate.itdreams.android.di.scope.FragmentScope;
import graduate.itdreams.android.ui.base.fragment.BaseFragment;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import graduate.itdreams.android.ui.main.account.AccountUnLoginViewModel;
import graduate.itdreams.android.ui.main.account.AccountViewModel;
import graduate.itdreams.android.ui.main.achievement.AchievementViewModel;
import graduate.itdreams.android.ui.main.home.HomeViewModel;
import graduate.itdreams.android.ui.main.login.OTPForgetPasswordViewModel;
import graduate.itdreams.android.ui.main.login.RequestForgetPasswordViewModel;
import graduate.itdreams.android.ui.main.register.QuizJobViewModel;
import graduate.itdreams.android.ui.main.register.SignUpViewModel;
import graduate.itdreams.android.ui.main.notification.NotificationViewModel;
import graduate.itdreams.android.ui.main.register.VerifyOTPViewModel;
import graduate.itdreams.android.ui.main.simulation.overview.OverviewViewModel;
import graduate.itdreams.android.ui.main.simulation.rate.RateViewModel;
import graduate.itdreams.android.ui.main.simulation.task.TaskViewModel;
import graduate.itdreams.android.ui.main.taskdetail.SubTaskViewModel;
import graduate.itdreams.android.ui.main.taskdetail.review.ReviewSimulationViewModel;

@Module
public class FragmentModule {

    private BaseFragment<?, ?> fragment;

    public FragmentModule(BaseFragment<?, ?> fragment) {
        this.fragment = fragment;
    }

    @Named("access_token")
    @Provides
    @FragmentScope
    String provideToken(Repository repository) {
        return repository.getToken();
    }
    @Provides
    @FragmentScope
    HomeViewModel provideHomeViewModel(Repository repository, Context application) {
        Supplier<HomeViewModel> supplier = () -> new HomeViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<HomeViewModel> factory = new ViewModelProviderFactory<>(HomeViewModel.class, supplier);
        return new ViewModelProvider(fragment, factory).get(HomeViewModel.class);
    }

    @Provides
    @FragmentScope
    NotificationViewModel provideNotificationViewModel(Repository repository, Context application) {
        Supplier<NotificationViewModel> supplier = () -> new NotificationViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<NotificationViewModel> factory = new ViewModelProviderFactory<>(NotificationViewModel.class, supplier);
        return new ViewModelProvider(fragment, factory).get(NotificationViewModel.class);
    }

    @Provides
    @FragmentScope
    AccountViewModel provideAccountViewModel(Repository repository, Context application) {
        Supplier<AccountViewModel> supplier = () -> new AccountViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<AccountViewModel> factory = new ViewModelProviderFactory<>(AccountViewModel.class, supplier);
        return new ViewModelProvider(fragment, factory).get(AccountViewModel.class);
    }

    @Provides
    @FragmentScope
    AccountUnLoginViewModel provideAccountUnLoginViewModel(Repository repository, Context application) {
        Supplier<AccountUnLoginViewModel> supplier = () -> new AccountUnLoginViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<AccountUnLoginViewModel> factory = new ViewModelProviderFactory<>(AccountUnLoginViewModel.class, supplier);
        return new ViewModelProvider(fragment, factory).get(AccountUnLoginViewModel.class);
    }

    @Provides
    @FragmentScope
    SignUpViewModel provideSignUpViewModel(Repository repository, Context application) {
        Supplier<SignUpViewModel> supplier = () -> new SignUpViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<SignUpViewModel> factory = new ViewModelProviderFactory<>(SignUpViewModel.class, supplier);
        return new ViewModelProvider(fragment, factory).get(SignUpViewModel.class);
    }

    @Provides
    @FragmentScope
    VerifyOTPViewModel provideVerifyOTPViewModel(Repository repository, Context application) {
        Supplier<VerifyOTPViewModel> supplier = () -> new VerifyOTPViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<VerifyOTPViewModel> factory = new ViewModelProviderFactory<>(VerifyOTPViewModel.class, supplier);
        return new ViewModelProvider(fragment, factory).get(VerifyOTPViewModel.class);
    }

    @Provides
    @FragmentScope
    QuizJobViewModel provideQuizJobViewModel(Repository repository, Context application) {
        Supplier<QuizJobViewModel> supplier = () -> new QuizJobViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<QuizJobViewModel> factory = new ViewModelProviderFactory<>(QuizJobViewModel.class, supplier);
        return new ViewModelProvider(fragment, factory).get(QuizJobViewModel.class);
    }

    @Provides
    @FragmentScope
    OverviewViewModel provideOverviewViewModel(Repository repository, Context application) {
        Supplier<OverviewViewModel> supplier = () -> new OverviewViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<OverviewViewModel> factory = new ViewModelProviderFactory<>(OverviewViewModel.class, supplier);
        return new ViewModelProvider(fragment, factory).get(OverviewViewModel.class);
    }

    @Provides
    @FragmentScope
    TaskViewModel provideTaskViewModel(Repository repository, Context application) {
        Supplier<TaskViewModel> supplier = () -> new TaskViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<TaskViewModel> factory = new ViewModelProviderFactory<>(TaskViewModel.class, supplier);
        return new ViewModelProvider(fragment, factory).get(TaskViewModel.class);
    }

    @Provides
    @FragmentScope
    RateViewModel provideRateViewModel(Repository repository, Context application) {
        Supplier<RateViewModel> supplier = () -> new RateViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<RateViewModel> factory = new ViewModelProviderFactory<>(RateViewModel.class, supplier);
        return new ViewModelProvider(fragment, factory).get(RateViewModel.class);
    }
    @Provides
    @FragmentScope
    SubTaskViewModel provideSubTaskViewModel(Repository repository, Context application) {
        Supplier<SubTaskViewModel> supplier = () -> new SubTaskViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<SubTaskViewModel> factory = new ViewModelProviderFactory<>(SubTaskViewModel.class, supplier);
        return new ViewModelProvider(fragment, factory).get(SubTaskViewModel.class);
    }
    @Provides
    @FragmentScope
    ReviewSimulationViewModel provideReviewSimulationViewModel(Repository repository, Context application) {
        Supplier<ReviewSimulationViewModel> supplier = () -> new ReviewSimulationViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<ReviewSimulationViewModel> factory = new ViewModelProviderFactory<>(ReviewSimulationViewModel.class, supplier);
        return new ViewModelProvider(fragment, factory).get(ReviewSimulationViewModel.class);
    }
    @Provides
    @FragmentScope
    AchievementViewModel provideAchievementViewModel(Repository repository, Context application) {
        Supplier<AchievementViewModel> supplier = () -> new AchievementViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<AchievementViewModel> factory = new ViewModelProviderFactory<>(AchievementViewModel.class, supplier);
        return new ViewModelProvider(fragment, factory).get(AchievementViewModel.class);
    }
    @Provides
    @FragmentScope
    RequestForgetPasswordViewModel provideRequestForgetPasswordViewModel(Repository repository, Context application) {
        Supplier<RequestForgetPasswordViewModel> supplier = () -> new RequestForgetPasswordViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<RequestForgetPasswordViewModel> factory = new ViewModelProviderFactory<>(RequestForgetPasswordViewModel.class, supplier);
        return new ViewModelProvider(fragment, factory).get(RequestForgetPasswordViewModel.class);
    }
    @Provides
    @FragmentScope
    OTPForgetPasswordViewModel provideOTPForgetPasswordViewModel(Repository repository, Context application) {
        Supplier<OTPForgetPasswordViewModel> supplier = () -> new OTPForgetPasswordViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<OTPForgetPasswordViewModel> factory = new ViewModelProviderFactory<>(OTPForgetPasswordViewModel.class, supplier);
        return new ViewModelProvider(fragment, factory).get(OTPForgetPasswordViewModel.class);
    }
}
