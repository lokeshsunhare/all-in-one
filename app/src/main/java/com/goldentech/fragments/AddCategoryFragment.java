package com.goldentech.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.goldentech.R;
import com.goldentech.adapter.AttachImageListAdapter;
import com.goldentech.adapter.CategoryLanguageListAdapter;
import com.goldentech.adapter.CategorySpinnerAdapter;
import com.goldentech.api.Api;
import com.goldentech.app.GPSTracker;
import com.goldentech.app.MyApplication;
import com.goldentech.common.AllApiActivity;
import com.goldentech.common.AppPreferences;
import com.goldentech.common.CheckPermission;
import com.goldentech.common.FileUtils;
import com.goldentech.common.NetworkCheckActivity;
import com.goldentech.common.ProgressRequestBody;
import com.goldentech.model.AttachmentImage;
import com.goldentech.model.CategoryLanguage;
import com.goldentech.model.CommonSpinner;
import com.goldentech.model.upload_multiple_file_res.UploadMultipleFileRes;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfImageObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class AddCategoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        AttachImageListAdapter.OnButtonClickListener, ProgressRequestBody.UploadCallbacks, OnPDFCompressedInterface {


    private View view;
    private long mLastClickTime = 0;
    private String POST_TIME = null;
    private AppPreferences appPreferences;
    private double latitude, longitude;
    private Dialog alertDialog;

    private SwipeRefreshLayout layout_swipe_refresh;
    private long fileLength = 0;

    private RecyclerView recycler_language_layout_list;
    private RecyclerView recycler_category_image_list;

    private static final String TAG = "AddCategoryFragment";

    private List<CategoryLanguage> categoryLanguages;
    private List<CommonSpinner> commonSpinnerList;
    private Spinner spinner_category_parent;
    private String categoryParentId = "";


    private String str_file_name;
    private String File_Size;
    private static final int REQUEST_CODE = 6384;
    // capture image
    private File imageFile = null;
    private String currentImagePath = null;
    private static final int IMAGE_REQUEST = 100;
    private TextView tvfilePath;
    private String File_path = "";
    private double file_size_count = 4;

    private List<AttachmentImage> attachmentImages;
    private ProgressDialog progressDialog;

    private File root = new File(Environment.getExternalStorageDirectory() + "/Download/");//path in which you want to save pdf
    private String tempPDFName = "";
    private String realPDFName = "";

    public AddCategoryFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appPreferences = new AppPreferences(getActivity());
        this.view = view;
        initView();
    }

    private void initView() {
        CheckPermission checkPermission = new CheckPermission(getActivity());
        checkPermission.getReadWriteCameraPermissions();

        GPSTracker gps = new GPSTracker(getActivity());
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();
        spinner_category_parent = view.findViewById(R.id.spinner_category_parent);
        initSpinnerSelection();

        layout_swipe_refresh = view.findViewById(R.id.layout_swipe_refresh);
        layout_swipe_refresh.setOnRefreshListener(this);

        recycler_language_layout_list = view.findViewById(R.id.recycler_language_layout_list);
        recycler_language_layout_list.setHasFixedSize(true);
        recycler_language_layout_list.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        ViewCompat.setNestedScrollingEnabled(recycler_language_layout_list, false);
        recycler_language_layout_list.setNestedScrollingEnabled(false);

        recycler_category_image_list = view.findViewById(R.id.recycler_category_image_list);
        recycler_category_image_list.setHasFixedSize(true);
        recycler_category_image_list.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        ViewCompat.setNestedScrollingEnabled(recycler_category_image_list, false);
        recycler_category_image_list.setNestedScrollingEnabled(false);


        if (NetworkCheckActivity.isNetworkAvailable(getActivity())) {
            getCategoryParentList();
        }
        categoryLanguages = new ArrayList<>();
        categoryLanguages.add(new CategoryLanguage("1", "Category name in " + "English", "", true));
        categoryLanguages.add(new CategoryLanguage("2", "Category name in " + "हिंदी", "", false));
        CategoryLanguageListAdapter adapter = new CategoryLanguageListAdapter(getActivity(), categoryLanguages);
        recycler_language_layout_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        view.findViewById(R.id.btn_submit).setOnClickListener(v -> {
            validateField();

        });


        attachmentImages = new ArrayList<>();
        //checkVisibilityAttachImage();
        view.findViewById(R.id.btn_select_image).setOnClickListener(v -> displayDialogSelectFile());
    }

    private void validateField() {
        boolean valid = true;
        if (attachmentImages != null && attachmentImages.size() == 0) {
            valid = false;
            Toast.makeText(getActivity(), "Please select alteast one Image", Toast.LENGTH_SHORT).show();
        } else if (categoryLanguages != null && categoryLanguages.size() == 0) {
            valid = false;
            Toast.makeText(getActivity(), "Please check atleast one language", Toast.LENGTH_SHORT).show();
        } else if (categoryLanguages.get(0).getCheckedCount() == 0) {
            valid = false;
            Toast.makeText(getActivity(), "Please check at least one language", Toast.LENGTH_SHORT).show();
        } else if (categoryLanguages.get(0).getCheckedCount() > 0 && getValidateLanguageName()) {
            valid = false;
            Toast.makeText(getActivity(), "Please enter " + getCheckedLanguageName(), Toast.LENGTH_SHORT).show();
        }
        if (valid) {


            String attachtype_image = "";
            for (int i = 0; i < attachmentImages.size(); i++) {
                attachtype_image += attachmentImages.get(i).getImage_name() + ",";
            }
            if (attachtype_image.endsWith(",")) {
                attachtype_image = attachtype_image.substring(0, attachtype_image.length() - 1);
            }
            mergePdfFiles();

//            if (NetworkCheckActivity.isNetworkAvailable(getActivity())) {
//
//                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
//                    return;
//                }
//                mLastClickTime = SystemClock.elapsedRealtime();
//                POST_TIME = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
//
//                String category_language_id = "";
//                for (int i = 0; i < categoryLanguages.size(); i++) {
//                    if (categoryLanguages.get(i).isChecked()) {
//                        category_language_id += categoryLanguages.get(i).getLanguageID() + "¶";
//                    }
//                }
//                if (category_language_id.endsWith("¶")) {
//                    category_language_id = category_language_id.substring(0, category_language_id.length() - 1);
//                }
//
//                String category_language = "";
//                for (int i = 0; i < categoryLanguages.size(); i++) {
//                    if (categoryLanguages.get(i).isChecked()) {
//                        category_language += categoryLanguages.get(i).getCategoryName() + "¶";
//                    }
//                }
//                if (category_language.endsWith("¶")) {
//                    category_language = category_language.substring(0, category_language.length() - 1);
//                }
//
//                String attachtype_image = "";
//                for (int i = 0; i < attachmentImages.size(); i++) {
//                    attachtype_image += attachmentImages.get(i).getImage_name() + "¶";
//                }
//                if (attachtype_image.endsWith("¶")) {
//                    attachtype_image = attachtype_image.substring(0, attachtype_image.length() - 1);
//                }
//
//                uploadImagesToServer(categoryLanguages.get(0).getCategoryName(), getCategoryParentId(),
//                        category_language_id, category_language);
//
//            } else {
//                Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
//            }

            //saveStudyMaterialFile(attachtype_image);
        }

    }

    private boolean getValidateLanguageName() {
        boolean valid = false;
        for (int i = 0; i < categoryLanguages.size(); i++) {
            if (categoryLanguages.get(i).isChecked()) {
                valid = categoryLanguages.get(i).getCategoryName().isEmpty();
            }
        }
        return valid;
    }

    private String getCheckedLanguageName() {
        String language = "";
        for (int i = 0; i < categoryLanguages.size(); i++) {
            if (categoryLanguages.get(i).isChecked()) {
                language = categoryLanguages.get(i).getLanguageName();
            }
        }
        return language;
    }

    @Override
    public void onRefresh() {
        layout_swipe_refresh.setRefreshing(false);
        initView();
    }


    private void displayDialogSelectFile() {
        final View dialogView = View.inflate(getActivity(), R.layout.dialog_select_file, null);
        alertDialog = new Dialog(getActivity());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setContentView(dialogView);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.MyAlertDialogStyle;
        CardView card_view_from_file = alertDialog.findViewById(R.id.card_view_from_file);
        CardView card_view_from_camera = alertDialog.findViewById(R.id.card_view_from_camera);
        alertDialog.show();

        card_view_from_file.setOnClickListener(v -> {
            showChooser();
        });

        card_view_from_camera.setOnClickListener(v -> {
            captureImage();
        });

    }

    private void showChooser() {
        // Use the GET_CONTENT intent from the utility class
        Intent target = FileUtils.createGetContentIntent(FileUtils.MIME_TYPE_ALL, false);
        // Create the chooser Intent
        Intent intent = Intent.createChooser(
                target, "Select File");
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
        }
        alertDialog.dismiss();
    }

    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {

            try {
                imageFile = getImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (imageFile != null) {
                Uri imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".fileprovider", imageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, IMAGE_REQUEST);
            }

        }
        alertDialog.dismiss();
    }

    private File getImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageName, ".png", storageDir);
        currentImagePath = imageFile.getAbsolutePath();

        return imageFile;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE:
                // If the file selection was successful
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        // Get the URI of the selected file

                        if (data.getClipData() != null) {
                            ClipData mClipData = data.getClipData();
                            for (int i = 0; i < mClipData.getItemCount(); i++) {
                                ClipData.Item item = mClipData.getItemAt(i);
                                Uri uri = item.getUri();

                                Log.i(TAG, "Uri = " + uri.toString());
                                try {
                                    // Get the file path from the URI
                                    final String path = FileUtils.getPath(getActivity(), uri);

                                    File file = FileUtils.getFile(getActivity(), uri);

                                    Cursor returnCursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                                    returnCursor.moveToFirst();
                                    String filename = returnCursor.getString(nameIndex);
                                    String extension = FileUtils.getExtension(path);
                                    long size = returnCursor.getInt(sizeIndex);
                                    Log.d(TAG, "onActivityResult: fp " + path);
                                    Log.d(TAG, "onActivityResult: s " + size);
                                    Log.d(TAG, "onActivityResult: e " + extension);
                                    File_path = path;
                                    fileLength = size;
                                    str_file_name = FileUtils.getFileName(getActivity(), uri);
                                    File_Size = FileUtils.getReadableFileSize(size);
                                    //setSelectedFileNameHandleIt(str_file_name, File_Size, file);
                                    setImageAttachment(File_path, uri);

                                } catch (Exception e) {
                                    Log.e(TAG, "File select error", e);
                                }
                            }
                        } else if (data.getData() != null) {
                            final Uri uri = data.getData();
                            Log.i(TAG, "Uri = " + uri.toString());
                            try {
                                // Get the file path from the URI
                                final String path = FileUtils.getPath(getActivity(), uri);

                                //File file = FileUtils.getFile(getActivity(), uri);

                                Cursor returnCursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                                returnCursor.moveToFirst();
                                String filename = returnCursor.getString(nameIndex);
                                String extension = FileUtils.getExtension(path);
                                long size = returnCursor.getInt(sizeIndex);
                                Log.d(TAG, "onActivityResult: f " + filename);
                                Log.d(TAG, "onActivityResult: s " + size);
                                Log.d(TAG, "onActivityResult: e " + extension);
                                File_path = path;
                                fileLength = size;
                                str_file_name = FileUtils.getFileName(getActivity(), uri);
                                File_Size = FileUtils.getReadableFileSize(size);
                                //setSelectedFileNameHandleIt(str_file_name, File_Size, file);
                                setImageAttachment(File_path, uri);
                            } catch (Exception e) {
                                Log.e(TAG, "File select error", e);
                            }
                        }


                    }
                }
                break;

            case IMAGE_REQUEST:

                if (resultCode == RESULT_OK) {
                    //final Uri uri = data.getData();
                    // Bitmap bitmap = BitmapFactory.decodeFile(currentImagePath);
                    //image_view.setImageBitmap(bitmap);
                    File file = new File(currentImagePath);
                    File_path = currentImagePath;
                    str_file_name = file.getName();
                    File_Size = FileUtils.getReadableFileSize(file.length());
                    fileLength = file.length();

                    Uri uri = Uri.fromFile(file);
                    //setSelectedFileNameHandleIt(str_file_name, File_Size, file);
                    Log.d(TAG, "onActivityResult: uri" + uri);
                    setImageAttachment(File_path, uri);
                }

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setImageAttachment(String file_path, Uri uriImage) {

        String extension = file_path.
                substring(file_path.lastIndexOf(".") + 1, file_path.toString().length());

//        if (spinner_category_parent.getSelectedItemPosition() == 0 && !extension.toLowerCase().equals("png")) {
//            Toast.makeText(getActivity(), "Please select PNG format image", Toast.LENGTH_SHORT).show();
//        } else {
//            attachmentImages.add(new AttachmentImage(file_path));
//            setAttachmentTypeAdapter();
//        }

        attachmentImages.add(new AttachmentImage(file_path, uriImage));
        setAttachmentTypeAdapter();

    }
//    private void setSelectedFileNameHandleIt(String file_name, String file_Size, File file) {
//        RelativeLayout layout_selected_file_detail = view.findViewById(R.id.layout_selected_file_detail);
//        TextView tv_view_file = view.findViewById(R.id.tv_view_file);
//        TextView tv_file_size = view.findViewById(R.id.tv_file_size);
//        layout_selected_file_detail.setVisibility(View.VISIBLE);
//        tvfilePath.setText(file_name);
//        tv_file_size.setText(file_Size);
//        tv_view_file.setOnClickListener(v -> {
//            //openFile(file);
//            try {
//                Intent t = FileUtils.getViewIntent(getActivity(), file);
//                startActivity(t);
//            } catch (ActivityNotFoundException e) {
//                Toast.makeText(getActivity(), "No application found which can open the file", Toast.LENGTH_SHORT).show();
//            }
//        });
//        if (fileLength >= (1024 * 1024) * file_size_count) {
//            tv_file_size.setTextColor(this.getResources().getColor(R.color.red));
//        } else {
//            tv_file_size.setTextColor(this.getResources().getColor(R.color.white));
//        }
//        tv_file_size.setVisibility(View.VISIBLE);
//    }

    private void clearedFileName() {
        RelativeLayout layout_selected_file_detail = view.findViewById(R.id.layout_selected_file_detail);
        TextView tv_file_size = view.findViewById(R.id.tv_file_size);
        layout_selected_file_detail.setVisibility(View.GONE);
        tvfilePath.setText("");
        tv_file_size.setText("");
    }

    private void clearAttachmentImage() {
        attachmentImages = new ArrayList<>();
        setAttachmentTypeAdapter();
    }


    private void setAttachmentTypeAdapter() {
        AttachImageListAdapter aiAdapter = new AttachImageListAdapter(getActivity(), attachmentImages, this);
        recycler_category_image_list.setAdapter(aiAdapter);
        aiAdapter.notifyDataSetChanged();
        //checkVisibilityAttachImage();
    }

//    private void checkVisibilityAttachImage() {
//        if (attachmentImages.size() > 0) {
//            layout_attach_image.setVisibility(View.VISIBLE);
//        } else {
//            layout_attach_image.setVisibility(View.GONE);
//        }
//    }

    @Override
    public void onDeleteButtonClick() {
        //checkVisibilityAttachImage();

    }

    private void getCategoryParentList() {

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AllApiActivity.GET_CATEGORY_PARENT_URL,
                response -> {
                    if (response != null) {
                        try {

                            commonSpinnerList = new ArrayList<>();
                            commonSpinnerList.add(new CommonSpinner("0", "--Select Parent Category--", ""));
                            Log.d(TAG, "onResponse: " + response);
                            JSONObject jsono = new JSONObject(response);
                            JSONArray jarray = jsono.getJSONArray("category_result");
                            for (int i = 0; i < jarray.length(); i++) {
                                JSONObject json_data_profile = jarray.getJSONObject(i);
                                String id = json_data_profile.getString("id");
                                String name = json_data_profile.getString("category_name");
                                commonSpinnerList.add(new CommonSpinner(id, name, ""));
                            }
                            setCategorySpinnerAdapter();
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println("    catch  JSONException   ");
                        }
                    } else {
                        Log.e("JSON Data", "JSON data error!");
                    }
                },
                error -> {
                    progressDialog.dismiss();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("Emp_Id", Emp_Id);
                return params;
            }
        };
        MyApplication.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void initSpinnerSelection() {
        spinner_category_parent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                setCategoryParentId(String.valueOf(commonSpinnerList.get(pos).getId()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setCategorySpinnerAdapter() {
        CategorySpinnerAdapter spinnerAdapter = new CategorySpinnerAdapter(getActivity(), commonSpinnerList);
        spinner_category_parent.setAdapter(spinnerAdapter);
        spinnerAdapter.notifyDataSetChanged();
    }

    public String getCategoryParentId() {
        return categoryParentId;
    }

    public void setCategoryParentId(String categoryParentId) {
        this.categoryParentId = categoryParentId;
    }

    private void uploadImagesToServer(String incatname, String categoryParentId,
                                      String categoryLanguageId, String categoryLanguageName) {


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        Api api = AllApiActivity.getInstance().getApi();
        // create list of file parts (photo, video, ...)
        List<MultipartBody.Part> parts = new ArrayList<>();

        if (attachmentImages != null) {
            // create part for file (photo, video, ...)
            for (int i = 0; i < attachmentImages.size(); i++) {
                parts.add(prepareFilePart("file" + i, attachmentImages.get(i).getImage_Uri()));
            }
        }

        // create a map of data to pass along
        // RequestBody in_file_size = createPartFromString("2MB~4MB");
        RequestBody in_cat_name = createPartFromString(incatname);
        RequestBody category_parent_id = createPartFromString(categoryParentId);
        RequestBody in_created_by = createPartFromString(appPreferences.getUserId());
        RequestBody category_language_id = createPartFromString(categoryLanguageId);
        RequestBody category_language_name = createPartFromString(categoryLanguageName);

        RequestBody size = createPartFromString("" + parts.size());


        // finally, execute the request
        Call<UploadMultipleFileRes> call = api.uploadMultiple(in_cat_name, category_parent_id,
                in_created_by, category_language_id, category_language_name, size, parts);

        call.enqueue(new Callback<UploadMultipleFileRes>() {
            @Override
            public void onResponse(@NonNull Call<UploadMultipleFileRes> call, @NonNull Response<UploadMultipleFileRes> response) {

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        String status_code = response.body().getResponseResult().getStatusCode();
                        String message = response.body().getResponseResult().getMessage();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                }
                progressDialog.cancel();
            }

            @Override
            public void onFailure(@NonNull Call<UploadMultipleFileRes> call, @NonNull Throwable t) {
                Log.e(TAG, "Image upload failed!", t);
                progressDialog.cancel();
            }
        });

    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(MediaType.parse(FileUtils.MIME_TYPE_TEXT), descriptionString);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        // use the FileUtils to get the actual file by uri
        File file = FileUtils.getFile(getActivity(), fileUri);
        ProgressRequestBody fileBody = new ProgressRequestBody(file, this);
        // create RequestBody instance from file
        //RequestBody requestFile = RequestBody.create(MediaType.parse(FileUtils.MIME_TYPE_ALL), file);
        String file_name = FileUtils.getFileName(getActivity(), fileUri);
        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file_name, fileBody);
    }

    @Override
    public void onProgressUpdate(int percentage) {
        progressDialog.setProgress(percentage);
    }

    @Override
    public void onError() {

        Log.d(TAG, "onError: upload failed");
    }

    @Override
    public void onFinish() {
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setProgress(100);
        progressDialog.dismiss();
    }

    @Override
    public void uploadStart() {
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setProgress(0);
    }

    public void mergePdfFiles() {
        // mergePdf();

        //File folderPath = new File(filePath);

        //File[] imageList = folderPath.listFiles();
        ArrayList<File> imagesArrayList = new ArrayList<>();
//        for (File absolutePath : imageList) {
//            imagesArrayList.add(absolutePath);
//        }
        for (int i = 0; i < attachmentImages.size(); i++) {
            imagesArrayList.add(FileUtils.getFile(getActivity(), attachmentImages.get(i).getImage_Uri()));
        }

        new CreatePdfTask(getContext(), imagesArrayList).execute();
    }


    public class CreatePdfTask extends AsyncTask<String, Integer, File> {
        Context context;
        ArrayList<File> files;
        ProgressDialog progressDialog;

        public CreatePdfTask(Context context2, ArrayList<File> arrayList) {
            context = context2;
            files = arrayList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Please wait...");
            progressDialog.setMessage("Creating pdf...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected File doInBackground(String... strings) {
            tempPDFName = "temp_1540.pdf";
            File root = new File(Environment.getExternalStorageDirectory() + "/Download/");//path in which you want to save pdf
            File outputMediaFile = new File(root.getAbsoluteFile() + "/" + tempPDFName);//path in which you want to save pdf

            Document document = new Document(PageSize.A4, 30.0f, 30.0f, 40.0f, 30.0f);
            try {
                PdfWriter.getInstance(document, new FileOutputStream(outputMediaFile));
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
            document.open();

            int i = 0;
            while (true) {
                if (i < this.files.size()) {
                    try {
                        Image image = Image.getInstance(files.get(i).getAbsolutePath());

                        float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                                - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
                        image.scalePercent(scaler);
                        image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
                        image.setAbsolutePosition((document.getPageSize().getWidth() - image.getScaledWidth()) / 2.0f,
                                (document.getPageSize().getHeight() - image.getScaledHeight()) / 2.0f);

                        document.add(image);
                        document.newPage();
                        publishProgress(i);
                        i++;
                    } catch (BadElementException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                } else {

                    document.close();
                    return outputMediaFile;
                }

            }


        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            this.progressDialog.setProgress(((values[0] + 1) * 100) / this.files.size());
            StringBuilder sb = new StringBuilder();
            sb.append("Processing images (");
            sb.append(values[0] + 1);
            sb.append("/");
            sb.append(this.files.size());
            sb.append(")");
            progressDialog.setTitle(sb.toString());

        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            progressDialog.dismiss();
            compressPDF(file);
        }
    }

    private void compressPDF(File file) {
        String input = "40";
        realPDFName = "merged.pdf";
        File outputMediaFile = new File(root.getAbsolutePath() + "/" + realPDFName);//path in which you want to save pdf
        int check;
        try {
            check = Integer.parseInt(input);
            if (check > 100 || check <= 0 || outputMediaFile == null) {

            } else {
                compressPDF(file.getAbsolutePath(), outputMediaFile.getAbsolutePath(), 100 - check, AddCategoryFragment.this);
            }
        } catch (NumberFormatException e) {

        }
    }

    public void compressPDF(String inputPath, String outputPath, int quality,
                            OnPDFCompressedInterface onPDFCompressedInterface) {
        new CompressPdfAsync(inputPath, outputPath, quality, onPDFCompressedInterface)
                .execute();
    }

    private static class CompressPdfAsync extends AsyncTask<String, String, String> {

        final int quality;
        final String inputPath;
        final String outputPath;
        boolean success;
        final OnPDFCompressedInterface mPDFCompressedInterface;

        CompressPdfAsync(String inputPath, String outputPath, int quality,
                         OnPDFCompressedInterface onPDFCompressedInterface) {
            this.inputPath = inputPath;
            this.outputPath = outputPath;
            this.quality = quality;
            this.mPDFCompressedInterface = onPDFCompressedInterface;
            success = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPDFCompressedInterface.pdfCompressionStarted();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                PdfReader reader = new PdfReader(inputPath);
                compressReader(reader);
                saveReader(reader);
                reader.close();
                success = true;
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
                success = false;
            }
            return null;
        }

        /**
         * Attempt to compress each object in a PdfReader
         *
         * @param reader - PdfReader to have objects compressed
         * @throws IOException
         */
        private void compressReader(PdfReader reader) throws IOException {
            int n = reader.getXrefSize();
            PdfObject object;
            PRStream stream;

            for (int i = 0; i < n; i++) {
                object = reader.getPdfObject(i);
                if (object == null || !object.isStream())
                    continue;
                stream = (PRStream) object;
                compressStream(stream);
            }

            reader.removeUnusedObjects();
        }

        /**
         * If given stream is image compress it
         *
         * @param stream - Steam to be compressed
         * @throws IOException
         */
        private void compressStream(PRStream stream) throws IOException {
            PdfObject pdfSubType = stream.get(PdfName.SUBTYPE);
            System.out.println(stream.type());
            if (pdfSubType != null && pdfSubType.toString().equals(PdfName.IMAGE.toString())) {
                PdfImageObject image = new PdfImageObject(stream);
                byte[] imageBytes = image.getImageAsBytes();
                Bitmap bmp;
                bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                if (bmp == null) return;

                int width = bmp.getWidth();
                int height = bmp.getHeight();

                Bitmap outBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas outCanvas = new Canvas(outBitmap);
                outCanvas.drawBitmap(bmp, 0f, 0f, null);

                ByteArrayOutputStream imgBytes = new ByteArrayOutputStream();
                outBitmap.compress(Bitmap.CompressFormat.JPEG, quality, imgBytes);
                stream.clear();
                stream.setData(imgBytes.toByteArray(), false, PRStream.BEST_COMPRESSION);
                stream.put(PdfName.TYPE, PdfName.XOBJECT);
                stream.put(PdfName.SUBTYPE, PdfName.IMAGE);
                stream.put(PdfName.FILTER, PdfName.DCTDECODE);
                stream.put(PdfName.WIDTH, new PdfNumber(width));
                stream.put(PdfName.HEIGHT, new PdfNumber(height));
                stream.put(PdfName.BITSPERCOMPONENT, new PdfNumber(8));
                stream.put(PdfName.COLORSPACE, PdfName.DEVICERGB);
            }
        }

        /**
         * Save changes to given reader's data to the output path
         *
         * @param reader - changed reader
         * @throws DocumentException
         * @throws IOException
         */
        private void saveReader(PdfReader reader) throws DocumentException, IOException {
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputPath));
            stamper.setFullCompression();
            stamper.close();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mPDFCompressedInterface.pdfCompressionEnded(outputPath, success);
        }
    }

    @Override
    public void pdfCompressionStarted() {

    }

    @Override
    public void pdfCompressionEnded(String path, Boolean success) {
        File file = new File(root.getAbsolutePath() + "/" + tempPDFName);
        File out = new File(root.getAbsolutePath() + "/" + realPDFName);
        file.delete();
        Toast.makeText(getActivity(), "Pdf store at " + out.getAbsolutePath(), Toast.LENGTH_SHORT).show();
    }

}
