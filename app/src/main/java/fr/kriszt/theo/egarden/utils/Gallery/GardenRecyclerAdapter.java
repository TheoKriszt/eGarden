package fr.kriszt.theo.egarden.utils.Gallery;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;


import java.util.List;
import com.android.volley.toolbox.ImageLoader;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

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

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder Viewholder, int position) {

        GardenAdapter gardenAdapterOBJ =  gardenHistory.get(position);

        imageLoader = ImageAdapter.getInstance(context).getImageLoader();
        Log.e(TAG, "onBindViewHolder: Viewholder.VolleyImageView =" + Viewholder.VolleyImageView);
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
        Log.e(TAG, "gardenHistory.sizeo() == " + gardenHistory.size());
//
//
//
//        Viewholder.itemLayout.setBackgroundColor(
//                gardenAdapterOBJ.isThirsty() ?
//                        Color.rgb(220, 120, 120) :
//                        Color.rgb(46, 185, 113)
//        );
//
//        if (gardenAdapterOBJ.isAutoWatering()){
//            // mettre l'icone auto watering : ON
//            int iconResource = context.getResources().getIdentifier(
//                    "auto_on", "drawable", context.getPackageName() );
//            Viewholder.autoIconView.setImageResource(iconResource);

//        }



    }

    @Override
    public int getItemCount() {
        return gardenHistory.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView ImageTitleTextView;
        public NetworkImageView VolleyImageView ;
        public ImageView autoIconView ;
        public RelativeLayout itemLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ImageTitleTextView = (TextView) itemView.findViewById(R.id.ImageNameTextView) ;

            VolleyImageView = (NetworkImageView) itemView.findViewById(R.id.VolleyImageView) ;

//            autoIconView = itemView.findViewById(R.id.auto_watering_icon);
//            autoIconView.setVisibility(View.INVISIBLE);
//            itemLayout = itemView.findViewById(R.id.plantItemLayout);


        }
    }
}