package com.app.project.silverbars;

/**
 * Created by andre_000 on 6/29/2016.
 */
public class SpotifyAdapter {

    private String href;
    private String items[];

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String[] getItems() {
        return items;
    }

    public void setItems(String[] items) {
        this.items = items;
    }

    public SpotifyAdapter() {
    }

    public SpotifyAdapter(String href, String[] items) {
        this.href = href;
        this.items = items;
    }
}
