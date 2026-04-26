package com.halilintar8.phonechecker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class DisplayTestActivity extends AppCompatActivity {

    public static final String EXTRA_TEST = "test_type";
    public static final String TEST_TOUCH      = "touch";
    public static final String TEST_MULTITOUCH = "multitouch";
    public static final String TEST_BRIGHTNESS = "brightness";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Must set content first before touching insets
        String test = getIntent().getStringExtra(EXTRA_TEST);

        FrameLayout root = new FrameLayout(this);

        if (TEST_TOUCH.equals(test)) {
            setupTouchTest(root);
        } else if (TEST_MULTITOUCH.equals(test)) {
            setupMultiTouchTest(root);
        } else if (TEST_BRIGHTNESS.equals(test)) {
            setupBrightnessTest(root);
        }

        setContentView(root);

        // Full screen AFTER setContentView
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
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
    }

    // ─── TOUCH SENSITIVITY ───────────────────────────────────────────────────

    private void setupTouchTest(FrameLayout root) {
        DrawView drawView = new DrawView(this);
        drawView.setBackgroundColor(Color.BLACK);
        root.addView(drawView, FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);

        TextView label = makeLabel("Draw anywhere to test touch.\nEvery area should respond smoothly.");
        root.addView(label);

        Button btnClear = makeClearButton("Clear");
        btnClear.setOnClickListener(v -> drawView.clear());
        root.addView(btnClear);

        Button btnBack = makeBackButton();
        btnBack.setOnClickListener(v -> finish());
        root.addView(btnBack);
    }

    // ─── MULTI-TOUCH ─────────────────────────────────────────────────────────

    private void setupMultiTouchTest(FrameLayout root) {
        MultiTouchView mtView = new MultiTouchView(this);
        mtView.setBackgroundColor(Color.BLACK);
        root.addView(mtView, FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);

        TextView label = makeLabel("Place multiple fingers.\nEach should show a separate circle.");
        root.addView(label);

        Button btnBack = makeBackButton();
        btnBack.setOnClickListener(v -> finish());
        root.addView(btnBack);
    }

    // ─── BRIGHTNESS / UNIFORMITY ─────────────────────────────────────────────

    private void setupBrightnessTest(FrameLayout root) {
        // Brighten screen to max
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 1.0f;
        getWindow().setAttributes(lp);

        final int[] colors = {
                Color.WHITE,
                Color.parseColor("#AAAAAA"),
                Color.BLACK
        };
        final String[] labels = {
                "WHITE — look for yellow spots or uneven glow",
                "GRAY — look for dim or bright patches",
                "BLACK — look for backlight bleed at edges"
        };
        final int[] index = {0};

        root.setBackgroundColor(colors[0]);

        TextView tvHint = new TextView(this);
        tvHint.setText(labels[0]);
        tvHint.setTextColor(Color.BLACK);
        tvHint.setTextSize(14f);
        tvHint.setPadding(40, 60, 40, 0);
        tvHint.setAlpha(0.5f);
        root.addView(tvHint);

        Button btnBack = makeBackButton();
        root.addView(btnBack);

        root.setOnClickListener(v -> {
            index[0]++;
            if (index[0] >= colors.length) {
                finish();
                return;
            }
            root.setBackgroundColor(colors[index[0]]);
            tvHint.setText(labels[index[0]]);
            tvHint.setTextColor(index[0] == 0 ? Color.BLACK : Color.WHITE);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    // ─── HELPER VIEWS ────────────────────────────────────────────────────────

    private TextView makeLabel(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(13f);
        tv.setAlpha(0.5f);
        tv.setPadding(40, 60, 40, 0);
        return tv;
    }

    private Button makeClearButton(String text) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setBackgroundColor(Color.parseColor("#555555"));
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        lp.gravity = android.view.Gravity.BOTTOM | android.view.Gravity.START;
        lp.leftMargin = 40;
        lp.bottomMargin = 60;
        btn.setLayoutParams(lp);
        return btn;
    }

    private Button makeBackButton() {
        Button btn = new Button(this);
        btn.setText("✕ Done");
        btn.setTextColor(Color.WHITE);
        btn.setBackgroundColor(Color.parseColor("#333333"));
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        lp.gravity = android.view.Gravity.BOTTOM | android.view.Gravity.END;
        lp.rightMargin = 40;
        lp.bottomMargin = 60;
        btn.setLayoutParams(lp);
        return btn;
    }

    // ─── DRAW VIEW (touch trails) ─────────────────────────────────────────────

    static class DrawView extends View {
        private final Paint paint = new Paint();
        private final List<Path> paths = new ArrayList<>();
        private Path currentPath;

        public DrawView(Context ctx) {
            super(ctx);
            paint.setColor(Color.parseColor("#00BCD4"));
            paint.setStrokeWidth(6f);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setAntiAlias(true);
        }

        public void clear() {
            paths.clear();
            currentPath = null;
            invalidate();
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            float x = e.getX(), y = e.getY();
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    currentPath = new Path();
                    currentPath.moveTo(x, y);
                    paths.add(currentPath);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (currentPath != null) currentPath.lineTo(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    currentPath = null;
                    break;
            }
            invalidate();
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            for (Path p : paths) canvas.drawPath(p, paint);
        }
    }

    // ─── MULTI-TOUCH VIEW ─────────────────────────────────────────────────────

    static class MultiTouchView extends View {
        private final Paint paint = new Paint();
        private final Paint textPaint = new Paint();
        private MotionEvent lastEvent;

        private final int[] TOUCH_COLORS = {
                Color.RED, Color.GREEN, Color.BLUE,
                Color.YELLOW, Color.CYAN, Color.MAGENTA,
                Color.WHITE, Color.parseColor("#FF9800"),
                Color.parseColor("#9C27B0"), Color.parseColor("#00BCD4")
        };

        public MultiTouchView(Context ctx) {
            super(ctx);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(36f);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setAntiAlias(true);
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            lastEvent = MotionEvent.obtain(e);
            invalidate();
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (lastEvent == null) return;
            int count = lastEvent.getPointerCount();
            for (int i = 0; i < count; i++) {
                float x = lastEvent.getX(i);
                float y = lastEvent.getY(i);
                paint.setColor(TOUCH_COLORS[i % TOUCH_COLORS.length]);
                paint.setAlpha(180);
                canvas.drawCircle(x, y, 80, paint);
                canvas.drawText(String.valueOf(i + 1), x, y + 13, textPaint);
            }
        }
    }
}