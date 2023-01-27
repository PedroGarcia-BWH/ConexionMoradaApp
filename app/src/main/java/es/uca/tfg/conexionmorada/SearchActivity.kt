package es.uca.tfg.conexionmorada

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.uca.tfg.conexionmorada.articles.ArticleActivity
import es.uca.tfg.conexionmorada.articles.adapter.ArticleAdapter
import es.uca.tfg.conexionmorada.articles.interfaces.CRUDInterface
import es.uca.tfg.conexionmorada.articles.model.Article
import es.uca.tfg.conexionmorada.retrofit.APIRetrofit
import es.uca.tfg.conexionmorada.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        search()
    }

    fun search() {
        var search = findViewById<SearchView>(R.id.search)
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //Toast.makeText(applicationContext, "Buscando: " + query, Toast.LENGTH_SHORT).show()
                searchArticles(query!!)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }


    fun searchArticles(query: String) {
        var imageNoResult = findViewById<ImageView>(R.id.imageNoResults)
        var textNoResult = findViewById<TextView>(R.id.txtNoResults)
        var call = APIRetrofit().searchArticles(query, 40)
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
}