package es.uca.tfg.conexionmorada.sistemaCompanero

import android.Manifest
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import es.uca.tfg.conexionmorada.R
import java.util.Locale


class AddPointActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private var editTextDate: EditText? = null
    private var editTextTime: EditText? = null
    private var myCalendar: Calendar? = null
    private var timeCalendar: Calendar? = null
    private var originMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private lateinit var googleApiClient: GoogleApiClient


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(es.uca.tfg.conexionmorada.R.layout.activity_add_point)
        val mapFragment = supportFragmentManager.findFragmentById(es.uca.tfg.conexionmorada.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        editTextDate = findViewById<EditText>(es.uca.tfg.conexionmorada.R.id.editTextDate)
        myCalendar = Calendar.getInstance()

        val dateSetListener =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                myCalendar!!.set(Calendar.YEAR, year)
                myCalendar!!.set(Calendar.MONTH, monthOfYear)
                myCalendar!!.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabel()
            }

        editTextDate!!.setOnClickListener(View.OnClickListener {
            DatePickerDialog(
                this, dateSetListener, myCalendar!!.get(
                    Calendar.YEAR
                ), myCalendar!!.get(Calendar.MONTH), myCalendar!!.get(Calendar.DAY_OF_MONTH)
            ).show()
        })

        editTextTime = findViewById(R.id.editTextTime);
        timeCalendar = Calendar.getInstance();

        val timeSetListener =
            OnTimeSetListener { view, hourOfDay, minute ->
                myCalendar!!.set(Calendar.HOUR_OF_DAY, hourOfDay)
                myCalendar!!.set(Calendar.MINUTE, minute)
                updateLabelTime()
            }

        editTextTime!!.setOnClickListener(View.OnClickListener {
            TimePickerDialog(
                this, timeSetListener, myCalendar!!.get(
                    Calendar.HOUR_OF_DAY
                ), myCalendar!!.get(Calendar.MINUTE), true
            ).show()
        })
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateLabel() {
        val myFormat = "dd/MM/yyyy" // Formato de fecha
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        editTextDate!!.setText(sdf.format(myCalendar!!.time))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateLabelTime() {
        val myFormat = "HH:mm" // Formato de hora
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        editTextTime!!.setText(sdf.format(myCalendar!!.time))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = false

        mMap.setOnMapClickListener(OnMapClickListener { latLng ->
            if (originMarker == null) {
                // Si no hay un marcador de origen, crea uno en la ubicación seleccionada
                originMarker = mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title("Origen")
                )
            } else if (destinationMarker == null) {
                // Si no hay un marcador de destino, crea uno en la ubicación seleccionada
                destinationMarker = mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title("Destino")
                )
                directions()
            } else {
                // Ya hay un marcador de origen y uno de destino, no se permite añadir más
                Toast.makeText(
                    this,
                    "Ya se han añadido un origen y un destino.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        mMap.setOnMarkerClickListener(OnMarkerClickListener { marker ->
            if (marker == originMarker) {
                // El usuario ha hecho clic en el marcador de origen, puedes implementar la lógica para actualizarlo
                // ...
                return@OnMarkerClickListener true
            } else if (marker == destinationMarker) {
                // El usuario ha hecho clic en el marcador de destino, puedes implementar la lógica para actualizarlo
                // ...
                return@OnMarkerClickListener true
            }
            false
        })

        setCenterLocation()
    }

    private fun setCenterLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
            }
        }
    }

    private fun directions() {

        val apiKey = getApiKeyFromManifest()
        val geoApiContext = GeoApiContext.Builder()
            .apiKey(apiKey)
            .build()

        val request = DirectionsApi.getDirections(
            geoApiContext,
            "${originMarker!!.position.latitude},${originMarker!!.position.longitude}",
            "${destinationMarker!!.position.latitude},${destinationMarker!!.position.longitude}"
        ).mode(TravelMode.WALKING)

        request.setCallback(object : PendingResult.Callback<DirectionsResult> {
            override fun onResult(result: DirectionsResult?) {
                if (result != null) {
                    val legs = result.routes[0].legs
                    val duration = legs[0].duration

                    val minutes = duration.inSeconds / 60
                    runOnUiThread {
                        Toast.makeText(
                            this@AddPointActivity,
                            "Tiempo aproximado: ${minutes} minutos",
                            Toast.LENGTH_SHORT
                        ).show()
                        var timeExpected = findViewById<TextView>(R.id.timeExpected)
                        timeExpected.text = "Tiempo de llegada estimado: ${minutes} minutos"

                    }

                    val polylineOptions = PolylineOptions()
                    for (step in result.routes[0].legs[0].steps) {
                        val startLocation = LatLng(
                            step.startLocation.lat,
                            step.startLocation.lng
                        )
                        polylineOptions.add(startLocation)
                    }

                    runOnUiThread {
                        mMap.addPolyline(polylineOptions)
                    }
                }
            }

            override fun onFailure(e: Throwable?) {
                e?.printStackTrace()
            }
        })

    }

    private fun getApiKeyFromManifest(): String {
        try {
            val ai = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val bundle = ai.metaData
            return bundle.getString("com.google.android.geo.API_KEY") ?: ""
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        return ""
    }
}