package com.buahbatu.streetwatcher;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buahbatu.streetwatcher.services.SoundAlert;
import com.buahbatu.streetwatcher.services.SoundLevelDetection;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MicrophoneFragment extends Fragment {
    // private OnFragmentInteractionListener mListener;
    private static final String TAG = "Microphone";
    SoundAlert soundService = null;

    TextView error_view;
    LineChart chart;

    public MicrophoneFragment() {
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
        View view = inflater.inflate(R.layout.fragment_microphone, container, false);
        chart = (LineChart)view.findViewById(R.id.chart);
        putTestChart();
        view.findViewById(R.id.start_button).setOnClickListener(startClick);
        view.findViewById(R.id.stop_button).setOnClickListener(stopClick);
        error_view = (TextView)view.findViewById(R.id.error_text);

        soundService = new SoundAlert(getContext());

        return view;
    }

    View.OnClickListener startClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick Start");
            if(soundService!=null)
                soundService.start();
        }
    };

    View.OnClickListener stopClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick Stop");
            if(soundService!=null)
                soundService.stop();
        }
    };

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    void putTestChart(){
        ArrayList<Entry> valsComp1 = new ArrayList<Entry>();
        ArrayList<Entry> valsComp2 = new ArrayList<Entry>();
        Entry c1e1 = new Entry(100.000f, 0); // 0 == quarter 1
        valsComp1.add(c1e1);
        Entry c1e2 = new Entry(50.000f, 1); // 1 == quarter 2 ...
        valsComp1.add(c1e2);
        // and so on ...

        Entry c2e1 = new Entry(120.000f, 0); // 0 == quarter 1
        valsComp2.add(c2e1);
        Entry c2e2 = new Entry(110.000f, 1); // 1 == quarter 2 ...
        valsComp2.add(c2e2);
        //...


        LineDataSet setComp1 = new LineDataSet(valsComp1, "Company 1");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);


        LineDataSet setComp2 = new LineDataSet(valsComp2, "Company 2");
        setComp2.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(setComp1);
        dataSets.add(setComp2);

        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("1.Q"); xVals.add("2.Q"); xVals.add("3.Q"); xVals.add("4.Q");

        LineData data = new LineData(xVals, dataSets);
        chart.setData(data);

        chart.invalidate(); // refresh
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            // un comment below later
//            OnFragmentInteractionListener as;
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        stopClick.onClick(new View(getActivity().getApplicationContext()));
//        mListener = null;
//    }
}
