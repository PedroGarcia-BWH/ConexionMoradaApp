package es.uca.tfg.conexionmorada.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.cmSocial.activities.HiloSelectedActivity
import java.util.Calendar
import java.util.Locale

class Utils {
    companion object {
        fun exit(activity: AppCompatActivity, imageView: ImageView) {
            imageView.setOnClickListener {
                activity.finish()
            }
        }

        fun hiloSelected(activity: AppCompatActivity, idHilo: String){
            var intent = Intent(activity, HiloSelectedActivity::class.java)
            intent.putExtra("idHilo", idHilo)
            activity.startActivity(intent)
        }

        @RequiresApi(Build.VERSION_CODES.N)
        fun obtenerTiempoTranscurrido(fecha: String): String {
            val formatosFecha = arrayOf(
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()),
                SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'z yyyy", Locale.getDefault())
            )

            val fechaActual = Calendar.getInstance().time

            for (formatoFecha in formatosFecha) {
                try {
                    val fechaPasada = formatoFecha.parse(fecha)
                    val diferencia = fechaActual.time - fechaPasada.time

                    val segundos = diferencia / 1000
                    val minutos = segundos / 60
                    val horas = minutos / 60
                    val dias = horas / 24
                    val meses = dias / 30
                    val años = meses / 12

                    return when {
                        segundos < 60 -> "Hace $segundos segundos"
                        minutos < 60 -> "Hace $minutos minutos"
                        horas < 24 -> "Hace $horas horas"
                        dias < 30 -> "Hace $dias días"
                        meses < 12 -> "Hace $meses meses"
                        else -> "Hace $años años"
                    }
                } catch (e: Exception) {
                    // Ignorar el formato inválido y continuar con el siguiente
                }
            }

            return "Ahora mismo"
        }

        fun createCustomMarkerBitmap(
            context: Context,
            userUuid: String,
            callback: (Bitmap?) -> Unit
        ) {
            val markerView: View =
                LayoutInflater.from(context).inflate(R.layout.punto_companero_layout, null)

            val userPhotoImageView = markerView.findViewById<ImageView>(R.id.userPhotoImageView)
            val imageref = Firebase.storage.reference.child("perfil/$userUuid")

            imageref.downloadUrl.addOnSuccessListener { Uri ->
                Glide.with(context)
                    .asBitmap()
                    .circleCrop()
                    .load(Uri)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            userPhotoImageView.setImageBitmap(resource)

                            markerView.measure(
                                View.MeasureSpec.UNSPECIFIED,
                                View.MeasureSpec.UNSPECIFIED
                            )
                            markerView.layout(
                                0,
                                0,
                                markerView.measuredWidth,
                                markerView.measuredHeight
                            )
                            markerView.buildDrawingCache()

                            val bitmap = Bitmap.createBitmap(
                                markerView.measuredWidth,
                                markerView.measuredHeight,
                                Bitmap.Config.ARGB_8888
                            )

                            val canvas = Canvas(bitmap)
                            markerView.draw(canvas)

                            // Llama al callback con el bitmap creado
                            callback(bitmap)
                        }
                    })
            }
        }
    }
}