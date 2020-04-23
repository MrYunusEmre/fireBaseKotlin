package com.example.firebasekotlin

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.tek_satir_layout.view.*
import java.util.zip.Inflater

class SohbetOdasiRecyclerViewAdapter(mActivity:AppCompatActivity,tumSohbetler:ArrayList<sohbetOdası>) : RecyclerView.Adapter<SohbetOdasiRecyclerViewAdapter.SohbetOdasiHolder>(){

    var tumSohbetler = tumSohbetler
    var mActivity = mActivity as SohbetActivity
    var currentPosition:Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SohbetOdasiHolder {
        var inflater = LayoutInflater.from(parent.context)
        var tekSatirSohbetOdalari = inflater.inflate(R.layout.tek_satir_layout,parent,false)

        return SohbetOdasiHolder(tekSatirSohbetOdalari)
    }

    override fun getItemCount(): Int {
        return tumSohbetler.size
    }

    override fun onBindViewHolder(holder: SohbetOdasiHolder, position: Int) {
        currentPosition = position

        var currentSohbetOdasi = tumSohbetler.get(position)
        holder.setData(currentSohbetOdasi,position)
    }

    inner class SohbetOdasiHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var tekSatirSohbetOdasi = itemView as CardView

        var sohbetOdasiOlusturan = tekSatirSohbetOdasi.tvTekSatırLayoutİsim
        var sohbetOdasiResim = tekSatirSohbetOdasi.imgTekSatırLayoutProfil as ImageView
        var sohbetOdasiSil = tekSatirSohbetOdasi.imgTekSatirLayoutDelete
        var sohbetOdasiMesajSayisi = tekSatirSohbetOdasi.tvTekSatirLayoutMesajSayisi
        var sohbetOdasiAdi = tekSatirSohbetOdasi.tvTekSatirLayoutOdaAdi

         fun setData(currentSohbetOdası:sohbetOdası,position: Int){

             sohbetOdasiAdi?.text = currentSohbetOdası.sohbetodası_adi

             sohbetOdasiSil?.setOnClickListener {

                 if(currentSohbetOdası.olusturan_id.toString().equals(FirebaseAuth.getInstance().currentUser?.uid.toString())){

                     var dialog = AlertDialog.Builder(itemView.context)
                     dialog.setTitle("Sohbet odası silinecektir!")
                     dialog.setMessage("Emin misiniz?")
                     dialog.setPositiveButton("Sil",object: DialogInterface.OnClickListener{
                         override fun onClick(dialog: DialogInterface?, which: Int) {

                             mActivity.sohbetOdasiSil(currentSohbetOdası.sohbetodasi_id.toString())

                         }

                     })

                     dialog.setNegativeButton("Vazgeç",object: DialogInterface.OnClickListener{
                         override fun onClick(dialog: DialogInterface?, which: Int) {

                         }

                     })
                     dialog.setCancelable(true)

                     dialog.show()

                 }else{
                     Toast.makeText(itemView.context,"Bu odayı silebilmek için yönetici olmanız gerekmektedir.",Toast.LENGTH_SHORT).show()


                 }

             }

             tekSatirSohbetOdasi.setOnClickListener {

                 var intent = Intent(itemView.context,SohbetOdasiActivity::class.java)
                 intent.putExtra("sohbet_odası_id",currentSohbetOdası.sohbetodasi_id)
                 mActivity.startActivity(intent)
             }

             FirebaseFirestore.getInstance().collection("users")
                 .document(currentSohbetOdası.olusturan_id.toString())
                 .get()
                 .addOnSuccessListener { result ->
                    var profilResmiPath = result.toObject(User::class.java)!!.profil_resmi.toString()
                     sohbetOdasiOlusturan?.text = result.toObject(User::class.java)!!.isim.toString()

                     Glide.with(itemView.context)
                         .load(profilResmiPath)
                         .into(sohbetOdasiResim)
                 }


        }

    }



}