package es.uca.tfg.conexionmorada.ui

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uca.tfg.conexionmorada.LoginActivity
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.SearchActivity
import es.uca.tfg.conexionmorada.settings.SettingsActivity
import es.uca.tfg.conexionmorada.articles.ArticleActivity
import es.uca.tfg.conexionmorada.articles.adapter.ArticleAdapter
import es.uca.tfg.conexionmorada.articles.model.Article
import es.uca.tfg.conexionmorada.articles.model.PayloadArticle
import es.uca.tfg.conexionmorada.utils.firestore.User
import es.uca.tfg.conexionmorada.utils.retrofit.APIRetrofit
import es.uca.tfg.conexionmorada.utils.storage.Storage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.Locale


class HomeFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPrefFile = "kotlinsharedpreference"
    private var user = Firebase.auth.currentUser
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
        sharedPreferences= activity?.getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE) as SharedPreferences

        var profile = view?.findViewById<ImageView>(R.id.imageView3)
        Storage().photoAccount(profile!!, user?.uid!!)

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

        lastArticlesClick()
        var regText = view?.findViewById<TextView>(R.id.errorText)

        var recargarButton = view?.findViewById<Button>(R.id.Recargar)
        recargarButton?.setOnClickListener {
            regText?.visibility = View.INVISIBLE
            recargarButton?.visibility = View.INVISIBLE
            lastArticlesClick()
        }

        searchActivity()

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
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
                var geocoder = Geocoder(requireContext(), Locale.getDefault())
                try {
                    val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1) as List<Address>
                    if (addresses.isNotEmpty()) {
                        val address: Address = addresses[0]
                        val city: String? = address.locality
                        val comunidad: String? = address.adminArea
                        val country: String? = address.countryName

                        val spinner: Spinner? = view?.findViewById(R.id.spinner2)
                        val items = arrayOf("Buscar en " + country, "Buscar en " + comunidad, "Buscar en " + city)
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)

                        spinner?.adapter = adapter
                        var progressbar = view?.findViewById<ProgressBar>(R.id.progressBar3)

                        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                progressbar?.visibility = View.VISIBLE
                                val selectedItem = items[position]
                                if (selectedItem.contains(comunidad.toString())) {
                                    lastArticlesClick("", comunidad.toString())
                                }
                                else if (selectedItem.contains(city.toString())) lastArticlesClick(city.toString(), "")
                                else lastArticlesClick("","")
                                progressbar?.visibility = View.INVISIBLE
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                // Acciones a realizar cuando no se selecciona ningún elemento
                            }
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun navegateSettings() {
        var regSettings = view?.findViewById<FloatingActionButton>(R.id.ruedaSettings)
        regSettings?.setOnClickListener {
            var intent = Intent(activity, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    fun lastArticlesClick(city: String = "", comunidad: String = "") {
        var call = APIRetrofit(requireContext()).lastArticles(createPayloadArticles(city, comunidad))

        if (call != null) {
            call.enqueue(object : Callback<List<Article>> {

                override fun onResponse(call: Call<List<Article>>, response: Response<List<Article>>){
                    if(response.isSuccessful){
                        var noHilo = activity?.findViewById<TextView>(R.id.noArticle)
                        var noHiloImage = activity?.findViewById<ImageView>(R.id.noArticleImage)

                        if(response.body()!!.isEmpty()){
                            if(city != "" ) noHilo?.text = "No existe artículos actualmente para la localidad " + city
                            else if(comunidad != "") noHilo?.text = "No existe artículos actualmente para la comunidad autónoma" + comunidad
                            else noHilo?.text = "No existe artículos actualmente para tu localización"
                            noHilo?.visibility = View.VISIBLE
                            noHiloImage?.visibility = View.VISIBLE

                        }else {
                            noHilo?.visibility = View.INVISIBLE
                            noHiloImage?.visibility = View.INVISIBLE
                            response.body()?.let { addRecyclerViewArticles(it) }
                        }


                    }else{
                        Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()
                        var regText = view?.findViewById<TextView>(R.id.errorText)
                    }
                }

                override fun onFailure(call: Call<List<Article>>, t: Throwable) {
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                    var regText = view?.findViewById<TextView>(R.id.errorText)
                    var regButtonError = view?.findViewById<Button>(R.id.Recargar)
                    regText?.visibility = View.VISIBLE
                    regButtonError?.visibility = View.VISIBLE

                }
            })
        }
    }

    fun createPayloadArticles(city: String, comunidad: String): PayloadArticle {
        var payloadArticle = PayloadArticle()
        payloadArticle.numberArticles = 10
        payloadArticle.status = "REQUESTED"
        payloadArticle.preferences = ArrayList<String>()
        var list = ArrayList<String>()

        val doc_ref = User.getDatosUser()
        doc_ref!!.get().addOnSuccessListener { document ->
            if (document.data != null) {
                val data_user = document.data
                val editor:SharedPreferences.Editor =  sharedPreferences.edit()
                if(data_user?.get("Violencia de genero") == true) editor.putString("genero", "Violencia de genero")

                if(data_user?.get("Violencia sexual") == true) editor.putString("sexual", "Violencia sexual")

                if(data_user?.get("Igualdad") == true) editor.putString("igualdad", "Igualdad")

                editor.apply()
                editor.commit()

            } else {
                Log.d(TAG, "No such document")
                var intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        sharedPreferences.getString("genero", "")?.let { list.add(it) }
        sharedPreferences.getString("sexual", "")?.let { list.add(it) }
        sharedPreferences.getString("igualdad", "")?.let { list.add(it) }
        list.add(city)
        list.add(comunidad)

        for(i in list) i.replace(" ", "")
        payloadArticle.preferences = list

        return payloadArticle
    }

    fun addRecyclerViewArticles(articles: List<Article>) {
        var recyclerview = view?.findViewById<RecyclerView>(R.id.RecyclerViewArticles)
        recyclerview?.layoutManager = LinearLayoutManager(activity)
        var adapter = ArticleAdapter()
        recyclerview?.adapter = adapter
        (recyclerview?.adapter as ArticleAdapter).setData(articles)
        //Toast.makeText(activity, articles.size, Toast.LENGTH_SHORT).show()
        adapter.setOnItemClickListener(object : ArticleAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                articleSelected(articles[position])
            }
        })
    }

    fun articleSelected(article: Article) {
        val intent =Intent(activity, ArticleActivity::class.java)
        intent.putExtra("title", article.title)
        intent.putExtra("description", article.description)
        intent.putExtra("body", article.body)
        intent.putExtra("date", article.creationDate)
        intent.putExtra("category", article.category)
        intent.putExtra("urlImg", article.urlFrontPage)
        intent.putExtra("creationDate", article.creationDate)

        activity?.startActivity(intent)
    }

    fun searchActivity() {
        var regSearch = view?.findViewById<FloatingActionButton>(R.id.ruedaDelete)
        regSearch?.setOnClickListener {
            var intent = Intent(activity, SearchActivity::class.java)
            startActivity(intent)
        }
    }
}