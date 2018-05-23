package fr.kriszt.theo.egarden.fragment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.os.Bundle;
import org.json.JSONArray;
import java.util.ArrayList;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.kriszt.theo.egarden.R;
import fr.kriszt.theo.egarden.utils.Connexion;
import fr.kriszt.theo.egarden.utils.Gallery.PlantAdapter;
import fr.kriszt.theo.egarden.utils.Gallery.PlantsRecyclerAdapter;

import static fr.kriszt.theo.egarden.activity.MainActivity.CURRENT_TAG;

public class PlantsListFragment extends Fragment {

    public static final String TAG = "PlantsFragment";

    private List<PlantAdapter> adaptedPlants = new ArrayList<>();

    private RecyclerView recyclerView;

    @BindView(R.id.plantsList_progressBar) ProgressBar _progressBar;

    private static final String plants_url = "/plants";

    RecyclerView.LayoutManager recyclerLayoutManager;

    RecyclerView.Adapter recyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.imgs_recycled , container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {

        recyclerView = view.findViewById(R.id.plantsRecycler);

        recyclerView.setHasFixedSize(true);

        recyclerLayoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(recyclerLayoutManager);

        fetch_plants();

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener()
        {

            GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return true;
                }

            });
            @Override
            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

                View item = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if(item != null && gestureDetector.onTouchEvent(motionEvent)) {

                    //Getting RecyclerView Clicked Item value.
                    final int itemPosition = Recyclerview.getChildAdapterPosition(item);


                    Runnable mPendingRunnable = new Runnable() {
                        @Override
                        public void run() {
                            String plantId = adaptedPlants.get(itemPosition).getPlantId();
                            Fragment plantFragment = PlantDetailsFragment .newInstance(plantId);
                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.frame, plantFragment, PlantDetailsFragment.TAG);
                            CURRENT_TAG = PlantDetailsFragment.TAG;
                            fragmentTransaction.commitAllowingStateLoss();
                        }
                    };
                    Handler mHandler = new Handler();
                    if (mPendingRunnable != null) {
                        mHandler.post(mPendingRunnable);
                    }
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        ButterKnife.bind(this, view);


    }

    public void fetch_plants(){

        Connexion.O(getContext()).sendGetRequest(plants_url, null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray res = new JSONArray(response);
                    parsePlantsList(res);
                } catch (JSONException e) {
                    Log.e(TAG, "onResponse: ", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error fetching plants", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onErrorResponse: " + Connexion.O().decodeError(error));
            }
        });
    }

    public void parsePlantsList(JSONArray plants){

        for(int i = 0; i<plants.length(); i++) {

            try {

                JSONObject json;
                json = plants.getJSONObject(i);
                adaptedPlants.add(new PlantAdapter(json));

            } catch (JSONException e) {
                Log.e(TAG, "parse plants list : ", e);
            }
        }


        recyclerViewAdapter = new PlantsRecyclerAdapter(adaptedPlants, getView().getContext());

        recyclerView.setAdapter(recyclerViewAdapter);

        _progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        Connexion.cancellAll();
    }

}