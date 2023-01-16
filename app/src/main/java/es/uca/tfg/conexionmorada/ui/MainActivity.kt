package es.uca.tfg.conexionmorada.ui

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import es.uca.tfg.conexionmorada.R

class MainActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUptabs()
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