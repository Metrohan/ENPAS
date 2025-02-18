package com.haeydra.enpas.project.Domain;

public class TrendSDomain {
    private String title;
    private String subtite;
    private String picAddress;

    public TrendSDomain(String title, String subtite, String picAddress) {
        this.title = title;
        this.subtite = subtite;
        this.picAddress = picAddress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtite() {
        return subtite;
    }

    public void setSubtite(String subtite) {
        this.subtite = subtite;
    }

    public String getPicAddress() {
        return picAddress;
    }

    public void setPicAddress(String picAddress) {
        this.picAddress = picAddress;
    }
}
