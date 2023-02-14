package es.uca.tfg.conexionmorada.utils.retrofit

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uca.tfg.conexionmorada.articles.interfaces.CRUDInterface
import es.uca.tfg.conexionmorada.articles.model.Article
import es.uca.tfg.conexionmorada.articles.model.PayloadArticle
import es.uca.tfg.conexionmorada.cmSocial.data.PayloadHilo
import es.uca.tfg.conexionmorada.cmSocial.data.PayloadSeguidores
import es.uca.tfg.conexionmorada.cmSocial.interfaces.cmSocialInterface
import es.uca.tfg.conexionmorada.usernames.data.PayloadUsername
import es.uca.tfg.conexionmorada.usernames.interfaces.UsernameInterface
import es.uca.tfg.conexionmorada.utils.Constants
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIRetrofit {
    var retrofit: Retrofit

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
        return crudInterface.addUsername(PayloadUsername(Firebase.auth.currentUser!!.uid, username))
    }

    fun deleteUsername(username : String): Call<Boolean>? {
        var crudInterface = retrofit.create(UsernameInterface::class.java)
        return crudInterface.deleteUsername(username)
    }

    fun lastArticles(payloadArticle: PayloadArticle): Call<List<Article>>  {
        var crudInterface = retrofit.create(CRUDInterface::class.java)
        return crudInterface.lastArticles(payloadArticle)
    }

    fun searchArticles(query: String, numberArticles: Int): Call<List<Article>> {
        var crudInterface = retrofit.create(CRUDInterface::class.java)
        return  crudInterface.query(query, numberArticles)
    }

    fun addUserApp(uuid: String): Call<Void> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.addUserApp(uuid)
    }

    fun getSeguidores(uuid: String): Call<Int> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return  crudInterface.getSeguidores(uuid)
    }

    fun getSeguidos(uuid: String): Call<Int> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return  crudInterface.getSeguidos(uuid)
    }

    fun addSeguidor(payloadSeguidores: PayloadSeguidores): Call<Void> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.addSeguidor(payloadSeguidores)
    }

    fun deleteSeguidor(uuidSeguido: String, uuidSeguidor: String): Call<Void> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.deleteSeguidor(uuidSeguido, uuidSeguidor)
    }

    fun seguidorExist(uuidSeguidor: String, uuidSeguido: String): Call<Boolean> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.getSeguidorExist(uuidSeguidor, uuidSeguido)
    }

    fun addHilo(payloadHilo: PayloadHilo) : Call<Void> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.addHilo(payloadHilo)
    }

    fun getLastHilos(uuid:String): Call<List<PayloadHilo>> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.getLastHilos(uuid)
    }

    fun getLastHilosSeguidos(uuid:String): Call<List<PayloadHilo>> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.getLastHilosSeguidos(uuid)
    }

    fun searchHilos(query: String): Call<List<PayloadHilo>> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.searchHilos(query, Firebase.auth.currentUser!!.uid)
    }

    fun searchUsuarios(query: String): Call<List<PayloadUsername>> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.searchUsuarios(query)
    }

    fun getHilosUser(uuid: String): Call<List<PayloadHilo>> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.getHilosUser(uuid)
    }

    fun getRespuestas(idHilo: String): Call<List<PayloadHilo>> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.getRespuestas(idHilo, Firebase.auth.currentUser!!.uid)
    }

    fun addLike(payloadHilo: PayloadHilo): Call<Void> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.addLike(payloadHilo)
    }

    fun deleteLike(idHilo: String, uuid: String): Call<Void> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.deleteLike(idHilo, uuid)
    }

    fun addDislike(payloadHilo: PayloadHilo): Call<Void> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.addDislike(payloadHilo)
    }

    fun deleteDislike(idHilo: String, uuid: String): Call<Void> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.deleteDislike(idHilo, uuid)
    }

    fun getLike(idHilo: String, uuid: String): Call<Boolean> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.getLike(idHilo, uuid)
    }

    fun getDislike(idHilo: String, uuid: String): Call<Boolean> {
        var crudInterface = retrofit.create(cmSocialInterface::class.java)
        return crudInterface.getDislike(idHilo, uuid)
    }
}