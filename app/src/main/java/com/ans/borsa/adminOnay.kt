package com.ans.borsa
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_admin_onay.*
import kotlinx.android.synthetic.main.activity_bilgi_giris.*
import kotlinx.android.synthetic.main.admin_bakiye_onay_ra.*
class adminOnay : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    var addbakiyeFromFB : ArrayList<String> = ArrayList()
    var addbakiyeOnayFromFB : ArrayList<String> = ArrayList()
    var UserEmailFromFB : ArrayList<String> = ArrayList()
    var adapter : adminBakiyeOnayRA? = null
    var i=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_onay)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        getDataFromFireStore()
        // recycle ayarlari
        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = adminBakiyeOnayRA(addbakiyeFromFB,addbakiyeOnayFromFB,UserEmailFromFB)
        recyclerView.adapter = adapter
    }
    fun getDataFromFireStore(){
        db.collection("Paralar").orderBy("addBakiyeID", Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(applicationContext,exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
            }else{
                if(snapshot != null){
                    if(!snapshot.isEmpty){
                        addbakiyeFromFB.clear()
                        addbakiyeOnayFromFB.clear()
                        UserEmailFromFB.clear()
                        var documents = snapshot.documents
                        for(document in documents){
                            val addBakiye = document.get("addBakiye") as Number
                            val addBakiyeOnay = document.get("addBakiyeOnay")  as String
                            val userEmail = document.get("UserEmail") as String
                            val addBakiyeText = addBakiye.toString() + "TL"
                            if(addBakiyeOnay=="HENÜZ İŞLEM YAPILMADI"){
                                UserEmailFromFB.add(userEmail)
                                addbakiyeOnayFromFB.add(addBakiyeOnay)
                                addbakiyeFromFB.add(addBakiyeText)
                            }
                            adapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }
    fun redButonClick(view: View) {
        butonClickControl(false)
    }
    fun kabulButonClick(view: View) {
        butonClickControl(true)
    }

    fun butonClickControl(deger: Boolean ){
        i=0
        db.collection("Paralar").orderBy("addBakiyeID", Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
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
                            if(addBakiyeOnay=="HENÜZ İŞLEM YAPILMADI" && i==0){
                                i++
                                if(deger==false){
                                    db.collection("Paralar").document(document.id).update("addBakiyeOnay","PARA AKTARIMI REDDEDİLDİ")
                                }else{
                                    db.collection("Paralar").document(document.id).update("addBakiyeOnay","PARA BAKİYEYE AKTARILDI")
                                }
                                break
                            }
                        }

                    }
                }

            }

        }
    }


}

