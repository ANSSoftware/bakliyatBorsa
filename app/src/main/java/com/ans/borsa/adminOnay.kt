package com.ans.borsa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_admin_onay.*

class adminOnay : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    var addbakiyeFromFB : ArrayList<Double> = ArrayList()
    var addbakiyeOnayFromFB : ArrayList<Number> = ArrayList()
    var UserEmailFromFB : ArrayList<String> = ArrayList()
    var adapter : adminBakiyeOnayRA? = null

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
        db.collection("Paralar").orderBy("addBakiyeOnay", Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(applicationContext,exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
            }else{
                if(snapshot != null){
                    if(!snapshot.isEmpty){

                        addbakiyeFromFB.clear()
                        addbakiyeOnayFromFB.clear()
                        UserEmailFromFB.clear()
                        val documents = snapshot.documents

                        for(document in documents){
                            val addBakiye = document.get("addBakiye") as Double
                            val addBakiyeOnay = document.get("addBakiyeOnay") as Number
                            val userEmail = document.get("UserEmail") as String
                                UserEmailFromFB.add(userEmail)
                                addbakiyeOnayFromFB.add(addBakiyeOnay)
                                addbakiyeFromFB.add(addBakiye)

                            adapter!!.notifyDataSetChanged()

                        }


                    }
                }
            }

        }
    }


}