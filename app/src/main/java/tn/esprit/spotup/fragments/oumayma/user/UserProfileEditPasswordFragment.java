package tn.esprit.spotup.fragments.oumayma.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import tn.esprit.spotup.R;
import tn.esprit.spotup.database.AppDatabase;
import tn.esprit.spotup.entities.User;
import tn.esprit.spotup.fragments.oumayma.main.LoginFragment;


public class UserProfileEditPasswordFragment extends Fragment {

    private EditText currentPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private Button applyButton, cancelButton;

    private AppDatabase database;
    private SharedPreferences sp;
    private User currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getActivity().getSharedPreferences(LoginFragment.SHARED_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile_edit_password, container, false);
        initViews(view);
        database= AppDatabase.getDatabase(getContext());
        currentUser = database.userDao().getUserByUid(Integer.parseInt(sp.getString("id","1")));

        applyButton.setOnClickListener(oumayma->{
            String currentPassword = currentPasswordEditText.getText().toString().trim();
            String newPassword = newPasswordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            // Check if any of the fields are empty
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if the current password matches the one in the database
            if (!currentUser.getPassword().equals(currentPassword)) {
                Toast.makeText(getActivity(), "Current password is incorrect", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if the new password and confirm password match
            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(getActivity(), "New password and confirm password do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update the user's password in the database
            currentUser.setPassword(newPassword);
            database.userDao().updateUser(currentUser);

            Toast.makeText(getActivity(), "Password updated successfully", Toast.LENGTH_SHORT).show();

            // Optionally, navigate back to the profile or another fragment
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.main_content, new UserProfileFragment()).commit();
        });

        cancelButton.setOnClickListener(oumayma->{
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.main_content, new UserProfileFragment()).commit();
        });

        return view;
    }

    private void initViews(View view) {
        currentPasswordEditText = view.findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = view.findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);
        applyButton = view.findViewById(R.id.applyButton);
        cancelButton = view.findViewById(R.id.cancelButton);
    }
}