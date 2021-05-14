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
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    var addurunFromFB : ArrayList<String> = ArrayList()
    var addurunOnayFromFB : ArrayList<String> = ArrayList()
    var addurunSayiFromFB : ArrayList<String> = ArrayList()
    var UserEmailFromUFB : ArrayList<String> = ArrayList()
    var adapterurun : adminUrunOnayRA? = null
    var y=0
    var z=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_onay_urun)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        getDataFromFSUrun()
        var layoutManagerUrun = LinearLayoutManager(this)
        urunlerRV.layoutManager = layoutManagerUrun
        adapterurun = adminUrunOnayRA(addurunFromFB,addurunOnayFromFB,addurunSayiFromFB,UserEmailFromUFB)
        urunlerRV.adapter = adapterurun
    }

    fun redButonClickUrun(view: View) {
        butonClickControlUrun(false)
    }

    fun kabulButonClickUrun(view: View) {
        butonClickControlUrun(true)
    }

    fun getDataFromFSUrun(){
        db.collection("KullaniciEklenenUrunler").orderBy("addUrunID", Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(applicationContext,exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
            }else{
                if(snapshot != null){
                    if(!snapshot.isEmpty){
                        addurunFromFB.clear()
                        addurunOnayFromFB.clear()
                        UserEmailFromUFB.clear()
                        addurunSayiFromFB.clear()
                        var documents = snapshot.documents
                        for(document in documents){
                            val addUrun = document.get("addUrun") as String
                            val addUrunOnay = document.get("addUrunOnay")  as String
                            val userEmail = document.get("UserEmail") as String
                            val addUrunSayi = document.get("addUrunMiktari") as Number
                            val addUrunSayiText = addUrunSayi.toString() + "KG"
                            if(addUrunOnay=="HENÜZ İŞLEM YAPILMADI"){
                                UserEmailFromUFB.add(userEmail)
                                addurunOnayFromFB.add(addUrunOnay)
                                addurunFromFB.add(addUrun)
                                addurunSayiFromFB.add(addUrunSayiText)
                            }
                            adapterurun!!.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    fun butonClickControlUrun(deger: Boolean ){
        z=0
        db.collection("KullaniciEklenenUrunler").orderBy("addUrunID", Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
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
                            if(addUrunOnay=="HENÜZ İŞLEM YAPILMADI" && z==0){
                                z++
                                if(deger==false){
                                    db.collection("KullaniciEklenenUrunler").document(document.id).update("addUrunOnay","URUN EKLEME REDDEDİLDİ")
                                }else if(deger==true){
                                    db.collection("KullaniciEklenenUrunler").document(document.id).update("addUrunOnay","URUN AKTARILDI")
                                    val userEmail = document.get("UserEmail") as String
                                    urunEkle(userEmail,addUrun,addUrunMiktari)
                                }
                                break
                            }
                        }
                    }
                }
            }
        }
    }


    fun urunEkle(Email: String,Urun: String,UrunMiktari: Number){

        val postMapurunEkle = hashMapOf<String, Any>()
        postMapurunEkle.put("UserEmail", Email)
        postMapurunEkle.put("UrunMiktari", UrunMiktari)

        db.collection("KullaniciUrunleri").document("KullaniciUrunleriDoc").collection(Urun).add(postMapurunEkle).addOnCompleteListener { task ->

            if (task.isComplete && task.isSuccessful) {
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
        }

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.options_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.logout) {
            auth.signOut()
            val intent = Intent(applicationContext,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}