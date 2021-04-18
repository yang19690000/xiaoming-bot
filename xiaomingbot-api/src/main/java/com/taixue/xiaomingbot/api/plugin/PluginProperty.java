package com.taixue.xiaomingbot.api.plugin;


import java.util.List;

public class PluginProperty {
    private String name;
    private String version;
    private String author;
    private List<String> authors;
    private List<String> fronts;
    private String main;

    public List<String> getFronts() {
        return fronts;
    }

    public void setFronts(List<String> fronts) {
        this.fronts = fronts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }
}
