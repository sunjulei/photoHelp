package com.sunlee.photoaide;

/**
 * Created by Administrator on 2017/3/10.
 */

public class IsSelectedDirestory {

    public IsSelectedDirestory() {
    }

    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "IsSelectedDirestory{" +
                "isSelected=" + isSelected +
                '}';
    }
}
