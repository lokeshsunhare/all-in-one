package com.goldentech.common;

import com.goldentech.api.Api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AllApiActivity {

    public static String MAIN_BASE_URL = "http://192.168.42.224:8080/";
//    public static String MAIN_BASE_URL = "http://192.168.42.224:80/";

    private static final String BASE_URL = MAIN_BASE_URL + "/allinone/";
    static final String kept = BASE_URL.toString().substring(0, BASE_URL.toString().lastIndexOf("/"));
    public static final String PUBLIC_URL = kept;

    private static AllApiActivity mInstance;
    private static Retrofit retrofit;

    private AllApiActivity() {

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .build();
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized AllApiActivity getInstance() {
        if (mInstance == null) {
            mInstance = new AllApiActivity();
        }
        return mInstance;
    }

    public Api getApi() {
        return retrofit.create(Api.class);
    }

    public static String GET_SERVER_STATUS_URL = BASE_URL + "getServerStatus";
    public static String GET_CATEGORY_PARENT_URL = BASE_URL + "get_masters_value.php";
    public static String SAVE_CATEGORY_URL = BASE_URL + "save_category.php";

}

