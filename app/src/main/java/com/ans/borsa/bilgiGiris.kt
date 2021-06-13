package com.ans.borsa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_bilgi_giris.*
import java.io.*
import java.util.*
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.beust.klaxon.Klaxon
import com.squareup.okhttp.Request
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONException
import java.net.URL
import java.nio.charset.Charset

class bilgiGiris : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    lateinit var urun_ekle_sp: Spinner
    lateinit var paraBirimiSpinner: Spinner

    var urunler = arrayListOf("LÜTFEN BİR ÜRÜN SEÇİNİZ")
    var paralar = arrayListOf("TRY","USD","EUR","GBP")
    var secilenUrun = ""
    var secilenPara = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bilgi_giris)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        urunListCekFB()
        urun_ekle_spfun()
        para_spfun()

    }

    fun urun_ekle_spfun() {
        urun_ekle_sp = findViewById(R.id.urun_ekle_sp) as Spinner
        urun_ekle_sp.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, urunler)
        urun_ekle_sp.onItemSelectedListener = object : AdapterView.OnItemClickListener,
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
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    fun para_spfun() {
        paraBirimiSpinner = findViewById(R.id.paraBirimiSpinner) as Spinner
        paraBirimiSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, paralar)
        paraBirimiSpinner.onItemSelectedListener = object : AdapterView.OnItemClickListener,
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
                secilenPara = paralar[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }


    fun urunListCekFB() {
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

    fun urunEkleOnClick(view: View) {
        val urunTutariText = urun_tutari_text.text.toString()
        val urunMiktariText = urun_adeti_ptext.text.toString()
        if (urunMiktariText != "" && urunTutariText != "") {
            if (secilenUrun != "LÜTFEN BİR ÜRÜN SEÇİNİZ") {
                val urunMiktari = urunMiktariText.toDouble()
                val urunTutari = urunTutariText.toDouble()
                if (urunMiktari != 0.0 && urunTutari != 0.0) {
                    val postMap = hashMapOf<String, Any>()
                    val uuid = UUID.randomUUID()
                    val userEmail = auth.currentUser!!.email.toString()
                    postMap.put("UserEmail", userEmail)
                    postMap.put("addUrunMiktari", urunMiktari)
                    postMap.put("addUrun", secilenUrun)
                    postMap.put("addUrunOnay", "HENÜZ İŞLEM YAPILMADI")
                    postMap.put("addUrunID", "$uuid")
                    postMap.put("addUrunTutari", urunTutari)
                    db.collection("KullaniciUrunlerEklenen").add(postMap).addOnCompleteListener { task ->
                    }.addOnFailureListener { exception ->
                        Toast.makeText(applicationContext, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
                    }.addOnCompleteListener {
                        Toast.makeText(applicationContext, "ÜRÜNÜNÜZ ADMİN ONAYINA GÖNDERİLMİŞTİR", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "LÜTFEN GEÇERLİ BİR DEĞER GİRİNİZ", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext, "LÜTFEN BİR ÜRÜN SEÇİNİZ", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(applicationContext, "LÜTFEN GEÇERLİ BİR DEĞER GİRİNİZ", Toast.LENGTH_SHORT).show()
        }
    }

    fun bakiyeYukleOnClick(view: View) {
        val addBakiyeText = bakiye_yukle_ptext.text.toString()
        if (addBakiyeText != "") {
            val addBakiye = addBakiyeText.toDouble()
            val uuid = UUID.randomUUID()
            if (addBakiye != 0.0) {
                val postMap = hashMapOf<String, Any>()
                val userEmail = auth.currentUser!!.email.toString()
                postMap.put("UserEmail", userEmail)
                postMap.put("addBakiye", addBakiye)
                postMap.put("addBakiyeOnay", "HENÜZ İŞLEM YAPILMADI")
                postMap.put("addBakiyeID", "$uuid")
                postMap.put("addParaBirimi", secilenPara)
                db.collection("KullaniciBakiyeEklenen").add(postMap).addOnCompleteListener { task ->
                }.addOnFailureListener { exception ->
                    Toast.makeText(applicationContext, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
                }.addOnCompleteListener {
                    Toast.makeText(applicationContext, "BAKİYENİZ ADMİN ONAYINA GÖNDERİLMİŞTİR", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext, "LÜTFEN GEÇERLİ BİR DEĞER GİRİNİZ", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(applicationContext, "LÜTFEN GEÇERLİ BİR DEĞER GİRİNİZ", Toast.LENGTH_SHORT).show()
        }
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
        } else if (item.itemId == R.id.anasayfa) {
            val intent = Intent(applicationContext, AnaMenu::class.java)
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






}




