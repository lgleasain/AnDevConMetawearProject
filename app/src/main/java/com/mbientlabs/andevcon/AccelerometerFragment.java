package com.mbientlabs.andevcon;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mbientlab.metawear.api.MetaWearController;
import com.mbientlab.metawear.api.Module;
import com.mbientlab.metawear.api.controller.Accelerometer;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccelerometerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccelerometerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccelerometerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters

    MetaWearController mwController;
    Accelerometer accelCtrllr;
    boolean isStarted;

    private OnFragmentInteractionListener mListener;

    private final Accelerometer.Callbacks accelerometerCallbacks = new Accelerometer.Callbacks() {
        @Override
        public void receivedDataValue(short x, short y, short z) {
            ((TextView) getView().findViewById(R.id.accelerometer_x)).setText(String.valueOf(x));
            ((TextView) getView().findViewById(R.id.accelerometer_y)).setText(String.valueOf(y));
            ((TextView) getView().findViewById(R.id.accelerometer_z)).setText(String.valueOf(z));
        }
    };

    public void addTriggers(MetaWearController mwController) {
   /*
    * The board will start logging once all triggers have been registered.  This is done
    * by having the receivedTriggerId callback fn start the logger when the ID for the
    * Z axis has been received
    */
        this.mwController = mwController;
        accelCtrllr = (Accelerometer) mwController.getModuleController(Module.ACCELEROMETER);

        final Accelerometer accelCtrllr = (Accelerometer) mwController.getModuleController(Module.ACCELEROMETER);
        accelCtrllr.enableXYZSampling().withFullScaleRange(Accelerometer.SamplingConfig.FullScaleRange.FSR_8G)
                .withHighPassFilter((byte) 0).withOutputDataRate(Accelerometer.SamplingConfig.OutputDataRate.ODR_100_HZ);
                //.withSilentMode();
        this.mwController.addModuleCallback(accelerometerCallbacks);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AccelerometerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccelerometerFragment newInstance() {
        AccelerometerFragment fragment = new AccelerometerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public AccelerometerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ((Button) getView().findViewById(R.id.accelerometer_start_stop)).setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isStarted) {
                            ((Button) v).setText("Start Accelerometer");
                            accelCtrllr.stopComponents();
                        } else {
                            ((Button) v).setText("Stop Accelerometer");
                            accelCtrllr.startComponents();
                        }
                    }
                }
        );
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_accelerometer, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
