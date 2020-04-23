package com.example.firebasekotlin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class YeniSohbetFragment : DialogFragment() {

    lateinit var etsohbetOdasıAd:EditText
    lateinit var btnSohbetOdası:Button
    lateinit var sBarSeviye:SeekBar
    lateinit var tvSeviye:TextView
    var seviye = 0
    var mProgress = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_yeni_sohbet, container, false)

        etsohbetOdasıAd = view.findViewById(R.id.ptYeniSohbetOdasıAdı)
        btnSohbetOdası = view.findViewById(R.id.btnYeniSohbetOdasıOlustur)
        sBarSeviye = view.findViewById(R.id.sBarSeviye)
        tvSeviye = view.findViewById(R.id.tvYeniSohbetSeviye)

        tvSeviye.setText(mProgress.toString())


        sBarSeviye.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mProgress = progress
                tvSeviye.setText(mProgress.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        kullaniciSeviyeBilgisiniGetir()

        btnSohbetOdası.setOnClickListener {

            if(etsohbetOdasıAd.text.toString().isNotEmpty()){

                if(seviye >= sBarSeviye.progress){

                    var myFirestore = Firebase.firestore

                    var sohbetOdasiRef = myFirestore.collection("chats").document()

                    var yeniSohbetOdasi = sohbetOdası()
                    yeniSohbetOdasi.olusturan_id = FirebaseAuth.getInstance().currentUser?.uid.toString()
                    yeniSohbetOdasi.seviye = mProgress.toString()
                    yeniSohbetOdasi.sohbetodası_adi = etsohbetOdasıAd.text.toString()
                    yeniSohbetOdasi.sohbetodasi_id = sohbetOdasiRef.id.toString()

                    myFirestore.collection("chats")
                        .document(sohbetOdasiRef.id)
                        .set(yeniSohbetOdasi)


                    var karsilamaMesaji = SohbetMesaj()
                    karsilamaMesaji.mesaj = "Sohbet Odasına Hoşgeldiniz"
                    karsilamaMesaji.timestamp = getMesajTarihi()

                    var mesajlarRef = myFirestore.collection("chats").document(sohbetOdasiRef.id)
                        .collection("sohbet_odasi_mesajları").document()

                    myFirestore.collection("chats")
                        .document(sohbetOdasiRef.id)
                        .collection("sohbet_odasi_mesajları")
                        .document(mesajlarRef.id)
                        .set(karsilamaMesaji)

                    Toast.makeText(context,"Sohbet Odası Oluşturuldu",Toast.LENGTH_SHORT).show()
                    (activity as SohbetActivity).init()
                    dialog?.dismiss()



                }else{
                    Toast.makeText(context,"Seviyeniz olan $seviye 'den yukarı sohbet odası oluşturulamaz!",Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(context,"Oda adını giriniz!",Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun kullaniciSeviyeBilgisiniGetir() {

         Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .get().addOnSuccessListener { documentSnapshot: DocumentSnapshot? ->

                val user = documentSnapshot?.toObject<User>()
                seviye = user?.seviye!!.toInt()

            }
    }

    private fun getMesajTarihi() : String{

        var sdf = SimpleDateFormat("YYYY-MM-dd HH:mm:ss", Locale("tr"))
        return sdf.format(Date()).toString()

    }

}
