package graduate.itdreams.android.di.component;

import graduate.itdreams.android.di.module.ActivityModule;
import graduate.itdreams.android.di.scope.ActivityScope;
import graduate.itdreams.android.ui.main.MainActivity;

import dagger.Component;
import graduate.itdreams.android.ui.main.account.EditProfileActivity;
import graduate.itdreams.android.ui.main.login.ForgetPasswordFlowActivity;
import graduate.itdreams.android.ui.main.login.LoginActivity;
import graduate.itdreams.android.ui.main.register.RegisterFlowActivity;
import graduate.itdreams.android.ui.main.simulation.SimulationOverviewActivity;
import graduate.itdreams.android.ui.main.taskdetail.PdfActivity;
import graduate.itdreams.android.ui.main.taskdetail.TaskDetailActivity;

@ActivityScope
@Component(modules = {ActivityModule.class}, dependencies = AppComponent.class)
public interface ActivityComponent {
    void inject(MainActivity activity);
    void inject(LoginActivity activity);
    void inject(RegisterFlowActivity activity);
    void inject(EditProfileActivity activity);
    void inject(SimulationOverviewActivity activity);
    void inject(TaskDetailActivity activity);
    void inject(PdfActivity activity);
    void inject(ForgetPasswordFlowActivity activity);

}

