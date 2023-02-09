package es.uca.tfg.conexionmorada.ui

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.cmSocial.activities.NuevoHiloActivity
import es.uca.tfg.conexionmorada.cmSocial.activities.SearchHiloActivity
import es.uca.tfg.conexionmorada.firestore.User
import es.uca.tfg.conexionmorada.retrofit.APIRetrofit
import es.uca.tfg.conexionmorada.storage.Storage


class CMSocialFragment : Fragment() {

    private var user = Firebase.auth.currentUser
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

        showDetailsUserDrawer()
        addHiloListener()

    }

    private fun showDetailsUserDrawer() {
        var navegationView = activity?.findViewById<NavigationView>(R.id.nav_view)
        var headerLayout = navegationView?.getHeaderView(0)
        var image = headerLayout?.findViewById<ImageView>(R.id.ImageDrawer)
        var name = headerLayout?.findViewById<TextView>(R.id.username)
        var seguidores = headerLayout?.findViewById<TextView>(R.id.numberSeguidores)
        var seguidos = headerLayout?.findViewById<TextView>(R.id.numberSeguidos)

        Storage().photoAccount(image!!, user?.uid!!)
         User.getUsername().addOnSuccessListener { document ->
             if (document != null) {
                 name?.text = document.data?.get("username").toString()
             }
         }

        var call = APIRetrofit().getSeguidores(user?.uid!!)
        if(call != null){
            call.enqueue(object : retrofit2.Callback<Int> {
                override fun onResponse(call: retrofit2.Call<Int>, response: retrofit2.Response<Int>) {
                    if (response.isSuccessful) {
                        seguidores?.text = response.body().toString()
                    }
                }
                override fun onFailure(call: retrofit2.Call<Int>, t: Throwable) {
                    seguidores?.text = "none"
                }
            })
        }

        call = APIRetrofit().getSeguidos(user?.uid!!)
        if(call != null){
            call.enqueue(object : retrofit2.Callback<Int> {
                override fun onResponse(call: retrofit2.Call<Int>, response: retrofit2.Response<Int>) {
                    if (response.isSuccessful) {
                        seguidos?.text = response.body().toString()
                    }
                }
                override fun onFailure(call: retrofit2.Call<Int>, t: Throwable) {
                    seguidos?.text = "none"
                }
            })
        }
    }

    private fun addHiloListener(){
        var addHilo = activity?.findViewById<ImageView>(R.id.addHilo)
        addHilo?.setOnClickListener {
            var intent = android.content.Intent(activity, NuevoHiloActivity::class.java)
            startActivity(intent)
        }
    }

    private fun clickDrawer(){
        var navegationView = activity?.findViewById<NavigationView>(R.id.nav_view)
        var menuLayout = navegationView?.menu
        var search = menuLayout?.findItem(R.id.search)
        var perfil = menuLayout?.findItem(R.id.perfil)
        var notifications = menuLayout?.findItem(R.id.notificaciones)

        /*search?.setOnMenuItemClickListener {
            var intent = android.content.Intent(activity, SearchHiloActivity::class.java)
        }*/
    }

}

