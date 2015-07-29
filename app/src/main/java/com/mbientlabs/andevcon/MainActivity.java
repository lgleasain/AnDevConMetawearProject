package com.mbientlabs.andevcon;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mbientlab.metawear.api.MetaWearBleService;
import com.mbientlab.metawear.api.MetaWearController;
import com.mbientlab.metawear.api.controller.Accelerometer;


public class MainActivity extends ActionBarActivity implements ServiceConnection,
        DeviceInformationFragment.MetaWearManager{

    private MetaWearBleService mwService= null;
    private MetaWearController mwCtrllr;
    private final String MW_MAC_ADDRESS = "D7:C7:48:6E:21:6C";
            //"C9:A0:A3:90:1E:0E";
    //"D7:C7:48:6E:21:6C"
            //"FA:97:BF:0C:38:7D";
    private Menu menu;
    private boolean isConnected = false;
    private DeviceInformationFragment deviceInfoFragment;
    private AccelerometerFragment accerometerFragment;


    public MetaWearController getCurrentController(){
        return mwCtrllr;
    }
    public boolean hasController(){
        return mwCtrllr != null;
    }
    public boolean controllerReady(){
        return(hasController() && mwCtrllr.isConnected());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getApplicationContext().bindService(new Intent(this, MetaWearBleService.class),
                this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_connect) {
            MenuItem connectMenuItem = menu.findItem(id);

            if(mwCtrllr.isConnected()){
                connectMenuItem.setTitle(getString(R.string.action_connect));
                mwCtrllr.close(true);
            }else{
                connectMenuItem.setTitle(getString(R.string.action_disconnect));
                mwCtrllr.connect();
            }

            return true;
        }else if (id == R.id.action_device_information) {
            deviceInfoFragment = DeviceInformationFragment.newInstance();
            if(mwCtrllr != null){
                deviceInfoFragment.controllerReady(mwCtrllr);
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.module_detail_container, deviceInfoFragment).commit();
        }else if (id == R.id.action_accelerometer) {
            accerometerFragment = AccelerometerFragment.newInstance();
            if(mwCtrllr != null){
                accerometerFragment.addTriggers(mwCtrllr);
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.module_detail_container, accerometerFragment).commit();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    private MetaWearController.DeviceCallbacks dCallback= new MetaWearController.DeviceCallbacks() {
        @Override
        public void connected() {
            Log.i("Metawear Controller", "Device Connected");
            Toast.makeText(getApplicationContext(), R.string.toast_connected, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void disconnected() {
            Log.i("Metawear Controler", "Device Disconnected");
            Toast.makeText(getApplicationContext(), R.string.toast_disconnected, Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        ///< Get a reference to the MetaWear service from the binder
        mwService= ((MetaWearBleService.LocalBinder) service).getService();

        final BluetoothManager btManager=
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothDevice mwBoard= btManager.getAdapter().getRemoteDevice(MW_MAC_ADDRESS);
        mwCtrllr= mwService.getMetaWearController(mwBoard);
    }

    ///< Don't need this callback method but we must implement it
    @Override
    public void onServiceDisconnected(ComponentName name) { }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(MetaWearBleService.getMetaWearBroadcastReceiver(),
                MetaWearBleService.getMetaWearIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(MetaWearBleService.getMetaWearBroadcastReceiver());
    }
}

