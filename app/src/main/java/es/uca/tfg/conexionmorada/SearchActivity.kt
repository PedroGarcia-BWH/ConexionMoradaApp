package es.uca.tfg.conexionmorada

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationServices
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import es.uca.tfg.conexionmorada.articles.ArticleActivity
import es.uca.tfg.conexionmorada.articles.adapter.ArticleAdapter
import es.uca.tfg.conexionmorada.articles.interfaces.CRUDInterface
import es.uca.tfg.conexionmorada.articles.model.Article
import es.uca.tfg.conexionmorada.utils.retrofit.APIRetrofit
import es.uca.tfg.conexionmorada.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.Locale

class SearchActivity : AppCompatActivity() {
    var city: String? = null
    var comunidad: String? = null
    var country: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        getLocation()
        search()
        exit()


    }

    fun search() {
        var search = findViewById<SearchView>(R.id.search)
        var tabLayoutManager = findViewById<TabLayout>(R.id.tabLayoutSearch)
        var tabCountry = findViewById<TabItem>(R.id.tabCountry)
        var tabComunidad = findViewById<TabItem>(R.id.tabComunidad)
        var tabCity = findViewById<TabItem>(R.id.tabCity)

        //tabCountry.text = "Country"
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
               // Toast.makeText(applicationContext, "Buscando: " + query, Toast.LENGTH_SHORT).show()
                val selectedTab = tabLayoutManager.selectedTabPosition

                when (selectedTab) {
                    0 -> {
                        // Acciones para la pestaña Country
                         searchArticles(query!!)
                    }
                    1 -> {
                        // Acciones para la pestaña Comunidad
                        searchArticles(query!!, comunidad!!)
                    }
                    2 -> {
                        // Acciones para la pestaña City
                        searchArticles(query!!, "null", city!!)
                    }
                    else -> {
                        // Acciones por defecto
                    }
                }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        tabLayoutManager?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if(!search.getQuery().toString().isEmpty()){
                    if(tab!!.position == 0) searchArticles(search.getQuery().toString())
                    else if(tab!!.position == 1) searchArticles(search.getQuery().toString(), comunidad!!)
                    else if (tab!!.position == 2) searchArticles(search.getQuery().toString(), "null", city!!)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
               // adapter.clearData()
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
                if(!search.getQuery().toString().isEmpty()){
                    if(tab!!.position == 0) searchArticles(search.getQuery().toString())
                    else if(tab!!.position == 1) searchArticles(search.getQuery().toString(), comunidad!!)
                    else if (tab!!.position == 2) searchArticles(search.getQuery().toString(), "null", city!!)
                }
            }
        })
    }


    fun searchArticles(query: String, comunidad: String = "null", city: String = "null") {
        var imageNoResult = findViewById<ImageView>(R.id.imageNoResults)
        var textNoResult = findViewById<TextView>(R.id.txtNoResults)
        var call = APIRetrofit(this).searchArticles(query, 40, comunidad, city)
        imageNoResult.visibility = View.INVISIBLE
        textNoResult.visibility = View.INVISIBLE

        call.enqueue(object : Callback<List<Article>> {

            override fun onResponse(call: Call<List<Article>>, response: Response<List<Article>>){
                if(response.isSuccessful){
                    //Toast.makeText(applicationContext, "Bien " + response.body()!!.size , Toast.LENGTH_SHORT).show()
                    if(response.body()!!.size == 0) {
                        imageNoResult.visibility = View.VISIBLE
                        textNoResult.visibility = View.VISIBLE
                    }
                    reloadReclyclerView(response.body() as ArrayList<Article>)

                }else{
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Article>>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }



    fun reloadReclyclerView(articles: ArrayList<Article>) {
        var recyclerView = findViewById<RecyclerView>(R.id.searchRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        var adapter = ArticleAdapter()
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : ArticleAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                articleSelected(articles[position])
            }
        })
        (recyclerView.adapter as ArticleAdapter).setData(articles)
    }

    fun articleSelected(article: Article) {
        val intent = Intent(this, ArticleActivity::class.java)
        intent.putExtra("title", article.title)
        intent.putExtra("description", article.description)
        intent.putExtra("body", article.body)
        intent.putExtra("date", article.creationDate)
        intent.putExtra("category", article.category)
        intent.putExtra("urlImg", article.urlFrontPage)
        intent.putExtra("creationDate", article.creationDate)

        this.startActivity(intent)
    }

    private fun getLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude: Double = location.latitude
                val longitude: Double = location.longitude
                var geocoder = Geocoder(this, Locale.getDefault())
                try {
                    val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1) as List<Address>
                    if (addresses.isNotEmpty()) {
                        val address: Address = addresses[0]
                        city = address.locality
                        comunidad = address.adminArea
                        country  = address.countryName

                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun exit(){
        var exit = findViewById<ImageView>(R.id.Exit)
        exit.setOnClickListener {
            finish()
        }
    }
}