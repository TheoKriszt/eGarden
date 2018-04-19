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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.kriszt.theo.egarden.R;
import fr.kriszt.theo.egarden.utils.Connexion;
import fr.kriszt.theo.egarden.utils.Gallery.PlantAdapter;


public class PlantDetailsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "PlantDetailsFragment";

    private static final String PLANT_ID = "plantId";

    private String plantId;

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.plant_detail_name) TextView _plantName;
    @BindView(R.id.plant_detail_description) TextView _plantDescription;
    @BindView(R.id.plant_detail_state) TextView _plantState;
    @BindView(R.id.plant_detail_autowater_switch) Switch _autoSwitch;
    @BindView(R.id.plant_detail_auto_image) ImageView _autoImage;
    @BindView(R.id.plant_detail_threshold_seekbar) SeekBar _autoThresholdBar;
    @BindView(R.id.plant_detail_save_button) Button _saveButton;


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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_plant_details, container, false);

        loadPlantInformation();

        return view;
    }

    private void loadPlantInformation() {

        // Get plant information
        Connexion.O().sendGetRequest("/plant/" + plantId, null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    PlantAdapter adapter = new PlantAdapter(json); // populate a pre-made entity adapter
                    updateInfoView(adapter);
                } catch (JSONException e) {
                    Log.e(TAG, "onResponse: ", e);
                }
            }
        }, null);

        // Get plant hygrometry historic
        Connexion.O().sendGetRequest("/soil/" + plantId, null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
             // TODO : update graph & clear spinner
            }
        }, null);
    }

    private void updateInfoView(PlantAdapter adapter) {
        // TODO : update view...
    }

    @OnClick(R.id.plant_detail_save_button)
    public void onSaveButtonPressed(){
        Toast.makeText(getContext(), "Not implemented yet", Toast.LENGTH_SHORT).show();
    }

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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
