package fr.kriszt.theo.egarden.fragment;

//import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.kriszt.theo.egarden.R;
import fr.kriszt.theo.egarden.utils.Connexion;


public class SettingsFragment extends Fragment {

    private static final String TAG = "Settings";
    private OnFragmentInteractionListener mListener;

    @BindView(R.id.wait_for_night_switch) Switch _waitForNightSwitch;
    @BindView(R.id.wait_for_rain_switch) Switch _waitForRainSwitch;
    @BindView(R.id.timelapse_enable_switch) Switch _timelapseEnableSwitch;
    @BindView(R.id.rain_wait_time) EditText _waitRainTimeEditText;
    @BindView(R.id.watering_duration) EditText _wateringDurationEditText;
    @BindView(R.id.timelapse_period_editText) EditText _timelapsePeriodEditText;
    @BindView(R.id.settings_progressBar) ProgressBar _settingsProgressBar;
    @BindView(R.id.textViewDuringHours) TextView _textViewDuringHours;
    @BindView(R.id.textViewLessThan) TextView _textViewLessThan;
    @BindView(R.id.textViewAllThe) TextView _textViewAllThe;
    @BindView(R.id.textViewtimelapseMinutes) TextView _textViewTimeLapseMinutes;


    private HashMap<String, String> serverSettings = new HashMap<>();

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        _timelapseEnableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                serverSettings.put("TIMELAPSE_ENABLE", isChecked ? "1" : "0");
                updateFieldsVisibility();

            }
        });

        _waitForNightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                serverSettings.put("WATER_AT_NIGHT", isChecked ? "1" : "0");

            }
        });

        _waitForRainSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                serverSettings.put("WAIT_FOR_RAIN", isChecked ? "1" : "0");
                updateFieldsVisibility();
            }
        });
                //


        Connexion.O().sendGetRequest("/garden-settings", null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject row = (JSONObject) jsonArray.get(i);
                        String k = row.getString("key");
                        String v = row.getString("value");

                        serverSettings.put(k, v);
                    }

                    _waitForNightSwitch.setChecked(serverSettings.get("WATER_AT_NIGHT").equals("1"));
                    _waitForRainSwitch.setChecked(serverSettings.get("WAIT_FOR_RAIN").equals("1"));
                    _waitRainTimeEditText.setText(serverSettings.get("WAIT_RAIN_TIME"));
                    _wateringDurationEditText.setText(serverSettings.get("WATERING_DURATION"));
                    _timelapseEnableSwitch.setChecked(serverSettings.get("TIMELAPSE_ENABLE").equals("1"));
                    _timelapsePeriodEditText.setText(serverSettings.get("TIMELAPSE_PERIOD"));

                    updateFieldsVisibility();

                } catch (JSONException e) {

                    Log.e(TAG, "onResponse: ", e);
                } finally {
                    _settingsProgressBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        });


    }

    private void updateFieldsVisibility() {
        _waitRainTimeEditText.setVisibility(_waitForRainSwitch.isChecked() ? View.VISIBLE : View.INVISIBLE);
        _textViewDuringHours.setVisibility(_waitForRainSwitch.isChecked() ? View.VISIBLE : View.INVISIBLE);
        _textViewLessThan.setVisibility(_waitForRainSwitch.isChecked() ? View.VISIBLE : View.INVISIBLE);

        _timelapsePeriodEditText.setVisibility(_timelapseEnableSwitch.isChecked() ? View.VISIBLE : View.INVISIBLE);
        _textViewTimeLapseMinutes.setVisibility(_timelapseEnableSwitch.isChecked() ? View.VISIBLE : View.INVISIBLE);
        _textViewAllThe.setVisibility(_timelapseEnableSwitch.isChecked() ? View.VISIBLE : View.INVISIBLE);
    }

    @OnClick(R.id.saveSettingsButton)
    public void onSaveButtonPressed(){

        serverSettings.put("WAIT_RAIN_TIME", _waitRainTimeEditText.getText().toString());
        serverSettings.put("WATERING_DURATION", _wateringDurationEditText.getText().toString());
        serverSettings.put("TIMELAPSE_PERIOD", _timelapsePeriodEditText.getText().toString());


        Connexion.O().sendPostRequest("/garden-settings", serverSettings, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getContext(), getResources().getString(R.string.saved_confirmation), Toast.LENGTH_SHORT).show();
            }
        }, null);



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

    public interface OnFragmentInteractionListener {
    }

    @Override
    public void onStop() {
        super.onStop();
        Connexion.cancellAll();
    }
}
