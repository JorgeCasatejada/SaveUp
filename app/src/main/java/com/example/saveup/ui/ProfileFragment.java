package com.example.saveup.ui;

import static androidx.core.app.ActivityCompat.finishAffinity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.saveup.R;
import com.example.saveup.model.Account;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private static final String ACCOUNT = "Account";
    private Account account;
    private View root;
    private Button btExit;
    private TextInputLayout etUserLayout, etEmailLayout;
    private ImageView imgProfile;

    public static ProfileFragment newInstance(Account account) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(ACCOUNT, account);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            account = getArguments().getParcelable(ACCOUNT);
        }
    }

    private void initializeVariables() {
        btExit = root.findViewById(R.id.btExit);
        etUserLayout = root.findViewById(R.id.outlinedTextFieldUser);
        etEmailLayout = root.findViewById(R.id.outlinedTextFieldEmail);
        imgProfile = root.findViewById(R.id.imgProfile);
        etUserLayout.getEditText().setText(account.getUserName());
        etEmailLayout.getEditText().setText(account.getEmail());
        Picasso.get().load(R.drawable.user_image).fit().into(imgProfile);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeVariables();

        btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity(getActivity());
            }
        });

        return root;
    }
}