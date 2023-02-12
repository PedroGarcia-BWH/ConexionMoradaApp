package es.uca.tfg.conexionmorada.cmSocial.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.cmSocial.adapter.HiloAdapter
import es.uca.tfg.conexionmorada.cmSocial.adapter.PersonaAdapter
import es.uca.tfg.conexionmorada.cmSocial.data.PayloadHilo
import es.uca.tfg.conexionmorada.retrofit.APIRetrofit
import es.uca.tfg.conexionmorada.usernames.data.PayloadUsername
import es.uca.tfg.conexionmorada.utils.Utils

class SearchHiloActivity : AppCompatActivity() {
    var text: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_hilo)
        var exit = findViewById<ImageView>(R.id.Exit)
        Utils.exit(this, exit)

        search()
        onTabSelected()
    }


    fun search() {
        var search = findViewById<SearchView>(R.id.search)
        /*var tabLayoutManager = findViewById<TabLayout>(R.id.tabLayoutSearch)
        var tabHilos = tabLayoutManager.getTabAt(0)
        var tabPersonas = tabLayoutManager.getTabAt(1)*/
        var noResultImage = findViewById<ImageView>(R.id.imageNoResults)
        var txtNoResult = findViewById<TextView>(R.id.txtNoResults)

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                text = query!!
                noResultImage.visibility = ImageView.GONE
                txtNoResult.visibility = TextView.GONE
                //Toast.makeText(applicationContext, "Buscando: " + query, Toast.LENGTH_SHORT).show()
                searchHilos()
                searchPersonas()

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    fun searchHilos() {
        var noResultImage = findViewById<ImageView>(R.id.imageNoResults)
        var txtNoResult = findViewById<TextView>(R.id.txtNoResults)
        var call = APIRetrofit().searchHilos(text!!)
        call.enqueue(object : retrofit2.Callback<List<PayloadHilo>> {
            override fun onResponse(
                call: retrofit2.Call<List<PayloadHilo>>,
                response: retrofit2.Response<List<PayloadHilo>>
            ) {
                if (response.isSuccessful) {
                    var hilos = response.body()
                    if(hilos!!.size == 0) {
                       // noResultImage.visibility = ImageView.VISIBLE
                        //txtNoResult.visibility = TextView.VISIBLE
                    }
                    addDataRecyclerViewHilos(hilos!!)

                }
            }

            override fun onFailure(call: retrofit2.Call<List<PayloadHilo>>, t: Throwable) {
                Toast.makeText(applicationContext, "Error al buscar hilos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun searchPersonas(){
        var noResultImage = findViewById<ImageView>(R.id.imageNoResults)
        var txtNoResult = findViewById<TextView>(R.id.txtNoResults)
        var call = APIRetrofit().searchUsuarios(text!!)
        call.enqueue(object : retrofit2.Callback<List<PayloadUsername>> {
            override fun onResponse(call: retrofit2.Call<List<PayloadUsername>>, response: retrofit2.Response<List<PayloadUsername>>) {
                if (response.isSuccessful) {
                    var personas = response.body()
                    if(personas!!.size == 0) {
                        //noResultImage.visibility = ImageView.VISIBLE
                        //txtNoResult.visibility = TextView.VISIBLE
                    }
                    addDataRecyclerViewPersonas(personas!!)
                }
            }

            override fun onFailure(call: retrofit2.Call<List<PayloadUsername>>, t: Throwable) {
                //Toast.makeText(applicationContext, "Error al buscar personas", Toast.LENGTH_SHORT).show()
            }
        })
    }
    fun addDataRecyclerViewHilos(hilos : List<PayloadHilo>) {
        var recyclerview = findViewById<RecyclerView>(R.id.searchRecyclerViewHilo)
        recyclerview?.layoutManager = LinearLayoutManager(this)
        var adapter = HiloAdapter()
        recyclerview?.adapter = adapter
        (recyclerview?.adapter as HiloAdapter).setData(hilos)
        //Toast.makeText(activity, articles.size, Toast.LENGTH_SHORT).show()
        adapter.setOnItemClickListener(object : HiloAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                //articleSelected(articles[position])
            }
        })
    }

    fun addDataRecyclerViewPersonas(personas : List<PayloadUsername>) {
        var recyclerview = findViewById<RecyclerView>(R.id.searchRecyclerViewPersona)
        recyclerview?.layoutManager = LinearLayoutManager(this)
        var adapter = PersonaAdapter()
        recyclerview?.adapter = adapter
        (recyclerview?.adapter as PersonaAdapter).setData(personas)
        //Toast.makeText(activity, articles.size, Toast.LENGTH_SHORT).show()
        adapter.setOnItemClickListener(object : PersonaAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                //articleSelected(articles[position])
            }
        })
    }

    fun onTabSelected() {
        var tabLayoutManager = findViewById<TabLayout>(R.id.tabLayoutSearch)
        var recyclerviewPersona = findViewById<RecyclerView>(R.id.searchRecyclerViewPersona)
        var recyclerviewHilo = findViewById<RecyclerView>(R.id.searchRecyclerViewHilo)
        tabLayoutManager.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if(text != null){
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
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }
}