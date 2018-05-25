package fr.kriszt.theo.egarden.utils.Gallery;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import fr.kriszt.theo.egarden.R;

public class GardenRecyclerAdapter extends RecyclerView.Adapter<GardenRecyclerAdapter.ViewHolder> {

    private static final String TAG = "GardenRecyclerAdapter";
    Context context;
    List<GardenAdapter> gardenHistory;
    ImageLoader imageLoader;

    public GardenRecyclerAdapter(List<GardenAdapter> getGardenAdapter, Context context){
        super();
        this.gardenHistory = getGardenAdapter;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gardenview, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder Viewholder, int position) {

        GardenAdapter gardenAdapterOBJ =  gardenHistory.get(position);

        imageLoader = ImageAdapter.getInstance(context).getImageLoader();
        Log.i(TAG, "onBindViewHolder: Viewholder.VolleyImageView =" + Viewholder.VolleyImageView);
        imageLoader.get(gardenAdapterOBJ.getImageUrl(),
                ImageLoader.getImageListener(
                        Viewholder.VolleyImageView,//Server Image
                        R.mipmap.ic_launcher,//Before loading server image the default showing image.
                        android.R.drawable.ic_dialog_alert //Error image if requested image dose not found on server.

                )
        );

        Viewholder.VolleyImageView.setImageUrl(gardenAdapterOBJ.getImageUrl(), imageLoader);
        Log.d(TAG, gardenAdapterOBJ.getImageUrl());
        Viewholder.ImageTitleTextView.setText(gardenAdapterOBJ.getImageTitle());



    }

    @Override
    public int getItemCount() {
        return gardenHistory.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView ImageTitleTextView;
        public NetworkImageView VolleyImageView ;

        public ViewHolder(View itemView) {
            super(itemView);
            ImageTitleTextView = itemView.findViewById(R.id.ImageNameTextView);

            VolleyImageView = itemView.findViewById(R.id.VolleyImageView);

        }
    }
}