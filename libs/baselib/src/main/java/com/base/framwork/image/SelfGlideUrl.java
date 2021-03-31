package com.base.framwork.image;

import android.net.Uri;
import android.text.TextUtils;

import com.bumptech.glide.load.model.GlideUrl;

import java.io.Serializable;

/**
 * 由于我们APP，图片的域名是动态的
 * <p>
 * 所以把图片url的路径当做缓存的key，防止图片重复加载
 */
public class SelfGlideUrl extends GlideUrl implements Serializable {
    public SelfGlideUrl(String url) {
        super(url);
    }

    @Override
    public String getCacheKey() {
        String key = super.getCacheKey();
        if (!TextUtils.isEmpty(key)) {
            Uri uri = Uri.parse(key);
            String path = uri.getPath();
            String query = uri.getQuery();
            String result = path + (query != null ? ("?" + query) : "");
            return result;
        }
        return key;
    }
}
