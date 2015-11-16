package com.refect.shared.models;

import android.util.Log;

import com.google.android.gms.wearable.DataMap;
import com.refect.shared.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by anelson on 8/4/15.
 */
public class PlaceModel implements Serializable {

    private String id;
    private String name;
    private LocationModel location;

    public PlaceModel() {

    }

    public PlaceModel(JSONObject json) {
        try {
            setId(json.getString("id"));
            setName(json.getString("name"));
            setLocation(new LocationModel(json.getJSONObject("location")));
        } catch(JSONException e) {
            Log.e("PlaceModel (constructor)", e.toString());
        }
    }

    public PlaceModel(DataMap dataMap) {
        setId(dataMap.getString("id"));
        setName(dataMap.getString("name"));
        setLocation(new LocationModel(dataMap.getDataMap("location")));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocationModel getLocation() {
        return location;
    }

    public void setLocation(LocationModel location) {
        this.location = location;
    }

    public DataMap toDataMap() {
        DataMap dataMap = new DataMap();

        dataMap.putString("id", getId());
        dataMap.putString("name", getName());
        dataMap.putDataMap("location", getLocation().toDataMap());

        return dataMap;
    }


    public class LocationModel implements Serializable {

        private String city;
        private String state;
        private String country;
        private String zip;
        private String street;
        private double longitude;
        private double latitude;

        public LocationModel() {

        }

        public LocationModel(JSONObject json) {
            try {
                setCity(Utils.checkJsonHasString("city", json));
                setState(Utils.checkJsonHasString("state", json));
                setStreet(Utils.checkJsonHasString("street", json));
                setZip(Utils.checkJsonHasString("zip", json));
                setCountry(Utils.checkJsonHasString("country", json));
                setLatitude(json.getDouble("latitude"));
                setLongitude(json.getDouble("longitude"));
            } catch(JSONException e) {
                Log.e("LocationModel (constructor)", e.toString());
            }
        }

        public LocationModel(DataMap dataMap) {
            setCity(dataMap.getString("city"));
            setState(dataMap.getString("state"));
            setStreet(dataMap.getString("street"));
            setZip(dataMap.getString("zip"));
            setCountry(dataMap.getString("country"));
            setLatitude(dataMap.getDouble("latitude"));
            setLongitude(dataMap.getDouble("longitude"));
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getZip() {
            return zip;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public DataMap toDataMap() {
            DataMap dataMap = new DataMap();

            dataMap.putString("city", getCity());
            dataMap.putString("state", getState());
            dataMap.putString("street", getStreet());
            dataMap.putString("zip", getZip());
            dataMap.putString("country", getCountry());
            dataMap.putDouble("latitude", getLatitude());
            dataMap.putDouble("longitude", getLongitude());

            return dataMap;
        }

    }
}
