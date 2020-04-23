package com.example.firebasekotlin

class SohbetMesaj {

    var mesaj:String? = null
    var kullanıcı_id:String? = null
    var timestamp:String? = null
    var profil_resmi:String? = null
    var adı:String? = null


    constructor(){}

    constructor(
        mesaj: String?,
        kullanıcı_id: String?,
        timestamp: String?,
        profil_resmi: String?,
        adı: String?
    ) {
        this.mesaj = mesaj
        this.kullanıcı_id = kullanıcı_id
        this.timestamp = timestamp
        this.profil_resmi = profil_resmi
        this.adı = adı
    }
}