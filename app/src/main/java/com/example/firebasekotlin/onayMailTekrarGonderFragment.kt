package com.example.firebasekotlin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth


/**
 * A simple [Fragment] subclass.
 */
class onayMailTekrarGonderFragment : DialogFragment() {

    lateinit var emailEditText: TextView
    lateinit var sifreEditText: TextView
    lateinit var mContext : FragmentActivity
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_dialog, container, false)

        var btnIptal = view.findViewById<Button>(R.id.btnDialogIptal)
        var btnGonder = view.findViewById<Button>(R.id.btnDialogGonder)
        emailEditText = view.findViewById(R.id.etDialogMail)
        sifreEditText = view.findViewById(R.id.etPwDialog)
        mContext = activity!!

        btnIptal.setOnClickListener { dialog?.dismiss() }

        btnGonder.setOnClickListener {

            if (emailEditText.text.toString().isNotEmpty() && sifreEditText.text.toString()
                    .isNotEmpty()
            ) {

                girisYapVeOnayMailiniTekrarGonder(
                    emailEditText.text.toString(),
                    sifreEditText.text.toString()
                )

            } else {
                Toast.makeText(mContext, "Alanları doldurunuz!", Toast.LENGTH_SHORT).show()
            }

        }


        return view
    }

    private fun girisYapVeOnayMailiniTekrarGonder(email: String, sifre: String) {

        var credential = EmailAuthProvider.getCredential(email,sifre)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener{ Task ->

                if(Task.isSuccessful){
                    onayMailiniTekrarGonder()
                    dialog?.dismiss()
                }else{
                    Toast.makeText(mContext,"Email veya Şifre Hatalı !",Toast.LENGTH_SHORT).show()
                }

            }



    }

    private fun onayMailiniTekrarGonder() {
        var kullanici = FirebaseAuth.getInstance().currentUser

        if(kullanici != null){
            kullanici.sendEmailVerification()
                .addOnCompleteListener{ Task ->
                    if(Task.isSuccessful){
                        Toast.makeText(mContext,"Mailinizi Onaylayınız.",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(mContext,"Mail Gönderilemedi!",Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

}
