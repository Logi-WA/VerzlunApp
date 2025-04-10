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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import is.hi.hbv601g.verzlunapp.R;
import is.hi.hbv601g.verzlunapp.databinding.FragmentAccountBinding;
import is.hi.hbv601g.verzlunapp.persistence.User;
import is.hi.hbv601g.verzlunapp.persistence.UserStorage;
import is.hi.hbv601g.verzlunapp.services.LogoutService;

public class AccountFragment extends Fragment {
    private FragmentAccountBinding binding;
    private LogoutService logoutService;
    private static final String TAG = "AccountFragment";
    private final Executor executor = Executors.newSingleThreadExecutor();
    private UserStorage userStorage;

    private ActivityResultLauncher<Intent> imageCaptureLauncher;
    private ActivityResultLauncher<Intent> imagePickLauncher;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userStorage = new UserStorage(requireContext());
        logoutService = new LogoutService(requireContext());
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "AccountFragment onResume: Refreshing user data display.");
        setupUserData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);

        setupImageLaunchers();

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        setupUserData();

        binding.editProfileButton.setOnClickListener(v ->
                navController.navigate(R.id.action_accountFragment_to_editProfileFragment));
        binding.changePasswordButton.setOnClickListener(v ->
                navController.navigate(R.id.action_accountFragment_to_changePasswordFragment));
        binding.addProductButton.setOnClickListener(v ->
                navController.navigate(R.id.action_accountFragment_to_addProductFragment));

        binding.logoutButton.setOnClickListener(v -> {
            Log.d("AccountFragment", "Logout button clicked");

            new AlertDialog.Builder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Logout", (dialog, which) -> {

                        logoutService.performLogout();

                        try {
                            navController.navigate(R.id.action_accountFragment_to_signInFragment);
                            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show();
                        } catch (IllegalArgumentException e) {
                            Log.e("AccountFragment", "Navigation to signInFragment failed.", e);
                            Toast.makeText(requireContext(), "Logout complete, navigation failed.", Toast.LENGTH_SHORT).show();
                        }

                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        binding.profileImage.setOnClickListener(v -> {
            Log.d("AccountFragment", "Profile image clicked");
            showImageSourceDialog();
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

    private void showImageSourceDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Update Profile Picture")
                .setItems(new CharSequence[]{"Take Photo", "Choose from Gallery"}, (dialog, which) -> {
                    if (which == 0) {
                        handleCameraOption();
                    } else {
                        launchGalleryIntent();
                    }
                })
                .show();
    }

    private void handleCameraOption() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        } else {

            launchCameraIntent();
        }
    }

    private void launchCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            imageCaptureLauncher.launch(takePictureIntent);
        } else {
            Log.e("AccountFragment", "No camera app found");
            Toast.makeText(requireContext(), "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void launchGalleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickLauncher.launch(pickPhoto);
    }

    private void setupUserData() {
        User currentUser = userStorage.getLoggedInUser();
        if (currentUser != null) {
            binding.accountName.setText(currentUser.getName() != null ? currentUser.getName() : "N/A");
            binding.accountEmail.setText(currentUser.getEmail());

        } else {

            binding.accountName.setText("Error");
            binding.accountEmail.setText("Not logged in");
            Log.e("AccountFragment", "User is null in AccountFragment, redirecting to login.");

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}