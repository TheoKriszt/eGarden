package fr.kriszt.theo.egarden.utils.Gallery;


import org.json.JSONException;
import org.json.JSONObject;

public class PlantAdapter
{
    private String imageURL;
    private String plantName;
    private String plantId;
    private int hygrometry;
    private int threshold;
    private boolean autoWatering;

    public PlantAdapter(JSONObject json) throws JSONException {
        imageURL = json.getString("imgURI");
        plantName = json.getString("name");
        plantId = json.getString("id");
        threshold = json.getInt("threshold");
        hygrometry = json.getInt("hygrometry");
        autoWatering = json.getInt("auto_watering") == 1;
    }


    public String getImageUrl() {
            return imageURL;
    }


    public String getPlantName() {
        return plantName;
    }


    public int getHygrometry() {
        return hygrometry;
    }

    public int getThreshold() {
        return threshold;
    }

    public boolean isThirsty() {
        return hygrometry < threshold;
    }

    public boolean isAutoWatering() {
        return autoWatering;
    }

    public String getPlantId() {
        return plantId;
    }
}