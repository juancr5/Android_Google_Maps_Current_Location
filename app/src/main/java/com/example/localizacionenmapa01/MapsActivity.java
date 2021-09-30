package com.example.localizacionenmapa01;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MapsActivity extends FragmentActivity {

    //Inicializamos el map fragment
    SupportMapFragment mapFragment;
    // Inicializamos el proveedor de ubicación fusionada
    FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        client = LocationServices.getFusedLocationProviderClient(this);
        // Se Obtiene el SupportMapFragment y recibe una notificación cuando el mapa esté listo para usarse.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);


        //Dexter es una biblioteca de Android que simplifica el proceso de solicitud de permisos en tiempo de ejecución.
        Dexter.withContext(getApplicationContext())
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            getLocation();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            //si se niega el permiso, navegará a la página de configuración predeterminada
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",getPackageName(),"");
                            intent.setData(uri);
                            startActivity(intent);
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        //sigue pidiendo permiso hasta que hayas aceptado
                        permissionToken.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();

    }//fin del Omcreate

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this
                , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //Obteniendo la ultima direccion
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {

                        // Añade un marcador al punto exacto y mueve la camara
                        Double latitude = location.getLatitude();
                        Double longitude = location.getLongitude();

                        LatLng latLng = new LatLng(latitude, longitude);
                        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                        markerOptions.title("Latitud: " + latitude + " / Longitude: " + longitude);

                        //Añadir el marcador de Google Map
                        googleMap.addMarker(markerOptions);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

                    }
                });
            }
        });
    }


}