package es.uca.tfg.conexionmorada.ui

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import es.uca.tfg.conexionmorada.firestore.User
import es.uca.tfg.conexionmorada.retrofit.APIRetrofit
import es.uca.tfg.conexionmorada.storage.Storage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
    }

    fun navegateSettings() {
        var regSettings = view?.findViewById<FloatingActionButton>(R.id.ruedaSettings)
        regSettings?.setOnClickListener {
            var intent = Intent(activity, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    fun lastArticlesClick() {
        var call = APIRetrofit().lastArticles(createPayloadArticles())

        if (call != null) {
            call.enqueue(object : Callback<List<Article>> {

                override fun onResponse(call: Call<List<Article>>, response: Response<List<Article>>){
                    if(response.isSuccessful){
                        //Toast.makeText(activity, "Bien " + response.body()!!.get(3), Toast.LENGTH_SHORT).show()
                        //Toast.makeText(activity, "Bien " + response.body() , Toast.LENGTH_SHORT).show()
                        response.body()?.let { addRecyclerViewArticles(it) }

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

    fun createPayloadArticles(): PayloadArticle {
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
        var regSearch = view?.findViewById<FloatingActionButton>(R.id.ruedaSearch)
        regSearch?.setOnClickListener {
            var intent = Intent(activity, SearchActivity::class.java)
            startActivity(intent)
        }
    }
}