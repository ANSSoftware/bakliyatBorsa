package com.ans.borsa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_bilgi_giris.*
import java.util.*

class bilgiGiris : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore


    lateinit var urun_ekle_sp : Spinner
    var urunler = arrayListOf("LÜTFEN BİR ÜRÜN SEÇİNİZ")
    var secilenUrun=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bilgi_giris)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        urunListCekFB()
        urun_ekle_spfun()

    }

    fun urun_ekle_spfun(){
        urun_ekle_sp = findViewById(R.id.urun_ekle_sp) as Spinner
        urun_ekle_sp.adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,urunler)
        urun_ekle_sp.onItemSelectedListener = object  : AdapterView.OnItemClickListener,
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
            ) { secilenUrun=urunler[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

    }

    fun urunListCekFB(){
        db.collection("Urunler").orderBy("Urun", Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(applicationContext,exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
            }else{
                if(snapshot != null){
                    if(!snapshot.isEmpty){
                        val documents = snapshot.documents
                        for(document in documents){
                            val urun = document.get("Urun") as String
                            urunler.add(urun)
                        }
                    }
                }
            }

        }

    }
    fun urunEkleOnClick(view: View){
        val urunMiktariText=urun_adeti_ptext.text.toString()
        if (urunMiktariText!="") {
            if (secilenUrun != "LÜTFEN BİR ÜRÜN SEÇİNİZ") {
                val urunMiktari = urunMiktariText.toDouble()

                if (urunMiktari != 0.0) {
                    val postMap = hashMapOf<String, Any>()
                    val userEmail = auth.currentUser!!.email.toString()
                    postMap.put("UserEmail", userEmail)
                    postMap.put("UrunMiktari", urunMiktari)
                    postMap.put("Urun", secilenUrun)
                    postMap.put("UrunOnayi", 0)

                    db.collection("KullaniciUrunleri").add(postMap).addOnCompleteListener { task ->


                    }.addOnFailureListener { exception ->
                        Toast.makeText(applicationContext, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "LÜTFEN GEÇERLİ BİR DEĞER GİRİNİZ", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext, "LÜTFEN BİR ÜRÜN SEÇİNİZ", Toast.LENGTH_SHORT).show()
            }
        }else {
            Toast.makeText(applicationContext, "LÜTFEN GEÇERLİ BİR DEĞER GİRİNİZ", Toast.LENGTH_SHORT).show()
        }
    }

    fun bakiyeYukleOnClick(view: View){
            val addBakiyeText=bakiye_yukle_ptext.text.toString()
            if (addBakiyeText!="") {
                val addBakiye = addBakiyeText.toDouble()
                val uuid= UUID.randomUUID()
                if (addBakiye != 0.0) {
                    val postMap = hashMapOf<String, Any>()
                    val userEmail = auth.currentUser!!.email.toString()
                    postMap.put("UserEmail", userEmail)
                    postMap.put("addBakiye", addBakiye)
                    postMap.put("addBakiyeOnay", "HENÜZ İŞLEM YAPILMADI")
                    postMap.put("addBakiyeID","$uuid")

                    db.collection("Paralar").add(postMap).addOnCompleteListener { task ->


                    }.addOnFailureListener { exception ->
                        Toast.makeText(applicationContext, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "LÜTFEN GEÇERLİ BİR DEĞER GİRİNİZ", Toast.LENGTH_SHORT).show()
                }
            }else {
                Toast.makeText(applicationContext, "LÜTFEN GEÇERLİ BİR DEĞER GİRİNİZ", Toast.LENGTH_SHORT).show()
            }
        }

    }




