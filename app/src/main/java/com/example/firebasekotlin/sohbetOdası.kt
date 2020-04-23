package com.example.firebasekotlin

import com.example.firebasekotlin.SohbetMesaj

class sohbetOdası {

    var sohbetodası_adi:String? = null
    var olusturan_id:String? = null
    var seviye:String? = null
    var sohbetodasi_id:String? = null
    var sohbet_odasi_mesajları:ArrayList<SohbetMesaj>? = null

    constructor(){}
    constructor(
        sohbetodası_adi: String?,
        olusturan_id: String?,
        seviye: String?,
        sohbetodasi_id: String?,
        sohbet_odasi_mesajları: ArrayList<SohbetMesaj>?
    ) {
        this.sohbetodası_adi = sohbetodası_adi
        this.olusturan_id = olusturan_id
        this.seviye = seviye
        this.sohbetodasi_id = sohbetodasi_id
        this.sohbet_odasi_mesajları = sohbet_odasi_mesajları
    }


}