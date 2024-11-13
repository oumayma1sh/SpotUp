package tn.esprit.spotup.fragments.oumayma.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import java.util.Locale;

import tn.esprit.spotup.R;
import tn.esprit.spotup.fragments.oumayma.main.LoginFragment;

public class UserSettingsFragment extends Fragment {

    private Button clearCacheButton;
    private RadioGroup darkModeRadioGroup;
    private Spinner languageSpinner;
    private SharedPreferences sharedPreferences;
    private boolean isSpinnerInitialized = false;  // Flag to check if the spinner is initialized

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_settings, container, false);
        initViews(view);

        // Get shared preferences
        sharedPreferences = getActivity().getSharedPreferences(LoginFragment.SHARED_NAME, Context.MODE_PRIVATE);

        // Handle clear cache / delete saved password
        clearCacheButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            // Clear specific preference for saved password
            editor.clear();
            editor.apply();
            Toast.makeText(getActivity(), "Saved password cleared", Toast.LENGTH_SHORT).show();
        });

        // Handle Dark Mode selection
        darkModeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.darkModeOff) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (checkedId == R.id.darkModeOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else if (checkedId == R.id.darkModeSystem) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        });

        // Handle language selection with a flag
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (isSpinnerInitialized) {
                    String selectedLanguage = languageSpinner.getSelectedItem().toString();
                    setAppLocale(selectedLanguage);
                } else {
                    // Ignore the first trigger of onItemSelected
                    isSpinnerInitialized = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        return view;
    }

    // Initialize views
    private void initViews(View view) {
        clearCacheButton = view.findViewById(R.id.clearCacheButton);
        darkModeRadioGroup = view.findViewById(R.id.darkModeRadioGroup);
        languageSpinner = view.findViewById(R.id.languageSpinner);
    }

    // Set the app locale based on the selected language
    private void setAppLocale(String languageCode) {
        if (getActivity() != null) {
            Locale locale = new Locale(languageCode);
            Locale.setDefault(locale);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("selected_language", languageCode);
            editor.apply();

            Configuration config = getActivity().getResources().getConfiguration();
            config.setLocale(locale);

            // Update configuration and restart activity
            getActivity().getResources().updateConfiguration(config, getActivity().getResources().getDisplayMetrics());

            // Recreate the activity to apply changes
            getActivity().recreate();

            Toast.makeText(getActivity(), "Language changed to " + languageCode, Toast.LENGTH_SHORT).show();
        }
    }
}
