package tn.esprit.spotup.fragments.oumayma.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import tn.esprit.spotup.R;
import tn.esprit.spotup.database.AppDatabase;
import tn.esprit.spotup.entities.User;


public class SignUpFragment extends Fragment {

    private Button returnfromsignuptologin, signup;
    private TextView privacy_policy_text;
    private EditText email_field, first_name_field, last_name_field, password_field, confirm_password_field;
    private RadioGroup genderGroup;
    private String gender = "";

    private AppDatabase database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        getElementsByTheirIds(view);

        returnfromsignuptologin.setOnClickListener(oumayma->{
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.main_fragment_container, new LoginFragment()).commit();

        });
        privacy_policy_text.setOnClickListener(oumayma->{
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.main_fragment_container, new PolicyFragment()).commit();
        });
        signup.setOnClickListener(oumayma -> {
            String email = email_field.getText().toString().trim();
            String firstname = first_name_field.getText().toString().trim();
            String lastname = last_name_field.getText().toString().trim();
            String password = password_field.getText().toString().trim();
            String confirmPassword = confirm_password_field.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter an email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (firstname.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter your first name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (lastname.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter your last name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter a password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedId = genderGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedRadioButton = view.findViewById(selectedId);
                gender = selectedRadioButton.getText().toString();
            } else {
                Toast.makeText(getActivity(), "Please select a gender", Toast.LENGTH_SHORT).show();
                return;
            }

            database= AppDatabase.getDatabase(getContext());
            User user = new User( email, firstname, lastname, gender, password, "user");
            database.userDao().insertUser(user);

            Toast.makeText(getActivity(), "User added to database", Toast.LENGTH_SHORT).show();
            System.out.println(User.userArrayList.size());
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.main_fragment_container, new LoginFragment()).commit();
        });

        return view;
    }

    private void getElementsByTheirIds(View view) {
        returnfromsignuptologin = view.findViewById(R.id.returnfromsignuptologin);
        signup = view.findViewById(R.id.btn_submit);
        email_field = view.findViewById(R.id.email_field);
        first_name_field = view.findViewById(R.id.first_name_field);
        last_name_field = view.findViewById(R.id.last_name_field);
        password_field = view.findViewById(R.id.password_field);
        confirm_password_field = view.findViewById(R.id.confirm_password_field);
        genderGroup = view.findViewById(R.id.gender_group);
        privacy_policy_text = view.findViewById(R.id.privacy_policy_text);
    }
}