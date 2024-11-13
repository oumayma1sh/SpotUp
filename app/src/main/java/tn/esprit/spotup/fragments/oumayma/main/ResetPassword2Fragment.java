package tn.esprit.spotup.fragments.oumayma.main;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import tn.esprit.spotup.R;


public class ResetPassword2Fragment extends Fragment {

    private Button goToNext;
    private TextView backToSignIn, messagetoemail, countdownText, warningTextView;
    private String emailorphone, digitcode;
    private CountDownTimer countDownTimer;
    private EditText emailcodeinput;
    private int userid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password2, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String receivedData = bundle.getString("emailorphone");
            String digitcode = bundle.getString("digitcode");
            userid = bundle.getInt("userid");
            emailorphone = receivedData;
            this.digitcode = digitcode;
        }

        goToNext = view.findViewById(R.id.goToNext);
        backToSignIn = view.findViewById(R.id.backToSignIn);
        messagetoemail = view.findViewById(R.id.messagetoemail);
        countdownText = view.findViewById(R.id.countdownText);
        emailcodeinput = view.findViewById(R.id.emailcodeinput);
        warningTextView = view.findViewById(R.id.warningTextView);

        messagetoemail.setText("Enter the 6-digit code we sent to your email(" + emailorphone + ") to finish setting up two-factor authentication."+ digitcode);
        startCountdown();

        backToSignIn.setOnClickListener(oumayma->{
            navigateToLogin();
        });
        goToNext.setOnClickListener(oumayma->{
            String code = emailcodeinput.getText().toString().trim();
            if (code.equals(digitcode))
            {
                ResetPassword3Fragment resetPassword3Fragment = new ResetPassword3Fragment();
                Bundle bundle1 = new Bundle();
                bundle1.putInt("userid", userid);
                resetPassword3Fragment.setArguments(bundle1);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.main_fragment_container, resetPassword3Fragment).commit();
            }
            else
            {
                warningTextView.setVisibility(View.VISIBLE);
                warningTextView.setText("Code incorrect");
            }
        });

        emailcodeinput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                warningTextView.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                countdownText.setText("Time remaining: " + seconds + " seconds");
            }

            @Override
            public void onFinish() {
                countdownText.setText("Time's up!");
                navigateToLogin();
            }
        }.start();
    }

    private void navigateToLogin() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.main_fragment_container, new LoginFragment()).commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel(); // Cancel the timer if the fragment is destroyed
        }
    }
}