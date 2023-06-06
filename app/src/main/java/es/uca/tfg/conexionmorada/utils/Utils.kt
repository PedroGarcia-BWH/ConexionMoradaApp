package es.uca.tfg.conexionmorada.utils

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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
            val formatoFecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
            val fechaActual = Calendar.getInstance().time
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
        }
    }
}