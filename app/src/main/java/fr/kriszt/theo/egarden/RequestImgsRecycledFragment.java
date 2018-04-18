package fr.kriszt.theo.egarden;
import android.support.annotation.Nullable;
import android.os.Bundle;
import org.json.JSONArray;
import java.util.ArrayList;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import fr.kriszt.theo.egarden.utils.Connexion;
import fr.kriszt.theo.egarden.utils.Gallery.PlantAdapter;
import fr.kriszt.theo.egarden.utils.Gallery.PlantsRecyclerAdapter;

public class RequestImgsRecycledFragment extends Fragment {

    private static final String TAG = "PlantsFragment";

    private List<PlantAdapter> adaptedPlants = new ArrayList<>();

    private RecyclerView recyclerView;

    private static final String plants_url = "/plants";

//    int RecyclerViewItemPosition ;

    RecyclerView.LayoutManager recyclerLayoutManager;

    RecyclerView.Adapter recyclerViewAdapter;

    ArrayList<String> ImageTitleNameArrayListForClick = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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

        // Implementing Click Listener on RecyclerView.
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
//                    RecyclerViewItemPosition =
                    int itemPosition = Recyclerview.getChildAdapterPosition(item);

                    // Showing RecyclerView Clicked Item value using Toast.
                    // Todo : get to given plant view
//                    Toast.makeText(item.getContext(), ImageTitleNameArrayListForClick.get(RecyclerViewItemPosition), Toast.LENGTH_LONG).show();
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


    }

    public void fetch_plants(){
        Connexion.O(getContext()).sendGetRequest(plants_url, null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray res = new JSONArray(response);
                    parsePlantsList(res);
                } catch (JSONException e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + Connexion.O().decodeError(error));
            }
        });
    }

    public void parsePlantsList(JSONArray plants){

        for(int i = 0; i<plants.length(); i++) {

//            PlantAdapter plantAdapter = new PlantAdapter();

            try {

                JSONObject json;
                json = plants.getJSONObject(i);
                adaptedPlants.add(new PlantAdapter(json));
//                plantAdapter.setPlantName(json.getString("name"));

                // Adding image title name in array to display on RecyclerView click event.
//                ImageTitleNameArrayListForClick.add(json.getString(Image_Name_JSON));



            } catch (JSONException e) {

                e.printStackTrace();
            }
//            adaptedPlants.add(plantAdapter);
        }

        recyclerViewAdapter = new PlantsRecyclerAdapter(adaptedPlants, getView().getContext());

        recyclerView.setAdapter(recyclerViewAdapter);
    }
}