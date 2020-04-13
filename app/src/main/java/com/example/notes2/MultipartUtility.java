package com.example.notes2;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MultipartUtility {
    private String TAG = getClass().getSimpleName();
    private String charset;
    private String boundary;
    private HttpURLConnection httpUrlConnection;
    private DataOutputStream dataOutputStream;
    private PrintWriter printWriter;
    private String lineEnd = "\r\n";
    private String twoHyphens = "--";
    private WeakReference<Activity> weakActivity;

    public MultipartUtility(WeakReference<Activity> myActivity, String requestUrl, String charset) {
        weakActivity = myActivity;
        this.charset = charset;
        boundary = "***" + System.currentTimeMillis() + "***";

        try {
            URL url = new URL(requestUrl);
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            httpUrlConnection.setRequestProperty("User-Agent", "Notes2");
            httpUrlConnection.setRequestProperty("Test", "Bonjour");

            dataOutputStream = new DataOutputStream(httpUrlConnection.getOutputStream());
            // PrintWriter is for textual data (e.g. fields), DataOutputStream.writeBytes() is for binary data (e.g. file)
            printWriter = new PrintWriter(new OutputStreamWriter(dataOutputStream, charset));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addFormField(String name, String value) {
        printWriter.append(twoHyphens).append(boundary).append(lineEnd);
        printWriter.append("Content-Disposition: form-data; name=\"").append(name).append("\"").append(lineEnd);
        printWriter.append("Content-Type: text/plain; charset=").append(charset).append(lineEnd);
        printWriter.append(lineEnd);
        printWriter.append(value).append(lineEnd);
        printWriter.flush();
    }

    public void addFilePart(String fieldName, String pathName, String fileName) {
        printWriter.append(twoHyphens).append(boundary).append(lineEnd);
        printWriter.append("Content-Disposition: form-data; name=\"").append(fieldName).append("\"; filename=\"").append(fileName).append("\"").append(lineEnd);
        printWriter.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(lineEnd);
        printWriter.append("Content-Transfer-Encoding: binary").append(lineEnd);
        printWriter.append(lineEnd);
        printWriter.flush();

        // Original example using "new FileInputStream(new File(pathToOurFile))", but will not work as is:
        // https://stackoverflow.com/questions/11044291/using-httppost-to-upload-file-android
        // https://stackoverflow.com/questions/2017414/post-multipart-request-with-android-sdk
        Uri uri = Uri.parse(pathName);
        DocumentFile pickedDir = DocumentFile.fromTreeUri(weakActivity.get(), uri);
        DocumentFile documentFile = pickedDir.findFile(fileName);

        try {
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            InputStream fileInputStream = weakActivity.get().getContentResolver().openInputStream(documentFile.getUri());
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dataOutputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            dataOutputStream.flush();
            fileInputStream.close();
            printWriter.append(lineEnd);
            printWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String finish() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        printWriter.append(twoHyphens).append(boundary).append(twoHyphens).append(lineEnd);
        printWriter.flush();
        printWriter.close();
        dataOutputStream.flush();
        dataOutputStream.close();

        int responseCode = httpUrlConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream responseStream = new BufferedInputStream(httpUrlConnection.getInputStream());
            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
            String line = "";
            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();
            responseStream.close();
            httpUrlConnection.disconnect();

            String response = stringBuilder.toString();
            Log.d(TAG, Integer.toString(responseCode));
            Log.d(TAG, response);
            return response;
        } else {
            httpUrlConnection.disconnect();
            throw new IOException("Server returned NOK status: " + responseCode);
        }
    }
}
