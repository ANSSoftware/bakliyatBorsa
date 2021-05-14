package com.ans.borsa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_kayit_ol.*

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
        val postMapKullanici = hashMapOf<String, Any>()
        postMapKullanici.put("Adi", name)
        postMapKullanici.put("Soyadi", surname)
        postMapKullanici.put("KullaniciAdi", kullaniciadi)
        postMapKullanici.put("Sifre", sifre)
        postMapKullanici.put("TcKimlikNo", tckimlik)
        postMapKullanici.put("TelefonNo", telefonno)
        postMapKullanici.put("Email", email)
        postMapKullanici.put("Adres", adres)
        postMapKullanici.put("UserID", userid)
        db.collection("Kullanicilar").add(postMapKullanici).addOnCompleteListener { task ->

            if (task.isComplete && task.isSuccessful) {
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
        }
        val postMapBakiye = hashMapOf<String, Any>()
        postMapBakiye.put("bakiye", 0)
        postMapBakiye.put("Email", email)
        db.collection("Bakiyeler").add(postMapBakiye).addOnCompleteListener { task ->

            if (task.isComplete && task.isSuccessful) {
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
        }
        val postMapKullaniciTipi = hashMapOf<String, Any>()
        postMapKullaniciTipi.put("Admin", false)
        postMapKullaniciTipi.put("Email", email)
        db.collection("KullaniciTipi").add(postMapKullaniciTipi).addOnCompleteListener { task ->

            if (task.isComplete && task.isSuccessful) {
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
        }

    }

}






