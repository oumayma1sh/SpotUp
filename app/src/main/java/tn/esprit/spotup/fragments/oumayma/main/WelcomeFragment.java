package tn.esprit.spotup.fragments.oumayma.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import tn.esprit.spotup.R;


public class WelcomeFragment extends Fragment {

    private Button getStartedButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);



        getStartedButton= view.findViewById(R.id.getStartedButton);



        getStartedButton.setOnClickListener(oumayma->{
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.main_fragment_container, new LoginFragment()).commit();
        });






        return view;
    }
}