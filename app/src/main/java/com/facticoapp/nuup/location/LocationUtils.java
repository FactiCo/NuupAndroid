package com.facticoapp.nuup.location;

import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import com.facticoapp.nuup.models.Address;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Edgar Z. on 6/20/16.
 */

public class LocationUtils {

    public static boolean isGpsOrNetworkProviderEnabled(Context context) {
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // If enabled GPS_PROVIDER or NETWORK_PROVIDER return true
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public static LatLng getLatLngFromLocation(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    public static String getAddressFromLatLong(Context context, double latitude, double longitude) {
        Geocoder geocoder;
        List<android.location.Address> addresses;
        geocoder = new Geocoder(context, new Locale("es", "MX"));

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if (addresses != null && addresses.size() > 0) {
                android.location.Address address = addresses.get(0);

                String addressLine = address.getAddressLine(0) != null ? address.getAddressLine(0) + " " : ""; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = address.getLocality() != null ? address.getLocality() + " " : "";
                String state = address.getAdminArea() != null ? address.getAdminArea() + " " : "";
                String country = address.getCountryName() != null ? address.getCountryName() + " " : "";
                String postalCode = address.getPostalCode() != null ? address.getPostalCode() + " " : "";
                String knownName = address.getFeatureName() != null ? address.getFeatureName() + " " : ""; // Only if available else return NULL

                Address addressBean = new Address();
                addressBean.setAddress(addressLine);
                addressBean.setCity(city);
                addressBean.setState(state);
                addressBean.setCountry(country);
                addressBean.setPostalCode(postalCode);
                addressBean.setKnownName(knownName);

                return addressLine + city + postalCode + state + country;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
