package com.tricktekno.optnio.oblu;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.joaquimley.faboptions.FabOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.tricktekno.optnio.oblu.R.id.canvas;
//import static com.tricktekno.optnio.oblu.REQUEST_TAKE_PHOTO;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = MainActivity.class.getSimpleName();
    private static final int SELECTED_PICTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    public static BluetoothLeService mBluetoothLeService = null;
    private String mDeviceName;
    public static String mDeviceAddress = null;
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    int ss;
    int sa;
    String mCurrentPhotoPath;
    String userInputValue;
    private boolean mLoaded = false;
    private Paint mTapPaint;
    private SurfaceHolder mSurfaceHolder;
    public static boolean flag = true;

    public static String file_name_save = null;
    public static String logDir = "Log Files";
    public static String parentDir = "oblu";
    public static String picturesDir = "Pictures";
    public static String saveDir = "Saved Sessions";
    private int rotateAngle;
    private static final int TAB = 0;
    public static int pathViewHeight;
    public static int pathViewWidth;
    ListPreference listPref;
    BluetoothAdapter adapter;
    BluetoothDevice MiDevice;
    BluetoothSocket socket;
    InputStream in;
    public static OutputStream out;
    Button send;
    Button connect;
    Button swapButton;
    static PathDrawView pathView;
    Thread BlueToothThread;
    boolean stop = false;
    int position;
    byte read[];
    int i = 0;
    int index = 0;
    int[] header = new int[512];
    private boolean mConnected = false;
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    ManualControlView manualView;
    Netstrings nt = new Netstrings();
    private Toolbar mToolbar;
    private static BluetoothGattCharacteristic mReadCharacteristic;
    private static BluetoothGattCharacteristic mNotifyCharacteristic;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public static Intent newStartIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }
    private ShareActionProvider mShareActionProvider;
    private FloatingActionMenu fam;
    private FloatingActionButton choose, reset, rotate, zoomout, zoomin;



    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    BluetoothLeService mtu = new BluetoothLeService();

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                invalidateOptionsMenu();
                Toast.makeText(getApplicationContext(), "oblu Connected",
                        Toast.LENGTH_SHORT).show();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                invalidateOptionsMenu();
                Toast.makeText(getApplicationContext(), "oblu Disconnected",
                        Toast.LENGTH_SHORT).show();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mtu.exchangeGattMtu(512);
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        pathView = (PathDrawView) findViewById(canvas);

      //  pathView.assignTextView((TextView)findViewById(R.id.text));

        SharedPreferences shPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        pathView.addSharedPreferences(shPref);
        PreferenceManager.setDefaultValues(this, R.xml.prefrences, true);
        String option = shPref.getString("PREF_LIST", "Medium");
        if (option == "Low") {

        }

        MainActivity.this.start_sensor();

        FabOptions fabOptions = (FabOptions) findViewById(R.id.fab_options);
        fabOptions.setOnClickListener(this);//data = (TextView)findViewById(R.id.data);


        choose = (FloatingActionButton) findViewById(R.id.fab1);
        reset = (FloatingActionButton) findViewById(R.id.fab2);
        rotate = (FloatingActionButton) findViewById(R.id.fab3);
        zoomout = (FloatingActionButton) findViewById(R.id.fab4);
        zoomin = (FloatingActionButton) findViewById(R.id.fab5);

        fam = (FloatingActionMenu) findViewById(R.id.fab_menu);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

    }

@Override
 public void onClick(View view) {
 // AlertDialog.Builder inputAlert;
   switch (view.getId()){
     case R.id.faboptions_favorite:
       if (mConnected) {
         if (pathView.stringList == null) {
           Toast.makeText(getApplicationContext(), "No Path Drawn",
           Toast.LENGTH_SHORT).show();
         } else {
             if (pathView.stringList.size() > 0) {
                 List<String> stringList = pathView.stringList;
                 for (String s : stringList) {
                  byte[] convertedBytes = convertingTobyteArray(s);
                    
                   BluetoothLeService.writeCharacteristicNoresponse(mReadCharacteristic, convertedBytes);
                   
                 }
                 Toast.makeText(getApplicationContext(), "Path Sent",
                         Toast.LENGTH_SHORT).show();
             } else {
                 Toast.makeText(getApplicationContext(), "Please draw a line.",
                         Toast.LENGTH_SHORT).show();
             }
         }
                        //pathView.resetObstacleDetected();

       } else {
           Toast.makeText(getApplicationContext(), "No Connection Found",
                   Toast.LENGTH_SHORT).show();
       }
                        break;
                    case R.id.faboptions_textsms:
                        Intent k = new Intent(this, DeviceScanActivity.class);
                        startActivity(k);
                        break;
                    case R.id.faboptions_download:

                       if (pathView.stringList2 == null) {
                            Toast.makeText(getApplicationContext(), "No Path Drawn",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            if (pathView.stringList2.size() > 0) {
                           File photoFile1 = null;

                                try {
                                    photoFile1 = this.createdataFile1(this.sa);
                                    FileOutputStream fos = new FileOutputStream(photoFile1);

                                   List<String> stringList = PathDrawView.stringList2;
                                    for (String s : stringList) {
                                        fos.write(s.getBytes());
                                    }

                                    fos.close();
                                    Toast.makeText(getApplicationContext(), "Session saved at "+photoFile1, Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Log.v("oblu", "Session has not been saved");
                                }
                            }
                                else {
                                    Toast.makeText(getApplicationContext(), "Please draw a line.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                        break;
                    case R.id.faboptions_share:
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.tricktekno.equifest.equifest");
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                        break;
                    case R.id.fab1:
                        Toast.makeText(getApplicationContext(), "Select JPEG Image To add as Map",
                                Toast.LENGTH_SHORT).show();
                       Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        startActivityForResult(intent, SELECTED_PICTURE);
                        break;

                    case R.id.fab2:
                        Toast.makeText(getApplicationContext(), "Clear the Curerent Session",
                                Toast.LENGTH_SHORT).show();
                        Bitmap whiteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.white);
                        PathDrawView.setGivenBackgroundBitmap(whiteBitmap);
                        break;
                    case R.id.fab3:
                       /* android.app.AlertDialog.Builder inputAlert1 = new android.app.AlertDialog.Builder(this);
                        inputAlert1.setTitle("Tag Data Point");
                        final EditText userInput1 = new EditText(this);
                        inputAlert1.setView(userInput1);
                        Toast.makeText(this, "Click Image", NONE).show();
                        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
                            File photoFile = null;
                            try {
                                photoFile = this.createImageFile(this.ss);
                            } catch (IOException e) {
                                System.out.println("error camera");
                                Toast.makeText(this, "Camera Error", NONE).show();
                            }
                            if (photoFile != null) {
                                this.userInputValue = userInput1.getText().toString();
                                takePictureIntent.putExtra("output", Uri.fromFile(photoFile));
                                this.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                            }
                        }*/
                        Intent settingsIntent = new Intent(MainActivity.this,
                                SettingsActivity.class);

                        settingsIntent.putExtra("width", pathView.getWidth());

                        startActivity(settingsIntent);
                        break;
                    case R.id.fab6:

                        break;
                    case R.id.fab4:

                       Intent AboutIntent = new Intent(MainActivity.this,
                                AboutActivity.class);

                        startActivity(AboutIntent);
                        break;
                    case R.id.fab5:
                        Intent ContactIntent = new Intent(MainActivity.this,
                                Contact.class);

                        //  AboutIntent.putExtra("width", pathView.getWidth());

                        startActivity(ContactIntent);
                        break;
                }
            }

    /**
     * Method to convert hex to byteArray
     */
    private byte[] convertingTobyteArray(String result) {
        String[] splited = result.split("\\s+");
        byte[] valueByte = new byte[splited.length];
        for (int i = 0; i < splited.length; i++) {
            if (splited[i].length() > 2) {
                String trimmedByte = splited[i].split("x")[1];
                valueByte[i] = (byte) convertstringtobyte(trimmedByte);
            }

        }
        return valueByte;

    }
    /**
     * Convert the string to byte
     *
     * @param string
     * @return
     */

    private int convertstringtobyte(String string) {
        return Integer.parseInt(string, 16);
    }
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case SELECTED_PICTURE:
                if(resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    String[]projection = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String filePath = cursor.getString(columnIndex);

                    Bitmap yourSelectionImage = BitmapFactory.decodeFile(filePath);
                    Bitmap img;
                    if(yourSelectionImage.getWidth()>yourSelectionImage.getHeight()) {
                        Matrix matrix = new Matrix();

                        matrix.postRotate(180);
                        img = Bitmap.createBitmap(yourSelectionImage, 0, 0, yourSelectionImage.getWidth(), yourSelectionImage.getHeight(), matrix, true);

                    }else{
                        img = yourSelectionImage;
                    }

                   //float pathViewWidth = getIntent().getExtras().getInt("width");

                  //  float ratio = Math.min(
                  //          pathViewWidth/img.getWidth(),
                  //          (float)MainActivity.pathViewHeight/img.getHeight());

                 //   img = Bitmap.createScaledBitmap( img, (int)(img.getWidth()*ratio),
                  //          (int)(img.getHeight()*ratio), true );

                    if(img.getWidth()>img.getHeight()){
                        Matrix matrix = new Matrix();

                        matrix.postRotate(180);
                        img = Bitmap.createBitmap(img , 0, 0, img.getWidth(), img.getHeight(), matrix, true);
                        PathDrawView.setGivenBackgroundBitmap(img);
                    }else{
                        PathDrawView.setGivenBackgroundBitmap(img);
                    }

                }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder inputAlert;
        switch (item.getItemId()) {
            case R.id.save:
                if (pathView.stringList == null) {
                    Toast.makeText(getApplicationContext(), "No Path Drawn",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (pathView.stringList.size() > 0) {
                        File photoFile1 = null;

                        try {
                            photoFile1 = this.createdataFile1(this.sa);
                            FileOutputStream fos = new FileOutputStream(photoFile1);

                            List<String> stringList = PathDrawView.stringList;
                            for (String s : stringList) {
                                fos.write(s.getBytes());
                            }

                            fos.close();
                            Toast.makeText(getApplicationContext(), "Session saved at "+photoFile1, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.v("oblu", "Session has not been saved");
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Please draw a line.",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case R.id.load:
                Intent saveIntent = new Intent(MainActivity.this,
                        session_list.class);

                //  AboutIntent.putExtra("width", pathView.getWidth());

                startActivity(saveIntent);
                break;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(MainActivity.this,
                        SettingsActivity.class);

                settingsIntent.putExtra("width", pathView.getWidth());

                startActivity(settingsIntent);
                break;
            case R.id.action_scan:
                Intent scan = new Intent(this,
                        DeviceScanActivity.class);

                startActivity(scan);
                break;
            case R.id.menu_item_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.tricktekno.equifest.equifest");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.id.about:
                Intent AboutIntent = new Intent(MainActivity.this,
                        AboutActivity.class);

                //  AboutIntent.putExtra("width", pathView.getWidth());

                startActivity(AboutIntent);
                break;
            case R.id.contact:
                Intent ContactIntent = new Intent(MainActivity.this,
                        Contact.class);

                //  AboutIntent.putExtra("width", pathView.getWidth());

                startActivity(ContactIntent);
                break;
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }
    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    protected void onRestart() {
        pathView.validateLine();
        pathView.invalidate();
        super.onRestart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "UART RESUME");
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
     /*   if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.e(TAG, "Connect request result=" + result);
        }*/
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        pathViewHeight = pathView.getHeight();
        pathViewWidth = pathView.getWidth();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
    public void start_sensor() {

    }

    public static boolean createDirIfNotExists(String path) {
        File file = new File(Environment.getExternalStorageDirectory(), path);
        if (file.exists()) {
            return true;
        }
        if (file.mkdirs()) {
            Log.i("FILE HANDLING", path + " Created successfully");
            return true;
        }
        Log.e("FILE ERROR ", "Problem creating folder");
        return false;
    }

    private File createImageFile(int ss) throws IOException {
        String imageFileName = "JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_";
        String path = "/" + MainActivity.parentDir + "/" + MainActivity.picturesDir + "/";
        File folder = new File(Environment.getExternalStorageDirectory().toString() + path);
        createDirIfNotExists(path);
        File image = File.createTempFile(imageFileName, ".jpg", folder);
        //tag_data_image[series2.getItemCount()] = image.getAbsolutePath();
        this.mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
    private File createdataFile1(int sa) throws IOException {
        String dataFileName = "DATA_"+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_";
        String path = "/" + MainActivity.parentDir + "/" + MainActivity.saveDir + "/";
        File folder = new File(Environment.getExternalStorageDirectory().toString() + path);
        createDirIfNotExists(path);
        File image = File.createTempFile(dataFileName, ".txt", folder);
        // tag_data_image[series2.getItemCount()] = image.getAbsolutePath();
        this.mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    //GATT DATA Receive here
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // final String action = intent.getAction();
        // if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
        //       .equals(action)) {
        // Logger.e("Service discovered");
        //  if(mTimer!=null)
        //     mTimer.cancel();
        //  prepareGattServices(BluetoothLeService.getSupportedGattServices());

                /*
                / Changes the MTU size to 512 in case LOLLIPOP and above devices
                */
        //   if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        //         BluetoothLeService.exchangeGattMtu(512);
        //     }
        //  } //else if (BluetoothLeService.ACTION_GATT_SERVICE_DISCOVERY_UNSUCCESSFUL
        //    .equals(action)) {
        // mProgressDialog.dismiss();
        // if(mTimer!=null)
        //  mTimer.cancel();
        //  showNoServiceDiscoverAlert();
        // }
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME,SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            if (uuid.equals(SampleGattAttributes.SERVER_UART)) {
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    String uuidchara = gattCharacteristic.getUuid().toString();
                    mReadCharacteristic = gattCharacteristic;
                    if (uuidchara.equalsIgnoreCase(SampleGattAttributes.SERVER_UART_tx)) {
                        Log.e(TAG,"gatt- "+gattCharacteristic);
                        mNotifyCharacteristic = gattCharacteristic;
                        prepareBroadcastDataNotify(mNotifyCharacteristic);
                    }
                }
            }
            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);

            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
    }

    static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    /**
     * Preparing Broadcast receiver to broadcast notify characteristics
     *
     * @param gattCharacteristic
     */
    void prepareBroadcastDataNotify(
            BluetoothGattCharacteristic gattCharacteristic) {
        if ((gattCharacteristic.getProperties() | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            BluetoothLeService.setCharacteristicNotification(gattCharacteristic,true);
        }
    }
}



