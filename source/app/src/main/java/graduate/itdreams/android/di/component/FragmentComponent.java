package graduate.itdreams.android.di.component;


import graduate.itdreams.android.di.module.FragmentModule;
import graduate.itdreams.android.di.scope.FragmentScope;

import dagger.Component;
import graduate.itdreams.android.ui.main.account.AccountFragment;
import graduate.itdreams.android.ui.main.account.AccountUnLoginFragment;

import graduate.itdreams.android.ui.main.achievement.AchievementFragment;
import graduate.itdreams.android.ui.main.home.HomeFragment;
import graduate.itdreams.android.ui.main.login.OTPForgetPasswordFragment;
import graduate.itdreams.android.ui.main.login.RequestForgetPasswordFragment;
import graduate.itdreams.android.ui.main.register.QuizJobFragment;
import graduate.itdreams.android.ui.main.register.SignUpFragment;
import graduate.itdreams.android.ui.main.notification.NotificationFragment;
import graduate.itdreams.android.ui.main.register.VerifyOTPFragment;
import graduate.itdreams.android.ui.main.simulation.overview.OverviewFragment;
import graduate.itdreams.android.ui.main.simulation.rate.RateFragment;
import graduate.itdreams.android.ui.main.simulation.task.TaskFragment;
import graduate.itdreams.android.ui.main.taskdetail.SubTaskFragment;
import graduate.itdreams.android.ui.main.taskdetail.review.ReviewSimulationFragment;

@FragmentScope
@Component(modules = {FragmentModule.class},dependencies = AppComponent.class)
public interface FragmentComponent {
    void inject(HomeFragment fragment);
    void inject(NotificationFragment fragment);
    void inject(AccountFragment fragment);
    void inject(AccountUnLoginFragment fragment);
    void inject(SignUpFragment fragment);
    void inject(VerifyOTPFragment fragment);
    void inject(QuizJobFragment fragment);
    void inject(OverviewFragment fragment);
    void inject(TaskFragment fragment);
    void inject(RateFragment fragment);
    void inject(SubTaskFragment fragment);
    void inject(ReviewSimulationFragment fragment);
    void inject(AchievementFragment fragment);
    void inject(RequestForgetPasswordFragment fragment);
    void inject(OTPForgetPasswordFragment fragment);

}
