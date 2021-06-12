package com.ans.borsa

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_ana_menu.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class AnaMenu : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    //degiskenler tanımlandı
    lateinit var urun_satin_al_sp: Spinner
    var urunler = arrayListOf("LÜTFEN BİR ÜRÜN SEÇİNİZ")
    var secilenUrun = ""
    var x = 0
    var y = 0
    var z = 0
    var t = 0
    var geciciUrunMiktari = 0.0
    var geciciTutar = 0.0
    var gecicibakiye = 0.0
    var almakIstenenUrunTut = 0.0
    var bakiyeKontrolEt = 0
    var simdikibakiye = 0.0
    // arrayler tanımlandı
    var urunMiktariFromFB: ArrayList<String> = ArrayList()
    var urunTutariFromFB: ArrayList<String> = ArrayList()
    var UserEmailFromFB: ArrayList<String> = ArrayList()
    var adaptersatinal: urunSatinAlRA? = null
    var a = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ana_menu)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        if (a == 0) {
            a++
            bakiyeBilgisiGetir()
        }




        urunListCekFB()
        urun_satin_al_spfun()


        var layoutManagerSatinAl = LinearLayoutManager(this)
        urunAlRV.layoutManager = layoutManagerSatinAl
        adaptersatinal = urunSatinAlRA(UserEmailFromFB, urunMiktariFromFB, urunTutariFromFB)
        urunAlRV.adapter = adaptersatinal

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.options_menu_kullanici, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.logout) {
            auth.signOut()
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else if (item.itemId == R.id.bilgigiris) {
            val intent = Intent(applicationContext, bilgiGiris::class.java)
            startActivity(intent)
        }else if (item.itemId == R.id.emirler) {
            val intent = Intent(applicationContext, emirler::class.java)
            startActivity(intent)
        }else if (item.itemId == R.id.raporla){
            val intent = Intent(applicationContext, raporla::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    fun urun_satin_al_spfun() { //Spinner bilgileri oluşturuldu
        urun_satin_al_sp = findViewById(R.id.urun_sec) as Spinner
        urun_satin_al_sp.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, urunler)
        urun_satin_al_sp.onItemSelectedListener = object : AdapterView.OnItemClickListener,
                AdapterView.OnItemSelectedListener {
            override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
            ) {
            }

            override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
            ) {
                secilenUrun = urunler[position]
                getDataFromFSSatinAl()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    fun urunListCekFB() { // urunler Spinnere çekiliyor
        db.collection("Urunler").orderBy("Urun", Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(applicationContext, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
            } else {
                if (snapshot != null) {
                    if (!snapshot.isEmpty) {
                        val documents = snapshot.documents
                        for (document in documents) {
                            val urun = document.get("Urun") as String
                            urunler.add(urun)
                        }
                    }
                }
            }
        }
    }

    fun getDataFromFSSatinAl() { //satın al bilgileri rv ye çekiliyor
        z = 0
        db.collection("KullaniciUrunleri").document("KullaniciUrunleriDoc").collection(secilenUrun).orderBy("UrunTutari", Query.Direction.ASCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(applicationContext, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
            } else {
                if (snapshot != null) {
                    if (!snapshot.isEmpty) {
                        urunMiktariFromFB.clear()
                        urunTutariFromFB.clear()
                        UserEmailFromFB.clear()
                        var documents = snapshot.documents
                        for (document in documents) {
                            val urunMiktari = document.get("UrunMiktari") as Number
                            val urunTutari = document.get("UrunTutari") as Number
                            val userEmail = document.get("UserEmail") as String
                            val urunMiktariText = urunMiktari.toString() + "KG"
                            val urunTutariText = urunTutari.toString() + "TL"
                            val urunMiktariD = urunMiktari.toDouble()

                            if (urunMiktariD != 0.0 && z == 0) {
                                UserEmailFromFB.add(userEmail)
                                urunMiktariFromFB.add(urunMiktariText)
                                urunTutariFromFB.add(urunTutariText)
                                adaptersatinal!!.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun satinAlOnClick(view: View) { // satın al butonuna basılırsa eğer bakiyede para varsa satın alma işlemi yapılıyor
        y = 0
        t = 0
        var saticininKalanUrunu: Number
        var almakIstenenKG = urun_tutari_text.text.toString().toDouble()

        db.collection("KullaniciUrunleri").document("KullaniciUrunleriDoc")
                .collection(secilenUrun).orderBy("UrunTutari", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        Toast.makeText(applicationContext, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
                    } else {
                        if (snapshot != null) {
                            if (!snapshot.isEmpty) {
                                var documents = snapshot.documents

                                for (document in documents) {
                                    var urunMiktari = document.get("UrunMiktari") as Number
                                    var urunTutari = document.get("UrunTutari") as Number
                                    var urunMiktariD = urunMiktari.toDouble()
                                    var urunTutariD = urunTutari.toDouble()

                                    if (almakIstenenKG <= urunMiktariD && y == 0 && t == 0) {
                                        y++
                                        bakiyeKontrolEt = 0
                                        saticininKalanUrunu = urunMiktariD - almakIstenenKG
                                        db.collection("KullaniciUrunleri").document("KullaniciUrunleriDoc")
                                                .collection(secilenUrun).document(document.id)
                                                .update("UrunMiktari", saticininKalanUrunu)

                                        bakiyeGuncelle(almakIstenenKG, urunTutariD, bakiyeKontrolEt)
                                        satinAlKaydet(almakIstenenKG, urunTutariD, secilenUrun)

                                        break
                                    } else if (almakIstenenKG > urunMiktariD && t == 0) {
                                        t++
                                        geciciUrunMiktari = urunMiktariD
                                        geciciTutar = urunTutariD
                                        db.collection("KullaniciUrunleri").document("KullaniciUrunleriDoc")
                                                .collection(secilenUrun).document(document.id)
                                                .update("UrunMiktari", 0)
                                    } else if (t == 1) {
                                        t++
                                        bakiyeKontrolEt = 1
                                        almakIstenenUrunTut = almakIstenenKG - geciciUrunMiktari
                                        gecicibakiye = geciciUrunMiktari * geciciTutar
                                        simdikibakiye = urunTutariD * almakIstenenUrunTut
                                        saticininKalanUrunu = urunMiktariD - almakIstenenUrunTut
                                        db.collection("KullaniciUrunleri").document("KullaniciUrunleriDoc")
                                                .collection(secilenUrun).document(document.id)
                                                .update("UrunMiktari", saticininKalanUrunu)
                                        bakiyeGuncelle(gecicibakiye, simdikibakiye, bakiyeKontrolEt)
                                        bakiyeKontrolEt = 0
                                        break
                                    }
                                }
                            }
                        }
                    }
                }

    }

      fun bakiyeGuncelle(almakIstenenKG: Double, urunTutariD: Double, bakiyeKontrol: Int) { // satın alma işleminden sonra bakiye güncelleniyor
        x = 0
        db.collection("Bakiyeler").addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(applicationContext, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
            } else {
                if (snapshot != null) {
                    if (!snapshot.isEmpty) {
                        var documents = snapshot.documents
                        for (document in documents) {
                            var userBakiye = document.get("bakiye") as Number
                            var userEmail = document.get("Email") as String
                            var userBakiyeD = userBakiye.toDouble()
                            var bakiyeBilgisiSondurum = 0.0
                            if (userEmail == auth.currentUser!!.email.toString() && x == 0 && bakiyeKontrol == 0) {
                                x++
                                bakiyeBilgisiSondurum = userBakiyeD - (almakIstenenKG * urunTutariD)
                                db.collection("Bakiyeler").document(document.id)
                                        .update("bakiye", bakiyeBilgisiSondurum)
                                bakiyeText.text = "BAKİYENİZ: " + bakiyeBilgisiSondurum + "TL"
                                break
                            } else if (userEmail == auth.currentUser!!.email.toString() && x == 0 && bakiyeKontrol == 1) {
                                x++
                                bakiyeBilgisiSondurum = userBakiyeD - (almakIstenenKG + urunTutariD)
                                db.collection("Bakiyeler").document(document.id)
                                        .update("bakiye", bakiyeBilgisiSondurum)
                                bakiyeText.text = "BAKİYENİZ: " + bakiyeBilgisiSondurum + "TL"
                                break
                            }
                        }
                    }
                }
            }
        }
    }


    fun bakiyeBilgisiGetir() { // bakiye bilgisi getiriliyor
        db.collection("Bakiyeler").addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(applicationContext, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
            } else {
                if (snapshot != null) {
                    if (!snapshot.isEmpty) {
                        var documents = snapshot.documents
                        for (document in documents) {
                            val userBakiye = document.get("bakiye") as Number
                            val userEmail = document.get("Email") as String
                            var bakiyeBilgisi = 0.0
                            if (userEmail == auth.currentUser!!.email.toString()) {
                                bakiyeBilgisi = userBakiye.toDouble()
                                bakiyeText.text = "BAKİYENİZ: " + bakiyeBilgisi + "TL"
                            }
                        }
                    }
                }
            }
        }
    }
    fun emirVerOnClick(view: View){ // Emir verme işlemini yapan kod
        var almakIstenenKG = urun_tutari_text.text.toString().toDouble()
        var emirTutar = emir_tutari_text.text.toString().toDouble()
        bakiyeGuncelle(almakIstenenKG, emirTutar, 0)
        val postMapEmir = hashMapOf<String, Any>()
        postMapEmir.put("UserEmail", auth.currentUser!!.email.toString())
        postMapEmir.put("UrunKG", almakIstenenKG)
        postMapEmir.put("EmirOnay", false)
        postMapEmir.put("Urun", secilenUrun)
        postMapEmir.put("EmirTutar", emirTutar) //emir bilgileri veri tabanına aktarılıyor
        db.collection("Emirler").add(postMapEmir).addOnCompleteListener { task ->

            if (task.isComplete && task.isSuccessful) {
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
        }
    }

    var suan = Timestamp.now().seconds
    @RequiresApi(Build.VERSION_CODES.O)
    fun satinAlKaydet(urunKGKayit: Number, urunTutarKayit: Number, urunKayit: String){ // Emir verme işlemini yapan kod
        var Tarih= LocalDate.parse(getDate(suan * 1000, "yyyy-MM-dd"), DateTimeFormatter.ISO_DATE)
        val postMapKayitUrun = hashMapOf<String, Any>()
        postMapKayitUrun.put("urunKGKayit", urunKGKayit)
        postMapKayitUrun.put("urunTutarKayit", urunTutarKayit)
        postMapKayitUrun.put("urunKayit", urunKayit)
        postMapKayitUrun.put("tarih", convertToDateViaInstant(Tarih)) //emir bilgileri veri tabanına aktarılıyor
        db.collection("UrunKayitlari").document(auth.currentUser!!.email.toString()).collection("SatinAlinanUrunler").add(postMapKayitUrun).addOnCompleteListener { task ->

            if (task.isComplete && task.isSuccessful) {
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
        }
    }

    fun getDate(second: Long, dateFormat: String): String
    {

        var formatter = SimpleDateFormat(dateFormat)
        var calendar = Calendar.getInstance()
        calendar.setTimeInMillis(second)
        return formatter.format(calendar.getTime())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertToDateViaInstant(dateToConvert: LocalDate): Date {
        return Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant())
    }


    }





