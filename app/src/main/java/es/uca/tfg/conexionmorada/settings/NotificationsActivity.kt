package es.uca.tfg.conexionmorada.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import es.uca.tfg.conexionmorada.R

class NotificationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        exit()
    }

    fun exit(){
        var exit = findViewById<ImageView>(R.id.exit)
        exit.setOnClickListener(){
            finish()
        }
    }
}