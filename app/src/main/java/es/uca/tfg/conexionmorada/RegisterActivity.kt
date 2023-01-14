package es.uca.tfg.conexionmorada

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val tCondition = findViewById<TextView>(R.id.textConditions)
        tCondition.setOnClickListener {
            val condAct = Intent(this, ConditionsActivity::class.java)
            startActivity(condAct)
        }
    }
}