package com.demo.ltud_n10.presentation.ui.dashboard;

public class DashboardMenuItem {
    private String title;
    private int iconRes;
    private int actionId;

    public DashboardMenuItem(String title, int iconRes, int actionId) {
        this.title = title;
        this.iconRes = iconRes;
        this.actionId = actionId;
    }

    public String getTitle() { return title; }
    public int getIconRes() { return iconRes; }
    public int getActionId() { return actionId; }
}
