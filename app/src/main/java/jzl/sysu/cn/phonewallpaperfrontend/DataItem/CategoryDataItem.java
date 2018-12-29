package jzl.sysu.cn.phonewallpaperfrontend.DataItem;

public class CategoryDataItem {
    private String name;
    private String background;

    public CategoryDataItem(String name, String background) {
        this.name = name;
        this.background = background;
    }

    public String getName() {
        return name;
    }

    public String getBackground() {
        return background;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBackground(String background) {
        this.background = background;
    }
}
