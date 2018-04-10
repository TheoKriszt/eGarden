package fr.kriszt.theo.egarden.fragment;

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
import android.widget.TextView;
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

import java.text.MessageFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    private static final String TAG = "DashboardFragment";
    private View view;

    private OnFragmentInteractionListener mListener;
    private LineChart lineChart;
    @BindView(R.id.weather_alert) TextView _weatherAlert;
    @BindView(R.id.temperature_alert) TextView _temperatureAlert;
    @BindView(R.id.hygrometry_alert) TextView _hygrometryAlert;
    @BindView(R.id.temperature_value) TextView _temperatureValue;
    @BindView(R.id.hydrometry_value) TextView _hygrometryValue;
    @BindView(R.id.weather_value) TextView _weatherValue;


    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    Log.e(TAG, "DHT JSONException: ", e);
                }


                //Humidity
                LineDataSet humDataSet = new LineDataSet(humEntries, "humidity %HR");
                humDataSet.setDrawCircles(false);
                humDataSet.setColor(Color.BLUE);
                humDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

                //Solar
                LineDataSet solDataSet = new LineDataSet(solEntries, "Solar");
                solDataSet.setDrawValues(false);
                solDataSet.setDrawCircles(false);
//                solDataSet.setDrawCircleHole(false);
                
                solDataSet.setColor(Color.rgb(255, 102, 0));
                solDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

                //Temperature
                LineDataSet tempDataSet = new LineDataSet(tempEntries, "Temperature °C");
                tempDataSet.setDrawCircles(false);
                tempDataSet.setColor(Color.GREEN);
                tempDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

                lineChart.animateX(1000);

                LineData lineData = new LineData(humDataSet, tempDataSet, solDataSet);

//                LineData lineData = new LineData(solDataSet, humDataSet, tempDataSet);

                //Description
                Description description = new Description();
                description.setText("DHT values");
                lineChart.setDescription(description);
                lineChart.invalidate();
                lineChart.setData(lineData);

                //lineChart.invalidate();

                getView().findViewById(R.id.dashboard_dht_progressbar).setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Erreur lors de la récupérationn des données environnementales", Toast.LENGTH_SHORT).show();
                getView().findViewById(R.id.dashboard_dht_progressbar).setVisibility(View.GONE);
            }
        });
    }

    private void fetchWeatherAPI(){



        Connexion.O().sendGetRequest("/weather", null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
//                    Log.e(TAG, response, null);
                    JSONObject weather = (JSONObject) json.get("weather");
                    JSONObject temperature = (JSONObject) json.get("temperature");
                    JSONObject humidity = (JSONObject) json.get("humidity");

                    String humidityValue = humidity.getString("value");
                    String tempMin = temperature.getString("min");
                    String tempMax = temperature.getString("max");
                    String tempAlert = temperature.getString("alert");
                    String weatherMsg = weather.getString("msg");
                    String weatherAlert = weather.getString("alert");
                    String weatherIcon = weather.getString("icon");

//                    Toast.makeText(view.getContext(), response , Toast.LENGTH_SHORT).show();

                    _weatherValue.setText( weatherMsg );
                    _weatherAlert.setText( weatherAlert );

                    // TODO : set weather icon

                    _temperatureValue.setText(MessageFormat.format("{0} à {1} (°C)", tempMin, tempMax));
                    _temperatureAlert.setText( tempAlert );

                    _hygrometryValue.setText(MessageFormat.format("{0} (% HR)", humidityValue));
                    // TODO : fetch & process hygro Alerts


                } catch (JSONException e) {
//                    Toast.makeText(view.getContext() , "erreur parsing JSON ", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onResponse: ", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "" + Connexion.O().decodeError(error));
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lineChart = view.findViewById(R.id.dht_chart);
        lineChart.setNoDataText("");
        ButterKnife.bind(this, view);
        fetchDHTValues();
        fetchWeatherAPI();


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
