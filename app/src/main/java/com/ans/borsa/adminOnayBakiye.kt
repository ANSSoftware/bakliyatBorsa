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
import kotlinx.android.synthetic.main.activity_admin_onay_bakiye.*
class adminOnayBakiye : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    var addbakiyeFromFB : ArrayList<String> = ArrayList()
    var addbakiyeOnayFromFB : ArrayList<String> = ArrayList()
    var UserEmailFromBFB : ArrayList<String> = ArrayList()
    var adapterbakiye : adminBakiyeOnayRA? = null
    var i=0
    var x=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_onay_bakiye)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // recycle ayarlari
                getDataFromFSBakiye()
                var layoutManagerBakiye = LinearLayoutManager(this)
                bakiyeRV.layoutManager = layoutManagerBakiye
                adapterbakiye = adminBakiyeOnayRA(addbakiyeFromFB,addbakiyeOnayFromFB,UserEmailFromBFB)
                bakiyeRV.adapter = adapterbakiye
    }
    fun getDataFromFSBakiye(){
        db.collection("KullaniciEklenenBakiye").orderBy("addBakiyeID", Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(applicationContext,exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
            }else{
                if(snapshot != null){
                    if(!snapshot.isEmpty){
                        addbakiyeFromFB.clear()
                        addbakiyeOnayFromFB.clear()
                        UserEmailFromBFB.clear()
                        var documents = snapshot.documents
                        for(document in documents){
                            val addBakiye = document.get("addBakiye") as Number
                            val addBakiyeOnay = document.get("addBakiyeOnay")  as String
                            val userEmail = document.get("UserEmail") as String
                            val addBakiyeText = addBakiye.toString() + "TL"
                            if(addBakiyeOnay=="HENÜZ İŞLEM YAPILMADI"){
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

    fun kabulButonClickBakiye(view: View) {
            butonClickControlBakiye(true)
    }
    fun butonClickControlBakiye(deger: Boolean ){
        i=0
        db.collection("KullaniciEklenenBakiye").orderBy("addBakiyeID", Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
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
                            if(addBakiyeOnay=="HENÜZ İŞLEM YAPILMADI" && i==0){
                                i++
                                if(deger==false){
                                    db.collection("KullaniciEklenenBakiye").document(document.id).update("addBakiyeOnay","PARA AKTARIMI REDDEDİLDİ")
                                }else if(deger==true){
                                    db.collection("KullaniciEklenenBakiye").document(document.id).update("addBakiyeOnay","PARA BAKİYEYE AKTARILDI")
                                    val userEmail = document.get("UserEmail") as String
                                    bakiyeGuncelle(userEmail,addBakiye)
                                }
                                break
                            }
                        }
                    }
                }
            }
        }
    }
    fun bakiyeGuncelle(userEmail: String,addBakiye: Number){
        x=0
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
                            val kontrolEmail = document.get("UserEmail") as String
                            var bakiyeYazdir : Number
                            var geciciBakiye=bakiye.toDouble()
                            var geciciAddBakiye=addBakiye.toDouble()
                            if(userEmail==kontrolEmail && x==0){
                                x++
                                bakiyeYazdir=geciciBakiye+geciciAddBakiye
                                    db.collection("Bakiyeler").document(document.id).update("bakiye",bakiyeYazdir)
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

