package com.example.phonechecker;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DeadPixelActivity extends AppCompatActivity {

    private final int[] COLORS = {
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.WHITE,
            Color.BLACK,
            Color.YELLOW,
            Color.CYAN,
            Color.MAGENTA
    };

    private final String[] COLOR_NAMES = {
            "RED", "GREEN", "BLUE", "WHITE", "BLACK", "YELLOW", "CYAN", "MAGENTA"
    };

    private int currentIndex = 0;
    private View rootView;
    private TextView tvInstruction;
    private TextView tvColorName;
    private TextView tvCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set content FIRST
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_dead_pixel);

        rootView      = findViewById(R.id.deadPixelRoot);
        tvInstruction = findViewById(R.id.tvInstruction);
        tvColorName   = findViewById(R.id.tvColorName);
        tvCounter     = findViewById(R.id.tvCounter);

        // Full screen AFTER setContentView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController c = getWindow().getInsetsController();
            if (c != null) {
                c.hide(android.view.WindowInsets.Type.statusBars()
                        | android.view.WindowInsets.Type.navigationBars());
                c.setSystemBarsBehavior(
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }

        applyColor(currentIndex);

        rootView.setOnClickListener(v -> {
            currentIndex++;
            if (currentIndex >= COLORS.length) {
                finish();
            } else {
                applyColor(currentIndex);
            }
        });
    }

    private void applyColor(int index) {
        int color = COLORS[index];
        rootView.setBackgroundColor(color);

        // Use contrasting text color so instruction is always readable
        boolean isDark = (color == Color.BLACK || color == Color.RED
                || color == Color.BLUE  || color == Color.MAGENTA);
        int textColor = isDark ? Color.WHITE : Color.BLACK;

        tvColorName.setTextColor(textColor);
        tvInstruction.setTextColor(textColor);
        tvCounter.setTextColor(textColor);

        tvColorName.setText(COLOR_NAMES[index]);
        tvCounter.setText((index + 1) + " / " + COLORS.length);
        tvInstruction.setText("Look for any dots that don't match this color.\nTap to continue.");
    }
}