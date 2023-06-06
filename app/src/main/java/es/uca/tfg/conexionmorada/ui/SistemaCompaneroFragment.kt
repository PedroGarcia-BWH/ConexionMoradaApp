package es.uca.tfg.conexionmorada.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.sistemaCompanero.AddPointActivity
import es.uca.tfg.conexionmorada.sistemaCompanero.PointCompaneroActivity
import es.uca.tfg.conexionmorada.sistemaCompanero.ZonaSistemaCompaneroActivity
import es.uca.tfg.conexionmorada.sistemaCompanero.data.PayloadPuntoCompanero
import es.uca.tfg.conexionmorada.utils.Utils
import es.uca.tfg.conexionmorada.utils.retrofit.APIRetrofit
import es.uca.tfg.conexionmorada.utils.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class SistemaCompaneroFragment : Fragment() {
    private lateinit var mMap: GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 1
    private val LOCATION_SETTINGS_REQUEST_CODE = 123
    private val DELAY_TIME = 5000L // 5 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Obtener permiso para la ubicación del usuario
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }

        checkLocationEnabled()


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
            startActivityForResult(intent, REQUEST_CODE_ADD_POINT)
        }

        val more = view?.findViewById<View>(es.uca.tfg.conexionmorada.R.id.zona_companero)
        more?.setOnClickListener {
            val intent = Intent(requireContext(), ZonaSistemaCompaneroActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_ADD_POINT && resultCode == Activity.RESULT_OK) {
            // Realizar las acciones que deseas al recibir el resultado "RESULT_OK" de AddPointActivity
            // por ejemplo, recargar la actividad anterior
            recreate(requireActivity())
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

        var call = APIRetrofit(requireContext()).getAllPuntoCompaneroActive()
        call.enqueue(object : retrofit2.Callback<List<PayloadPuntoCompanero>> {
            override fun onResponse(
                call: retrofit2.Call<List<PayloadPuntoCompanero>>,
                response: retrofit2.Response<List<PayloadPuntoCompanero>>
            ) {
                if (response.isSuccessful) {
                    val puntos = response.body()
                    if (puntos != null) {
                        for (punto in puntos) {

                            Utils.createCustomMarkerBitmap(requireContext(), punto.uuidSolicitante) { bitmap ->
                                if (bitmap != null) {
                                    val markerOptions = MarkerOptions()
                                        .position(LatLng(punto.markerDestinoLatitud.toDouble(), punto.markerDestinoLongitud.toDouble()))
                                        .title(punto.id)
                                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                    mMap.addMarker(markerOptions)
                                } else {
                                    // Manejar el caso en que el bitmap sea nulo
                                }
                            }

                           /*val markerOptions = MarkerOptions()
                                .position(LatLng(punto.markerDestinoLatitud.toDouble(), punto.markerDestinoLongitud.toDouble()))
                                .title(punto.id)
                                .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarkerBitmap(requireContext(), punto.uuidSolicitante )!!))
                            mMap.addMarker(markerOptions)*/


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


   /* private fun createCustomMarkerBitmap(context: Context, userUuid: String, callback: (Bitmap?) -> Unit) {
        val markerView: View = LayoutInflater.from(context).inflate(R.layout.punto_companero_layout, null)
        val userPhotoImageView = markerView.findViewById<ImageView>(R.id.userPhotoImageView)

        val imageRef = Firebase.storage.reference.child("perfil/$userUuid")
        Glide.with(context)
            .asBitmap()
            .load(imageRef)
            .circleCrop()
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    userPhotoImageView.setImageBitmap(resource)
                    markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                    markerView.layout(0, 0, markerView.measuredWidth, markerView.measuredHeight)
                    markerView.buildDrawingCache()
                    val markerBitmap = Bitmap.createBitmap(
                        markerView.measuredWidth,
                        markerView.measuredHeight,
                        Bitmap.Config.ARGB_8888
                    )
                    val canvas = Canvas(markerBitmap)
                    markerView.draw(canvas)
                    callback(markerBitmap)
                }
            })
    }*/



    companion object {
        private const val REQUEST_CODE_ADD_POINT = 1001
    }

    private fun checkLocationEnabled() {
        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        val isLocationEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true || locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true


        if (!isLocationEnabled) {
            AlertDialog.Builder(requireContext())
                .setTitle("Activar ubicación")
                .setMessage("Para que la aplicación funcione correctamente es necesario que actives la opción de ubicación. ¿Desea activarla ahora?")
                .setCancelable(false)
                .setPositiveButton("Aceptar") { _, _ ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivityForResult(intent, LOCATION_SETTINGS_REQUEST_CODE)
                    Handler().postDelayed({
                        restartActivity()
                    }, DELAY_TIME)
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    requireActivity().finishAffinity()
                }
                .show()
        }
    }

    private fun restartActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        requireActivity().finish()
    }
}