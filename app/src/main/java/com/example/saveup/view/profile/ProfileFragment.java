package com.example.saveup.view.profile;

import static androidx.core.app.ActivityCompat.finishAffinity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.saveup.R;
import com.example.saveup.databinding.FragmentProfileBinding;
import com.example.saveup.model.firestore.FireUser;
import com.example.saveup.view.login.LoginActivity;
import com.example.saveup.viewModel.MainViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    public static final int INTENT_SELECT_IMAGE = 1;
    private FragmentProfileBinding binding;
    private MainViewModel viewModel;
    private Uri imageUri;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    public static void checkProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context).load(imageUri).circleCrop().into(imageView);
    }

    public static void setProfilePic(Context context, String imageUri, ImageView imageView) {
        if (imageUri.isEmpty()) {
            Glide.with(context).load(R.drawable.user_image).circleCrop().into(imageView);
        } else {
            Glide.with(context).load(imageUri).circleCrop().into(imageView);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding.btExit.setOnClickListener(__ -> finishAffinity(getActivity()));

        binding.btCloseSession.setOnClickListener(__ -> {
            viewModel.logOutFromCurrentUser();
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
            Toast.makeText(getContext(), getResources().getString(R.string.infoLoggedOut), Toast.LENGTH_LONG).show();
        });

        binding.btEditData.setOnClickListener(__ -> {
            binding.btEditData.setVisibility(View.GONE);
            binding.btSaveData.setVisibility(View.VISIBLE);
            binding.outlinedTextFieldUser.setEnabled(true);
            binding.imgEditImgProfile.setVisibility(View.VISIBLE);
            binding.imgProfile.setClickable(true);
        });

        binding.btSaveData.setOnClickListener(__ -> {
            binding.btEditData.setVisibility(View.VISIBLE);
            binding.btSaveData.setVisibility(View.GONE);
            binding.outlinedTextFieldUser.setEnabled(false);
            binding.imgEditImgProfile.setVisibility(View.GONE);
            binding.imgProfile.setClickable(false);
            saveData();
        });

        binding.imgProfile.setOnClickListener(__ -> selectImage());
        binding.imgProfile.setClickable(false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), this::updateUI);
        viewModel.registerCurrentUserListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.unregisterCurrentUserListener();
    }

    private void updateUI(FireUser user) {
        binding.etUser.setText(user.getUserName());
        binding.etEmail.setText(user.getEmail());
        setProfilePic(getContext(), user.getImagePath(), binding.imgProfile);
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
                checkProfilePic(getContext(), imageUri, binding.imgProfile);
            }
        }
    }

    private void saveData() {
        // Guarda nuevos datos
        viewModel.saveData(binding.etUser.getText().toString(), imageUri);
        imageUri = null;
    }
}