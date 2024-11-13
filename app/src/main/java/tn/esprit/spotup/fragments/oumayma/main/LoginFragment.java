package tn.esprit.spotup.fragments.oumayma.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Objects;

import tn.esprit.spotup.R;
import tn.esprit.spotup.activities.AdminActivity;
import tn.esprit.spotup.activities.UserActivity;
import tn.esprit.spotup.database.AppDatabase;
import tn.esprit.spotup.entities.User;


public class LoginFragment extends Fragment {

    private TextView signup, warningTextView, forgotPassword;
    private EditText username, password;
    private Button login;
    private User connectedUser;

    private AppDatabase database;

    public static final String SHARED_NAME = "myPrefs";
    private SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadFromDBToMemory();
        sp = getActivity().getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);

    }

    private void loadFromDBToMemory() {
        database= AppDatabase.getDatabase(getContext());
        User.userArrayList.clear();
        User.userArrayList.addAll(database.userDao().getAll());
        User admin = database.userDao().getUserByEmail("admin");
        if (admin == null) {
            admin = new User("admin", "admin", "admin@example.com", "male", "admin", "admin");
            database.userDao().insertUser(admin);
        } else {
            System.out.println("Default Admin already exists");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_login, container, false);

        signup= view.findViewById(R.id.signup);
        forgotPassword= view.findViewById(R.id.forgotPassword);
        warningTextView= view.findViewById(R.id.warningTextView);
        username= view.findViewById(R.id.username);
        password= view.findViewById(R.id.password);
        login= view.findViewById(R.id.login);

        if(sp.contains("login") || sp.contains("password")){
            username.setText(sp.getString("login", ""));
            password.setText(sp.getString("password", ""));
            verifiButtonLogin();
        }

        forgotPassword.setOnClickListener(oumayma->{
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.main_fragment_container, new ResetPassword1Fragment()).commit();
        });

        signup.setOnClickListener(oumayma->{
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.main_fragment_container, new SignUpFragment()).commit();
        });

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                verifiButtonLogin();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                verifiButtonLogin();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        login.setOnClickListener(oumayma->{
            detectRole();
        });
        return view;
    }
    private void verifiButtonLogin() {
        warningTextView.setVisibility(View.GONE);
        if (username.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
            login.setEnabled(false);
        }
        else{
            login.setEnabled(true);
        }
    }

    private void detectRole() {
        User user = User.getUserForEmail(username.getText().toString());
        if (user == null) {
            warningTextView.setText("This email doesn't exist");
            warningTextView.setVisibility(View.VISIBLE);
            return;
        }
        if (!user.getPassword().equals(password.getText().toString())) {
            warningTextView.setText("The password is incorrect");
            warningTextView.setVisibility(View.VISIBLE);
            return;
        }
        connectedUser = user;
        Editor editor =sp.edit();
        editor.putString("id", String.valueOf(connectedUser.getUid()));
        editor.putString("login", username.getText().toString());
        editor.putString("password", password.getText().toString());
        editor.apply();
        System.out.println("and his name is "+connectedUser.toString());
        switchActivityBasedOnRole(connectedUser);
        warningTextView.setVisibility(View.GONE);
    }

    private void switchActivityBasedOnRole(User user) {
        Intent intentadmin= new Intent(getActivity(), AdminActivity.class);
        Intent intentuser= new Intent(getActivity(), UserActivity.class);
        if (user == null){
            warningTextView.setText("email or password incorrect");
            warningTextView.setVisibility(View.VISIBLE);
        }
        else {
            if (Objects.equals(user.getRole(), "admin")){
                intentadmin.putExtra("userid", connectedUser.getUid());
                startActivity(intentadmin);
            }
            else{
                if (Objects.equals(user.getRole(), "user")||Objects.equals(user.getRole(), "student")||Objects.equals(user.getRole(), "teacher")){
                    intentuser.putExtra("userid", connectedUser.getUid());
                    startActivity(intentuser);
                }
                else{
                    warningTextView.setText("erreur impossible");
                    System.out.println("erreur impossible"+user);
                    warningTextView.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}