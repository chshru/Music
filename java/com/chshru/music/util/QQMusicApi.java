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


    private static final String SEARCH_SONG = "https://c.y.qq.com/soso/fcgi-bin/" +
            "client_search_cp?aggr=1&cr=1&flag_qc=0&";

    private static String buildSearchUrl(int page, int num, String word) {

        StringBuilder sb = new StringBuilder(SEARCH_SONG)
                .append("p=").append(page).append("&")
                .append("n=").append(num).append("&")
                .append("w=").append(word);
        return sb.toString();
    }


    private static final String SONG_URL_LEFT = "http://ws.stream.qqmusic.qq.com/C100";
    private static final String SONG_URL_RIGHT = ".m4a?fromtag=0&guid=126548448";

    public static String buildSongUrl(String url) {
        StringBuilder sb = new StringBuilder();
        sb.append(SONG_URL_LEFT);
        sb.append(url);
        sb.append(SONG_URL_RIGHT);
        return sb.toString();
    }


    final static private String ALBUM_URL_LEFT = "https://y.gtimg.cn/music/photo_new/T002R300x300M000";
    final static private String ALBUM_URL_RIGHT = ".jpg";

    public static String buildAlbumUrl(String url) {
        StringBuilder sb = new StringBuilder();
        sb.append(ALBUM_URL_LEFT);
        sb.append(url);
        sb.append(ALBUM_URL_RIGHT);
        return sb.toString();
    }


    public static String getFirstFromResult(String result) {
        JSONObject obj = JSON.parseObject(result);
        String ans = obj
                .getJSONObject("data")
                .getJSONObject("song")
                .getJSONArray("list")
                .getJSONObject(0)
                .getString("songmid");
        return ans;
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
                    j.getString("songmid"),
                    j.getString("songname"),
                    j.getJSONArray("singer").getJSONObject(0).getString("name"),
                    null,
                    j.getString("albummid")
            );
            ans.add(m);
        }
        return ans;
    }

    public static void query(int page, int num, String word, QueryHandler handler) {

        HttpURLConnection conn = null;
        StringBuilder sb = new StringBuilder();
        try {

            URL url = new URL(buildSearchUrl(page, num, word));
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
