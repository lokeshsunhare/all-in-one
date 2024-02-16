package com.goldentech.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.goldentech.R;
import com.goldentech.adapter.CategoryListAdapter;
import com.goldentech.api.Api;
import com.goldentech.common.AllApiActivity;
import com.goldentech.common.AppPreferences;
import com.goldentech.common.DbContract;
import com.goldentech.common.FileUtils;
import com.goldentech.common.IOnBackPressed;
import com.goldentech.common.NetworkCheckActivity;
import com.goldentech.model.Category;
import com.goldentech.model.prfile_detail_res.ProfileDetailRes;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class HomeFragment extends Fragment implements CategoryListAdapter.OnButtonClickListener,
        SwipeRefreshLayout.OnRefreshListener, IOnBackPressed {
    private View view;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private RecyclerView recycle_category_list;
    private SwipeRefreshLayout layout_swipe_refresh;


    private CircleImageView image_profile;
    private TextView tv_user_name;
    private TextView tv_mobile;
    private ProgressBar progress_bar;

    private AppPreferences appPreferences;

    private static final String TAG = "HomeFragment";




//    private static final int REQUEST_CODE = 6384;
//    // capture image
//    private File imageFile = null;
//    private String currentImagePath = null;
//    private static final int IMAGE_REQUEST = 100;
//
//
//    private BottomSheetDialog bottomSheetDialog;
//    private int pageNumber = 0;
//    private String pdfFileName;
//    private PDFView pdfView;
//    private TextView tv_lbl_timer;
//    // crop image
//    private Dialog alertDialog;
//    private boolean lockAspectRatio = false, setBitmapMaxWidthHeight = false;
//    private int ASPECT_RATIO_X = 16, ASPECT_RATIO_Y = 9, bitmapMaxWidth = 1000, bitmapMaxHeight = 1000;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appPreferences = new AppPreferences(getActivity());
        this.view = view;
        initView();
    }

    private void initView() {

        getRuntimePermissions();
        layout_swipe_refresh = view.findViewById(R.id.layout_swipe_refresh);
        layout_swipe_refresh.setOnRefreshListener(this);


        toolbar = view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }

        progress_bar = view.findViewById(R.id.progress_bar);
        image_profile = view.findViewById(R.id.image_profile);
        tv_user_name = view.findViewById(R.id.tv_user_name);
        tv_mobile = view.findViewById(R.id.tv_mobile);

        drawer = ((AppCompatActivity) getActivity()).findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        view.findViewById(R.id.image_open_nav).setOnClickListener(v -> {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        recycle_category_list = view.findViewById(R.id.recycle_category_list);
        recycle_category_list.setHasFixedSize(true);
//        recycle_category_list.setLayoutManager(new LinearLayoutManager(getContext(),
//                LinearLayoutManager.HORIZONTAL, false));
        recycle_category_list.setNestedScrollingEnabled(false);
        recycle_category_list.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recycle_category_list.setItemAnimator(new DefaultItemAnimator());
        List<Category> categoryList = new ArrayList<>();

        String Slider1 = AllApiActivity.MAIN_BASE_URL + "AllInOne/Images/Slider/1.png";

        categoryList.add(new Category("1", "Numbers", Slider1));
        categoryList.add(new Category("1", "Numbers", Slider1));
        categoryList.add(new Category("1", "Numbers", Slider1));
        categoryList.add(new Category("1", "Numbers", Slider1));
        categoryList.add(new Category("1", "Numbers", Slider1));
        categoryList.add(new Category("1", "Numbers", Slider1));


        if (NetworkCheckActivity.isNetworkAvailable(getActivity())) {
            getUserProfile();
        }
        setAdapter(categoryList);

    }

    private void setAdapter(List<Category> categoryList) {
        CategoryListAdapter adapter = new CategoryListAdapter(getActivity(), categoryList, HomeFragment.this);
        recycle_category_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClickRowItem(Category model) {
        //displayDialogSelectFile();
        Fragment fragment = new AddCategoryFragment();
        if (fragment != null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            ft.replace(R.id.frameContainer, fragment).addToBackStack(null);
            ft.commit();
        }
    }

    @Override
    public void onRefresh() {
        layout_swipe_refresh.setRefreshing(false);
        initView();
    }


    private void getUserProfile() {
        progress_bar.setVisibility(View.VISIBLE);
        Api api = AllApiActivity.getInstance().getApi();
        Call<ProfileDetailRes> resCall = api.getUserProfileDetail(DbContract.UNIQUE_ID, appPreferences.getUserId());
        resCall.enqueue(new Callback<ProfileDetailRes>() {
            @Override
            public void onResponse(Call<ProfileDetailRes> call, Response<ProfileDetailRes> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        String status_code = response.body().getStatusCode();
                        String message = response.body().getMessage();
                        if (status_code.equals("1")) {
                            String name = response.body().getProfileDetail().getName();
                            String mobile = response.body().getProfileDetail().getMobile();
                            String email_id = response.body().getProfileDetail().getEmailId();
                            String profile_photo = response.body().getProfileDetail().getProfilePhoto();
                            String user_type_id = response.body().getProfileDetail().getUserTypeID();
                            String user_type_name = response.body().getProfileDetail().getUserTypeName();
                            if (appPreferences.isLoggedIn()) {
                                HashMap<String, String> profile = appPreferences.getUserDetails();
                                if (appPreferences.getUserDetails() != null && profile.get("user_name") != null) {
                                    tv_user_name.setText(profile.get("user_name").toUpperCase());
                                }
                            }
                            tv_mobile.setText(mobile);
                        }
                    }
                }
                progress_bar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ProfileDetailRes> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
                progress_bar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }


    private boolean getRuntimePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if ((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED)
            ) {
                requestPermissions(new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                        },

                        0);
                return false;
            }
        }
        return true;
    }

//    private void displayDialogSelectFile() {
//        final View dialogView = View.inflate(getActivity(), R.layout.dialog_select_file, null);
//        alertDialog = new Dialog(getActivity());
//        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        alertDialog.setContentView(dialogView);
//        alertDialog.getWindow().getAttributes().windowAnimations = R.style.MyAlertDialogStyle;
//        CardView card_view_from_file = alertDialog.findViewById(R.id.card_view_from_file);
//        CardView card_view_from_camera = alertDialog.findViewById(R.id.card_view_from_camera);
//        alertDialog.show();
//
//        card_view_from_file.setOnClickListener(v -> {
//            showChooser();
//        });
//
//        card_view_from_camera.setOnClickListener(v -> {
//            captureImage();
//        });
//
//    }
//
//    private void showChooser() {
//        // Use the GET_CONTENT intent from the utility class
//        Intent target = FileUtils.createGetContentIntent(FileUtils.MIME_TYPE_IMAGE, true);
//        // Create the chooser Intent
//        Intent intent = Intent.createChooser(
//                target, "Select File");
//        try {
//            startActivityForResult(intent, REQUEST_CODE);
//        } catch (ActivityNotFoundException e) {
//            // The reason for the existence of aFileChooser
//        }
//        if (alertDialog != null)
//            alertDialog.dismiss();
//    }
//
//    public void captureImage() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
//            try {
//                imageFile = getImageFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (imageFile != null) {
//                Uri imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".fileprovider", imageFile);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                startActivityForResult(intent, IMAGE_REQUEST);
//            }
//
//        }
//        if (alertDialog != null)
//            alertDialog.dismiss();
//    }
//
//
//    private File getImageFile() throws IOException {
//
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageName = timeStamp + "_";
//        // File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_DCIM);
////        File storageDir = new File(
////                Environment
////                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
////                "Camera");
////        if (!storageDir.exists())
////            storageDir.mkdir();
//
//        File imageFile = File.createTempFile(imageName, ".jpg", new File(DbContract.root));
////        File imageFile = File.createTempFile(imageName, ".jpg", storageDir);
//        currentImagePath = imageFile.getAbsolutePath();
//
//        return imageFile;
//    }
//
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        if (resultCode == Activity.RESULT_CANCELED) {
//            return;
//        }
//
//        switch (requestCode) {
//            case REQUEST_CODE:
//                // If the file selection was successful
//                if (resultCode == RESULT_OK) {
//                    if (data != null) {
//                        // Get the URI of the selected file
////                        ArrayList<File> imagesArrayList = new ArrayList<>();
//                        if (data.getClipData() != null) {
//                            ClipData mClipData = data.getClipData();
//                            for (int i = 0; i < mClipData.getItemCount(); i++) {
//                                ClipData.Item item = mClipData.getItemAt(i);
//                                Uri uri = item.getUri();
//                                //File file = FileUtils.getFile(ExamDashActivity.this, uri);
//                                // addAnswerSheet(uri);
//                            }
//
//                        } else if (data.getData() != null) {
//                            final Uri uri = data.getData();
//                            Log.d(TAG, "Uri = " + uri.toString());
//                            // File file = FileUtils.getFile(this, uri);
//                            //addAnswerSheet(uri);
//                        }
//
//                    }
//                }
//                break;
//
//            case IMAGE_REQUEST:
//
//                if (resultCode == RESULT_OK) {
//                    File file = new File(currentImagePath);
//                    Uri uri = Uri.fromFile(file);
//                    openCropActivity(uri, uri);
//
//                    // addAnswerSheet(uri);
//
////                    File_path = currentImagePath;
////                    str_file_name = file.getName();
////                    File_Size = FileUtils.getReadableFileSize(file.length());
////                    fileLength = file.length();
//                }
//                break;
//
//            case REQUEST_CROP:
//                if (resultCode == RESULT_OK) {
//                    Uri uri = UCrop.getOutput(data);
//                    //addAnswerSheet(uri);
//                }
//                break;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
//
//        UCrop.Options options = new UCrop.Options();
//        options.setCompressionQuality(80);
//        options.setToolbarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
//        options.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
//        options.setActiveWidgetColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
//
//        if (lockAspectRatio)
//            options.withAspectRatio(ASPECT_RATIO_X, ASPECT_RATIO_Y);
//
//        if (setBitmapMaxWidthHeight)
//            options.withMaxResultSize(bitmapMaxWidth, bitmapMaxHeight);
//
//        UCrop.of(sourceUri, destinationUri)
//                .withOptions(options)
//                .start(getActivity());
//    }

}
