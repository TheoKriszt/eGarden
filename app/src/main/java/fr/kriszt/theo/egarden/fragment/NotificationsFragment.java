package fr.kriszt.theo.egarden.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.kriszt.theo.egarden.R;
import fr.kriszt.theo.egarden.utils.Connexion;


public class NotificationsFragment extends Fragment {

    private static final String TAG = "Notifications";
    private OnFragmentInteractionListener mListener;

    @BindView(R.id.general_enable_switch) Switch _generalSwitch;
    @BindView(R.id.thirsty_switch) Switch _thirstySwitch;
    @BindView(R.id.reservoir_switch) Switch _reservoirSwitch;
    @BindView(R.id.meteo_condition_switch) Switch _meteoSwitch;
    @BindView(R.id.time_delay_spinner) Spinner _delaySpinner;
    @BindView(R.id.storage_size_spinner) Spinner _storageSpinner;
    @BindView(R.id.notification_storage_edit) EditText _storageSizeEdit;
    @BindView(R.id.notification_delay_edit) EditText _delayValueEdit;
    @BindView(R.id.notification_progressBar) ProgressBar _progressBar;
    @BindView(R.id.general_notifs_textView) TextView _general_textView;
    @BindView(R.id.storagetextView) TextView _storage_textView;
    @BindView(R.id.notification_storage_edit_layout) TextInputLayout storage_edit_layout;
    @BindView(R.id.notification_delay_edit_layout) TextInputLayout delay_edit_layout;
    private ArrayAdapter<CharSequence> storageTypesAdapter;
    private ArrayAdapter<CharSequence> delayTypesAdapter;
    private boolean generalEnabled;
    private boolean thirstEnabled;
    private boolean reservoirEnabled;
    private boolean meteoEnabled;


    public NotificationsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        setFieldsVisibility(false);

        storageTypesAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.notification_storage_sizes, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        storageTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        _storageSpinner.setAdapter(storageTypesAdapter);

        delayTypesAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.notification_delay_types, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        delayTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        _delaySpinner.setAdapter(delayTypesAdapter);

        loadUserPreferences();

        _generalSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setFieldsVisibility(isChecked);
                generalEnabled = isChecked;
            }
        });

        _thirstySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                thirstEnabled = isChecked;
            }
        });

        _reservoirSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                reservoirEnabled = isChecked;
            }
        });

        _meteoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                meteoEnabled = isChecked;
            }
        });




    }

    private void setFieldsVisibility(boolean visible) {
        View[] views = {_thirstySwitch, _reservoirSwitch, _meteoSwitch, _delayValueEdit, _delaySpinner, _storageSpinner, _storageSizeEdit, _storage_textView, storage_edit_layout, delay_edit_layout};

        for (View v : views){
            v.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @OnClick(R.id.saveNotifsbutton)
    public void onSaveButtonPressed(){
        HashMap<String, String> params = new HashMap<>();
        params.put("enabled", generalEnabled ? "1" : "0");
        params.put("thirst", thirstEnabled ? "1" : "0");
        params.put("reservoir", reservoirEnabled ? "1" : "0");
        params.put("meteo", meteoEnabled ? "1" : "0");
        params.put("storage", String.valueOf(_storageSizeEdit.getText()));
        params.put("delay", String.valueOf(_delayValueEdit.getText()));
        params.put("delay_unit", _delaySpinner.getSelectedItem().toString());
        params.put("storage_unit", _storageSpinner.getSelectedItem().toString());


        Connexion.O().sendPostRequest("/userPrefs", params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getContext(), getResources().getString(R.string.saved_confirmation), Toast.LENGTH_SHORT).show();
            }
        }, null);
    }

    private void loadUserPreferences() {

        Connexion.O(getContext()).sendGetRequest("/userPrefs", null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    Log.w(TAG, "onResponse: " + response);

                    boolean general = jsonObject.getInt("enabled") == 1;
                    boolean thirst = jsonObject.getInt("thirst") == 1;
                    boolean reservoir = jsonObject.getInt("reservoir") == 1;
                    boolean meteo = jsonObject.getInt("meteo") == 1;
                    int storage = jsonObject.getInt("storage");
                    int delay = jsonObject.getInt("delay");
                    String storageUnit = jsonObject.getString("storage_unit");
                    String delayUnit = jsonObject.getString("delay_unit");

                    if (delayUnit != null) {
                        int spinnerPosition = delayTypesAdapter.getPosition(delayUnit);
                        _delaySpinner.setSelection(spinnerPosition);
                    }
                    if (storageUnit != null) {
                        int spinnerPosition = storageTypesAdapter.getPosition(storageUnit);
                        _storageSpinner.setSelection(spinnerPosition);
                    }

                    _generalSwitch.setChecked(general);
                    _thirstySwitch.setChecked(thirst);
                    _reservoirSwitch.setChecked(reservoir);
                    _meteoSwitch.setChecked(meteo);
                    _storageSizeEdit.setText(String.valueOf(storage));
                    _delayValueEdit.setText(String.valueOf(delay));

                    setFieldsVisibility(general);


                } catch (JSONException e) {
                    Log.e(TAG, "onResponse: ", e);
                }
                finally {
                    _progressBar.setVisibility(View.GONE);
                }
            }
        }, null);

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


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStop() {
        super.onStop();
        Connexion.cancellAll();
    }
}
