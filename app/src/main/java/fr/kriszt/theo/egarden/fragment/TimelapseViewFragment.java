package fr.kriszt.theo.egarden.fragment;

import android.content.Context;
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
import android.widget.Button;
import android.widget.CompoundButton;
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
    private OnFragmentInteractionListener mListener;
    @BindView(R.id.videoView) VideoView _videoView;
    @BindView(R.id.video_progressBar) ProgressBar _progressBar;
    @BindView(R.id.weekDaySpinner) Spinner _weekdaySpinner;
    @BindView(R.id.selectSpinner) Spinner _selectSpinner;
    @BindView(R.id.imageView) ImageView _imageView;
    @BindView(R.id.loadButton) Button _loadButton;
    @BindView(R.id.wallplugButton) ToggleButton _wallplugButton;

    Handler refreshHandler = new Handler();

    // video mp4 d'exemple
    String vidAddress = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
    Uri vidUri = Uri.parse(vidAddress);

    private Runnable refreshDirect = new Runnable() {
        @Override
        public void run() {

            final Runnable instance = this;
            Connexion.O().downloadImage("snapshot", new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    _imageView.setImageBitmap(response);
                    _progressBar.setVisibility(View.INVISIBLE);
                    refreshHandler.postDelayed(instance, 1000);
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

    public static TimelapseViewFragment newInstance() {
        TimelapseViewFragment fragment = new TimelapseViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        return inflater.inflate(R.layout.fragment_timelapse_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        refreshHandler.post(refreshDirect);

        MediaController videoController = new MediaController(getContext());
        videoController.setAnchorView(_videoView);
        _videoView.setVideoURI(vidUri);
        _videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                _progressBar.setVisibility(GONE);
            }
        });
//        _videoView.start();

        WeekDayTypesAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.timelapse_weekday_select, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        WeekDayTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        _weekdaySpinner.setAdapter(WeekDayTypesAdapter);

        _weekdaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getContext(), "Item selected  : " + position, Toast.LENGTH_SHORT).show();
                String type = "direct";
                switch (position){
                    case 1:
                        type = "day";
                        break;
                    case 2 :
                        type = "week";
                        break;
                    default: // direct

                }

                if (type.equals("direct")){
                    _videoView.setVisibility(View.INVISIBLE);
                    _imageView.setVisibility(View.VISIBLE);
                    _selectSpinner.setVisibility(View.INVISIBLE);
                    refreshHandler.post(refreshDirect);
//                    Connexion.O().downloadImage("snapshot", new Response.Listener<Bitmap>() {
                    return;
                }else {
                    refreshHandler.removeCallbacks(refreshDirect);
                    _videoView.setVisibility(View.VISIBLE);
                    _imageView.setVisibility(View.INVISIBLE);
                    _selectSpinner.setVisibility(View.VISIBLE);
//                    Toast.makeText(getContext(), "TODO : lister les timelapses disponibles", Toast.LENGTH_SHORT).show();
                }

                Connexion.O(getContext()).sendGetRequest("/timelapses/" + type, null, new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
//                        SelectTimelapseAdapter = ArrayAdapter.createFromResource(getContext(),
//                                R.array.timelapse_weekday_select, android.R.layout.simple_spinner_item);
//                        // Specify the layout to use when the list of choices appears
//                        SelectTimelapseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        // Apply the adapter to the spinner

                        ArrayList<String> names = new ArrayList<>();
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                names.add(jsonArray.getString(i));
                            }
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                                    (getContext(), android.R.layout.simple_spinner_item, (String[]) names.toArray()); //selected item will look like a spinner set from XML
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                                    .simple_spinner_dropdown_item);
                            _selectSpinner.setAdapter(spinnerArrayAdapter);

                        } catch (JSONException e) {

                            Log.e(TAG, "onResponse: ", e);
                        }

                        finally {
                            Toast.makeText(getContext(), names.toString(), Toast.LENGTH_SHORT).show();
                        }



                    }
                }, null);
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


    @OnClick(R.id.loadButton)
    public void onLoadButtonPressed(){
        Toast.makeText(getContext(), "TODO  charger la video correspondante", Toast.LENGTH_SHORT).show();
    }

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
