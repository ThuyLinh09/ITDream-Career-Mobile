package graduate.itdreams.android.ui.main.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;

import graduate.itdreams.android.BR;
import graduate.itdreams.android.R;
import graduate.itdreams.android.data.model.api.request.login.CandidateLoginRequest;
import graduate.itdreams.android.data.model.api.request.login.GoogleLoginRequest;
import graduate.itdreams.android.data.socket.dto.Message;
import graduate.itdreams.android.databinding.ActivityLoginBinding;
import graduate.itdreams.android.di.component.ActivityComponent;
import graduate.itdreams.android.ui.base.activity.BaseActivity;
import graduate.itdreams.android.ui.main.MainActivity;
import graduate.itdreams.android.ui.main.register.RegisterFlowActivity;

public class LoginActivity extends BaseActivity<ActivityLoginBinding, LoginViewModel> {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding.setA(this);
        viewBinding.setVm(viewModel);
        setUpPassword();
        setUpValidation();
        viewModel.loginSuccess.observe(this, unused -> {
            Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
        });

    }
    public void onBackMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("from_login", true);
        startActivity(intent);
        finish();
    }
    public void onForgetPasswordClick() {
        Intent intent = new Intent(this, ForgetPasswordFlowActivity.class);
        startActivity(intent);
    }
    public void onSignUpClick() {
        Intent intent = new Intent(this, RegisterFlowActivity.class);
        startActivity(intent);
    }
    @SuppressLint("ClickableViewAccessibility")
    public void setUpPassword() {
        viewBinding.password.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (viewBinding.password.getRight() - viewBinding.password.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    if (viewBinding.password.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                        viewBinding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        viewBinding.password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.eye_open, 0);
                    } else {
                        viewBinding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        viewBinding.password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.eye_closed, 0);
                    }
                    viewBinding.password.setSelection(viewBinding.password.getText().length());
                    return true;
                }
            }
            return false;
        });
    }

    private final ActivityResultLauncher<Intent> googleLoginLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        handleSignInResult(task);
                    }
            );

    public void onGoogleLoginClick() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(
                        new Scope("https://www.googleapis.com/auth/userinfo.profile"),
                        new Scope("https://www.googleapis.com/auth/userinfo.email")
                )
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleLoginLauncher.launch(signInIntent);
        });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);

            new Thread(() -> {
                try {
                    String scope = "oauth2:profile email";
                    String accessToken = GoogleAuthUtil.getToken(getApplicationContext(), account.getEmail(), scope);
                    Log.d("GOOGLE", "Access Token: " + accessToken);

                    GoogleLoginRequest request = new GoogleLoginRequest();
                    request.setAccessToken(accessToken);
                    runOnUiThread(() -> viewModel.googleLogin(request));

                } catch (UserRecoverableAuthException e) {
                    startActivity(e.getIntent());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (ApiException e) {
            Log.e("GOOGLE", "SignIn failed: " + e.getMessage());
        }
    }

    public void onLoginClick() {
        hideKeyboard();
        String phone = viewBinding.email.getText().toString().trim();
        String password = viewBinding.password.getText().toString().trim();

        boolean hasError = false;

        if (phone.isEmpty()) {
            viewBinding.email.setBackgroundResource(R.drawable.bg_text_box_select);
            viewBinding.mgsErEmail.setVisibility(View.VISIBLE);
            hasError = true;
        }

        if (password.isEmpty()) {
            viewBinding.password.setBackgroundResource(R.drawable.bg_text_box_select);
            viewBinding.mgsErPassword.setVisibility(View.VISIBLE);
            hasError = true;
        }

        if (hasError) return;

        CandidateLoginRequest request = new CandidateLoginRequest();
        request.setEmail(viewBinding.email.getText().toString().trim());
        request.setPassword(viewBinding.password.getText().toString().trim());

        viewModel.candidateLogin(request);

    }

    @SuppressLint("ClickableViewAccessibility")
    public void setUpValidation() {
        viewBinding.email.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String email = viewBinding.email.getText().toString().trim();
                if (email.isEmpty()) {
                    viewBinding.email.setBackgroundResource(R.drawable.bg_text_box_select);
                    viewBinding.mgsErEmail.setText(R.string.err_email);
                    viewBinding.mgsErEmail.setVisibility(View.VISIBLE);
                }else if (!isValidEmail(email)) {
                    viewBinding.email.setBackgroundResource(R.drawable.bg_text_box_select);
                    viewBinding.mgsErEmail.setText(R.string.err_email_2);
                    viewBinding.mgsErEmail.setVisibility(View.VISIBLE);
                }
            }
        });

        viewBinding.password.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String password = viewBinding.password.getText().toString().trim();
                if (password.isEmpty()) {
                    viewBinding.password.setBackgroundResource(R.drawable.bg_text_box_select);
                    viewBinding.mgsErPassword.setVisibility(View.VISIBLE);
                }
            }
        });

        viewBinding.email.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = s.toString().trim();
                if (!email.isEmpty() ) {
                    viewBinding.email.setBackgroundResource(R.drawable.bg_text_box_un_select);
                    viewBinding.mgsErEmail.setVisibility(View.GONE);
                }
            }
        });

        viewBinding.password.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    viewBinding.password.setBackgroundResource(R.drawable.bg_text_box_un_select);
                    viewBinding.mgsErPassword.setVisibility(View.GONE);
                }
            }
        });
    }
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
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
