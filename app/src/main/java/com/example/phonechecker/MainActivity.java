package com.example.phonechecker;

import android.os.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    private List<CheckItem> checkItems;
    private ChecklistAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkItems = buildChecklist();
        adapter = new ChecklistAdapter(checkItems);

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        Button btnSummary = findViewById(R.id.btnSummary);
        btnSummary.setOnClickListener(v -> showSummary());
    }

    private List<CheckItem> buildChecklist() {
        List<CheckItem> list = new ArrayList<>();

        // Display & Touch (moved to top)
        list.add(new CheckItem("Display", "Dead pixels",         "Tap ▶ to run full-screen color test"));
        list.add(new CheckItem("Display", "Touch sensitivity",   "Tap ▶ to draw and test touch response"));
        list.add(new CheckItem("Display", "Multi-touch",         "Tap ▶ to test multiple fingers at once"));
        list.add(new CheckItem("Display", "Brightness uniform",  "Tap ▶ to check for yellow spots or dim areas"));

        // Physical condition
        list.add(new CheckItem("Physical", "Screen cracks / scratches", "Inspect under bright light"));
        list.add(new CheckItem("Physical", "Body dents / bent frame",   "Feel edges and check corners"));
        list.add(new CheckItem("Physical", "Buttons responsive",        "Press power, volume, fingerprint"));
        list.add(new CheckItem("Physical", "Charging port clean",       "Check for damage or debris"));
        list.add(new CheckItem("Physical", "Water damage sticker",      "Check inside SIM tray — white = OK, pink/red = damaged"));

        // Audio
        list.add(new CheckItem("Audio", "Main speaker",   "Play YouTube at full volume — no crackling"));
        list.add(new CheckItem("Audio", "Earpiece",       "Make a call and check voice clarity"));
        list.add(new CheckItem("Audio", "Microphone",     "Record voice memo, play back"));
        list.add(new CheckItem("Audio", "Headphone jack", "Plug in earphones (if applicable)"));

        // Camera
        list.add(new CheckItem("Camera", "Rear camera",      "Take a photo, check autofocus and clarity"));
        list.add(new CheckItem("Camera", "Front camera",     "Take a selfie, check for blur"));
        list.add(new CheckItem("Camera", "Camera flash",     "Test flash in a dark area"));
        list.add(new CheckItem("Camera", "Video recording",  "Record 30s video, check quality"));

        // Connectivity
        list.add(new CheckItem("Connectivity", "SIM / mobile signal", "Insert your SIM and check bars"));
        list.add(new CheckItem("Connectivity", "Wi-Fi",               "Connect and browse a website"));
        list.add(new CheckItem("Connectivity", "Bluetooth",           "Pair with another device"));
        list.add(new CheckItem("Connectivity", "GPS",                 "Open Maps and wait for location lock"));
        list.add(new CheckItem("Connectivity", "NFC",                 "Enable NFC in settings and verify"));

        // Battery
        list.add(new CheckItem("Battery", "Battery health",  "Check Settings → Battery or use AccuBattery"));
        list.add(new CheckItem("Battery", "No bulging back",  "Press back panel — should be flat"));
        list.add(new CheckItem("Battery", "Charging works",   "Plug in charger and verify charging icon"));

        // Performance
        list.add(new CheckItem("Performance", "No lag",        "Open 5+ apps and switch between them"));
        list.add(new CheckItem("Performance", "No overheating","Play a game or video for 5 minutes"));

        // Software & Security
        list.add(new CheckItem("Software", "IMEI not blacklisted", "Check IMEI on imei.info"));
        list.add(new CheckItem("Software", "Google FRP removed",   "Factory reset — should not ask old account"));
        list.add(new CheckItem("Software", "Carrier unlocked",     "Insert your SIM — should get signal"));
        list.add(new CheckItem("Software", "Android up to date",   "Check Settings → System → Updates"));

        return list;
    }

    private void showSummary() {
        int total = checkItems.size();
        int checked = 0, passed = 0, failed = 0;

        for (CheckItem item : checkItems) {
            if (item.checked) {
                checked++;
                if (item.passed) passed++;
                else failed++;
            }
        }

        int unchecked = total - checked;

        String msg = "✅ Passed: " + passed + "\n"
                + "❌ Failed: " + failed + "\n"
                + "⬜ Not checked: " + unchecked + "\n\n";

        if (failed == 0 && unchecked == 0) {
            msg += "This phone looks great! Safe to buy.";
        } else if (failed >= 3) {
            msg += "Many issues found. Consider walking away.";
        } else if (failed > 0) {
            msg += "Some issues found. Use as bargaining points for a lower price.";
        } else {
            msg += "Still " + unchecked + " items left to check.";
        }

        new android.app.AlertDialog.Builder(this)
                .setTitle("Inspection Summary")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }
}