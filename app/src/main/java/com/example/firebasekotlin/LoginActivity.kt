package com.example.firebasekotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    lateinit var mAuthState : FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initMyAuthStateListener()

        etSistemeKayitOl.setOnClickListener {
            var intent = Intent(this,
                RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {

            if(etMailGiris.text.isNotEmpty() && etPwGiris.text.isNotEmpty()){
                progressBarGoster()

                FirebaseAuth.getInstance().signInWithEmailAndPassword(etMailGiris.text.toString(),etPwGiris.text.toString())
                    .addOnCompleteListener { Task ->
                        if(Task.isSuccessful){

                            if(!Task.result!!.user!!.isEmailVerified){
                                FirebaseAuth.getInstance().signOut()
                            }

                        }else{
                            progressBarGizle()
                            Toast.makeText(this@LoginActivity,"Giriş yapılamadı",Toast.LENGTH_SHORT).show()

                        }

                    }

            }else{
                Toast.makeText(this@LoginActivity,"Boş alanları doldurunuz",Toast.LENGTH_SHORT).show()
            }
            progressBarGizle()
        }

        tvMailYenidenGonder.setOnClickListener {

            var dialogGoster =
                onayMailTekrarGonderFragment()
            dialogGoster.show(supportFragmentManager,"gosterDialog")

        }

        tvSifreUnuttum.setOnClickListener {

            var dialogSifreyiTekrarGonder =
                SifremiUnuttumDialogFragment()
            dialogSifreyiTekrarGonder.show(supportFragmentManager,"gosterDialogSifre")

        }

    }


    private fun progressBarGoster(){
        progressBarLogin.visibility = View.VISIBLE
    }
    private fun progressBarGizle(){
        progressBarLogin.visibility = View.INVISIBLE
    }

    private fun initMyAuthStateListener(){

        mAuthState = object: FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var kullanici = p0.currentUser

                if(kullanici != null){//sisteme giriş yapmışsa
                    if(kullanici.isEmailVerified){
                        Toast.makeText(this@LoginActivity,"Mail onaylanmış.",Toast.LENGTH_SHORT).show()
                        var intent = Intent(this@LoginActivity,
                            MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(this@LoginActivity,"Mail adresinizi onaylayın!",Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(mAuthState)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthState)
    }

}
