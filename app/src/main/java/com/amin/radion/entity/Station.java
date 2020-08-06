package com.amin.radion.entity;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Objects;

public class Station {
    private String name;
    private String logoUrl;
    private String url;
    private String language;
    private String tag;
    private String codec;
    private String bitrate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public String getBitrate() {
        return bitrate;
    }

    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" --- ")
                .append(tag).append(" --- ")
                .append(language).append(" --- ")
                .append(codec).append(" -- ").append(bitrate);
        return sb.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(name, station.name) &&
                Objects.equals(logoUrl, station.logoUrl) &&
                Objects.equals(url, station.url) &&
                Objects.equals(language, station.language) &&
                Objects.equals(tag, station.tag) &&
                Objects.equals(codec, station.codec) &&
                Objects.equals(bitrate, station.bitrate);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(name, logoUrl, url, language, tag, codec, bitrate);

    }


}
