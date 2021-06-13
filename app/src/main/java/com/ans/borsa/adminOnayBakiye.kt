package com.ans.borsa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_admin_onay_bakiye.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

class adminOnayBakiye : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    var addbakiyeFromFB: ArrayList<String> = ArrayList()
    var addbakiyeOnayFromFB: ArrayList<String> = ArrayList()
    var UserEmailFromBFB: ArrayList<String> = ArrayList()
    var adapterbakiye: adminBakiyeOnayRA? = null
    var paralar = arrayListOf("TRY","USD","EUR","GBP")
    var paraDegeri = arrayListOf<Double>()
    var i = 0
    var x = 0
    var paraTutari= 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_onay_bakiye)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        paraBirimi()
        // recycle ayarlari
        getDataFromFSBakiye()
        var layoutManagerBakiye = LinearLayoutManager(this)
        bakiyeRV.layoutManager = layoutManagerBakiye
        adapterbakiye = adminBakiyeOnayRA(addbakiyeFromFB, addbakiyeOnayFromFB, UserEmailFromBFB)
        bakiyeRV.adapter = adapterbakiye

    }

    fun getDataFromFSBakiye() { // eklenen bakiye bilgilerini veritabanından çekiyoruz
        db.collection("KullaniciBakiyeEklenen").orderBy("addBakiyeID", Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(applicationContext, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
            } else {
                if (snapshot != null) {
                    if (!snapshot.isEmpty) {
                        addbakiyeFromFB.clear()
                        addbakiyeOnayFromFB.clear()
                        UserEmailFromBFB.clear()
                        var documents = snapshot.documents
                        for (document in documents) {
                            val addBakiye = document.get("addBakiye") as Number
                            val addBakiyeOnay = document.get("addBakiyeOnay") as String
                            val userEmail = document.get("UserEmail") as String
                            val addParaBirimi = document.get("addParaBirimi") as String
                            val addBakiyeText = addBakiye.toString()+addParaBirimi
                            if (addBakiyeOnay == "HENÜZ İŞLEM YAPILMADI") {
                                UserEmailFromBFB.add(userEmail)
                                addbakiyeOnayFromFB.add(addBakiyeOnay)
                                addbakiyeFromFB.add(addBakiyeText)
                            }
                            adapterbakiye!!.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    fun redButonClickBakiye(view: View) {
        butonClickControlBakiye(false)
    }
        // buton kontrollerini yapıyoruz
    fun kabulButonClickBakiye(view: View) {
        butonClickControlBakiye(true)
    }

    fun butonClickControlBakiye(deger: Boolean) { // buton kontrollerine göre veritabanında bilgi değişikliği yapıyoruz
        i = 0
        db.collection("KullaniciBakiyeEklenen").orderBy("addBakiyeID", Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
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
                            val addBakiyeOnay = document.get("addBakiyeOnay") as String
                            val addBakiye = document.get("addBakiye") as Number
                            val addParaBirimi = document.get("addParaBirimi") as String
                            val userEmail = document.get("UserEmail") as String
                            if (addBakiyeOnay == "HENÜZ İŞLEM YAPILMADI" && i == 0) {
                                i++
                                if (deger == false) {
                                    db.collection("KullaniciBakiyeEklenen").document(document.id).update("addBakiyeOnay", "PARA AKTARIMI REDDEDİLDİ")
                                } else if (deger == true) {
                                    db.collection("KullaniciBakiyeEklenen").document(document.id).update("addBakiyeOnay", "PARA BAKİYEYE AKTARILDI")

                                    for (a in 0..3){
                                        if(paralar[a]==addParaBirimi){
                                           paraTutari=paraDegeri[a]
                                        }

                                    }
                                    bakiyeGuncelle(userEmail,addBakiye,paraTutari)
                                }
                                break
                            }
                        }
                    }
                }
            }
        }
    }

    fun bakiyeGuncelle(userEmail: String, addBakiye: Number,paraTutari: Double) { // eğer kabul butonuna basılırsa bakiye güncelleniyor
        x = 0
        db.collection("Bakiyeler").addSnapshotListener { snapshot, exception ->
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
                            val bakiye = document.get("bakiye") as Number
                            val kontrolEmail = document.get("Email") as String
                            var bakiyeYazdir: Number
                            var geciciBakiye = bakiye.toDouble()
                            var geciciaddBakiye = (addBakiye.toDouble())*paraTutari
                            if (userEmail == kontrolEmail && x == 0) {
                                x++
                                bakiyeYazdir = geciciBakiye + geciciaddBakiye
                                db.collection("Bakiyeler").document(document.id).update("bakiye", bakiyeYazdir)
                                break
                            }
                        }
                    }
                }
            }

        }
    }

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
        } else if (item.itemId == R.id.urun_onay) {
            val intent = Intent(applicationContext, AdminOnayUrun::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }


     fun paraBirimi() {
         var url = "https://api.genelpara.com/embed/doviz.json"

         doAsync{
             calistir(url)
            uiThread {}
         }
     }
    fun calistir(url:String){
        var json = URL(url).readText()
        Log.d(javaClass.simpleName,json)
        paraDegeri.add(1.0)
        paraDegeri.add((json.substring(17,23)).toDouble())
        paraDegeri.add((json.substring(76,83)).toDouble())
        paraDegeri.add((json.substring(137,144)).toDouble())
    }


}

