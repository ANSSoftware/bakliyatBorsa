package com.ans.borsa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    //FireBase kullanici işlemleri ve database işlemleri için kütüphaneler tanımlandı
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        //Eger kullanıcı daha önce uygulamaya giriş yaptıysa tekrar giriş yapmasına gerek kalmıyor
        val currentUser = auth.currentUser
        if (currentUser != null) {
            adminmisin()
            finish()
        }
    }

    fun girisYapClick(view: View) { // giriş Yap butonuna tıklandığında

        var email = emailGirisText.text.toString()
        var sifre = sifreGirisText.text.toString()


        auth.signInWithEmailAndPassword(email, sifre).addOnCompleteListener { task ->
            //kullanıcının girdiği şifre ve e posta kontrol ediliyor

            if (task.isSuccessful) {
                Toast.makeText(
                        applicationContext,
                        "Hoşgeldin: ${auth.currentUser?.email}",
                        Toast.LENGTH_LONG
                ).show()
                adminmisin()
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(
                    applicationContext,
                    exception.localizedMessage.toString(),
                    Toast.LENGTH_LONG
            ).show()
        }
    }

    fun kayitolgirisclick(view: View) {
        // kayit ol butonuna tıklanınca kayit ol sayfasına yönlendiriliyor
        val intent = Intent(applicationContext, kayitOl::class.java)
        startActivity(intent)
    }

    fun adminmisin() {
        // admin misin kontrolu ile admin ve diğer kullanıcılar birbirinden ayriliyor
        db.collection("KullaniciTipi").addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(applicationContext, exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
            } else {
                if (snapshot != null) {
                    if (!snapshot.isEmpty) {
                        val documents = snapshot.documents
                        for (document in documents) {
                            var useremail = document.get("Email") as String // veritabanından e mail bilgisi getiriliyor
                            if (useremail == auth.currentUser!!.email.toString()) { // getirilen e mail kullanıcının e mailine eşit mi diye kontrol ediliyor
                                val admin = document.get("Admin") as Boolean
                                if (admin == false) { // admin olmayan kullanıcı anamenu activiy sinde başlıyor.
                                    val intent = Intent(applicationContext, AnaMenu::class.java)
                                    startActivity(intent)
                                } else if (admin == true) { // admin olan kullanıcı admin onay urun activitysinde başlıyor.
                                    val intent = Intent(applicationContext, AdminOnayUrun::class.java)
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}


