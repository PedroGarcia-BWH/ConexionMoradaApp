package es.uca.tfg.conexionmorada.cmSocial.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PatternMatcher
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.like.IconType
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.cmSocial.adapter.HiloAdapter
import es.uca.tfg.conexionmorada.cmSocial.data.PayloadHilo
import es.uca.tfg.conexionmorada.utils.retrofit.APIRetrofit
import es.uca.tfg.conexionmorada.utils.Utils

class HiloSelectedActivity : AppCompatActivity() {
    lateinit var idHilo: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hilo_selected)

        idHilo = intent.getStringExtra("idHilo").toString()

        dataRecyclerView()

        Utils.exit(this, findViewById(R.id.Exit))
    }

    fun dataRecyclerView(){
        var recyclerView = findViewById<RecyclerView>(R.id.recyclerViewHilo)
        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        var adapter = HiloAdapter()
        recyclerView.adapter = adapter

        var call = APIRetrofit().getRespuestas(idHilo)

        call.enqueue(object : retrofit2.Callback<List<PayloadHilo>> {
            override fun onResponse(call: retrofit2.Call<List<PayloadHilo>>, response: retrofit2.Response<List<PayloadHilo>>) {
                if (response.isSuccessful) {
                    //Toast.makeText(this@HiloSelectedActivity, response.body()!!.size, Toast.LENGTH_SHORT).show()
                    adapter.setData(response.body()!!)

                    adapter.setOnItemClickListener(object : HiloAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            //hiloSelected(response.body()!![position])
                        }
                    })
                    layoutManager.scrollToPosition(findHilo(response.body()!!))
                }
            }
            override fun onFailure(call: retrofit2.Call<List<PayloadHilo>>, t: Throwable) {
                Toast.makeText(this@HiloSelectedActivity, "Error al cargar los hilos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun findHilo(payloadHilos: List<PayloadHilo>) : Int {
        //for each
        for (i in payloadHilos.indices) {
            if (payloadHilos[i].idHilo.equals(idHilo)) {
                return i
            }
        }
        return 0
    }
}