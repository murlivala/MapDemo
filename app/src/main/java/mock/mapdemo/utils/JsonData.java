package mock.mapdemo.utils;

import android.os.Parcel;
import android.os.Parcelable;

import mock.mapdemo.Constants;

/**
 * Class to hold JSON data from server
 */
public class JsonData implements Parcelable {
    int id;
    String name;
    String car;
    String train;
    double lng;
    double lat;
    public JsonData(int i,String name,String cr, String t,double l,double lt){
        id = i;
        this.name = name;
        car = cr;
        train = t;
        lng = l;
        lat = lt;
    }

    public JsonData(){

    }

    public String getStringValue(int index){
        String result = "";
        switch (index){
            case Constants.NAME_KEY:
                result  = name;
                break;
            case Constants.CAR_KEY:
                result = car;
                break;
            case Constants.TRAIN_KEY:
                result = train;
                break;
        }
        return result;
    }

    public double getLng(){
        return lng;
    }

    public double getLat(){
        return lat;
    }

    public int describeContents() {
        return 0;
    }

    /** save object in parcel */
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(name);
        out.writeString(car);
        out.writeString(train);
        out.writeDouble(lng);
        out.writeDouble(lat);
    }

    public final Creator<JsonData> CREATOR
            = new Creator<JsonData>() {
        public JsonData createFromParcel(Parcel in) {
            return new JsonData(in);
        }

        public JsonData[] newArray(int size) {
            return new JsonData[size];
        }
    };

    /** recreate object from parcel */
    private JsonData(Parcel in) {
        id = in.readInt();
        name = in.readString();
        car = in.readString();
        train = in.readString();
        lng = in.readDouble();
        lat = in.readDouble();
    }
}