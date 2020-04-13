package com.example.notes2;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.documentfile.provider.DocumentFile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.List;

public class ExportTask extends AsyncTask<ExportSettings, Integer, Long> {
    private String TAG = getClass().getSimpleName();
    // Weak references will still allow the Activity to be garbage-collected
    private final WeakReference<Activity> weakActivity;

    ExportTask(Activity myActivity) {
        weakActivity = new WeakReference<>(myActivity);
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Long doInBackground(ExportSettings... exportSettings) {
        if (exportSettings.length == 1 && exportToFile(exportSettings[0]) && exportSettings[0].isUrlExportEnabled) {
            exportToUrl(exportSettings[0]);
        }
        // Return value is not yet used
        return null;
    }

    private boolean exportToFile(ExportSettings exportSettings) {
        List<Note> notes = MainActivity.database.getNoteDao().getAll();
        Gson gson = new Gson();
        Type type = new TypeToken<List<Note>>(){}.getType();
        String json = gson.toJson(notes, type);

        try {
            Uri uri = Uri.parse(exportSettings.path);
            DocumentFile pickedDir = DocumentFile.fromTreeUri(weakActivity.get(), uri);
            DocumentFile documentFile = pickedDir.findFile(exportSettings.file);
            if (documentFile == null) {
                // Check is needed, otherwise a new "file(1)" will be created
                documentFile = pickedDir.createFile("text/plain", exportSettings.file);
            }
            OutputStream out = weakActivity.get().getContentResolver().openOutputStream(documentFile.getUri());
            out.write(json.getBytes());
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean exportToUrl(ExportSettings exportSettings) {
        MultipartUtility multipartUtility = new MultipartUtility(weakActivity, exportSettings.url, "UTF-8");
        multipartUtility.addFilePart("file", exportSettings.path, exportSettings.file);
        try {
            multipartUtility.finish();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
    }

    @Override
    protected void onPostExecute(Long result) {
    }
}
