package es.uca.tfg.conexionmorada.retrofit

import es.uca.tfg.conexionmorada.articles.interfaces.CRUDInterface
import es.uca.tfg.conexionmorada.articles.model.Article
import es.uca.tfg.conexionmorada.articles.model.PayloadArticle
import es.uca.tfg.conexionmorada.usernames.interfaces.UsernameInterface
import es.uca.tfg.conexionmorada.utils.Constants
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIRetrofit {
    lateinit var retrofit: Retrofit

    constructor(){
        retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }

    fun getUsername(username : String): Call<Boolean>? {
        var crudInterface = retrofit.create(UsernameInterface::class.java)
        return crudInterface.existUsername(username)
    }

    fun addUsername(username : String): Call<Boolean>? {
        var crudInterface = retrofit.create(UsernameInterface::class.java)
        return crudInterface.addUsername(username)
    }

    fun lastArticles(payloadArticle: PayloadArticle): Call<List<Article>>  {
        var crudInterface = retrofit.create(CRUDInterface::class.java)
        return crudInterface.lastArticles(payloadArticle)
    }

    fun searchArticles(query: String, numberArticles: Int): Call<List<Article>> {
        var crudInterface = retrofit.create(CRUDInterface::class.java)
        return  crudInterface.query(query, numberArticles)
    }
}