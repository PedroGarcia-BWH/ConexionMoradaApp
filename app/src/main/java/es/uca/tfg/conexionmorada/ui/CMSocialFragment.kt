package es.uca.tfg.conexionmorada.ui

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.cmSocial.activities.*
import es.uca.tfg.conexionmorada.cmSocial.adapter.HiloAdapter
import es.uca.tfg.conexionmorada.cmSocial.data.PayloadHilo
import es.uca.tfg.conexionmorada.utils.firestore.User
import es.uca.tfg.conexionmorada.utils.retrofit.APIRetrofit
import es.uca.tfg.conexionmorada.utils.storage.Storage
import es.uca.tfg.conexionmorada.utils.Utils
import retrofit2.Call


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
        var button = activity?.findViewById<View>(R.id.showDrawer)

       //open navigationView
        button?.setOnClickListener {
            drawer?.openDrawer(Gravity.LEFT)
        }

        showDetailsUserDrawer()
        addHiloListener()
        clickDrawer()
        recyclerViewAddSeguidos()

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

        var call = APIRetrofit(requireContext()).getSeguidores(user?.uid!!)
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

        call = APIRetrofit(requireContext()).getSeguidos(user?.uid!!)
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
        var addHilo = activity?.findViewById<ImageView>(R.id.seePoints)
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

        search?.setOnMenuItemClickListener {
            var intent = android.content.Intent(activity, SearchHiloActivity::class.java)
            startActivity(intent)
            true
        }

        perfil?.setOnMenuItemClickListener {
            var intent = android.content.Intent(activity, PerfilSocialActivity::class.java)
            intent.putExtra("uuid", Firebase.auth.currentUser?.uid)
            startActivity(intent)
            true
        }

        notifications?.setOnMenuItemClickListener {
            var intent = android.content.Intent(activity, NotificacionesSocialActivity::class.java)
            startActivity(intent)
            true
        }

    }
    private fun recyclerViewAddSeguidos(){
        //var content = activity?.findViewById<LinearLayout>(R.id.content)
        var tabLayoutManager = activity?.findViewById<TabLayout>(R.id.tabLayoutSearch)
        var tabSeguidos = activity?.findViewById<TabItem>(R.id.tabHilos)
        var tabParaTi = activity?.findViewById<TabItem>(R.id.tabPersonas)
        var recyclerView = activity?.findViewById<RecyclerView>(R.id.reciclerViewSocial)
        var layoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = layoutManager
        var adapter = HiloAdapter()
        recyclerView?.adapter = adapter

        var progressBar = activity?.findViewById<ProgressBar>(R.id.progressBar3)
        tabLayoutManager?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                progressBar?.visibility = View.VISIBLE
                getHilos(tab,adapter)
                progressBar?.visibility = View.GONE
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                adapter.clearData()
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
                progressBar?.visibility = View.VISIBLE
                getHilos(tab,adapter)
                progressBar?.visibility = View.GONE
            }
        })

        // primera llamada a la api
        progressBar?.visibility = View.VISIBLE
        var call = APIRetrofit(requireContext()).getLastHilosSeguidos(user?.uid!!)
        call.enqueue(object : retrofit2.Callback<List<PayloadHilo>> {
            override fun onResponse(call: retrofit2.Call<List<PayloadHilo>>, response: retrofit2.Response<List<PayloadHilo>>) {
                if (response.isSuccessful) {
                    adapter.setData(response.body()!!)
                    progressBar?.visibility = View.GONE
                    adapter.setOnItemClickListener(object : HiloAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            Utils.hiloSelected(
                                activity!! as AppCompatActivity, response.body()!![position].idHilo
                            )
                        }
                    })
                }
            }
            override fun onFailure(call: retrofit2.Call<List<PayloadHilo>>, t: Throwable) {
                Toast.makeText(activity, "Error al cargar los hilos, intentelo de nuevo", Toast.LENGTH_SHORT).show()
            }
        })

        var logo = activity?.findViewById<ImageView>(R.id.logo)
        logo?.setOnClickListener {
            adapter.clearData()
            progressBar?.visibility = View.VISIBLE
            if(tabLayoutManager?.getTabAt(0)?.isSelected == true) getHilos(tabLayoutManager?.getTabAt(0),adapter)
            else getHilos(tabLayoutManager?.getTabAt(1),adapter)
            progressBar?.visibility = View.GONE
        }

    }

    fun getHilos(tab: TabLayout.Tab?, adapter: HiloAdapter){
        lateinit var call: Call<List<PayloadHilo>>
        when(tab?.position){
            0 -> {
                call = APIRetrofit(requireContext()).getLastHilosSeguidos(user?.uid!!)
            }
            1 -> {
                call = APIRetrofit(requireContext()).getLastHilos(user?.uid!!)
            }
            else->{
                Toast.makeText(activity, "Error en la selección, intentelo de nuevo", Toast.LENGTH_SHORT).show()
            }
        }

        call.enqueue(object : retrofit2.Callback<List<PayloadHilo>> {
            override fun onResponse(call: retrofit2.Call<List<PayloadHilo>>, response: retrofit2.Response<List<PayloadHilo>>) {
                if (response.isSuccessful) {
                    adapter.setData(response.body()!!)

                    adapter.setOnItemClickListener(object : HiloAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            Utils.hiloSelected(
                                activity!! as AppCompatActivity, response.body()!![position].idHilo
                            )
                        }
                    })
                }
            }
            override fun onFailure(call: retrofit2.Call<List<PayloadHilo>>, t: Throwable) {
                Toast.makeText(activity, "Error al cargar los hilos, intentelo de nuevo", Toast.LENGTH_SHORT).show()
            }
        })
    }

}

