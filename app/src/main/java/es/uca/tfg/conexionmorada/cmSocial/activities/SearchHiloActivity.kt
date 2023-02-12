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

class SearchHiloActivity : AppCompatActivity() {
    lateinit var text: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_hilo)

        search()
    }


    fun search() {
        var search = findViewById<SearchView>(R.id.search)
        var tabHilos = findViewById<TabItem>(R.id.tabHilos)
        var tabPersonas = findViewById<TabItem>(R.id.tabPersonas)
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                text = query!!
                //Toast.makeText(applicationContext, "Buscando: " + query, Toast.LENGTH_SHORT).show()
                if(tabHilos.isSelected) {
                    searchHilos()
                } else if(tabPersonas.isSelected) {
                    searchPersonas()
                }
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
        var call = APIRetrofit().searchHilos(text)
        call.enqueue(object : retrofit2.Callback<List<PayloadHilo>> {
            override fun onResponse(
                call: retrofit2.Call<List<PayloadHilo>>,
                response: retrofit2.Response<List<PayloadHilo>>
            ) {
                if (response.isSuccessful) {
                    var hilos = response.body()
                    if(hilos!!.size == 0) {
                        noResultImage.visibility = ImageView.VISIBLE
                        txtNoResult.visibility = TextView.VISIBLE
                    }else{
                        addDataRecyclerViewHilos(hilos!!)
                    }
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
        var call = APIRetrofit().searchUsuarios(text)
        call.enqueue(object : retrofit2.Callback<List<String>> {
            override fun onResponse(call: retrofit2.Call<List<String>>, response: retrofit2.Response<List<String>>) {
                if (response.isSuccessful) {
                    var personas = response.body()
                    if(personas!!.size == 0) {
                        noResultImage.visibility = ImageView.VISIBLE
                        txtNoResult.visibility = TextView.VISIBLE
                    }else{
                        addDataRecyclerViewPersonas(personas!!)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<List<String>>, t: Throwable) {
                Toast.makeText(applicationContext, "Error al buscar hilos", Toast.LENGTH_SHORT).show()
            }
        })
    }
    fun addDataRecyclerViewHilos(hilos : List<PayloadHilo>) {
        var recyclerview = findViewById<RecyclerView>(R.id.searchRecyclerView)
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

    fun addDataRecyclerViewPersonas(personas : List<String>) {
        var recyclerview = findViewById<RecyclerView>(R.id.searchRecyclerView)
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
        tabLayoutManager.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if(text != null){
                    when(tab?.position){
                        0 -> {
                            searchHilos()
                        }
                        1 -> {
                            searchPersonas()
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