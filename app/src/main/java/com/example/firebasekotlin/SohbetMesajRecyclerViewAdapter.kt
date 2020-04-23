package com.example.firebasekotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_new_hesap_ayarlari.view.*
import kotlinx.android.synthetic.main.tek_satir_chat_layout.view.*

class SohbetMesajRecyclerViewAdapter(context: Context,tumMesajlar:ArrayList<SohbetMesaj>) :
    RecyclerView.Adapter<SohbetMesajRecyclerViewAdapter.SohbetMesajViewHolder>() {

    var tumMesajlar = tumMesajlar
    var mContext = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SohbetMesajViewHolder {
        var inflater = LayoutInflater.from(mContext)
        var view = inflater.inflate(R.layout.tek_satir_chat_layout,parent,false)

        return SohbetMesajViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tumMesajlar.size
    }

    override fun onBindViewHolder(holder: SohbetMesajViewHolder, position: Int) {

        var currentMesaj = tumMesajlar.get(position)
        holder.setData(currentMesaj,position)
    }

    inner class SohbetMesajViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var tumLayout = itemView as CardView

        var profilResmi = tumLayout.imgChat
        var mesaj = tumLayout.tvChatMesaj
        var isim = tumLayout.tvChatYazar
        var tarih = tumLayout.tvChatTarih

        fun setData(currentMesaj:SohbetMesaj,position:Int){

            mesaj.text = currentMesaj.mesaj
            isim.text = currentMesaj.adı
            tarih.text = currentMesaj.timestamp

            if(!currentMesaj.profil_resmi.toString().isNullOrEmpty()){
                Glide.with(itemView).load(currentMesaj.profil_resmi).into(profilResmi)
            }




        }

    }




}