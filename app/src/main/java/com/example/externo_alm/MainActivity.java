package com.example.externo_alm;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends Activity {
    private EditText editText;
    private TextView textView;
    private Button saveButton;
    private Button readButton;


    private static final int REQUEST_ID_READ_PERMISSION = 100;
    private static final int REQUEST_ID_WRITE_PERMISSION = 200;

    private final String fileName = "note.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);
        textView = (TextView) findViewById(R.id.textView);

        saveButton = (Button) findViewById(R.id.button_save);
        readButton = (Button) findViewById(R.id.button_read);

        saveButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                askPermissionAndWriteFile();
            }

        });


        readButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                askPermissionAndReadFile();
            }

        });

    }

    private void askPermissionAndWriteFile() {
        boolean canWrite = this.askPermission(REQUEST_ID_WRITE_PERMISSION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //
        if (canWrite) {
            this.writeFile();
        }
    }

    private void askPermissionAndReadFile() {
        boolean canRead = this.askPermission(REQUEST_ID_READ_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        //
        if (canRead) {
            this.readFile();
        }
    }


    private boolean askPermission(int requestId, String permissionName) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            int permission = ActivityCompat.checkSelfPermission(this, permissionName);


            if (permission != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(
                        new String[]{permissionName},
                        requestId
                );
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        // Note: If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0) {
            switch (requestCode) {
                case REQUEST_ID_READ_PERMISSION: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        readFile();
                    }
                }
                case REQUEST_ID_WRITE_PERMISSION: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        writeFile();
                    }
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Permission Cancelled!", Toast.LENGTH_SHORT).show();
        }
    }


    private void writeFile() {

        File extStore = Environment.getExternalStorageDirectory();
        // ==> /storage/emulated/0/note.txt
        String path = extStore.getAbsolutePath() + "/" + fileName;
        Log.i("ExternalStorageDemo", "Save to: " + path);

        String data = editText.getText().toString();

        try {
            File myFile = new File(path);
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);
            myOutWriter.close();
            fOut.close();

            Toast.makeText(getApplicationContext(), fileName + " saved", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readFile() {

        File extStore = Environment.getExternalStorageDirectory();
        // ==> /storage/emulated/0/note.txt
        String path = extStore.getAbsolutePath() + "/" + fileName;
        Log.i("ExternalStorageDemo", "Read file: " + path);

        String s = "";
        String fileContent = "";
        try {
            File myFile = new File(path);
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(
                    new InputStreamReader(fIn));

            while ((s = myReader.readLine()) != null) {
                fileContent += s + "\n";
            }
            myReader.close();

            this.textView.setText(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), fileContent, Toast.LENGTH_LONG).show();
    }



}