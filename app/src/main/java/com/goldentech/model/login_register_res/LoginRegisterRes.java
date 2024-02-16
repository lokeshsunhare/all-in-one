
package com.goldentech.model.login_register_res;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoginRegisterRes {

    @SerializedName("response_result")
    @Expose
    private List<ResponseResult> result = null;

    public List<ResponseResult> getResult() {
        return result;
    }

    public void setResult(List<ResponseResult> result) {
        this.result = result;
    }

}
