package com.example.firebasekotlin

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_hesap_ayarlari.btnDegisiklkKaydet
import kotlinx.android.synthetic.main.activity_hesap_ayarlari.etMailSifreGuncelle
import kotlinx.android.synthetic.main.activity_hesap_ayarlari.btnMailiGuncellee
import kotlinx.android.synthetic.main.activity_hesap_ayarlari.etSifreSifirla
import kotlinx.android.synthetic.main.activity_hesap_ayarlari.btnSifreyiGuncellee
import kotlinx.android.synthetic.main.activity_hesap_ayarlari.etHesapAyarlarıKullaniciAd
import kotlinx.android.synthetic.main.activity_new_hesap_ayarlari.*
import java.io.ByteArrayOutputStream

class HesapAyarlariActivity : AppCompatActivity() , ProfilResmiFragment.onProfilResmiListener {

    var izinDurumu = false
    var galeridenGelenUri:Uri? = null
    var kameradanGelenBitmap:Bitmap? = null

    inner class BackgroundResimCompressing : AsyncTask<Uri,Void,ByteArray?>{

        var myBitmap:Bitmap? = null


        constructor(){}

        constructor(bm:Bitmap){
            if(bm != null){
                myBitmap = bm
            }

        }

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Uri?): ByteArray? {
            //galeriden resim seçilmiş
            if(myBitmap == null){

                myBitmap = MediaStore.Images.Media.getBitmap(this@HesapAyarlariActivity.contentResolver,params[0])
            }

            var resimBytes:ByteArray? = null

            for(i in 1..5){
                resimBytes = convertBitmapToByte(myBitmap,100/i)
                publishProgress()
            }

            return resimBytes

        }


        private fun convertBitmapToByte(myBitmap: Bitmap?, i: Int): ByteArray? {

            var stream = ByteArrayOutputStream()

            myBitmap?.compress(Bitmap.CompressFormat.JPEG,i,stream)
            return stream.toByteArray()
        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: ByteArray?) {
            super.onPostExecute(result)
            uploadFotoToFirebase(result)
        }
    }


    private fun uploadFotoToFirebase(result: ByteArray?) {

        var storage = FirebaseStorage.getInstance().getReference()
        var uploadTask = storage.child("users").child("images")
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .putBytes(result!!)
            .addOnSuccessListener { task ->
                Toast.makeText(this,"Fotograf Basarıyla Kaydedildi!",Toast.LENGTH_SHORT).show()
            }

        var ref = storage.child("users").child("images")
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())

        var url = uploadTask.continueWithTask{ task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                Firebase.firestore.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .update("profil_resmi",downloadUri.toString())
            } else {
                // Handle failures
                // ...
            }
        }





    }

    override fun getResimYolu(resimPath: Uri?) {

        galeridenGelenUri = resimPath

        Glide
            .with(this)
            .load(galeridenGelenUri)
            .centerCrop()
            .into(imgProfilResmi)

    }

    override fun getResimBitMap(bitMap: Bitmap) {

        kameradanGelenBitmap = bitMap

        Glide.with(this).load(kameradanGelenBitmap).into(imgProfilResmi)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_hesap_ayarlari)

        var kullanici = FirebaseAuth.getInstance().currentUser

        kullaniciBilgileriniOku()



        etSifreSifirla.setOnClickListener {

            FirebaseAuth.getInstance().sendPasswordResetEmail(kullanici?.email.toString())
                .addOnCompleteListener { Task->

                    if(Task.isSuccessful){
                        Toast.makeText(this,"Sıfırlama maili başarıyla gönderildi!", Toast.LENGTH_SHORT).show()

                    }else{
                        Toast.makeText(this,"Hata oluştu! : " +Task.exception?.message, Toast.LENGTH_SHORT).show()

                    }

                }
        }

        btnDegisiklkKaydet.setOnClickListener {

            var myFirestore = Firebase.firestore

            if(etHesapAyarlarıKullaniciAd.text.isNotEmpty()){

                if(!etHesapAyarlarıKullaniciAd.text.toString().equals(kullanici?.displayName.toString())){

                    var bilgileriGuncelle = UserProfileChangeRequest.Builder()
                        .setDisplayName(etHesapAyarlarıKullaniciAd.text.toString())
                        .build()
                    kullanici?.updateProfile(bilgileriGuncelle)
                        ?.addOnCompleteListener { task ->
                            if(task.isSuccessful){

                                var degisecekAlan = myFirestore.collection("users")
                                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())

                                degisecekAlan.update("isim",etHesapAyarlarıKullaniciAd.text.toString())
                                    .addOnCompleteListener { task ->
                                        if(task.isSuccessful){
                                            Toast.makeText(this@HesapAyarlariActivity,"Bilgileriniz Güncellendi",Toast.LENGTH_SHORT).show()
                                        }else{

                                        }
                                    }

                            }
                            else{

                            }
                        }
                }

            }else{
                Toast.makeText(this@HesapAyarlariActivity,"Kullanıcı adını doldurunuz",Toast.LENGTH_SHORT).show()
            }

            if(etKullanıcıAyarlariTelefonNo.text.toString().isNotEmpty()){

                     myFirestore.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .update("telefon",etKullanıcıAyarlariTelefonNo.text.toString())
            }

            if(galeridenGelenUri != null){

                fotoCompressed(galeridenGelenUri!!)

            }else if(kameradanGelenBitmap != null){

                fotoCompressed(kameradanGelenBitmap!!)
            }

        }


        etMailSifreGuncelle.setOnClickListener {

            if(editText7.text.isNotEmpty()){

                var credential = EmailAuthProvider.getCredential(kullanici?.email.toString(),editText7.text.toString())
                kullanici?.reauthenticate(credential)
                    ?.addOnCompleteListener {task ->
                        if(task.isSuccessful){

                            constraintLayout.visibility = View.VISIBLE

                            btnSifreyiGuncellee.setOnClickListener{

                                sifreGuncelle()

                            }

                            btnMailiGuncellee.setOnClickListener{

                                mailGuncelle()
                            }

                        }else{
                            Toast.makeText(this@HesapAyarlariActivity,"Şifrenizi yanlış girdiniz!",Toast.LENGTH_SHORT).show()
                            constraintLayout.visibility = View.INVISIBLE
                        }
                    }

            }else{
                Toast.makeText(this@HesapAyarlariActivity,"Geçerli şifrenizi girmelisiniz",Toast.LENGTH_SHORT).show()
            }


        }

        imgProfilResmi.setOnClickListener {

            if(izinDurumu){
                var dialog = ProfilResmiFragment()
                dialog.show(supportFragmentManager,"foto")
            }else{
                izinleriIste()
            }


        }

    }

    private fun fotoCompressed(galeridenGelenUri: Uri) {

        var compressed = BackgroundResimCompressing()
        compressed.execute(galeridenGelenUri)
    }

    private fun fotoCompressed(kameradanGelenBitmap : Bitmap){

        var compressed = BackgroundResimCompressing(kameradanGelenBitmap)
        var uri:Uri? = null
        compressed.execute(uri)
    }

    private fun izinleriIste() {

        var izinler = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA
        )

        if(ContextCompat.checkSelfPermission(this,izinler[0]) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this,izinler[1]) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this,izinler[2]) == PackageManager.PERMISSION_GRANTED){

            izinDurumu = true
        }else{
            ActivityCompat.requestPermissions(this,izinler,150)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 150){

            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2] == PackageManager.PERMISSION_GRANTED){
                var dialog = ProfilResmiFragment()
                dialog.show(supportFragmentManager,"foto")
            }else{
                Toast.makeText(this,"Tüm izinleri vermelisiniz!",Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun kullaniciBilgileriniOku() {
        var kullanici = FirebaseAuth.getInstance().currentUser
        var myFirebase = Firebase.firestore
        var storage = FirebaseStorage.getInstance().getReference()
        val docRef = myFirebase.collection("users").document(kullanici?.uid.toString())

        docRef.get().addOnSuccessListener { documentSnapshot: DocumentSnapshot? ->

            val user = documentSnapshot?.toObject<User>()

            etKullanıcıAyarlariEmail.setText(kullanici?.email.toString())
            etHesapAyarlarıKullaniciAd.setText(user?.isim)
            etKullanıcıAyarlariTelefonNo.setText(user?.telefon)

            Glide.with(this)
                .load(user?.profil_resmi.toString())
                .into(imgProfilResmi)


        }


    }

    private fun sifreGuncelle() {
        var kullanici = FirebaseAuth.getInstance().currentUser

        if(kullanici != null){

            kullanici?.updatePassword(editText6.text.toString())
                .addOnCompleteListener{ task ->

                    if(task.isSuccessful){
                        Toast.makeText(this@HesapAyarlariActivity,"Şifreniz guncellendi",Toast.LENGTH_SHORT).show()
                        FirebaseAuth.getInstance().signOut()
                        loginSayfasinaYonlendir()
                    }else{

                    }

                }

        }
    }

    private fun mailGuncelle() {

        var kullanici = FirebaseAuth.getInstance().currentUser

        if(kullanici != null){

            FirebaseAuth.getInstance()?.fetchSignInMethodsForEmail(editText5.text.toString())
                .addOnSuccessListener { result ->
                    val signInMethods = result.signInMethods
                    if (signInMethods!!.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                        // User can sign in with email/password
                    } else if (signInMethods.contains(EmailAuthProvider.EMAIL_LINK_SIGN_IN_METHOD)) {
                        // User can sign in with email/link
                    }

                    kullanici?.updateEmail(editText5.text.toString())
                        .addOnCompleteListener{ task ->

                            if(task.isSuccessful){
                                Toast.makeText(this@HesapAyarlariActivity,"Mail adresiniz guncellendi",Toast.LENGTH_SHORT).show()
                                FirebaseAuth.getInstance().signOut()
                                loginSayfasinaYonlendir()
                            }else{

                            }

                        }

                }
                .addOnFailureListener { exception ->
                    Log.e("EHE", "Error getting sign in methods for user", exception)
                }



        }

    }

    private fun loginSayfasinaYonlendir(){
        var intent = Intent(this@HesapAyarlariActivity,
            LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


}
