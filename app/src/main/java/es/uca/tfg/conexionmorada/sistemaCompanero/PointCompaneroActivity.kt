package es.uca.tfg.conexionmorada.sistemaCompanero

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.cmSocial.activities.PerfilSocialActivity
import es.uca.tfg.conexionmorada.sistemaCompanero.data.PayloadPuntoCompanero
import es.uca.tfg.conexionmorada.utils.firestore.User
import es.uca.tfg.conexionmorada.utils.retrofit.APIRetrofit
import es.uca.tfg.conexionmorada.utils.storage.Storage

class PointCompaneroActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var id: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point_companero)
        id = intent.getStringExtra("id").toString()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        var accept = findViewById<AppCompatButton>(R.id.btnAcept)
        accept.setOnClickListener {
            var call = APIRetrofit(this).aceptPuntoCompanero(id, FirebaseAuth.getInstance().currentUser!!.uid)
            call.enqueue(object : retrofit2.Callback<Boolean> {
                override fun onResponse(
                    call: retrofit2.Call<Boolean>,
                    response: retrofit2.Response<Boolean>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@PointCompaneroActivity, "Punto compañero aceptado, revisa tus chats para obtener más información", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@PointCompaneroActivity, "Ha ocurrido un error, el punto compañero no existe o ya ha sido aceptado", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }

                override fun onFailure(call: retrofit2.Call<Boolean>, t: Throwable) {
                    Toast.makeText(this@PointCompaneroActivity, "Ha ocurrido un error con la comunicación con el servidor", Toast.LENGTH_SHORT).show()
                }
            })
        }

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

        var call = APIRetrofit(this).getPuntoCompaneroById(id)
        call.enqueue(object : retrofit2.Callback<PayloadPuntoCompanero> {
            override fun onResponse(
                call: retrofit2.Call<PayloadPuntoCompanero>,
                response: retrofit2.Response<PayloadPuntoCompanero>
            ) {
                if (response.isSuccessful) {
                    if(response.body() != null){
                        val markerOptionsOrigin = MarkerOptions()
                            .position(LatLng(response.body()!!.markerOrigenLatitud.toDouble(), response.body()!!.markerOrigenLongitud.toDouble()))
                            .title(response.body()!!.markerOrigenTitulo)
                            .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarkerBitmap()!!))
                        mMap.addMarker(markerOptionsOrigin)

                        val markerOptionsDestino = MarkerOptions()
                            .position(LatLng(response.body()!!.markerDestinoLatitud.toDouble(), response.body()!!.markerDestinoLongitud.toDouble()))
                            .title(response.body()!!.markerDestinoTitulo)
                            .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarkerBitmap()!!))
                        mMap.addMarker(markerOptionsDestino)

                        val latLng = LatLng(response.body()!!.markerDestinoLatitud.toDouble(), response.body()!!.markerDestinoLongitud.toDouble())
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))

                        directions(markerOptionsOrigin, markerOptionsDestino)
                        if(response.body()!!.uuidAceptante != null  || response.body()!!.uuidSolicitante.equals(FirebaseAuth.getInstance().uid.toString())){
                            var accept = findViewById<AppCompatButton>(R.id.btnAcept)
                            accept.visibility = View.INVISIBLE
                        }

                        var cardViewSolicitante = findViewById<CardView>(R.id.cardViewSolicitante)
                        var cardViewAceptante = findViewById<CardView>(R.id.cardViewAceptante)
                        var textViewSolicitante = findViewById<TextView>(R.id.txtSolicitante)
                        var textViewAceptante = findViewById<TextView>(R.id.txtAceptante)
                        var imgSolicitante = findViewById<ImageView>(R.id.PerfilSolicitante)
                        var imgAceptante = findViewById<ImageView>(R.id.PerfilAceptante)

                        //datos solicitante
                        cardViewSolicitante.setOnClickListener(View.OnClickListener {
                            var intent = Intent(this@PointCompaneroActivity, PerfilSocialActivity::class.java)
                            intent.putExtra("uuid", response.body()!!.uuidSolicitante)
                            startActivity(intent)
                        })
                        var data = User.getUsername(response.body()!!.uuidSolicitante)
                        data.addOnSuccessListener { document ->
                            if (document != null) {
                                textViewSolicitante.text = document.getString("username")
                            }
                        }
                        Storage().photoAccount(imgSolicitante, response.body()!!.uuidSolicitante)

                        if(response.body()!!.uuidAceptante == null) {
                            textViewAceptante.text = "Sin aceptar"
                        }
                        else{
                            //datos aceptante
                            cardViewAceptante.setOnClickListener(View.OnClickListener {
                                var intent = Intent(this@PointCompaneroActivity, PerfilSocialActivity::class.java)
                                intent.putExtra("uuid", response.body()!!.uuidAceptante)
                                startActivity(intent)
                            })

                            data = User.getUsername(response.body()!!.uuidAceptante)
                            data.addOnSuccessListener { document ->
                                if (document != null) {
                                    textViewAceptante.text = document.getString("username")
                                }
                            }

                            Storage().photoAccount(imgAceptante, response.body()!!.uuidAceptante)
                        }

                        var txtDateCreation = findViewById<TextView>(R.id.txtDateCreation)
                        var txtDateEvento = findViewById<TextView>(R.id.txtDateEvento)
                        txtDateCreation.text = "Fecha de creación: " + response.body()!!.dateCreated
                        txtDateEvento.text = "Fecha de inicio de evento: "  + response.body()!!.dateEvento
                    }
                }
            }

            override fun onFailure(
                call: retrofit2.Call<PayloadPuntoCompanero>,
                t: Throwable
            ) {
                Toast.makeText(this@PointCompaneroActivity, "Error al cargar los puntos", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun createCustomMarkerBitmap(): Bitmap? {
        val markerView: View =
            LayoutInflater.from(this).inflate(es.uca.tfg.conexionmorada.R.layout.punto_companero_layout, null)
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


    private fun directions(originMarker: MarkerOptions?, destinationMarker: MarkerOptions?) {

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