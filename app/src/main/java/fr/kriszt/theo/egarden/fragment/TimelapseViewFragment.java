package fr.kriszt.theo.egarden.fragment;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;
import android.widget.MediaController;

import com.android.volley.Response;
import com.android.volley.VolleyError;

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

import static android.view.View.GONE;


public class TimelapseViewFragment extends Fragment {


    private static final String TAG = "Timelapse";
    private ArrayAdapter<CharSequence> WeekDayTypesAdapter;
    @BindView(R.id.videoView) VideoView _videoView;
    @BindView(R.id.video_progressBar) ProgressBar _progressBar;
    @BindView(R.id.weekDaySpinner) Spinner _weekdaySpinner;
    @BindView(R.id.selectSpinner) Spinner _selectSpinner;
    @BindView(R.id.imageView) ImageView _imageView;
    @BindView(R.id.loadButton) ImageButton _loadButton;
    @BindView(R.id.downloadButton) ImageButton _downloadButton;
    @BindView(R.id.wallplugButton) ToggleButton _wallplugButton;

    Handler refreshHandler = new Handler();

    private String feed = "direct";
    private String[] videoFileNames;
    private String videoFileName;
    private String[] videoFilePaths;
    private String videoFilePath;


    private Runnable refreshDirect = new Runnable() {
        @Override
        public void run() {

            final Runnable instance = this;
            Connexion.O().downloadImage("snapshot", new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    _imageView.setImageBitmap(response);
                    _progressBar.setVisibility(View.INVISIBLE);
                    if(_weekdaySpinner.getSelectedItemPosition() == 0){
                        refreshHandler.postDelayed(instance, 1000);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    refreshHandler.postDelayed(instance, 10000);
                }
            });

        }
    };

    public TimelapseViewFragment() {
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
        return inflater.inflate(R.layout.fragment_timelapse_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        if (_weekdaySpinner.getSelectedItemPosition() > 0)
            refreshHandler.post(refreshDirect);

        MediaController videoController = new MediaController(getContext());
        videoController.setMediaPlayer(_videoView);
        videoController.setAnchorView(_videoView);

        WeekDayTypesAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.timelapse_weekday_select, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        WeekDayTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        _weekdaySpinner.setAdapter(WeekDayTypesAdapter);

        _selectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                videoFileName = videoFileNames[position];
                videoFilePath = videoFilePaths[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                videoFileName = null;
            }
        });

        _weekdaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectWeekDayMode(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        _wallplugButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                HashMap<String, String> params = new HashMap<>();

                params.put("state", isChecked ? "1" : "0");
                params.put("t", "60"); // ignoré si state == 0

                Connexion.O().sendPostRequest("/powerplug", params, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                    }
                }, null);
            }
        });

    }

    private void selectWeekDayMode(int position) {

        switch (position){
            case 1:
                feed = "day";
                break;
            case 2 :
                feed = "week";
                break;
            default: // direct
                feed = "direct";

        }

        if (feed.equals("direct")){
            _videoView.setVisibility(View.GONE);
            _imageView.setVisibility(View.VISIBLE);
            _selectSpinner.setVisibility(View.GONE);
            _downloadButton.setVisibility(GONE);
            _loadButton.setVisibility(GONE);
            refreshHandler.post(refreshDirect);
//                    Connexion.O().downloadImage("snapshot", new Response.Listener<Bitmap>() {
            return;
        }else {
            refreshHandler.removeCallbacks(refreshDirect);
            _videoView.setVisibility(View.VISIBLE);
            _imageView.setVisibility(View.GONE);
            _selectSpinner.setVisibility(View.VISIBLE);
            _downloadButton.setVisibility(View.VISIBLE);
            _loadButton.setVisibility(View.VISIBLE);
        }

        Connexion.O(getContext()).sendGetRequest("/timelapses/" + feed, null, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {

                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> paths = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);


                        names.add(jsonObject.getString("name"));
                        paths.add(jsonObject.getString("path"));
                    }

                    videoFileNames = names.toArray(new String[0]);
                    videoFilePaths = paths.toArray(new String[0]);

                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                            (getContext(), android.R.layout.simple_spinner_item, videoFileNames); //selected item will look like a spinner set from XML
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                            .simple_spinner_dropdown_item);
                    _selectSpinner.setAdapter(spinnerArrayAdapter);

                } catch (JSONException e) {

                    Log.e(TAG, "onResponse: ", e);
                }


            }
        }, null);

    }


    @OnClick(R.id.loadButton)
    public void onLoadButtonPressed(){

        if (feed.equals("direct")) return;


        _progressBar.setVisibility(View.VISIBLE);

        Uri uri = Connexion.O().getTimelapseURI(videoFilePath, feed);

        if (uri == null) return;

        _videoView.setVideoURI(uri);
        _videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                _progressBar.setVisibility(GONE);
                _videoView.start();
            }
        });

    }

    @OnClick(R.id.downloadButton)
    public void onDownloadButtonPressed(){
        Uri uri = Connexion.O().getTimelapseURI(videoFilePath, feed);
        Connexion.O(getContext()).downloadFile(uri, videoFileName, "Timelapse eGarden");
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshHandler.removeCallbacks(refreshDirect);
        Connexion.cancellAll();
    }


}
