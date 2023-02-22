package es.uca.tfg.conexionmorada.cmSocial.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.cmSocial.adapter.HiloAdapter
import es.uca.tfg.conexionmorada.cmSocial.adapter.NotificationHiloAdapter
import es.uca.tfg.conexionmorada.cmSocial.adapter.NotificationPersonaAdapter
import es.uca.tfg.conexionmorada.cmSocial.data.PayloadNotificationHilo
import es.uca.tfg.conexionmorada.cmSocial.data.PayloadNotificationPersona
import es.uca.tfg.conexionmorada.usernames.data.PayloadUsername
import es.uca.tfg.conexionmorada.utils.Utils
import es.uca.tfg.conexionmorada.utils.retrofit.APIRetrofit

class NotificacionesSocialActivity : AppCompatActivity() {

    lateinit var adapterHilo : NotificationHiloAdapter
    lateinit var adapterPersona : NotificationPersonaAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificaciones_social)

        Utils.exit(this, findViewById(R.id.Exit))

        onTabSelected()
        addDataRecyclerViewPersona()
        addDataRecyclerViewHilo()
        deleteNotificationsClick()
    }

    fun addDataRecyclerViewHilo(){
        var recyclerviewHilo = findViewById<RecyclerView>(R.id.RecyclerViewHilo)
        var layoutManager = LinearLayoutManager(this)
        recyclerviewHilo.layoutManager = layoutManager
        adapterHilo = NotificationHiloAdapter()
        recyclerviewHilo.adapter = adapterHilo

        var call = APIRetrofit().getNotificationsHilo(Firebase.auth.currentUser!!.uid)
        call.enqueue(object : retrofit2.Callback<List<PayloadNotificationHilo>> {
            override fun onResponse(call: retrofit2.Call<List<PayloadNotificationHilo>>, response: retrofit2.Response<List<PayloadNotificationHilo>>) {
                if (response.isSuccessful) {
                    if(response.body()!!.size == 0) {
                        //noResultImage.visibility = ImageView.VISIBLE
                        //txtNoResult.visibility = TextView.VISIBLE
                    }else{
                        adapterHilo.setData(response.body()!!)
                    }

                    adapterHilo.setOnItemClickListener(object : NotificationHiloAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            Utils.hiloSelected(this@NotificacionesSocialActivity, response.body()!![position].idHilo)
                        }
                    })
                }
            }

            override fun onFailure(call: retrofit2.Call<List<PayloadNotificationHilo>>, t: Throwable) {
                Toast.makeText(applicationContext, "Error al cargar las notificaciones, intentelo de nuevo", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun addDataRecyclerViewPersona(){
        var recyclerviewPersona = findViewById<RecyclerView>(R.id.RecyclerViewPersona)
        var layoutManager = LinearLayoutManager(this)
        recyclerviewPersona.layoutManager = layoutManager
        adapterPersona = NotificationPersonaAdapter()
        recyclerviewPersona.adapter = adapterPersona

        var call = APIRetrofit().getNotificationsPersona(Firebase.auth.currentUser!!.uid)
        call.enqueue(object : retrofit2.Callback<List<PayloadNotificationPersona>> {
            override fun onResponse(call: retrofit2.Call<List<PayloadNotificationPersona>>, response: retrofit2.Response<List<PayloadNotificationPersona>>) {
                if (response.isSuccessful) {
                    if(response.body()!!.size == 0) {
                        //noResultImage.visibility = ImageView.VISIBLE
                        //txtNoResult.visibility = TextView.VISIBLE
                    }else{
                        adapterPersona.setData(response.body()!!)
                    }

                    adapterPersona.setOnItemClickListener(object : NotificationPersonaAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            var intent = android.content.Intent(this@NotificacionesSocialActivity, PerfilSocialActivity::class.java)
                            intent.putExtra("uuid", response.body()!![position].username)
                            startActivity(intent)
                        }
                    })
                }
            }

            override fun onFailure(call: retrofit2.Call<List<PayloadNotificationPersona>>, t: Throwable) {
                Toast.makeText(applicationContext, "Error al cargar las notificaciones, intentelo de nuevo", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun onTabSelected() {
        var tabLayoutManager = findViewById<TabLayout>(R.id.tabLayoutSearch)
        var recyclerviewPersona = findViewById<RecyclerView>(R.id.RecyclerViewPersona)
        var recyclerviewHilo = findViewById<RecyclerView>(R.id.RecyclerViewHilo)
        tabLayoutManager.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                    when(tab?.position){
                        0 -> {
                            recyclerviewHilo.visibility = RecyclerView.VISIBLE
                            recyclerviewPersona.visibility = RecyclerView.GONE
                        }
                        1 -> {
                            recyclerviewHilo.visibility = RecyclerView.GONE
                            recyclerviewPersona.visibility = RecyclerView.VISIBLE
                        }
                        else->{
                            //Toast.makeText(this, "Error en la selección, intentelo de nuevo", Toast.LENGTH_SHORT).show()
                        }
                    }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    fun deleteNotificationsClick(){
        var deleteButton = findViewById<FloatingActionButton>(R.id.ruedaDelete)
        deleteButton.setOnClickListener {
            if(adapterHilo.getData().size == 0 && adapterPersona.getData().size == 0){
                Toast.makeText(applicationContext, "No hay notificaciones para eliminar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else {
                var call = APIRetrofit().deleteNotifications(Firebase.auth.currentUser!!.uid)
                call.enqueue(object : retrofit2.Callback<Void> {
                    override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(applicationContext, "Notificaciones eliminadas", Toast.LENGTH_SHORT).show()
                            adapterHilo.clearData()
                            adapterPersona.clearData()
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                        Toast.makeText(applicationContext, "Error al eliminar las notificaciones, intentelo de nuevo", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}