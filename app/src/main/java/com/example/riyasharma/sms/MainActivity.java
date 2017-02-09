package com.example.riyasharma.sms;

import android.Manifest;
import android.content.ContentResolver;

import android.content.Context;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;



public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {

    private EditText searchView;
    private ListView messageList;
    private TodoCursorAdapter todoAdapter = null;
    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;
    private Cursor oldCursor = null;
    private Cursor cursor = null;
    private String t_id = null;
    protected static final int REQUEST_CODE_RESOLUTION = 1;
    private GoogleApiClient mGoogleApiClient;
    private static DriveId Id;
    private static final String TAG = "BackupToDrive";
    protected static final File DATABASE_DIRECTORY =
            new File(Environment.getExternalStorageDirectory(),"MyDirectory");

       protected static final File IMPORT_FILE =
            new File(DATABASE_DIRECTORY,"SMS.db");

    public static final String PACKAGE_NAME = "com.example.riyasharma.sms";
    public static final String DATABASE_NAME = "SMS.db";
    public static final String DATABASE_TABLE = "SMS";


    private static final File DATA_DIRECTORY_DATABASE =
            new File(Environment.getDataDirectory() +
                    "/data/" + PACKAGE_NAME +
                    "/databases/" + DATABASE_NAME );



    File dbFile = DATA_DIRECTORY_DATABASE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messageList = (ListView) findViewById(R.id.messageList);
        searchView = (EditText) findViewById(R.id.search_area);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        }

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View container, int position, long id) {
                searchView.clearFocus();
                searchView.setVisibility(View.GONE);
                LinearLayout linearLayoutParent = (LinearLayout) container;

                TextView threadID = (TextView) linearLayoutParent.getChildAt(2);
                t_id = threadID.getText().toString();

                Cursor managedCursor = getContentResolver().query(Uri.parse("content://sms"), null, "thread_id=" + t_id, null, "date ASC");
                if (todoAdapter != null) {
                    oldCursor = todoAdapter.swapCursor(managedCursor);
                }
            }
        };
        messageList.setOnItemClickListener(itemClickListener);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
                searchView.setCursorVisible(true);
            }

        });

        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    // Hide soft keyboard.
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                    searchView.setCursorVisible(false);
                }
            }
        });

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (cs.length() != 0 && cs != null) {
                    cursor = getContentResolver().query(Uri.parse("content://sms"), null, "body LIKE '%" + cs + "%'", null, "date DESC");
                    todoAdapter.changeCursor(cursor);
                    messageList.setAdapter(todoAdapter);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        menu.findItem(R.id.send).setIcon(resizeImage(R.drawable.compose, 108, 108));
        menu.findItem(R.id.upload).setIcon(resizeImage(R.drawable.drive, 108, 108));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.upload:
                Drive.DriveApi.newDriveContents(getGoogleApiClient())
                        .setResultCallback(driveContentsCallback);
                return true;
            case R.id.send:
                Intent intent2=new Intent(this,SendSMS.class);
                startActivity(intent2);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        populateMessageList();
        populatedb(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    public void getPermissionToReadSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_SMS)) {
                Toast.makeText(this, "Please allow permission!", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_SMS},
                    READ_SMS_PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        if (requestCode == READ_SMS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read SMS permission granted", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Read SMS permission denied", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void onBackPressed() {
        if (oldCursor != null) {
            todoAdapter.changeCursor(oldCursor);
            searchView.setVisibility(View.VISIBLE);
            searchView.setCursorVisible(false);
            oldCursor = null;
        } else
            super.onBackPressed();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == RESULT_OK) {
            mGoogleApiClient.connect();
        }
    }


    public void populateMessageList() {
        cursor = getContentResolver().query(Uri.parse("content://sms"), null, "thread_id IS NOT NULL) GROUP BY (thread_id ", null, "date DESC");
        todoAdapter = new TodoCursorAdapter(this, cursor);
        messageList.setAdapter(todoAdapter);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "GoogleApiClient connected");

    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new
            ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create new file contents");
                        return;
                    }


                    final DriveContents driveContents = result.getDriveContents();

                    // Perform I/O off the UI thread.
                    new Thread() {
                        @Override
                        public void run() {
                            Query query = new Query.Builder()
                                    .addFilter(Filters.eq(SearchableField.TITLE, "SMS.db"))
                                    .build();
                            Drive.DriveApi.query(getGoogleApiClient(), query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {

                                @Override
                                public void onResult(DriveApi.MetadataBufferResult result) {

                                }
                            });
                            OutputStream outputStream = driveContents.getOutputStream();

                            FileInputStream inputStream = null;
                            try {
                                inputStream = new FileInputStream(dbFile);
                            } catch (FileNotFoundException e) {

                                e.printStackTrace();
                            }

                            byte[] buf = new byte[1024];
                            int bytesRead;
                            try {
                                if (inputStream != null) {
                                    while ((bytesRead = inputStream.read(buf)) > 0) {
                                        outputStream.write(buf, 0, bytesRead);
                                    }
                                }
                            } catch (IOException e) {

                                e.printStackTrace();
                            }


                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle("SMS.db")
                                    .setMimeType("text/plain")
                                    .build();

                            Drive.DriveApi.getRootFolder(getGoogleApiClient())
                                    .createFile(getGoogleApiClient(), changeSet, driveContents).
                                    setResultCallback(fileCallback);
                        }
                    }.start();
                }
            };


    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create the file");
                        return;
                    }
                    Id=result.getDriveFile().getDriveId();
                    showMessage("Uploaded to drive ");
                }
            };

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }


    public void populatedb(Context context) {

        Uri myMessage = Uri.parse("content://sms/");
        ContentResolver cr = this.getContentResolver();
        Cursor c = cr.query(myMessage, null, null, null, null);
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);


        if (c.moveToNext()) {

            do {
                String date = millisToDate(c.getLong(c.getColumnIndex("date")));
                String address = c.getString(c.getColumnIndex("address"));
                String body = c.getString(c.getColumnIndex("body"));
                String type = c.getString(c.getColumnIndex("type"));
                String thread_id = c.getString(c.getColumnIndex("thread_id"));
                dataBaseHelper.insertSMS(thread_id, address, date, body, type);


            } while (c.moveToNext());
        }

        c.close();

    }

    public static String millisToDate(long currentTime) {
        String finalDate;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        Date date = calendar.getTime();
        finalDate = date.toString();
        return finalDate;
    }

    private Drawable resizeImage(int resId, int w, int h)
    {
        // load the origial Bitmap
        Bitmap BitmapOrg = BitmapFactory.decodeResource(getResources(), resId);
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;
        // calculate the scale
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0,width, height, matrix, true);
        return new BitmapDrawable(resizedBitmap);
    }

}






