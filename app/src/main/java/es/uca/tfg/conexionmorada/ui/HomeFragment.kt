package es.uca.tfg.conexionmorada.ui

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import es.uca.tfg.conexionmorada.LoginActivity
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.SettingsActivity
import es.uca.tfg.conexionmorada.articles.ArticleActivity
import es.uca.tfg.conexionmorada.articles.adapter.ArticleAdapter
import es.uca.tfg.conexionmorada.articles.interfaces.CRUDInterface
import es.uca.tfg.conexionmorada.articles.model.Article
import es.uca.tfg.conexionmorada.articles.model.PayloadArticle
import es.uca.tfg.conexionmorada.firestore.User
import es.uca.tfg.conexionmorada.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {
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
    }

    fun navegateSettings() {
        var regSettings = view?.findViewById<FloatingActionButton>(R.id.ruedaSettings)
        regSettings?.setOnClickListener {
            var intent = Intent(activity, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    fun lastArticlesClick() {
        var retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        var crudInterface = retrofit.create(CRUDInterface::class.java)
        var call = crudInterface.lastArticles(createPayloadArticles())

        call.enqueue(object : Callback<List<Article>> {

           override fun onResponse(call: Call<List<Article>>, response: Response<List<Article>>){
               if(response.isSuccessful){
                   Toast.makeText(activity, "Bien" + response.body()!!.size , Toast.LENGTH_SHORT).show()
                   addRecyclerViewArticles(response.body()!!)

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

    fun createPayloadArticles(): PayloadArticle {
        var payloadArticle = PayloadArticle()
        payloadArticle.numberArticles = 10
        payloadArticle.status = "REQUESTED"
        payloadArticle.preferences = ArrayList<String>()

        val doc_ref = User.getDatosUser()
        doc_ref!!.get().addOnSuccessListener { document ->
            if (document.data != null) {
                var list = ArrayList<String>()
                val data_user = document.data
                if(data_user?.get("Violencia de genero") == true) list.add(data_user?.get("Violencia de genero").toString())

                if(data_user?.get("Violencia sexual") == true) list.add(data_user?.get("Violencia sexual").toString())

                if(data_user?.get("Igualdad") == true) list.add(data_user?.get("Igualdad").toString())


                payloadArticle.preferences = list
            } else {
                Log.d(TAG, "No such document")
                var intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        return payloadArticle
    }

    fun addRecyclerViewArticles(articles: List<Article>) {
        val recyclerview = view?.findViewById<RecyclerView>(R.id.RecyclerViewArticles)
        recyclerview?.layoutManager = LinearLayoutManager(activity)
        (recyclerview?.adapter as ArticleAdapter).setData(articles)

        recyclerview?.adapter = ArticleAdapter { article ->
            articleSelected(article as Article)
        }
    }

    fun articleSelected(article: Article) {
        val intent =Intent(activity, ArticleActivity::class.java)
        intent.putExtra("title", article.title)
        intent.putExtra("body", article.body)
        intent.putExtra("date", article.creationDate)
        intent.putExtra("category", article.category)
        activity?.startActivity(intent)
    }
}