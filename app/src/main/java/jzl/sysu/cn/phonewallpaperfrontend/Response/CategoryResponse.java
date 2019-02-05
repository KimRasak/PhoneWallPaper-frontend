package jzl.sysu.cn.phonewallpaperfrontend.Response;

import java.util.List;

import jzl.sysu.cn.phonewallpaperfrontend.Model.Category;

public class CategoryResponse {
    private String hostName;
    private List<Category> categories;

    public CategoryResponse(String hostName, List<Category> categories) {
        this.hostName = hostName;
        this.categories = categories;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
