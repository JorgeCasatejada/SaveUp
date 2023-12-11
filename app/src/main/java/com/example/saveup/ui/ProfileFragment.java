package com.example.saveup.ui;

import static androidx.core.app.ActivityCompat.finishAffinity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.saveup.LoginActivity;
import com.example.saveup.MainViewModel;
import com.example.saveup.R;
import com.example.saveup.databinding.FragmentProfileBinding;
import com.example.saveup.model.Account;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private static final String ACCOUNT = "Account";
    private Account account;
    private FragmentProfileBinding binding;
    private MainViewModel viewModel;

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
        binding.etUser.setText(viewModel.getUserName());
        binding.etEmail.setText(viewModel.getUserEmail());
        Picasso.get().load(R.drawable.user_image).fit().into(binding.imgProfile);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        initializeVariables();

        binding.btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity(getActivity());
            }
        });

        binding.btCloseSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.logOutFromCurrentUser();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
                Toast.makeText(getContext(), getResources().getString(R.string.infoLoggedOut), Toast.LENGTH_LONG).show();
            }
        });

        return binding.getRoot();
    }
}