package es.uca.tfg.conexionmorada.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import es.uca.tfg.conexionmorada.sistemaCompanero.AddPointActivity
import es.uca.tfg.conexionmorada.sistemaCompanero.PointCompaneroActivity
import es.uca.tfg.conexionmorada.sistemaCompanero.ZonaSistemaCompaneroActivity
import es.uca.tfg.conexionmorada.sistemaCompanero.data.PayloadPuntoCompanero
import es.uca.tfg.conexionmorada.utils.retrofit.APIRetrofit


class SistemaCompaneroFragment : Fragment() {
    private lateinit var mMap: GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Obtener permiso para la ubicación del usuario
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         return inflater.inflate(es.uca.tfg.conexionmorada.R.layout.fragment_sistema_companero, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Inicializar el mapa
        val mapFragment = childFragmentManager.findFragmentById(es.uca.tfg.conexionmorada.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            mMap = googleMap
            mMap.uiSettings.isZoomControlsEnabled = false
            checkLocationPermissionAndSetCenter()
            //onmarkerClick
           /* mMap.setOnMarkerClickListener { marker ->
                // Aquí puedes agregar el código para abrir otra actividad al hacer clic en el marcador
                if (marker.title == "Mi marcador") {
                    val intent = Intent(context, PointCompaneroActivity::class.java)
                    startActivity(intent)
                }

                // Si quieres que el comportamiento predeterminado de los marcadores también se ejecute, devuelve false
                // De lo contrario, devuelve true para indicar que ya se ha manejado el clic en el marcador
                true
            }*/

        }

        val addButon = view?.findViewById<View>(es.uca.tfg.conexionmorada.R.id.add)
        addButon?.setOnClickListener {
            val intent = Intent(requireContext(), AddPointActivity::class.java)
            startActivity(intent)
        }

        val more = view?.findViewById<View>(es.uca.tfg.conexionmorada.R.id.zona_companero)
        more?.setOnClickListener {
            val intent = Intent(requireContext(), ZonaSistemaCompaneroActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, centrar el mapa en la ubicación
                checkLocationPermissionAndSetCenter()
            } else {
                // Permiso denegado, mostrar mensaje al usuario
                Toast.makeText(requireContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLocationPermissionAndSetCenter() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // El permiso de ubicación no está concedido, no se puede centrar el mapa en la ubicación
            return
        }
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                /*val markerOptions = MarkerOptions()
                    .position(LatLng(location.latitude, location.longitude))
                    .title("Mi marcador")
                    .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarkerBitmap()!!))
                mMap.addMarker(markerOptions)*/
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
            }
        }

        var call = APIRetrofit().getAllPuntoCompaneroActive()
        call.enqueue(object : retrofit2.Callback<List<PayloadPuntoCompanero>> {
            override fun onResponse(
                call: retrofit2.Call<List<PayloadPuntoCompanero>>,
                response: retrofit2.Response<List<PayloadPuntoCompanero>>
            ) {
                if (response.isSuccessful) {
                    val puntos = response.body()
                    if (puntos != null) {
                        for (punto in puntos) {
                           val markerOptions = MarkerOptions()
                                .position(LatLng(punto.markerDestinoLatitud.toDouble(), punto.markerDestinoLongitud.toDouble()))
                                .title(punto.id)
                                .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarkerBitmap()!!))
                            mMap.addMarker(markerOptions)
                        }

                        mMap.setOnMarkerClickListener { marker ->
                                val intent = Intent(context, PointCompaneroActivity::class.java)
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
                Toast.makeText(requireContext(), "Error al cargar los puntos", Toast.LENGTH_SHORT).show()
            }
        })


    }


    private fun createCustomMarkerBitmap(): Bitmap? {
        val markerView: View =
            LayoutInflater.from(context).inflate(es.uca.tfg.conexionmorada.R.layout.punto_companero_layout, null)
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
}