package com.raphaelmarco.vianderito.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.binding.ValidationErrorData;
import com.raphaelmarco.vianderito.databinding.ActivityProfileEditBinding;
import com.raphaelmarco.vianderito.network.RetrofitClient;
import com.raphaelmarco.vianderito.network.model.Picture;
import com.raphaelmarco.vianderito.network.model.ValidationError;
import com.raphaelmarco.vianderito.network.model.auth.User;
import com.raphaelmarco.vianderito.network.service.AuthService;
import com.raphaelmarco.vianderito.network.service.ProfileService;
import com.raphaelmarco.vianderito.view.PicassoImageView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileEditActivity extends AuthenticatedActivity implements
        EasyPermissions.PermissionCallbacks {

    private static final int SELECT_PICTURE_REQUEST = 1005;

    private static final int REQUEST_PERMISSIONS = 1006;

    private static final String[] permissions = new String[] {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private boolean hasPermissions = false;

    private UiData ui = new UiData();

    private ValidationErrorData validationError = new ValidationErrorData();

    private AuthService authService;

    private ProfileService profileService;

    private PicassoImageView avatarView;

    private boolean avatarInserted = false;

    public ProfileEditActivity() {
        authService = RetrofitClient.getInstance().create(AuthService.class);
        profileService = RetrofitClient.getInstance().create(ProfileService.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityProfileEditBinding binding = DataBindingUtil.setContentView(
                this, R.layout.activity_profile_edit);

        binding.setUi(ui);
        binding.setValidationError(validationError);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        avatarView = findViewById(R.id.user_picture_view);

        findViewById(R.id.user_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createSelectionIntent();
            }
        });

        ui.isLoading.set(true);

        authService.me().enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                ui.isLoading.set(false);

                ui.user.set(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                ui.isLoading.set(false);
            }
        });

        requestPermissions();
    }

    private void requestPermissions() {
        if (EasyPermissions.hasPermissions(this, permissions)) {
            hasPermissions = true;

            return;
        }

        EasyPermissions.requestPermissions(
                this, getString(R.string.profile_edit_permission_request),
                REQUEST_PERMISSIONS, permissions);
    }

    private void saveChanges() {
        ui.isLoading.set(true);

        User user = ui.user.get();

        profileService.save(user).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (!response.isSuccessful()) {
                    ui.isLoading.set(false);

                    validationError.fill(new ValidationError.Parser(response).parse());

                    return;
                }

                if (avatarInserted) {
                    uploadAvatar();

                    return;
                }

                setResult(Activity.RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                ui.isLoading.set(false);

                t.printStackTrace();
            }
        });
    }

    private void uploadAvatar() {
        ui.isLoading.set(true);

        Bitmap bitmap = ((BitmapDrawable) avatarView.getDrawable()).getBitmap();
        ByteArrayOutputStream imageBytes = getImageBytes(bitmap);

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("multipart/form-data"),
                imageBytes.toByteArray());

        MultipartBody.Part part = MultipartBody.Part.createFormData(
                "file", "avatar", requestBody);

        profileService.setPicture(part).enqueue(new Callback<Picture>() {
            @Override
            public void onResponse(
                    @NonNull Call<Picture> call, @NonNull Response<Picture> response) {

                setResult(Activity.RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<Picture> call, @NonNull Throwable t) {
                ui.isLoading.set(false);

                t.printStackTrace();
            }
        });
    }

    private void createSelectionIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*");

        if (hasPermissions) {
            intent = Intent.createChooser(intent, getString(R.string.upload_picture));

            intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{
                    new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            });
        }

        startActivityForResult(intent, SELECT_PICTURE_REQUEST);
    }

    private void insertAvatar(Uri uri) {
        Picasso.get()
                .load(uri)
                .centerCrop()
                .resize(512, 512)
                .into(avatarView);

        avatarInserted = true;
    }

    private ByteArrayOutputStream getImageBytes(Bitmap image) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        return bytes;
    }

    private Uri getImageUri(Bitmap image) {
        String path = MediaStore.Images.Media.insertImage(
                getContentResolver(), image, "Avatar", null);

        return Uri.parse(path);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isFinishing()) {
            overridePendingTransition(R.anim.zoom_in, R.anim.slide_to_right);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data.getData() != null) {
                insertAvatar(data.getData());

                return;
            }

            Bitmap image = (Bitmap) data.getExtras().get("data");

            insertAvatar(getImageUri(image));
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        boolean allGranted = !perms.retainAll(Arrays.asList(permissions));

        if (allGranted)
            hasPermissions = true;
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        hasPermissions = false;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.save:
                saveChanges();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class UiData extends BaseObservable {

        public ObservableField<User> user = new ObservableField<>();

        public ObservableBoolean isLoading = new ObservableBoolean();

    }
}
