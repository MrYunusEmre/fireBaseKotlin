package com.example.firebasekotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    lateinit var myAuthStateListener : FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAuthStateListener()

    }

    private fun initAuthStateListener() { // sureklı tetiklenen yer
        myAuthStateListener = object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var kullanici = p0.currentUser

                if(kullanici != null){//giris yapmıstr

                }else{
                    var intent = Intent(this@MainActivity,
                        LoginActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(myAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        if(myAuthStateListener != null){
            FirebaseAuth.getInstance().removeAuthStateListener { myAuthStateListener }
        }
    }

    override fun onResume() {
        super.onResume()
        kullaniciyiKontrolEt()
        setKullaniciBilgileri()
    }

    private fun kullaniciyiKontrolEt() {
        var kullanici = FirebaseAuth.getInstance().currentUser

        if(kullanici == null){
            var intent = Intent(this@MainActivity,
                LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.anamenu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item?.itemId){

            R.id.menuCıkısYap ->{
                cikisYap()
                return true
            }

            R.id.menuHesapAyarlari ->{
                hesapAyarlariniGoster()
                return true
            }

            R.id.menuSohbet ->{
                var  intent = Intent(this,
                    SohbetActivity::class.java)
                startActivity(intent)
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun hesapAyarlariniGoster() {
        var  intent = Intent(this,
            HesapAyarlariActivity::class.java)
        startActivity(intent)

    }

    private fun cikisYap() {
        FirebaseAuth.getInstance().signOut()
    }

    private fun setKullaniciBilgileri(){

        var kullanici = FirebaseAuth.getInstance().currentUser

        if(kullanici != null){

            var tvMail:TextView = findViewById(R.id.tvEmail)
            var tvUserId:TextView = findViewById(R.id.tvUserID)
            var kullaniciAd:TextView = findViewById(R.id.tvKullaniciAd)

            kullaniciAd.text = if(kullanici.displayName.isNullOrEmpty()) "tanımlanmadı" else kullanici.displayName
            tvMail.text = kullanici.email.toString()
            tvUserId.text = kullanici.uid

        }

    }
}
