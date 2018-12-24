package com.chshru.music.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by abc on 18-10-22.
 */

public class QQMusicApi {


    private static final String SEARCH_SONG = "http://c.y.qq.com/soso/fcgi-bin/" +
            "client_search_cp?aggr=1&cr=1&flag_qc=0&";

    private static String buildSearchUrl(int page, int num, String word) {
        StringBuilder sb = new StringBuilder(SEARCH_SONG)
                .append("p=").append(page).append("&")
                .append("n=").append(num).append("&")
                .append("w=").append(word);
        System.out.println("chenshanru url = " + sb.toString());
        return sb.toString();
    }


    private static final String GET_KEY_LEFT = "http://c.y.qq.com/base/fcgi-bin/fcg_music_express_mobile3.fcg?" +
            "format=json205361747&platform=yqq&cid=205361747&songmid=";
    private static final String GET_KEY_MIDD = "&filename=C400";
    private static final String GET_KEY_RIGHT = ".m4a&guid=126548448";

    private static String getKeyForSong(String mid) {
        StringBuilder sb = new StringBuilder();
        sb.append(GET_KEY_LEFT);
        sb.append(mid);
        sb.append(GET_KEY_MIDD);
        sb.append(mid);
        sb.append(GET_KEY_RIGHT);
        String result = getJSONFromURL(sb.toString());
        return result;
    }

    private static final String SONG_URL_LEFT = "http://dl.stream.qqmusic.qq.com/";
    private static final String SONG_URL_MIDD = "?fromtag=0&guid=126548448&vkey=";

    public static String buildSongUrl(String url) {
        String result = getKeyForSong(url);
        JSONObject obj = JSON.parseObject(result);
        String key = obj.getJSONObject("data")
                .getJSONArray("items")
                .getJSONObject(0)
                .getString("vkey");
        String file = obj.getJSONObject("data")
                .getJSONArray("items")
                .getJSONObject(0)
                .getString("filename");
        String mid = obj.getJSONObject("data")
                .getJSONArray("items")
                .getJSONObject(0)
                .getString("songmid");
        StringBuilder sb = new StringBuilder();
        sb.append(SONG_URL_LEFT);
        sb.append(file);
        sb.append(SONG_URL_MIDD);
        sb.append(key);
        return sb.toString();
    }

    final static private String ALBUM_URL_LEFT = "http://y.gtimg.cn/music/photo_new/T002R300x300M000";
    final static private String ALBUM_URL_RIGHT = ".jpg";

    private static String buildAlbumUrl(String url) {
        StringBuilder sb = new StringBuilder();
        sb.append(ALBUM_URL_LEFT);
        sb.append(url);
        sb.append(ALBUM_URL_RIGHT);
        return sb.toString();
    }


    public static List<Song> getSongFromResult(String res) {
        JSONObject obj = JSON.parseObject(res);
        List<Song> ans = new LinkedList<>();
        JSONArray musicArray = obj
                .getJSONObject("data")
                .getJSONObject("song")
                .getJSONArray("list");

        for (int i = 0; i < musicArray.size(); i++) {
            JSONObject j = musicArray.getJSONObject(i);
            Song m = new Song(
                    j.getInteger("songid"),
                    Song.TYPE_NET,
                    buildAlbumUrl(j.getString("albummid")),
                    j.getString("albumname"),
                    j.getString("songmid"),
                    j.getString("songname"),
                    j.getJSONArray("singer").getJSONObject(0).getString("name"),
                    null
            );
            ans.add(m);
        }
        return ans;
    }

    private static String getJSONFromURL(String str) {
        HttpURLConnection conn = null;
        StringBuilder sb = new StringBuilder();
        try {

            URL url = new URL(str);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(60000);
            conn.connect();

            if (conn.getResponseCode() == 200) {

                InputStream is = conn.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                br.close();
                isr.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return sb.toString();
    }

    public static void query(int page, int num, String word, QueryHandler handler) {

        String url = getJSONFromURL(buildSearchUrl(page, num, word));
        StringBuilder sb = new StringBuilder(url);
        try {
            sb.delete(0, 9).delete(sb.length() - 1, sb.length());
        } catch (Exception e) {
            sb = new StringBuilder();
        }
        if (handler != null) {
            handler.exeComplete(sb.toString());
        }
    }
}
