package fr.kriszt.theo.egarden.fragment;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.VideoView;
import android.widget.MediaController;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    String vidAddress = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
    Uri vidUri = Uri.parse(vidAddress);

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

        MediaController videoController = new MediaController(getContext());
        videoController.setAnchorView(_videoView);
        _videoView.setVideoURI(vidUri);
        _videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                _progressBar.setVisibility(GONE);
            }
        });
        _videoView.start();

        WeekDayTypesAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.timelapse_weekday_select, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        WeekDayTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        _weekdaySpinner.setAdapter(WeekDayTypesAdapter);

        _weekdaySpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String type = "direct";
                switch (position){
                    case 1: // week
                        type = "week";
                        break;
                    case 2 : // day
                        type = "day";
                        break;
                    default: // direct

                }

                Connexion.O(getContext()).sendGetRequest("/timelapses/" + type, null, new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
//                        SelectTimelapseAdapter = ArrayAdapter.createFromResource(getContext(),
//                                R.array.timelapse_weekday_select, android.R.layout.simple_spinner_item);
//                        // Specify the layout to use when the list of choices appears
//                        SelectTimelapseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        // Apply the adapter to the spinner

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            ArrayList<String> names = new ArrayList<>();

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



                    }
                }, null);
            }
        });

//        WeekDayTypesAdapter = ArrayAdapter.createFromResource(getContext(),
//                R.array.timelapse_weekday_select, android.R.layout.simple_spinner_item);
//        // Specify the layout to use when the list of choices appears
//        WeekDayTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        // Apply the adapter to the spinner
//        _weekdaySpinner.setAdapter(WeekDayTypesAdapter);



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
