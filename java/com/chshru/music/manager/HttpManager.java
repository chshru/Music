package com.chshru.music.manager;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;

/**
 * Created by abc on 18-10-22.
 */

public class HttpManager {

    private static HttpProxyCacheServer mCacheServer;

    private HttpManager() {

    }

    public synchronized static HttpProxyCacheServer getCacheServer(Context context) {
        if (mCacheServer == null) {
            try {
                mCacheServer = new HttpProxyCacheServer(
                        context.getApplicationContext()
                );
            } catch (Exception e) {
                mCacheServer = null;
            }
        }
        return mCacheServer;
    }

}
