package fr.kriszt.theo.egarden.utils.Gallery;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;


import java.util.List;
import com.android.volley.toolbox.ImageLoader;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import fr.kriszt.theo.egarden.R;

public class PlantsRecyclerAdapter extends RecyclerView.Adapter<PlantsRecyclerAdapter.ViewHolder> {

    Context context;

    List<PlantAdapter> adaptedPlants;

    ImageLoader imageLoader;

    public PlantsRecyclerAdapter(List<PlantAdapter> getPlantAdapter, Context context){
        super();
        this.adaptedPlants = getPlantAdapter;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder Viewholder, int position) {

        PlantAdapter plantAdapterOBJ =  adaptedPlants.get(position);

        imageLoader = ImageAdapter.getInstance(context).getImageLoader();

        imageLoader.get(plantAdapterOBJ.getImageUrl(),
                ImageLoader.getImageListener(
                        Viewholder.VollyImageView,//Server Image
                        R.mipmap.ic_launcher,//Before loading server image the default showing image.
                        android.R.drawable.ic_dialog_alert //Error image if requested image dose not found on server.
                )
        );

        Viewholder.VollyImageView.setImageUrl(plantAdapterOBJ.getImageUrl(), imageLoader);

        Viewholder.ImageTitleTextView.setText(plantAdapterOBJ.getPlantName());
        // TODO : paramtrer l'affichage

        if (plantAdapterOBJ.isThirsty()){
            // passer l'affichage en rouge
        }

        if (plantAdapterOBJ.isAutoWatering()){
            // mettre l'icone auto watering : ON
            int iconResource = context.getResources().getIdentifier(
                    "auto_on", "drawable", context.getPackageName() );
            Viewholder.autoIconView.setImageResource(iconResource);

        }



    }

    @Override
    public int getItemCount() {
        return adaptedPlants.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView ImageTitleTextView;
        public NetworkImageView VollyImageView ;
        public ImageView autoIconView ;

        public ViewHolder(View itemView) {
            super(itemView);
            ImageTitleTextView = itemView.findViewById(R.id.ImageNameTextView);
            VollyImageView = itemView.findViewById(R.id.VolleyImageView);
            autoIconView = itemView.findViewById(R.id.auto_watering_icon);
            // Todo : parametrer l'affichage

        }
    }
}