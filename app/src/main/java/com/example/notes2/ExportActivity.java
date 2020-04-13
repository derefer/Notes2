package com.example.notes2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

public class ExportActivity extends AppCompatActivity {
    private int REQUEST_CODE_SELECT_DIRECTORY = 100;

    private Button browseButton;
    private Button exportButton;
    private Button cancelButton;
    private CheckBox urlCheckBox;
    private EditText pathEditText;
    private EditText fileEditText;
    private EditText urlEditText;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        initWidgets();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == REQUEST_CODE_SELECT_DIRECTORY && resultCode == RESULT_OK) {
            // The "Storage Access Framework" way, not File object anymore:
            // https://stackoverflow.com/questions/26744842/how-to-use-the-new-sd-card-access-api-presented-for-android-5-0-lollipop
            Uri treeUri = resultData.getData();
            DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);

            // Persist access permissions
            final int takeFlags = resultData.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(treeUri, takeFlags);

            pathEditText.setText(pickedDir.getUri().toString());
        }
    }

    private void initWidgets() {
        browseButton = findViewById(R.id.browseButton);
        exportButton = findViewById(R.id.exportButton);
        cancelButton = findViewById(R.id.cancelButton);
        urlCheckBox = findViewById(R.id.urlCheckBox);
        pathEditText = findViewById(R.id.pathEditText);
        fileEditText = findViewById(R.id.fileEditText);
        urlEditText = findViewById(R.id.urlEditText);

        sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notes2", Context.MODE_PRIVATE);
        boolean isUrlExportEnabled = sharedPreferences.getBoolean("isUrlExportEnabled", false);
        String path = sharedPreferences.getString("path", "");
        String file = sharedPreferences.getString("file", "");
        String url = sharedPreferences.getString("url", "");

        pathEditText.setText(path);
        fileEditText.setText(file);
        urlEditText.setText(url);

        if (isUrlExportEnabled) {
            urlCheckBox.setActivated(true);
            urlCheckBox.setChecked(true);
        } else {
            urlEditText.setEnabled(false);
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        urlCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                urlEditText.setEnabled(isChecked);
                if (!isChecked) {
                    urlEditText.setInputType(InputType.TYPE_NULL);
                } else {
                    urlEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }
        });

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (areExportSettingsValid()) {
                    saveExportSettings();
                    exportDatabase();
                    Toast.makeText(ExportActivity.this, "Exporting database...", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });

        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, REQUEST_CODE_SELECT_DIRECTORY);
            }
        });
    }

    private void saveExportSettings() {
        sharedPreferences.edit().putBoolean("isUrlExportEnabled", urlCheckBox.isChecked()).apply();
        sharedPreferences.edit().putString("path", pathEditText.getText().toString()).apply();
        sharedPreferences.edit().putString("file", fileEditText.getText().toString()).apply();
        sharedPreferences.edit().putString("url", urlEditText.getText().toString()).apply();
    }

    private boolean areExportSettingsValid() {
        if (pathEditText.getText().toString().isEmpty() || fileEditText.getText().toString().isEmpty()) {
            Toast.makeText(ExportActivity.this, "Invalid path/file settings", Toast.LENGTH_LONG).show();
            return false;
        }

        if (urlCheckBox.isChecked()) {
            if (urlEditText.getText().toString().isEmpty()) {
                Toast.makeText(ExportActivity.this, "Invalid URL settings", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }

    private ExportSettings getExportSettings() {
        ExportSettings exportSettings = new ExportSettings();
        exportSettings.path = pathEditText.getText().toString();
        exportSettings.file = fileEditText.getText().toString();
        exportSettings.isUrlExportEnabled = urlCheckBox.isChecked();
        exportSettings.url = exportSettings.isUrlExportEnabled ? urlEditText.getText().toString() : null;

        return exportSettings;
    }

    private void exportDatabase() {
        ExportSettings exportSettings = getExportSettings();
        new ExportTask(this).execute(exportSettings);
    }
}
