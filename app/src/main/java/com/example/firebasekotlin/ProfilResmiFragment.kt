package com.example.firebasekotlin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment

/**
 * A simple [Fragment] subclass.
 */
class ProfilResmiFragment : DialogFragment() {

    interface onProfilResmiListener{
        fun getResimYolu(resimPath: Uri?)
        fun getResimBitMap(bitMap:Bitmap)
    }

    lateinit var myProfilResimListener:onProfilResmiListener

    lateinit var tvGaleridenSec:TextView
    lateinit var tvFotoCek:TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_profil_resmi, container, false)

        tvGaleridenSec = view.findViewById(R.id.tvGaleridenFoto)
        tvFotoCek = view.findViewById(R.id.tvKameradanFoto)

        tvGaleridenSec.setOnClickListener {

            var intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent,100)

        }

        tvFotoCek.setOnClickListener {
            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,200)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //galeriden resim
        if(requestCode == 100 && resultCode == Activity.RESULT_OK && data != null){

            var secilenResimYolu = data.data
            myProfilResimListener.getResimYolu(secilenResimYolu)
            dismiss()

        }
        //kameradan resim
        else if(requestCode == 200 && resultCode == Activity.RESULT_OK && data != null){

            var kameradanCekilenResim:Bitmap
            kameradanCekilenResim = data.extras?.get("data") as Bitmap
            myProfilResimListener.getResimBitMap(kameradanCekilenResim)
            dismiss()
        }


    }

    override fun onAttach(context: Context) {

        myProfilResimListener = activity as onProfilResmiListener
        super.onAttach(context)
    }

}
