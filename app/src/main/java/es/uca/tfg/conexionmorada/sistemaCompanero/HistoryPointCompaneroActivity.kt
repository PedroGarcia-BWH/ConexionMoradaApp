package es.uca.tfg.conexionmorada.sistemaCompanero

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.rpc.context.AttributeContext.Auth
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.sistemaCompanero.data.PayloadPuntoCompanero
import es.uca.tfg.conexionmorada.utils.Utils
import es.uca.tfg.conexionmorada.utils.retrofit.APIRetrofit

class HistoryPointCompaneroActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_point_companero)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        exit()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = false

        setCenterLocation()

        var call = APIRetrofit(this).getAllPuntoCompaneroByUuid(FirebaseAuth.getInstance().currentUser!!.uid)
        call.enqueue(object : retrofit2.Callback<List<PayloadPuntoCompanero>> {
            override fun onResponse(
                call: retrofit2.Call<List<PayloadPuntoCompanero>>,
                response: retrofit2.Response<List<PayloadPuntoCompanero>>
            ) {
                if (response.isSuccessful) {
                    val puntos = response.body()
                    if (puntos != null) {
                        for (punto in puntos) {
                            /*val markerOptions = MarkerOptions()
                                .position(LatLng(punto.markerDestinoLatitud.toDouble(), punto.markerDestinoLongitud.toDouble()))
                                .title(punto.id)
                                .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarkerBitmap()!!))
                            mMap.addMarker(markerOptions)*/

                            Utils.createCustomMarkerBitmap(this@HistoryPointCompaneroActivity, punto.uuidSolicitante) { bitmap ->
                                if (bitmap != null) {
                                    val markerOptions = MarkerOptions()
                                        .position(LatLng(punto.markerDestinoLatitud.toDouble(), punto.markerDestinoLongitud.toDouble()))
                                        .title(punto.id)
                                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                    mMap.addMarker(markerOptions)
                                }
                            }
                            val markerOptions = MarkerOptions()
                                .position(LatLng(punto.markerDestinoLatitud.toDouble(), punto.markerDestinoLongitud.toDouble()))
                                .title(punto.id)
                                .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarkerBitmapWithoutPhoto(this@HistoryPointCompaneroActivity)!!))
                            mMap.addMarker(markerOptions)
                        }

                        mMap.setOnMarkerClickListener { marker ->
                            val intent = Intent(this@HistoryPointCompaneroActivity, PointCompaneroActivity::class.java)
                            intent.putExtra("id", marker.title)
                            startActivity(intent)

                            true
                        }
                    }
                }
            }

            override fun onFailure(
                call: retrofit2.Call<List<PayloadPuntoCompanero>>,
                t: Throwable
            ) {
                Toast.makeText(this@HistoryPointCompaneroActivity, "Error al cargar los puntos", Toast.LENGTH_SHORT).show()
            }
        })
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

    private fun createCustomMarkerBitmapWithoutPhoto(context: Context): Bitmap? {
        val markerView: View =
            LayoutInflater.from(context).inflate(R.layout.punto_companero_layout, null)

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


    fun exit(){
        var exit = findViewById<ImageView>(R.id.exit)
        exit.setOnClickListener {
            finish()
        }
    }
}