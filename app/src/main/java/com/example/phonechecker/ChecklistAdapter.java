package com.example.phonechecker;

import android.graphics.Color;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.content.Intent;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ViewHolder> {

    private final List<CheckItem> items;

    public ChecklistAdapter(List<CheckItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_check, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        CheckItem item = items.get(pos);

        h.title.setText(item.title);
        h.hint.setText(item.hint);
        h.category.setText(item.category);

        // Status indicator color
        if (!item.checked) {
            h.statusDot.setBackgroundColor(Color.GRAY);
            h.btnPass.setEnabled(true);
            h.btnFail.setEnabled(true);
        } else if (item.passed) {
            h.statusDot.setBackgroundColor(Color.parseColor("#4CAF50")); // green
            h.btnPass.setEnabled(false);
            h.btnFail.setEnabled(true);
        } else {
            h.statusDot.setBackgroundColor(Color.parseColor("#F44336")); // red
            h.btnPass.setEnabled(true);
            h.btnFail.setEnabled(false);
        }

        h.btnPass.setOnClickListener(v -> {
            item.checked = true;
            item.passed = true;
            notifyItemChanged(pos);
        });

        h.btnFail.setOnClickListener(v -> {
            item.checked = true;
            item.passed = false;
            notifyItemChanged(pos);
        });
        // Show "Run Test" button only for Dead Pixels
        Button btnTest = h.btnTest;
        String testType = null;

        switch (item.title) {
            case "Touch sensitivity":  testType = DisplayTestActivity.TEST_TOUCH;       break;
            case "Multi-touch":        testType = DisplayTestActivity.TEST_MULTITOUCH;  break;
            case "Brightness uniform": testType = DisplayTestActivity.TEST_BRIGHTNESS;  break;
            case "Dead pixels":        break; // handled separately below
        }

        final String finalTestType = testType;

        if (item.title.equals("Dead pixels")) {
            btnTest.setVisibility(View.VISIBLE);
            btnTest.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), DeadPixelActivity.class);
                v.getContext().startActivity(intent);
            });
        } else if (finalTestType != null) {
            btnTest.setVisibility(View.VISIBLE);
            btnTest.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), DisplayTestActivity.class);
                intent.putExtra(DisplayTestActivity.EXTRA_TEST, finalTestType);
                v.getContext().startActivity(intent);
            });
        } else {
            btnTest.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        Button btnTest;
        TextView category, title, hint;
        Button btnPass, btnFail;
        View statusDot;

        ViewHolder(View v) {
            super(v);
            category  = v.findViewById(R.id.tvCategory);
            title     = v.findViewById(R.id.tvTitle);
            hint      = v.findViewById(R.id.tvHint);
            btnPass   = v.findViewById(R.id.btnPass);
            btnFail   = v.findViewById(R.id.btnFail);
            statusDot = v.findViewById(R.id.statusDot);
            btnTest   = v.findViewById(R.id.btnTest);  // ← assign to the field, no "Button" keyword here
        }
    }
}