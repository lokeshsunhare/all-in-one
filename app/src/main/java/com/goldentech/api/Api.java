package com.goldentech.api;

import com.goldentech.model.login_register_res.LoginRegisterRes;
import com.goldentech.model.prfile_detail_res.ProfileDetailRes;
import com.goldentech.model.upload_multiple_file_res.UploadMultipleFileRes;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {

//    @FormUrlEncoded
//    @POST("GetAdmimLogin")
//    Call<LoginRegisterRes> getLogin(@Field("user_id") String user_id,
//                            @Field("password") String pass,
//                            @Field("ip_address") String ip_address
//    );

    @FormUrlEncoded
    @POST("login.php")
    Call<LoginRegisterRes> getUserLogin(@Field("uniqueid") String uniqueid,
                                        @Field("post_time") String post_time,
                                        @Field("mobile") String mobile,
                                        @Field("password") String password,
                                        @Field("latitude") String latitude,
                                        @Field("longitude") String longitude,
                                        @Field("device_id") String device_id,
                                        @Field("release_verson") String release_verson,
                                        @Field("brand") String brand,
                                        @Field("model") String model,
                                        @Field("sdk_number") String sdk_number,
                                        @Field("private_ip_address") String private_ip_address,
                                        @Field("public_ip_address") String public_ip_address
    );

    @FormUrlEncoded
    @POST("register.php")
    Call<LoginRegisterRes> userRegister(@Field("uniqueid") String uniqueid,
                                        @Field("post_time") String post_time,
                                        @Field("name") String name,
                                        @Field("email") String email,
                                        @Field("mobile") String mobile,
                                        @Field("password") String password,
                                        @Field("latitude") String latitude,
                                        @Field("longitude") String longitude,
                                        @Field("device_id") String device_id,
                                        @Field("release_verson") String release_verson,
                                        @Field("brand") String brand,
                                        @Field("model") String model,
                                        @Field("sdk_number") String sdk_number,
                                        @Field("private_ip_address") String private_ip_address,
                                        @Field("public_ip_address") String public_ip_address
    );

    @FormUrlEncoded
    @POST("get_user_detail.php")
    Call<ProfileDetailRes> getUserProfileDetail(@Field("uniqueid") String uniqueid,
                                                @Field("user_id") String user_id

    );


    @Multipart
    @POST("save_category.php")
    Call<UploadMultipleFileRes> uploadMultiple(
            @Part("in_cat_name") RequestBody in_cat_name,
            @Part("in_parent_id") RequestBody in_parent_id,
            @Part("in_created_by") RequestBody in_created_by,
            @Part("in_language_id") RequestBody in_language_id,
            @Part("in_cat_laugane_name") RequestBody in_cat_laugane_name,
            @Part("size") RequestBody size,
            @Part List<MultipartBody.Part> files);

//    @Multipart
//    @POST("saveFileDemo")
//    Call<UploadMultipleFileRes> uploadMultiple(
//            @Part("in_file_size") RequestBody in_file_size,
//            @Part("in_cat_name") RequestBody in_cat_name,
//            @Part("in_parent_id") RequestBody in_parent_id,
//            @Part("in_created_by") RequestBody in_created_by,
//            @Part("in_language_id") RequestBody in_language_id,
//            @Part("in_cat_laugane_name") RequestBody in_cat_laugane_name,
//            @Part("size") RequestBody size,
//            @Part List<MultipartBody.Part> files);


}

