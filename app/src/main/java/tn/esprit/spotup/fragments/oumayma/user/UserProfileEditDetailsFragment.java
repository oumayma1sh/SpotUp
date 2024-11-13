package tn.esprit.spotup.fragments.oumayma.user;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import tn.esprit.spotup.R;
import tn.esprit.spotup.database.AppDatabase;
import tn.esprit.spotup.entities.User;
import tn.esprit.spotup.fragments.oumayma.main.LoginFragment;


public class UserProfileEditDetailsFragment extends Fragment {

    private Button cancelButton, applyButton;
    private EditText editEmail, editFirstName, editLastName, editBirthday, editPhone, editAboutMe;
    private Spinner genderSpinner;

    private AppDatabase database;
    private SharedPreferences sp;

    private User currentUser;

    @SuppressLint("SimpleDateFormat")
    private static final DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getActivity().getSharedPreferences(LoginFragment.SHARED_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile_edit_details, container, false);
        initViews(view);

        database= AppDatabase.getDatabase(getContext());
        currentUser = database.userDao().getUserByUid(Integer.parseInt(sp.getString("id","1")));

        filledittext();

        cancelButton.setOnClickListener(oumayma->{
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.main_content, new UserProfileFragment()).commit();
        });

        applyButton.setOnClickListener(oumayma->{

            String email = editEmail.getText().toString().trim();
            String firstName = editFirstName.getText().toString().trim();
            String lastName = editLastName.getText().toString().trim();
            String gender = genderSpinner.getSelectedItem().toString().trim();
            String birthday = editBirthday.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();
            String aboutMe = editAboutMe.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter an email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (firstName.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter your first name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (lastName.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter your last name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (gender.isEmpty()) {
                Toast.makeText(getActivity(), "Please select a gender", Toast.LENGTH_SHORT).show();
                return;
            }

            currentUser.setEmail(email);
            currentUser.setFirstName(firstName);
            currentUser.setLastName(lastName);
            currentUser.setGender(gender);

            if (!birthday.isEmpty()) {
                currentUser.setBirthday(getDateFromString(birthday));
            }
            if (!phone.isEmpty()) {
                currentUser.setPhoneNumber(phone);
            }
            if (!aboutMe.isEmpty()) {
                currentUser.setAboutMe(aboutMe);
            }



            currentUser.setPhoneNumber(phone);
            currentUser.setAboutMe(aboutMe);

            database.userDao().updateUser(currentUser);
            Toast.makeText(getActivity(), "User updated successfully", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.main_content, new UserProfileFragment()).commit();
        });

        return view;
    }

    private void filledittext() {
        editEmail.setText(currentUser.getEmail());
        editFirstName.setText(currentUser.getFirstName());
        editLastName.setText(currentUser.getLastName());
        String[] genderOptions = {"Male", "Female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, genderOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);
        int spinnerPosition = adapter.getPosition(currentUser.getGender());
        genderSpinner.setSelection(spinnerPosition);
        editBirthday.setText(getStringFormDate(currentUser.getBirthday()));
        editPhone.setText(currentUser.getPhoneNumber());
        editAboutMe.setText(currentUser.getAboutMe());
    }

    private void initViews(View view) {
        cancelButton = view.findViewById(R.id.cancelButton);
        applyButton = view.findViewById(R.id.applyButton);
        editEmail = view.findViewById(R.id.editEmail);
        editFirstName = view.findViewById(R.id.editFirstName);
        editLastName = view.findViewById(R.id.editLastName);
        genderSpinner = view.findViewById(R.id.spinnerGender);
        editBirthday = view.findViewById(R.id.editBirthday);
        editPhone = view.findViewById(R.id.editPhone);
        editAboutMe = view.findViewById(R.id.editAboutMe);
    }

    private Date getDateFromString(String string) {
        try {
            return dateFormat.parse(string);
        } catch (ParseException | NullPointerException e) {
            return null;
        }
    }

    private String getStringFormDate(Date date) {
        if (date == null) {
            return null;
        }
        return dateFormat.format(date);
    }
}