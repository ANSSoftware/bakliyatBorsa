package com.ans.borsa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_admin_onay_urun.*

class AdminOnayUrun : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    var addurunFromFB: ArrayList<String> = ArrayList()
    var addurunOnayFromFB: ArrayList<String> = ArrayList()
    var addurunSayiFromFB: ArrayList<String> = ArrayList()
    var UserEmailFromUFB: ArrayList<String> = ArrayList()
    var addurunTutarFromFB: ArrayList<String> = ArrayList()
    var adapterurun: adminUrunOnayRA? = null
    var z = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_onay_urun)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        getDataFromFSUrun()
        var layoutManagerUrun = LinearLayoutManager(this)
        urunlerRV.layoutManager = layoutManagerUrun
        adapterurun = adminUrunOnayRA(addurunFromFB, addurunOnayFromFB, addurunSayiFromFB, UserEmailFromUFB, addurunTutarFromFB)
        urunlerRV.adapter = adapterurun // Receyceler view a arraydaki verilerimiz aktarılıyor.
    }

    fun redButonClickUrun(view: View) {
        butonClickControlUrun(false)
    }

    fun kabulButonClickUrun(view: View) {
        butonClickControlUrun(true)
    }

    fun getDataFromFSUrun() { //eklenen ürünleri admin onayına sunuyoruz
        db.collection("KullaniciUrunlerEklenen").orderBy("addUrunID", Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(applicationContext, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
            } else {
                if (snapshot != null) {
                    if (!snapshot.isEmpty) {
                        addurunFromFB.clear()
                        addurunOnayFromFB.clear()
                        UserEmailFromUFB.clear()
                        addurunSayiFromFB.clear()
                        addurunTutarFromFB.clear()
                        var documents = snapshot.documents
                        for (document in documents) {
                            val addUrun = document.get("addUrun") as String
                            val addUrunOnay = document.get("addUrunOnay") as String
                            val userEmail = document.get("UserEmail") as String
                            val addUrunSayi = document.get("addUrunMiktari") as Number
                            val addUrunSayiText = addUrunSayi.toString() + "KG"
                            val addUrunTutar = document.get("addUrunTutari") as Number
                            val addUrunTutarText = addUrunTutar.toString() + "TL"
                            if (addUrunOnay == "HENÜZ İŞLEM YAPILMADI") {
                                UserEmailFromUFB.add(userEmail)
                                addurunOnayFromFB.add(addUrunOnay)
                                addurunFromFB.add(addUrun)
                                addurunSayiFromFB.add(addUrunSayiText)
                                addurunTutarFromFB.add(addUrunTutarText)
                            }
                            adapterurun!!.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    fun butonClickControlUrun(deger: Boolean) { // basılan buton kontrol ediliyor
        z = 0
        db.collection("KullaniciUrunlerEklenen").orderBy("addUrunID", Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(
                        applicationContext,
                        exception.localizedMessage.toString(),
                        Toast.LENGTH_LONG
                ).show()
            } else {
                if (snapshot != null) {
                    if (!snapshot.isEmpty) {
                        val documents = snapshot.documents
                        for (document in documents) {
                            val addUrunOnay = document.get("addUrunOnay") as String
                            val addUrunMiktari = document.get("addUrunMiktari") as Number
                            val addUrun = document.get("addUrun") as String
                            val addUrunTutari = document.get("addUrunTutari") as Number
                            if (addUrunOnay == "HENÜZ İŞLEM YAPILMADI" && z == 0) {
                                z++
                                if (deger == false) { // red butonuna basılırsa ürün ekleme reddediliyor
                                    db.collection("KullaniciUrunlerEklenen").document(document.id).update("addUrunOnay", "URUN EKLEME REDDEDİLDİ")
                                } else if (deger == true) { // kabul butonuna basılırsa ürün ekleniyor
                                    db.collection("KullaniciUrunlerEklenen").document(document.id).update("addUrunOnay", "URUN AKTARILDI")
                                    val userEmail = document.get("UserEmail") as String
                                    urunEkle(userEmail, addUrun, addUrunMiktari, addUrunTutari)
                                }
                                break
                            }
                        }
                    }
                }
            }
        }
    }


    fun urunEkle(Email: String, Urun: String, UrunMiktari: Number, UrunTutari: Number) {
        //kabul butonuna basılırsa urunleri veritabanına ekliyoruz
        val postMapurunEkle = hashMapOf<String, Any>()
        postMapurunEkle.put("UserEmail", Email)
        postMapurunEkle.put("UrunMiktari", UrunMiktari)
        postMapurunEkle.put("UrunTutari", UrunTutari)
        db.collection("KullaniciUrunleri").document("KullaniciUrunleriDoc").collection(Urun).add(postMapurunEkle).addOnCompleteListener { task ->
            if (task.isComplete && task.isSuccessful) {
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
        }

    }
        //options ayarlarını yapıyoruz (admine göre) çünkü admin ve kullanici options u farklı
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.options_menu_admin, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.logout) {
            auth.signOut()
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else if (item.itemId == R.id.bakiye_onay) {
            val intent = Intent(applicationContext, adminOnayBakiye::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}