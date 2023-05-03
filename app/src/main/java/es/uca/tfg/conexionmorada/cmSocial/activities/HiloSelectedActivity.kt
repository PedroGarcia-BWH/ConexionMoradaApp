package es.uca.tfg.conexionmorada.cmSocial.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.cmSocial.adapter.HiloAdapter
import es.uca.tfg.conexionmorada.cmSocial.data.PayloadHilo
import es.uca.tfg.conexionmorada.cmSocial.filter.SocialProfanity
import es.uca.tfg.conexionmorada.utils.retrofit.APIRetrofit
import es.uca.tfg.conexionmorada.utils.Utils
import es.uca.tfg.conexionmorada.utils.storage.Storage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class HiloSelectedActivity : AppCompatActivity() {
    lateinit var idHilo: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hilo_selected)

        idHilo = intent.getStringExtra("idHilo").toString()

        dataRecyclerView()

        Utils.exit(this, findViewById(R.id.Exit))

        Storage().photoAccount(findViewById<ImageView>(R.id.perfilUser), Firebase.auth.currentUser!!.uid)


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
                         /*   Utils.hiloSelected(
                                this@HiloSelectedActivity,
                                response.body()!![position].idHilo
                            )*/
                        }
                    })
                    layoutManager.scrollToPosition(findHilo(response.body()!!))
                    addHiloListener(response.body()!![0].idHilo, adapter)

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

    private fun addHiloListener(hiloPadreUuid: String, adapter: HiloAdapter){
        var createHilo = findViewById<FloatingActionButton>(R.id.ruedaDelete)
        var mensaje = findViewById<TextInputEditText>(R.id.mensaje)

        createHilo.setOnClickListener {
            if(mensaje.text.toString().isEmpty()){

            }else{
                var payloadHilo = PayloadHilo(null, Firebase.auth.currentUser?.uid.toString(), SocialProfanity().replaceProfanity(mensaje.text.toString()), hiloPadreUuid, Date(), 0, 0, false, false)
                var call = APIRetrofit().addHilo(payloadHilo)
                call.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if(response.isSuccessful){
                            //Toast.makeText(this@NuevoHiloActivity, "Hilo creado correctamente", Toast.LENGTH_SHORT).show()
                            adapter.addData(payloadHilo)
                            mensaje.setText("")
                            //finish()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@HiloSelectedActivity, "No se pudo crear el hilo, intentelo de nuevo", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}