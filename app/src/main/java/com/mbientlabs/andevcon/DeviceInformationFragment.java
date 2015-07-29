package com.mbientlabs.andevcon;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mbientlab.metawear.api.GATT;
import com.mbientlab.metawear.api.MetaWearController;
import com.mbientlab.metawear.api.Module;
import com.mbientlab.metawear.api.characteristic.Battery;
import com.mbientlab.metawear.api.characteristic.DeviceInformation;
import com.mbientlab.metawear.api.controller.Debug;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeviceInformationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceInformationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private Debug debugController;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DeviceInformationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeviceInformationFragment newInstance() {
        DeviceInformationFragment fragment = new DeviceInformationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private MetaWearController.DeviceCallbacks dCallback= new MetaWearController.DeviceCallbacks() {
        @Override
        public void receivedGATTCharacteristic(
                GATT.GATTCharacteristic characteristic, byte[] data) {
            values.put(characteristic, new String(data));

            final Integer viewId = views.get(characteristic);

            if(viewId != null && isVisible()){
                ((TextView) getView().findViewById(viewId)).setText(values.get(characteristic));
            }

        }
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        (view.findViewById(R.id.reset)).setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        if(mwManager.controllerReady()){
                            debugController.resetDevice();
                        }
                    }
                }
        );
    }


    public void controllerReady(MetaWearController mwController){
        mwController.addDeviceCallback(dCallback);
        if(mwController.isConnected()){
            mwController.readDeviceInformation();
        }
        debugController= (Debug) mwController.getModuleController(Module.DEBUG);

    }


    private HashMap<GATT.GATTCharacteristic, String> values= new HashMap<>();
    private final static HashMap<GATT.GATTCharacteristic, Integer> views= new HashMap<>();
    static {
        views.put(DeviceInformation.MANUFACTURER_NAME, R.id.manufacturer_name);
        views.put(DeviceInformation.SERIAL_NUMBER, R.id.serial_number);
        views.put(DeviceInformation.FIRMWARE_VERSION, R.id.firmware_version);
        views.put(DeviceInformation.HARDWARE_VERSION, R.id.hardware_version);
        views.put(Battery.BATTERY_LEVEL, R.id.battery_level);
    }


    public DeviceInformationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_device_information, container, false);
    }

    protected MetaWearManager mwManager;

    public interface MetaWearManager {
        public MetaWearController getCurrentController();
        public boolean hasController();
        public boolean controllerReady();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof MetaWearManager)) {
            throw new IllegalStateException(
                    "Activity must implement fragment's callbacks.");
        }
        mwManager= (MetaWearManager) activity;
    }

}
