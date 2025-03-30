package is.hi.hbv601g.verzlunapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.io.IOException;

import is.hi.hbv601g.verzlunapp.R;
import is.hi.hbv601g.verzlunapp.databinding.FragmentAccountBinding;
import is.hi.hbv601g.verzlunapp.services.LogoutService;
import is.hi.hbv601g.verzlunapp.services.NetworkService;
import is.hi.hbv601g.verzlunapp.services.UserService;
import is.hi.hbv601g.verzlunapp.services.serviceimplementations.NetworkServiceImpl;
import is.hi.hbv601g.verzlunapp.services.serviceimplementations.UserServiceImpl;

public class AccountFragment extends Fragment {
    private FragmentAccountBinding binding;
    private LogoutService logoutService;

    private ActivityResultLauncher<Intent> imageCaptureLauncher;
    private ActivityResultLauncher<Intent> imagePickLauncher;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);

        // Setup image intent launchers
        setupImageLaunchers();

        // Handle navigation
        NetworkService networkService = NetworkServiceImpl.getInstance();
        UserService userService = new UserServiceImpl();
        logoutService = new LogoutService(networkService, userService);

        setupUserData(userService);

        binding.editProfileButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.editProfileFragment));
        binding.changePasswordButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.changePasswordFragment));
        binding.logoutButton.setOnClickListener(v -> {
            new Thread(() -> {
                boolean success = logoutService.logoutUser();
                getActivity().runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(v).navigate(R.id.signInFragment);
                    } else {
                        Toast.makeText(requireContext(), "Logout failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });

        // Tap on profile image
        binding.profileImage.setOnClickListener(v -> {
            Log.d("AccountFragment", "Profile image clicked");
            dispatchTakePictureIntent();
        });

        return binding.getRoot();
    }

    private void setupImageLaunchers() {
        imageCaptureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            binding.profileImage.setImageBitmap(imageBitmap);
                        }
                    }
                });

        imagePickLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                            binding.profileImage.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            Log.e("AccountFragment", "Failed to load image", e);
                            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Request permission for CAMERA
        requestCameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        launchCameraIntent();
                    } else {
                        Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void dispatchTakePictureIntent() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Choose option")
                .setItems(new CharSequence[]{"Take Photo", "Choose from Gallery"}, (dialog, which) -> {
                    if (which == 0) {
                        // Check permission before launching camera
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(requireActivity(),
                                    new String[]{Manifest.permission.CAMERA},
                                    100);
                            return; // Wait for user to grant permission
                        } else {
                            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                        }
                    } else {
                        launchGalleryIntent();
                    }
                })
                .show();
    }

    private void launchCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            imageCaptureLauncher.launch(takePictureIntent);
        } else {
            Log.e("AccountFragment", "Camera intent could not be resolved");
            Toast.makeText(requireContext(), "Camera not available", Toast.LENGTH_SHORT).show();
            Log.e("AccountFragment", "Camera intent could not be resolved");
        }
    }

    private void launchGalleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickLauncher.launch(pickPhoto);
    }

    private void setupUserData(UserService userService) {
        if (userService.getCurrentUser() != null) {
            binding.accountName.setText(userService.getCurrentUser().getName());
            binding.accountEmail.setText(userService.getCurrentUser().getEmail());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
