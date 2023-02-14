package es.uca.tfg.conexionmorada.utils

import android.content.Intent
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.cmSocial.activities.HiloSelectedActivity

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
    }
}