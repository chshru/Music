package com.chshru.music.data.model;

/**
 * Created by abc on 19-1-23.
 */

public class OnlineList {

    public String id;
    public static final String _id = "id";

    public String name;
    public static final String _name = "name";

    public String logo;
    public static final String _logo = "logo";

    public String songnum;
    public static final String _songnum = "songnum";

    public OnlineList(String id, String name, String logo, String songnum) {
        this.id = id;
        this.name = name;
        this.logo = logo;
        this.songnum = songnum;
    }

    @Override
    public String toString() {
        return "id = " + id + ", "
                + "name = " + name + ", "
                + "logo = " + logo + ", ";
    }
}
