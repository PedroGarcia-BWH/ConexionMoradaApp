package es.uca.tfg.conexionmorada.ui

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uca.tfg.conexionmorada.R
import es.uca.tfg.conexionmorada.utils.firestore.User
import es.uca.tfg.conexionmorada.utils.retrofit.APIRetrofit
import es.uca.tfg.conexionmorada.verify.VerifyDialog
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUptabs()

        User.UserVerificado()?.get()?.addOnSuccessListener { document ->
            if (document.data != null) {
                val data_user = document.data
                if(data_user?.get("verificado") as Boolean != true) VerifyDialog(this).startDialog()

            } else {
                Log.d(ContentValues.TAG, "No such document")
            }
        }

    }

    private fun setUptabs(){
        val adapter = Controller(supportFragmentManager)
        adapter.addFragment(CMSocialFragment(), "CM Social")
        adapter.addFragment(HomeFragment(), "Home")
        adapter.addFragment(SistemaCompaneroFragment(), "Sistema Compañero")
        //tabs.getTabAt(0)?.setIcon(R.drawable.)
        val viewpager = findViewById<ViewPager>(R.id.viewpager)
        val tabs = findViewById<TabLayout>(R.id.tabs)
        viewpager.adapter = adapter
        tabs.setupWithViewPager(viewpager)
        viewpager.setCurrentItem(1)
    }


    fun recreateFragment(fragment : Fragment){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            supportFragmentManager.beginTransaction().detach(fragment).commitNow()
            supportFragmentManager.beginTransaction().attach(fragment).commitNow()
        }else{
            supportFragmentManager.beginTransaction().detach(fragment).attach(fragment).commitNow()
        }
    }
}