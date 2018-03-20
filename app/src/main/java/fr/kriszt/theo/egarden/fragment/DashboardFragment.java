package fr.kriszt.theo.egarden.fragment;

//import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.kriszt.theo.egarden.R;
import fr.kriszt.theo.egarden.utils.Connexion;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DashboardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "DashboardFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private LineChart lineChart;


    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    private void fetchDHTValues() {
        Connexion.O().sendGetRequest("/DHT", null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ArrayList<Entry> solEntries = new ArrayList<>();
                ArrayList<Entry> humEntries = new ArrayList<>();
                ArrayList<Entry> tempEntries = new ArrayList<>();
                ArrayList<String> dateLabels = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    Log.w(TAG, "JSONARRAY: " + jsonArray.length() + " rows fetched");

                    for (int i = 0; i < jsonArray.length(); i++){ // sol; hum, temp, date
                        float sol = Float.parseFloat(String.valueOf(jsonArray.getJSONObject(i).get("sol")));
                        float hum = Float.parseFloat(String.valueOf(jsonArray.getJSONObject(i).get("hum")));
                        float temp = Float.parseFloat(String.valueOf(jsonArray.getJSONObject(i).get("temp")));
                        String date = String.valueOf(jsonArray.getJSONObject(i).get("date"));

                        solEntries.add(new Entry(i, sol));
                        humEntries.add(new Entry(i, hum));
                        tempEntries.add(new Entry(i, temp));
                        dateLabels.add(date);
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "onResponse: ", e);
                }

                LineDataSet humDataSet = new LineDataSet(humEntries, "humidity");
                LineDataSet solDataSet = new LineDataSet(humEntries, "Solar");
                solDataSet.setColor(Color.YELLOW);
                LineDataSet tempDataSet = new LineDataSet(humEntries, "Temperature");
                tempDataSet.setColor(Color.RED);

                LineData data = new LineData();

                data.addDataSet(solDataSet);
                data.addDataSet(humDataSet);
                data.addDataSet(tempDataSet);
                lineChart.setData(data);
                Description description = new Description();
                description.setText("DHT values");
                lineChart.setDescription(description);
                getView().findViewById(R.id.dashboard_dht_progressbar).setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Erreur lors de la recuperationn des DHTs", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);


//        ArrayList<Entry> entries = new ArrayList<>();
//        entries.add(new Entry(4, 0));
//        entries.add(new Entry(8, 1));
//        entries.add(new Entry(6, 2));
//        entries.add(new Entry(2, 3));
//        entries.add(new Entry(18, 4));
//        entries.add(new Entry(9, 5));
//
//        LineDataSet dataSet = new LineDataSet(entries, "# of calls");
//        ArrayList<String> labels = new ArrayList<>();
//        labels.add("Jan");
//        labels.add("Fev");
//        labels.add("Mar");
//        labels.add("Avr");
//        labels.add("Mai");
//        labels.add("Juin");
//
//        LineData data = new LineData();
//        data.addDataSet(dataSet);
//        data.set





        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lineChart = view.findViewById(R.id.dht_chart);
        fetchDHTValues();


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
