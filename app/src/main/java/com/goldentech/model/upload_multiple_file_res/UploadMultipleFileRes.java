
package com.goldentech.model.upload_multiple_file_res;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UploadMultipleFileRes {

    @SerializedName("response_result")
    @Expose
    private ResponseResult responseResult;

    public ResponseResult getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(ResponseResult responseResult) {
        this.responseResult = responseResult;
    }

}
