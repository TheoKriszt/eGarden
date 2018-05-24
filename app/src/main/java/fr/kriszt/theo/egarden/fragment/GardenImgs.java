package fr.kriszt.theo.egarden.fragment;
//import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.os.Bundle;
import org.json.JSONArray;
import java.util.ArrayList;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.kriszt.theo.egarden.R;
import fr.kriszt.theo.egarden.utils.Connexion;
import fr.kriszt.theo.egarden.utils.Gallery.GardenAdapter;
import fr.kriszt.theo.egarden.utils.Gallery.GardenRecyclerAdapter;

public class GardenImgs extends Fragment {

    private static final String TAG = "GardenImgs";
    List<GardenAdapter> listOfdataAdapter;

    RecyclerView recyclerView;

    final String garden_imgs_url = "http://sparklife.freeboxos.fr/garden/imgs";
//    String garden_imgs_url = "http://michaelCorp.zzzz.io:1880/garden/imgs";
    final String  garden_imgs_uri = "/garden/imgs";

    final String image_Name_JSON = "image_title";
    final String image_URL_JSON = "image_url";

    //Single selected image url storage for download
    GardenAdapter current_selected_image = null;

    RequestQueue requestQueue ;

    //View view ;
    @BindView(R.id.download_garden_img_button)
    ImageButton downloadGardenImgButton;
    int recyclerViewItemPosition ;

    RecyclerView.LayoutManager layoutManagerOfrecyclerView;

    RecyclerView.Adapter recyclerViewadapter;

    ArrayList<String> ImageTitleNameArrayListForClick;
    ImageView single_gardenview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        single_gardenview= (ImageView) inflater.inflate(R.layout.single_gardenview, container, false);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.garden_details , container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
      ButterKnife.bind(this,view);
        ImageTitleNameArrayListForClick = new ArrayList<>();

        listOfdataAdapter = new ArrayList<GardenAdapter>();
        single_gardenview=(ImageView)view.findViewById(R.id.single_gardenview);
        recyclerView = (RecyclerView) view.findViewById(R.id.gardenImgs
        );

        recyclerView.setHasFixedSize(true);

        final Context mContext = view.getContext();
        layoutManagerOfrecyclerView = new LinearLayoutManager(view.getContext());

        recyclerView.setLayoutManager(layoutManagerOfrecyclerView);

        JSON_HTTP_CALL();

        // Implementing Click Listener on RecyclerView.
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener()
        {

            GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {

                @Override public boolean onSingleTapUp(MotionEvent motionEvent) {

                    return true;
                }

            });
            @Override
            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

                View view = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());


                Log.e(TAG , "onInterceptTouchEvent : hello");

                if(view != null && gestureDetector.onTouchEvent(motionEvent)) {

                    //Getting RecyclerView Clicked Item value.
                    recyclerViewItemPosition = Recyclerview.getChildAdapterPosition(view);


                    ImageView little_gardenview=(ImageView) view.findViewById(R.id.VolleyImageView);

                    Bitmap bitmap = ((BitmapDrawable)little_gardenview.getDrawable()).getBitmap();
                    single_gardenview.setImageBitmap(bitmap);
                    single_gardenview.setVisibility(View.VISIBLE);
                    single_gardenview.setOnClickListener(new View.OnClickListener() {
                        //Click in lonely focused image
                        @Override
                        public void onClick(View v) {
                            single_gardenview.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            downloadGardenImgButton.setVisibility(View.GONE);

                        }
                    });
    //CLick over image in Recycled list
                    downloadGardenImgButton.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    downloadGardenImgButton.setVisibility(View.VISIBLE);

                    // Showing RecyclerView Clicked Item value using Toast.
                    Toast.makeText(view.getContext(), ImageTitleNameArrayListForClick.get(recyclerViewItemPosition) , Toast.LENGTH_LONG).show();
                    Log.e(TAG,"onInterceptToucheEvent : listOfdataAdapter.getImageUrl =  " + listOfdataAdapter.get(recyclerViewItemPosition).getImageUrl());
                    current_selected_image= listOfdataAdapter.get(recyclerViewItemPosition);
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

    public void onDestroy()
    {
        super.onDestroy();
        recyclerView.setAdapter(null);
    }
    public void JSON_HTTP_CALL(){

        Connexion.O(getContext()).sendGetRequest(garden_imgs_uri, null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray res = new JSONArray(response);
                    ParseJSonResponse(res);
                    //Toast.makeText(getContext(), "Reçu : " + res.length() + " elements", Toast.LENGTH_SHORT).show();
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


//        RequestOfJSonArray = new JsonArrayRequest(garden_imgs_url,
//
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//
//                        ParseJSonResponse(response);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                    }
//                });
//
//        requestQueue = Volley.newRequestQueue(this.getView().getContext());
//
//        requestQueue.add(RequestOfJSonArray);
    }

    public void ParseJSonResponse(JSONArray array){

        for(int i = 0; i<array.length(); i++) {

            GardenAdapter plantAdapter = new GardenAdapter();

            JSONObject json = null;
            try {

                json = array.getJSONObject(i);

                ImageTitleNameArrayListForClick.add(json.getString(image_Name_JSON));
                plantAdapter.setImageTitle(json.getString(image_Name_JSON));



                plantAdapter.setImageUrl(Connexion.O().getServerURL()+ json.getString(image_URL_JSON));
                listOfdataAdapter.add(plantAdapter);
                Log.e(TAG, "ParseJSonResponse: plantAdapter.getImageUrl " + plantAdapter.getImageUrl() );

            } catch (JSONException e) {

                e.printStackTrace();
            }
        }

        recyclerViewadapter = new GardenRecyclerAdapter(listOfdataAdapter, getView().getContext());


        recyclerView.setAdapter(recyclerViewadapter);
    }

    @OnClick(R.id.download_garden_img_button)
    public void onDownloadButtonPressed(){
        Connexion.O(getContext()).downloadFile(Uri.parse(current_selected_image.getImageUrl()),current_selected_image.getImageTitle(), null /*getResources().getString(R.string.garden_image_download_in_progress)*/);
    }



}