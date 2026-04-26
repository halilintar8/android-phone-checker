package com.halilintar8.phonechecker;

public class CheckItem {
    public String category;   // e.g. "Display"
    public String title;      // e.g. "Touch screen"
    public String hint;       // e.g. "Tap all corners to test"
    public boolean checked;
    public boolean passed;    // true = OK, false = Issue

    public CheckItem(String category, String title, String hint) {
        this.category = category;
        this.title = title;
        this.hint = hint;
        this.checked = false;
        this.passed = false;
    }
}