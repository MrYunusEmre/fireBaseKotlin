package com.example.firebasekotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.NumberPicker
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide.init
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import io.opencensus.metrics.export.Summary
import io.opencensus.metrics.export.Value
import kotlinx.android.synthetic.main.activity_sohbet_odasi.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class SohbetOdasiActivity : AppCompatActivity() {

    var myAuthListener: FirebaseAuth.AuthStateListener? = null

    var secilenSohbetOdasıID: String = ""

    var tumMesajlar: ArrayList<SohbetMesaj>? = null
    var mesajIdSet : HashSet<String>? = null
    var myAdapter:SohbetMesajRecyclerViewAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sohbet_odasi)

        başlatFirebaseAuthListener()

        sohbetOdasiniOgren()

        init()

    }

    private fun init(){



        btnMesajiGonder.setOnClickListener{

            if(!etMesajYazmaYeri.text.isNullOrEmpty()){

                var yazılanMesaj = etMesajYazmaYeri.text.toString()

                var kaydedilecekMesaj:SohbetMesaj = SohbetMesaj()
                kaydedilecekMesaj.mesaj = yazılanMesaj
                kaydedilecekMesaj.kullanıcı_id = FirebaseAuth.getInstance().currentUser?.uid
                kaydedilecekMesaj.timestamp = getMesajTarih()
                 Firebase.firestore.collection("users")
                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                     .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                        kaydedilecekMesaj.profil_resmi = documentSnapshot?.toObject<User>()?.profil_resmi
                         Log.e("Resim",documentSnapshot?.toObject<User>()?.profil_resmi)
                     }


                var newRef = Firebase.firestore.collection("chats").document(secilenSohbetOdasıID)
                    .collection("sohbet_odasi_mesajları").document().id

                Firebase.firestore.collection("chats").document(secilenSohbetOdasıID)
                    .collection("sohbet_odasi_mesajları")
                    .document(newRef)
                    .set(kaydedilecekMesaj)



            }


        }
    }

    private fun getMesajTarih(): String {

        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("tr")).format(Date())
    }

    private fun sohbetOdasiniOgren() {

        secilenSohbetOdasıID = intent.getStringExtra("sohbet_odası_id")
        baslatMesajListener()


    }


    private fun baslatMesajListener() { // burada kaldk snapshot ekledm eventListener yerine for döngusu icine metod koy hocaya ayak uydur

        Firebase.firestore.collection("chats").document(secilenSohbetOdasıID)
            .collection("sohbet_odasi_mesajları")
            .addSnapshotListener { snapshots, e ->

                if (e != null) {
                    Log.w("EHE", "listen:error", e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        Log.d("EHE", "New city: ${dc.document.data}")
                    } else {

                    }
                    sohbetOdasındakiMesajlariGetir()


                }

            }


    }


    private fun sohbetOdasındakiMesajlariGetir() {

        if (tumMesajlar == null) {

            tumMesajlar = ArrayList<SohbetMesaj>()
            mesajIdSet = HashSet()

        }


            Firebase.firestore.collection("chats").document(secilenSohbetOdasıID)
                .collection("sohbet_odasi_mesajları")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                    for (currentMesaj in querySnapshot!!.documents) {
                        var eklenecekMesaj = SohbetMesaj()

                        var kullaniciId =
                            currentMesaj.toObject(SohbetMesaj::class.java)!!.kullanıcı_id

                        if(!mesajIdSet!!.contains(currentMesaj?.toString())){

                            mesajIdSet!!.add(currentMesaj.toString())

                            if (kullaniciId != null) {
                                eklenecekMesaj.kullanıcı_id = kullaniciId
                                eklenecekMesaj.mesaj =
                                    currentMesaj.toObject(SohbetMesaj::class.java)!!.mesaj
                                eklenecekMesaj.timestamp =
                                    currentMesaj.toObject(SohbetMesaj::class.java)!!.timestamp

                                Firebase.firestore.collection("users").document(kullaniciId)
                                    .addSnapshotListener(object : EventListener<DocumentSnapshot> {
                                        override fun onEvent(
                                            p0: DocumentSnapshot?,
                                            p1: FirebaseFirestoreException?
                                        ) {
                                            var bulunanKullanici = p0?.toObject<User>()
                                            eklenecekMesaj.profil_resmi = bulunanKullanici?.profil_resmi
                                            eklenecekMesaj.adı = bulunanKullanici?.isim
                                        }

                                    })

                                tumMesajlar!!.add(eklenecekMesaj)
                                myAdapter?.notifyDataSetChanged()
                                rvSohbetOdasıActiviy.scrollToPosition(myAdapter?.itemCount!! - 1)

                            } else {
                                eklenecekMesaj.mesaj =
                                    currentMesaj.toObject(SohbetMesaj::class.java)!!.mesaj
                                eklenecekMesaj.timestamp =
                                    currentMesaj.toObject(SohbetMesaj::class.java)!!.timestamp
                                tumMesajlar!!.add(eklenecekMesaj)
                                myAdapter?.notifyDataSetChanged()

                            }

                        }



                    }
                }



        if(myAdapter == null){
            initMesajList()
        }


    }

    private fun initMesajList() {
        myAdapter = SohbetMesajRecyclerViewAdapter(this,tumMesajlar!!)

        rvSohbetOdasıActiviy.adapter = myAdapter
        rvSohbetOdasıActiviy.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rvSohbetOdasıActiviy.scrollToPosition(myAdapter?.itemCount!! - 1)
    }

    private fun başlatFirebaseAuthListener() {
        myAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {

                var kullanici = p0.currentUser

                if (kullanici == null) {
                    var intent = Intent(this@SohbetOdasiActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {

                }

            }

        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(myAuthListener!!)
    }

    override fun onStop() {
        super.onStop()
        if (myAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(myAuthListener!!)
        }
    }

    override fun onResume() {
        super.onResume()
        kullaniciyiKontrolEt()
    }

    private fun kullaniciyiKontrolEt() {
        var kullanici = FirebaseAuth.getInstance().currentUser

        if (kullanici == null) {
            var intent = Intent(this@SohbetOdasiActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}


/*  private fun sohbetOdasndakiVerileriGetir() {
        var secilenSohbetOdasıId = intent.getStringExtra("sohbet_odası_id")
        tumMesajlar = ArrayList<SohbetMesaj>()

        Firebase.firestore.collection("chats").document(secilenSohbetOdasıId.toString())
            .collection("sohbet_odasi_mesajları")
            .addSnapshotListener{querySnapshot, firebaseFirestoreException ->

                for(currentMesaj in querySnapshot!!.documents){
                    var eklenecekMesaj = SohbetMesaj()

                    var kullaniciId = currentMesaj.toObject(SohbetMesaj::class.java)!!.kullanıcı_id

                    if(kullaniciId != null){
                        eklenecekMesaj.kullanıcı_id = kullaniciId
                        eklenecekMesaj.mesaj = currentMesaj.toObject(SohbetMesaj::class.java)!!.mesaj
                        eklenecekMesaj.timestamp = currentMesaj.toObject(SohbetMesaj::class.java)!!.timestamp
                        tumMesajlar.add(eklenecekMesaj)

                    }else{
                        eklenecekMesaj.mesaj = currentMesaj.toObject(SohbetMesaj::class.java)!!.mesaj
                        eklenecekMesaj.timestamp = currentMesaj.toObject(SohbetMesaj::class.java)!!.timestamp
                        tumMesajlar.add(eklenecekMesaj)

                    }

                }
            }

    }


 */