package jzl.sysu.cn.phonewallpaperfrontend.Model;

public class Category {
    private String name;
    private String background;

    public Category(String name, String background) {
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
