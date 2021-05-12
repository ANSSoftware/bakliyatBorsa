package com.ans.borsa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_kayit_ol.*
import android.app.ProgressDialog

class kayitOl : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private  lateinit var  db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kayit_ol)



        auth =  FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(applicationContext, AnaMenu::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun kayitOlClick(view : View) {

        var email = emailKayitText.text.toString()
        var sifre = sifreKayitText.text.toString()


        auth.createUserWithEmailAndPassword(email, sifre).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                val intent = Intent(applicationContext, AnaMenu::class.java)
                startActivity(intent)
                finish()
                //veritabanina bilgileri atiyoruz
                val userid = auth.uid
                val name = adText.text.toString()
                val surname = soyadText.text.toString()
                val kullaniciadi = kullaniciAdi.text.toString()
                val tckimlik = tcText.text.toString()
                val telefonno = telefonText.text.toString()
                val adres = adresText.text.toString()
                if (userid != null) {
                    kayitFireBaseAktarma(name,surname,kullaniciadi,sifre,
                            tckimlik,telefonno,email,adres,userid)
                }
            }
            }.addOnFailureListener { exception ->
                if (exception != null) {
                    Toast.makeText(
                            applicationContext,
                            exception.localizedMessage.toString(),
                            Toast.LENGTH_LONG
                    ).show()
                }
            }

     }

    fun kayitFireBaseAktarma(name: String,surname: String,kullaniciadi: String,sifre: String
                             ,tckimlik: String,telefonno: String,email: String,adres: String,userid: String){
        val postMap = hashMapOf<String, Any>()
        val bakiye=0
        postMap.put("Adi", name)
        postMap.put("Soyadi", surname)
        postMap.put("KullaniciAdi", kullaniciadi)
        postMap.put("Sifre", sifre)
        postMap.put("TcKimlikNo", tckimlik)
        postMap.put("TelefonNo", telefonno)
        postMap.put("Email", email)
        postMap.put("Adres", adres)
        postMap.put("UserID", userid)
        postMap.put("bakiye",bakiye)

        db.collection("Kullanicilar").add(postMap).addOnCompleteListener { task ->

            if (task.isComplete && task.isSuccessful) {
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
        }


    }
}






