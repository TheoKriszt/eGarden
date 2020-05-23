package fr.kriszt.theo.egarden.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.kriszt.theo.egarden.R;
import fr.kriszt.theo.egarden.utils.Connexion;


public class PlantDetailsFragment extends Fragment {
    public static final String TAG = "PlantDetailsFragment";

    private static final String PLANT_ID = "plantId";

    private String plantId;

    @BindView(R.id.plant_detail_name)
    TextView _plantName;
    @BindView(R.id.plant_detail_description)
    TextView _plantDescription;
    @BindView(R.id.plant_detail_state)
    TextView _plantState;
    @BindView(R.id.plant_detail_autowater_switch)
    Switch _autoSwitch;
    @BindView(R.id.plant_detail_auto_image)
    ImageView _autoImage;
    @BindView(R.id.plant_detail_threshold_seekbar)
    SeekBar _autoThresholdBar;
    @BindView(R.id.plant_detail_save_button)
    Button _saveButton;
    @BindView(R.id.plant_detail_water_button)
    Button _waterButton;
    @BindView(R.id.plant_detail_hygrometry_progressbar)
    ProgressBar _hygroProgressBar;
    @BindView(R.id.plant_detail_hygrometry_chart)
    LineChart _lineChart;
    @BindView(R.id.plant_detail_image)
    ImageView _plantImage;


    private boolean autowater;


    public PlantDetailsFragment() {
        // Required empty public constructor
    }

    public static PlantDetailsFragment newInstance(String plantId) {
        PlantDetailsFragment fragment = new PlantDetailsFragment();
        Bundle args = new Bundle();
        args.putString(PLANT_ID, plantId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            plantId = getArguments().getString(PLANT_ID);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        updatePlantInfo();


        Connexion.O().sendGetRequest("/soil/" + plantId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    loadHydroGraph(response);
                } catch (JSONException e) {
                    Log.e(TAG, "onResponse: ", e);
                    _hygroProgressBar.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Erreur en recuperant l'hygrometrie de la plante", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onErrorResponse: " + Connexion.O().decodeError(error));
                _hygroProgressBar.setVisibility(View.GONE);
            }
        });

        _autoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                autowater = isChecked;
                if (autowater) {
                    _autoImage.setImageResource(R.drawable.auto_on);
                } else {
                    _autoImage.setImageResource(R.drawable.auto_off);
                }

            }
        });

    }

    private void updatePlantInfo() {
        Connexion.O().sendGetRequest("/plant/" + plantId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    _plantName.setText(json.getString("name"));
                    int threshold = Integer.parseInt(json.getString("threshold"));
                    _autoThresholdBar.setProgress(threshold);
                    _autoSwitch.setChecked(json.getString("auto_watering").equals("true")
                            || json.getString("auto_watering").equals("1"));

                    _plantDescription.setText(json.getString("description"));

                    try {
                        String stateWord = getActivity().getResources().getString(R.string.state);
                        String stateString = getActivity().getResources().getString(PlantState.PLANT_UNKNOWN.getDescriptionRessource());

                        PlantState plantState = PlantState.valueOf(json.getString("plantState"));
                        if (plantState != null) {
                            stateString = getResources().getString(plantState.getDescriptionRessource());
                        }
                        _plantState.setText(String.format("%s : %s", stateWord, stateString));

                        updateImageView(json.get("imgURI").toString());
                    } catch (IllegalStateException e) {
                        Log.e(TAG, "onResponse: perte de contexte", e);
                    }


                } catch (JSONException e) {
                    Log.e(TAG, "onResponse: ", e);
                }
            }
        }, null);
    }

    private void updateImageView(String imgURI) {

        Connexion.O().downloadImage(imgURI, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                _plantImage.setImageBitmap(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

    }

    private void loadHydroGraph(String response) throws JSONException {
        JSONArray array = new JSONArray(response);
        ArrayList<Entry> entries = new ArrayList<>();
        final ArrayList<String> dateLabels = new ArrayList<>();
        if (array.length() == 0) {
            _lineChart.setVisibility(View.GONE);
            return;
        }

        for (int i = 0; i < array.length(); i++) {
            float hygro = Float.parseFloat(String.valueOf(array.getJSONObject(i).get("value")));
            String date = String.valueOf(array.getJSONObject(i).get("time"));

            entries.add(new Entry(i, hygro));
            dateLabels.add(date);
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "hygrometrie");
        lineDataSet.setDrawCircles(false);
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        _lineChart.animateX(1000);
        XAxis xAxis = _lineChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dateLabels.get((int) value);
            }
        });

        LineData lineData = new LineData(lineDataSet);

        //Description
        Description description = new Description();
        description.setText("Plant hygrometry");
        _lineChart.setDescription(description);
        _lineChart.invalidate();
        _lineChart.setData(lineData);

        _hygroProgressBar.setVisibility(View.GONE);
        _lineChart.setVisibility(View.VISIBLE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plant_details, container, false);
    }

    @OnClick(R.id.plant_detail_save_button)
    public void onSaveButtonPressed() {
        HashMap<String, String> params = new HashMap<>();
        params.put("autowater", autowater ? "true" : "false");
        params.put("threshold", String.valueOf(_autoThresholdBar.getProgress()));
        params.put("plantID", plantId);

        Connexion.O().sendPostRequest("/plant", params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getContext(), getResources().getString(R.string.saved_confirmation), Toast.LENGTH_SHORT).show();
                updatePlantInfo();
            }
        }, null);

    }

    @OnClick(R.id.plant_detail_water_button)
    public void onWaterButtonPressed() {
        Connexion.O().sendPostRequest("/soil/" + plantId, null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    Toast.makeText(getActivity().getBaseContext(), response, Toast.LENGTH_SHORT).show();
                } catch (NullPointerException e) {
                    Log.e(TAG, "onResponse, perte de contexte: ", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + Connexion.O().decodeError(error));
                Toast.makeText(getContext(), "Une erreur est survenue", Toast.LENGTH_SHORT).show();
            }
        });
    }


    enum PlantState {

        PLANT_THIRSTY(R.string.plant_thirsty),
        PLANT_HYDRATED(R.string.plant_hydrated),
        PLANT_WATERING(R.string.plant_watering),
        PLANT_UNKNOWN(R.string.plant_unknown);

        private final int desc;

        PlantState(int res) {
            this.desc = res;
        }

        public int getDescriptionRessource() {
            return desc;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Connexion.cancellAll();
    }
}
