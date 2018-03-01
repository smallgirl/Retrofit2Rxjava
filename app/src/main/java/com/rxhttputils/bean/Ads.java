package com.rxhttputils.bean;

import java.io.Serializable;

/**
 * Created by wanghaitao1 on 2017/7/19.
 */

public class Ads implements Serializable {
    private boolean isvalid;

    public String getAdvertImage() {
        return advertImage;
    }

    public void setAdvertImage(String advertImage) {
        this.advertImage = advertImage;
    }

    public boolean isvalid() {
        return isvalid;
    }

    public void setIsvalid(boolean isvalid) {
        this.isvalid = isvalid;
    }

    public String getAdvertUrl() {
        return advertUrl;
    }

    public void setAdvertUrl(String advertUrl) {
        this.advertUrl = advertUrl;
    }

    private String advertImage;
    private String advertUrl;

    @Override
    public String toString() {
        return "Ads{" +
                "isvalid=" + isvalid +
                ", advertImage='" + advertImage + '\'' +
                ", advertUrl='" + advertUrl + '\'' +
                '}';
    }
}
