package com.example.saveup.view.profile;

import static androidx.core.app.ActivityCompat.finishAffinity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.saveup.R;
import com.example.saveup.databinding.FragmentProfileBinding;
import com.example.saveup.model.Account;
import com.example.saveup.view.login.LoginActivity;
import com.example.saveup.viewModel.MainViewModel;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    public static final int INTENT_SELECT_IMAGE = 1;
    private static final String ACCOUNT = "Account";
    private Account account;
    private FragmentProfileBinding binding;
    private MainViewModel viewModel;
    private Uri imageUri;

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

        binding.btEditData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btEditData.setVisibility(View.GONE);
                binding.btSaveData.setVisibility(View.VISIBLE);
                binding.outlinedTextFieldUser.setEnabled(true);
                binding.imgEditImgProfile.setVisibility(View.VISIBLE);
                binding.imgProfile.setClickable(true);
            }
        });

        binding.btSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btEditData.setVisibility(View.VISIBLE);
                binding.btSaveData.setVisibility(View.GONE);
                binding.outlinedTextFieldUser.setEnabled(false);
                binding.imgEditImgProfile.setVisibility(View.GONE);
                binding.imgProfile.setClickable(false);
                saveData();
            }
        });

        binding.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        binding.imgProfile.setClickable(false);

        return binding.getRoot();
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, INTENT_SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == INTENT_SELECT_IMAGE) {
            if (data != null) {
                imageUri = data.getData();
                binding.imgProfile.setImageURI(imageUri);
            }
        }
    }

    private void saveData() {
        // Guarda nuevos datos
        viewModel.saveData(binding.etUser.getText().toString(), imageUri);
    }
}