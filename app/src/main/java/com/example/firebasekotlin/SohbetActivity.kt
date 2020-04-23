package com.example.firebasekotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.NumberPicker
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sohbet.*

class SohbetActivity : AppCompatActivity() {

    lateinit var tumSohbetOdaları:ArrayList<sohbetOdası>
    var myAdapter:SohbetOdasiRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sohbet)

        init()

    }

    fun init() {
        myAdapter?.notifyDataSetChanged()
        tumSohbetOdalarınıGetir()

        fabYeniSohbetOdasi.setOnClickListener {

            var dialog = YeniSohbetFragment()
            dialog.show(supportFragmentManager,"gosterYeniSohbetOdası")

        }


    }

    private fun tumSohbetOdalarınıGetir() {

        tumSohbetOdaları = ArrayList<sohbetOdası>()

        FirebaseFirestore.getInstance().collection("chats")
            .get()
            .addOnSuccessListener { result ->

                for(document in result.documents){
                    var currentSohbetOdası = sohbetOdası()

                    currentSohbetOdası.olusturan_id = document.get("olusturan_id").toString()
                    currentSohbetOdası.seviye = document.get("seviye").toString()
                    currentSohbetOdası.sohbetodasi_id = document.get("sohbetodasi_id").toString()
                    currentSohbetOdası.sohbetodası_adi = document.get("sohbetodası_adi").toString()

                    var tumMesajlar = ArrayList<SohbetMesaj>()

                    FirebaseFirestore.getInstance().collection("chats").document(document.id)
                        .collection("sohbet_odasi_mesajları").addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                            if (firebaseFirestoreException != null) {

                            } else {
                                for (document in querySnapshot!!.getDocuments()) {
                                    var sohbetMesaji = SohbetMesaj()
                                    sohbetMesaji.adı = document.toObject(SohbetMesaj::class.java)!!.adı
                                    sohbetMesaji.kullanıcı_id = document.toObject(SohbetMesaj::class.java)!!.kullanıcı_id
                                    sohbetMesaji.mesaj = document.toObject(SohbetMesaj::class.java)!!.mesaj
                                    sohbetMesaji.timestamp = document.toObject(SohbetMesaj::class.java)!!.timestamp
                                    sohbetMesaji.profil_resmi = document.toObject(SohbetMesaj::class.java)!!.profil_resmi

                                    Log.e("EHe",sohbetMesaji.mesaj.toString() + " time : "+sohbetMesaji.timestamp.toString())

                                    tumMesajlar.add(sohbetMesaji)
                                    myAdapter?.notifyDataSetChanged()
                                }

                            }
                        }
                    currentSohbetOdası.sohbet_odasi_mesajları = tumMesajlar
                    tumSohbetOdaları.add(currentSohbetOdası)
                    myAdapter?.notifyDataSetChanged()

                }

                Log.e("Ehe","Tüm sohbet odası sayısı : " + tumSohbetOdaları.size)
            }

        if(myAdapter == null)
            sohbetOdalarıListele()
    }

    private fun sohbetOdalarıListele() {

        myAdapter = SohbetOdasiRecyclerViewAdapter(this@SohbetActivity,tumSohbetOdaları)

        rvSohbetOdalari.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rvSohbetOdalari.adapter = myAdapter
    }

     fun sohbetOdasiSil(silinecekSohbetOdasiID:String){

         Firebase.firestore.collection("chats")
             .document(silinecekSohbetOdasiID)
             .delete()

         Toast.makeText(this,"Sohbet odası silindi",Toast.LENGTH_SHORT).show()
         myAdapter?.notifyDataSetChanged()
         init()

    }


    fun adapterGonder():SohbetOdasiRecyclerViewAdapter{
        return myAdapter!!
    }
}
