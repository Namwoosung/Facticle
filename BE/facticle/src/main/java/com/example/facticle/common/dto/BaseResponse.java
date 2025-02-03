package com.example.facticle.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
사용 시 아래와 같이 사용
Map<String, Object> data = new HashMap<>();
data.put("code", 200);
BaseResponse response = BaseResponse.success(data, "Logout successful.");
 */

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse {
    private boolean success;
    private Object data;
    private String message;

    public static BaseResponse success(Object data, String message){
        return new BaseResponse(true, data, message);
    }

    public static BaseResponse failure(Object data, String message){
        return new BaseResponse(false, data, message);
    }
}

