package es.uca.tfg.conexionmorada.ui

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.navigation.NavigationView
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.databinding.ActivityMainBinding
import es.uca.tfg.conexionmorada.firestore.User
import es.uca.tfg.conexionmorada.verify.VerifyDialog


class CMSocialFragment : Fragment() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_c_m_social, container, false)
    }

    override  fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var drawer = activity?.findViewById<DrawerLayout>(R.id.drawer_layout)
        var button = activity?.findViewById<View>(R.id.button)

       //open navigationView
        button?.setOnClickListener {
            drawer?.openDrawer(Gravity.LEFT)
        }

    }

}