package es.uca.tfg.conexionmorada.sistemaCompanero

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.sistemaCompanero.data.PayloadPuntoCompanero
import es.uca.tfg.conexionmorada.utils.Utils
import es.uca.tfg.conexionmorada.utils.retrofit.APIRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
import java.util.Locale


class AddPointActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private var editTextDate: EditText? = null
    private var editTextTime: EditText? = null
    private var myCalendar: Calendar? = null
    private var timeCalendar: Calendar? = null
    private var originMarker: Marker? = null
    private var destinationMarker: Marker? = null

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
            val datePicker = DatePickerDialog(
                this,
                dateSetListener,
                myCalendar!!.get(Calendar.YEAR),
                myCalendar!!.get(Calendar.MONTH),
                myCalendar!!.get(Calendar.DAY_OF_MONTH)
            )

            // Establecer la fecha mínima como la fecha actual
            val currentDate = Calendar.getInstance()
            datePicker.datePicker.minDate = currentDate.timeInMillis

            datePicker.show()
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

        addPoint()
        exit()
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
                val markerOptions = MarkerOptions()
                    .position(latLng)
                    .title("Origen")
                    .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarkerBitmap()!!))
                originMarker = mMap.addMarker(markerOptions)

                /*Utils.createCustomMarkerBitmap(this@AddPointActivity, FirebaseAuth.getInstance().uid.toString()) { bitmap ->
                    if (bitmap != null) {
                        val markerOptions = MarkerOptions()
                            .position(latLng)
                            .title("Origen")
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        originMarker = mMap.addMarker(markerOptions)
                    } else {
                        // Manejar el caso en que el bitmap sea nulo
                    }
                }*/

            } else if (destinationMarker == null) {
                // Si no hay un marcador de destino, crea uno en la ubicación seleccionada
                val markerOptions = MarkerOptions()
                    .position(latLng)
                    .title("Destino")
                    .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarkerBitmap()!!))
                destinationMarker = mMap.addMarker(markerOptions)

                /*Utils.createCustomMarkerBitmap(this@AddPointActivity, FirebaseAuth.getInstance().uid.toString()) { bitmap ->
                    if (bitmap != null) {
                        val markerOptions = MarkerOptions()
                            .position(latLng)
                            .title("Destino")
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        destinationMarker = mMap.addMarker(markerOptions)
                    } else {
                        // Manejar el caso en que el bitmap sea nulo
                    }
                }*/

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


    private fun createCustomMarkerBitmap(): Bitmap? {
        val markerView: View =
            LayoutInflater.from(this).inflate(es.uca.tfg.conexionmorada.R.layout.punto_companero_layout, null)
        val userPhotoImageView = markerView.findViewById<ImageView>(R.id.userPhotoImageView)
        userPhotoImageView.visibility = View.INVISIBLE
        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        markerView.layout(0, 0, markerView.measuredWidth, markerView.measuredHeight)
        markerView.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(
            markerView.measuredWidth,
            markerView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        markerView.draw(canvas)
        return bitmap
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
                        .color(ContextCompat.getColor(this@AddPointActivity, R.color.morada_main)) // Establecer el color de la línea

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

    @RequiresApi(Build.VERSION_CODES.N)
    private fun addPoint() {
        var btnAddPoint = findViewById<AppCompatButton>(R.id.addPoint)
        //click listener
        btnAddPoint.setOnClickListener {
            //input validation
            if (editTextDate!!.text.toString().isEmpty()) {
                Toast.makeText(this, "Por favor, introduce una fecha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (editTextTime!!.text.toString().isEmpty()) {
                Toast.makeText(this, "Por favor, introduce una hora", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (originMarker == null) {
                Toast.makeText(this, "Por favor, seleccione un punto de  origen", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (destinationMarker == null) {
                Toast.makeText(this, "Por favor, seleccione un punto de destino", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            //get current user
            val user = FirebaseAuth.getInstance().currentUser


            //get current date
            val date = editTextDate?.text.toString()
            val time = editTextTime?.text.toString()

            val dateTimeString = "$date $time"
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val dateTime: Date = dateFormat.parse(dateTimeString)

            //create new point
            var payloadPuntoCompanero = PayloadPuntoCompanero(
                null, //idPunto
                user!!.uid, //idSolicitante
                null,   //idAceptante
                originMarker!!.position.latitude.toString(),
                originMarker!!.position.longitude.toString(),
                originMarker!!.title,
                destinationMarker!!.position.latitude.toString(),
                destinationMarker!!.position.longitude.toString(),
                destinationMarker!!.title,
                Date().toString(),
                null,
                dateTime.toString()
            )

            var call = APIRetrofit(this).addPuntoCompanero(payloadPuntoCompanero)
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if(response.isSuccessful){
                        Toast.makeText(this@AddPointActivity, "Punto compañero creado correctamente", Toast.LENGTH_SHORT).show()
                        setResult(Activity.RESULT_OK) // Establecer el resultado como "RESULT_OK"
                        finish()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@AddPointActivity, "Ha ocurrido un error al crear el punto compañero, por favor intentélo de nuevo", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun exit(){
        var exit = findViewById<ImageView>(R.id.Exit)
        exit.setOnClickListener {
            finish()
        }
    }
}