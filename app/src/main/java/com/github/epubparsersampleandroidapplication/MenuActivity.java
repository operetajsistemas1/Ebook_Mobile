package com.github.epubparsersampleandroidapplication;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.Manifest.permission.*;

import com.github.mertakdut.Reader;
import com.github.mertakdut.exception.ReadingException;

import android.app.Activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        verifyStoragePermissions(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ((GridView) findViewById(R.id.grid_book_info)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String clickedItemFilePath = ((BookInfo) adapterView.getAdapter().getItem(i)).getFilePath();
                askForWidgetToUse(clickedItemFilePath);
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        new ListBookInfoTask().execute();
    }

    private class ListBookInfoTask extends AsyncTask<Object, Object, List<BookInfo>> {

        private Exception occuredException;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<BookInfo> doInBackground(Object... params) {
            List<BookInfo> bookInfoList = searchForPdfFiles();

            Reader reader = new Reader();
            for (BookInfo bookInfo : bookInfoList) {
                try {
                    reader.setInfoContent(bookInfo.getFilePath());

                    String title = reader.getInfoPackage().getMetadata().getTitle();
                    if (title != null && !title.equals("")) {
                        bookInfo.setTitle(reader.getInfoPackage().getMetadata().getTitle());
                    } else { // If title doesn't exist, use fileName instead.
                        int dotIndex = bookInfo.getTitle().lastIndexOf('.');
                        bookInfo.setTitle(bookInfo.getTitle().substring(0, dotIndex));
                    }

                    bookInfo.setCoverImage(reader.getCoverImage());
                } catch (ReadingException e) {
                    occuredException = e;
                    e.printStackTrace();
                }
            }

            return bookInfoList;
        }

        @Override
        protected void onPostExecute(List<BookInfo> bookInfoList) {
            super.onPostExecute(bookInfoList);
            progressBar.setVisibility(View.GONE);

            if (bookInfoList != null) {
                BookInfoGridAdapter adapter = new BookInfoGridAdapter(MenuActivity.this, bookInfoList);
                ((GridView) findViewById(R.id.grid_book_info)).setAdapter(adapter);
            }

            if (occuredException != null) {
                Toast.makeText(MenuActivity.this, occuredException.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private List<BookInfo> searchForPdfFiles() {
        Log.i("searchForPdfFiles", "searchForPdfFiles");
        boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        Log.i("isdpressent ", String.valueOf(isSDPresent));
        List<BookInfo> bookInfoList = null;

        if (isSDPresent) {
            bookInfoList = new ArrayList<>();

            File directory = new File(Environment.getExternalStorageDirectory().toString() + "/Download/");
            File[] files = directory.listFiles();
            if (files == null) {
                Log.i("searchForPdfFiles", "files are null");
            }
            Log.i("searchForPdfFiles", (Environment.getExternalStorageDirectory().toString() + "/Download/"));
            //   File sampleFile = getFileFromAssets("pg28885-images_new.epub");
            //   files.add(0, sampleFile);
            Log.i("searchForPdfFiles", String.valueOf(files.length));
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().substring(files[i].getName().length() - 5,
                        files[i].getName().length()).equals(".epub")) {
                    Log.i("searchForPdfFiles", files[i].getAbsolutePath());
                    BookInfo bookInfo = new BookInfo();

                    bookInfo.setTitle(files[i].getName());
                    bookInfo.setFilePath(files[i].getPath());

                    bookInfoList.add(bookInfo);
                }
            }
        }

        return bookInfoList;
    }

    public File getFileFromAssets(String fileName) {

        File file = new File(getCacheDir() + "/" + fileName);

        if (!file.exists()) try {

            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(buffer);
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return file;
    }

    private List<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<>();
        File[] files = parentDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    inFiles.addAll(getListFiles(file));
                } else {
                    if (file.getName().endsWith(".epub")) {
                        inFiles.add(file);
                    }
                }
            }
        }
        return inFiles;
    }

    private void askForWidgetToUse(final String filePath) {

        final Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        intent.putExtra("filePath", filePath);

        new AlertDialog.Builder(MenuActivity.this)
                .setTitle("Pick your widget")
                .setMessage("Textview or WebView?")
                .setPositiveButton("TextView", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        intent.putExtra("isWebView", false);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("WebView", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        intent.putExtra("isWebView", true);
                        startActivity(intent);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

}
