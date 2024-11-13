package tn.esprit.spotup.fragments.oumayma.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.dhaval2404.imagepicker.ImagePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import tn.esprit.spotup.R;
import tn.esprit.spotup.database.AppDatabase;
import tn.esprit.spotup.entities.User;
import tn.esprit.spotup.fragments.oumayma.main.LoginFragment;


public class UserProfileFragment extends Fragment {

    private static final int PROFILE_PIC_REQUEST_CODE = 1001;
    private static final int COVER_PIC_REQUEST_CODE = 1002;

    private TextView emailTextView, firstNameTextView, lastNameTextView, genderTextView, roleTextView, birthdayTextView, phoneNumberTextView, aboutMeTextView, changeProfilePic, changeCoverPic;
    private TextView aboutMeLabel, phoneLabel, birthdayLabel;
    private String email, firstName, lastName, gender, role, birthday, phoneNumber, aboutMe;
    private User currentUser;
    private ImageView profileImageView, coverImage;
    private Button editInfoButton, editPasswordButton;

    private Uri imageUri;
    private Uri coverUri;

    private AppDatabase database;
    private SharedPreferences sp;

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
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        initializeViews(view);
        getconnecteduser();
        fillthetext();
        removeemptifields();

        changeProfilePic.setOnClickListener(oumayma -> {
            ImagePicker.with(this)
                    .crop()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .start(PROFILE_PIC_REQUEST_CODE);  // Include request code here
        });

        changeCoverPic.setOnClickListener(oumayma -> {
            ImagePicker.with(this)
                    .crop()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .start(COVER_PIC_REQUEST_CODE);  // Include request code here
        });

        editInfoButton.setOnClickListener(oumayma->{
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.main_content, new UserProfileEditDetailsFragment()).commit();

        });
        editPasswordButton.setOnClickListener(oumayma->{
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.main_content, new UserProfileEditPasswordFragment()).commit();

        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                if (requestCode == PROFILE_PIC_REQUEST_CODE) {
                    profileImageView.setImageURI(selectedImageUri);
                    currentUser.setProfilePicture(selectedImageUri.toString());
                    database.userDao().updateUser(currentUser);
                    Toast.makeText(getContext(), "Profile picture updated!", Toast.LENGTH_SHORT).show();
                } else if (requestCode == COVER_PIC_REQUEST_CODE) {
                    coverImage.setImageURI(selectedImageUri);
                    currentUser.setCoverPicture(selectedImageUri.toString());
                    database.userDao().updateUser(currentUser);
                    Toast.makeText(getContext(), "Cover picture updated!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadProfileImage() {
        String profileImageUri = currentUser.getProfilePicture();
        if (profileImageUri != null && !profileImageUri.isEmpty()) {
            profileImageView.setImageURI(Uri.parse(profileImageUri));
        } else {
            if ("male".equalsIgnoreCase(currentUser.getGender()))
                profileImageView.setImageResource(R.drawable.avatar);
            if ("female".equalsIgnoreCase(currentUser.getGender()))
                profileImageView.setImageResource(R.drawable.avatara);
        }
    }

    private void initializeViews(View view) {
        emailTextView = view.findViewById(R.id.emailText);
        firstNameTextView = view.findViewById(R.id.firstNameText);
        lastNameTextView = view.findViewById(R.id.lastNameText);
        genderTextView = view.findViewById(R.id.genderText);
        roleTextView = view.findViewById(R.id.roleText);
        birthdayTextView = view.findViewById(R.id.birthdayText);
        phoneNumberTextView = view.findViewById(R.id.phoneText);
        aboutMeTextView = view.findViewById(R.id.aboutMeText);
        profileImageView = view.findViewById(R.id.profileImage);
        changeProfilePic = view.findViewById(R.id.changeProfilePic);
        editInfoButton = view.findViewById(R.id.editInfoButton);
        editPasswordButton = view.findViewById(R.id.editPasswordButton);
        coverImage = view.findViewById(R.id.coverImage);
        changeCoverPic = view.findViewById(R.id.changeCoverPic);

        aboutMeLabel = view.findViewById(R.id.aboutMeLabel);
        phoneLabel = view.findViewById(R.id.phoneLabel);
        birthdayLabel = view.findViewById(R.id.birthdayLabel);

    }

    private void getconnecteduser() {
        database= AppDatabase.getDatabase(getContext());
        //Bundle bundle = getArguments();
        //if (bundle != null)
            //currentUser = database.userDao().getUserByUid(bundle.getInt("userid"));
        currentUser = database.userDao().getUserByUid(Integer.parseInt(sp.getString("id","1")));
    }

    private void fillthetext() {
        emailTextView.setText(currentUser.getEmail());
        firstNameTextView.setText(currentUser.getFirstName());
        lastNameTextView.setText(currentUser.getLastName());
        genderTextView.setText(currentUser.getGender());
        roleTextView.setText(currentUser.getRole());
        birthdayTextView.setText(getStringFormDate(currentUser.getBirthday()));
        phoneNumberTextView.setText(currentUser.getPhoneNumber());
        aboutMeTextView.setText(currentUser.getAboutMe());

        loadProfileImage();
        loadCoverImage();
    }

    private void loadCoverImage() {
        String coverImageUri = currentUser.getCoverPicture();
        if (coverImageUri != null && !coverImageUri.isEmpty()) {
            coverImage.setImageURI(Uri.parse(coverImageUri));
        } else {
            coverImage.setImageResource(R.drawable.coverprofile);

        }
    }

    private String getStringFormDate(Date date) {
        if (date == null) {
            return null;
        }
        return dateFormat.format(date);
    }

    private void removeemptifields() {
        if (birthdayTextView.getText().toString().isEmpty()) {
            birthdayLabel.setVisibility(View.GONE);
            birthdayTextView.setVisibility(View.GONE);
        }
        if (phoneNumberTextView.getText().toString().isEmpty()) {
            phoneLabel.setVisibility(View.GONE);
            phoneNumberTextView.setVisibility(View.GONE);
        }
        if (aboutMeTextView.getText().toString().isEmpty()) {
            aboutMeLabel.setVisibility(View.GONE);
            aboutMeTextView.setVisibility(View.GONE);
        }
    }
}