package es.uca.tfg.conexionmorada.ui

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import es.uca.tfg.conexionmorada.LoginActivity
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.SettingsActivity
import es.uca.tfg.conexionmorada.firestore.User

class HomeFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override  fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navegateSettings()

        val doc_ref = User.getDatosUser()
        doc_ref!!.get().addOnSuccessListener { document ->
            if (document.data != null) {
                val data_user = document.data
                val regNombre =  view?.findViewById<TextView>(R.id.alias)
                regNombre?.text = "Bienvenido\n" + data_user?.get("username")
            } else {
                Log.d(TAG, "No such document")
                var intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    fun navegateSettings() {
        var regSettings = view?.findViewById<FloatingActionButton>(R.id.ruedaSettings)
        regSettings?.setOnClickListener {
            var intent = Intent(activity, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}