package com.example.firebasekotlin

class User {

    var isim:String? = null
    var telefon:String? = null
    var profil_resmi:String? = null
    var seviye:String? = null
    var kullanıcı_id:String? = null

    constructor(isim: String?, telefon: String?, profil_resmi: String?, seviye: String?, kullanıcı_id: String?) {
        this.isim = isim
        this.telefon = telefon
        this.profil_resmi = profil_resmi
        this.seviye = seviye
        this.kullanıcı_id = kullanıcı_id
    }
    constructor(){

    }
}