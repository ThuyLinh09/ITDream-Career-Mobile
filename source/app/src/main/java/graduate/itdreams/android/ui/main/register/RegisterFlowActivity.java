package graduate.itdreams.android.ui.main.register;

import android.os.Bundle;

import androidx.annotation.Nullable;

import eu.davidea.flexibleadapter.databinding.BR;
import graduate.itdreams.android.R;
import graduate.itdreams.android.data.socket.dto.Message;
import graduate.itdreams.android.databinding.ActivityRegisterFlowBinding;
import graduate.itdreams.android.di.component.ActivityComponent;
import graduate.itdreams.android.ui.base.activity.BaseActivity;

public class RegisterFlowActivity extends BaseActivity<ActivityRegisterFlowBinding, RegisterFlowViewModel> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new SignUpFragment() )
                    .commit();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_register_flow;
    }

    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    public void performDependencyInjection(ActivityComponent buildComponent) {
        buildComponent.inject(this);
    }

    @Override
    public void onMessageReceived(Message message) {

    }

    @Override
    public void onConnectionClosed() {

    }

    @Override
    public void onConnectionClosing() {

    }
}
