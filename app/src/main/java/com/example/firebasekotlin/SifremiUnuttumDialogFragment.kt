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
import com.google.firebase.auth.FirebaseAuth

/**
 * A simple [Fragment] subclass.
 */
class SifremiUnuttumDialogFragment : DialogFragment() {

    lateinit var emailEditText: TextView
    lateinit var sifreEditText: TextView
    lateinit var mContext : FragmentActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_sifremi_unuttum_dialog, container, false)

        var btnIptal = view.findViewById<Button>(R.id.btnIptalSifreyiUnuttumDialog)
        var btnGonder = view.findViewById<Button>(R.id.btnGonderSifreyiUnuttumDialog)
        emailEditText = view.findViewById(R.id.etSifreyiTekrarGonder)
        mContext = activity!!

        btnIptal.setOnClickListener { dialog?.dismiss() }

        btnGonder.setOnClickListener {
            FirebaseAuth.getInstance().sendPasswordResetEmail(emailEditText.text.toString())
                .addOnCompleteListener { Task->

                    if(Task.isSuccessful){
                        Toast.makeText(mContext,"Sıfırlama maili başarıyla gönderildi!",Toast.LENGTH_SHORT).show()
                        dialog?.dismiss()
                    }else{
                        Toast.makeText(mContext,"Hata oluştu! : " +Task.exception?.message,Toast.LENGTH_SHORT).show()
                        dialog?.dismiss()
                    }

                }

        }

        return view
    }

}
