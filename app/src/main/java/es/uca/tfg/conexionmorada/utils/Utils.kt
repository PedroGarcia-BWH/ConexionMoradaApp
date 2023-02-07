package es.uca.tfg.conexionmorada.utils

import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import es.uca.tfg.conexionmorada.R

class Utils {
    companion object {
        fun exit(activity: AppCompatActivity, imageView: ImageView) {
            imageView.setOnClickListener {
                activity.finish()
            }
        }
    }
}