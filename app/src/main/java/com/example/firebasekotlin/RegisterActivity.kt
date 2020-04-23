package com.example.firebasekotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnKayıtOl.setOnClickListener{

            if(etMail.text.isNotEmpty() && etPW1.text.isNotEmpty() && etPW2.text.isNotEmpty()){

                if(etPW1.text.toString().equals(etPW2.text.toString())){

                    yeniUyeKayit(etMail.text.toString(),etPW1.text.toString())

                }else{
                    Toast.makeText(this,"Şifreler aynı değil!",Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(this,"Boş alanları doldurunuz!",Toast.LENGTH_SHORT).show()
            }


        }
    }

    private fun yeniUyeKayit(mail: String, sifre: String) {

        progressBarGoster()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail,sifre)
            .addOnCompleteListener(this){ task ->

                if(task.isSuccessful){

                    onayMailiGonder()

                    var veriTabaninaEklenecekUser: User =
                        User()
                    veriTabaninaEklenecekUser.isim = mail.substring(0,mail.indexOf("@"))
                    veriTabaninaEklenecekUser.kullanıcı_id = task.result?.user?.uid.toString()
                    veriTabaninaEklenecekUser.profil_resmi = ""
                    veriTabaninaEklenecekUser.telefon = "123"
                    veriTabaninaEklenecekUser.seviye = "1"

                    val db = Firebase.firestore

                    db.collection("users").document(veriTabaninaEklenecekUser.kullanıcı_id.toString())
                        .set(veriTabaninaEklenecekUser)
                        .addOnCompleteListener{task ->

                            if(task.isSuccessful){
                                Toast.makeText(this@RegisterActivity,"Üye kaydedildi!",Toast.LENGTH_SHORT).show()
                                FirebaseAuth.getInstance().signOut()
                                loginSayfasinaYonlendir()

                            }else{

                                Log.e("Ehe","Eklenmedi hata : "+task.exception.toString())

                            }

                        }



                }else{
                    Toast.makeText(this@RegisterActivity,"Üye kaydedilemedi!",Toast.LENGTH_SHORT).show()

                }
            }

        progressBarGizle()
    }

    private fun onayMailiGonder() {
        var kullanici = FirebaseAuth.getInstance().currentUser

        if(kullanici != null){
            kullanici.sendEmailVerification()
                .addOnCompleteListener{ Task ->
                    if(Task.isSuccessful){
                        Toast.makeText(this@RegisterActivity,"Mailinizi Onaylayınız.",Toast.LENGTH_SHORT).show()

                    }else{
                        Toast.makeText(this@RegisterActivity,"Mail Gönderilemedi!",Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }

    private fun progressBarGoster(){
        progressBar.visibility = View.VISIBLE
    }
    private fun progressBarGizle(){
        progressBar.visibility = View.INVISIBLE
    }

    private fun loginSayfasinaYonlendir(){
        var intent = Intent(this,
            LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
