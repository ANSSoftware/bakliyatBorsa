package com.ans.borsa

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_raporla.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class raporla : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    var x=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_raporla)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        var suan =Timestamp.now().seconds*1000
        if(x==0){
            x++
            baslangicCalendarView.setDate(suan)
            baslangicTarihText.text=getDate(suan, "yyyy-MM-dd")
            bitisTarihText.text=getDate(suan, "yyyy-MM-dd")
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
        }else if (item.itemId == R.id.bilgigiris){
            val intent = Intent(applicationContext, bilgiGiris::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    fun getDate(second: Long, dateFormat: String): String
    {

        var formatter = SimpleDateFormat(dateFormat)
        var calendar = Calendar.getInstance()
        calendar.setTimeInMillis(second)
        return formatter.format(calendar.getTime())
    }

    fun baslangicTarihiOnClick(view: View) {
        baslangicCalendarView.setOnDateChangeListener { baslangicCalendarView, year, month, dayOfMonth ->
            if(month<9) {
                if(dayOfMonth<10){
                    baslangicTarihText.text = "$year-0" + (month + 1) + "-0$dayOfMonth"
                }else {
                    baslangicTarihText.text = "$year-0" + (month + 1) + "-$dayOfMonth"
                }
            }else{
                baslangicTarihText.text = "$year-" + (month + 1) + "-$dayOfMonth"
                if(dayOfMonth<10){
                    baslangicTarihText.text = "$year-" + (month + 1) + "-0$dayOfMonth"
                }else{
                    baslangicTarihText.text = "$year-" + (month + 1) + "-$dayOfMonth"
                }
            }
        }
    }
    fun bitisTarihiOnClick(view: View) {
        baslangicCalendarView.setOnDateChangeListener { baslangicCalendarView, year, month, dayOfMonth ->
            if(month<9) {
                if(dayOfMonth<10){
                    bitisTarihText.text = "$year-0" + (month + 1) + "-0$dayOfMonth"
                }else {
                    bitisTarihText.text = "$year-0" + (month + 1) + "-$dayOfMonth"
                }
            }else{
                bitisTarihText.text = "$year-" + (month + 1) + "-$dayOfMonth"
                if(dayOfMonth<10){
                    bitisTarihText.text = "$year-" + (month + 1) + "-0$dayOfMonth"
                }else{
                    bitisTarihText.text = "$year-" + (month + 1) + "-$dayOfMonth"
                }
            }
        }
    }
    lateinit var  baslangicTarihi: LocalDate
    lateinit var  bitisTarihi: LocalDate

    @RequiresApi(Build.VERSION_CODES.O)
    fun yazdirOnClick(view: View) {

          baslangicTarihi= LocalDate.parse(baslangicTarihText.text.toString(), DateTimeFormatter.ISO_DATE)
          bitisTarihi= LocalDate.parse(bitisTarihText.text.toString(), DateTimeFormatter.ISO_DATE)
        if(baslangicTarihi>bitisTarihi){
            Toast.makeText(applicationContext, "LÜTFEN BİTİŞ TARİHİNİ BAŞLANGIÇ TARİHİNDEN ERKEN BİR TARİH SEÇİNİZ!!!", Toast.LENGTH_SHORT).show()
        }else{
            yazdirVeriGetir()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun yazdirVeriGetir(){ // satın al butonuna basılırsa eğer bakiyede para varsa satın alma işlemi yapılıyor
        db.collection("UrunKayitlari").document(auth.currentUser!!.email.toString())
                .collection("SatinAlinanUrunler").orderBy("tarih", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        Toast.makeText(
                                applicationContext,
                                exception.localizedMessage.toString(),
                                Toast.LENGTH_LONG
                        ).show()
                    } else {
                        if (snapshot != null) {
                            if (!snapshot.isEmpty) {
                                var documents = snapshot.documents

                                for (document in documents) {
                                    var tarih = document.get("tarih") as Timestamp
                                    var urunKGKayit = document.get("urunKGKayit") as Number
                                    var urunKayit = document.get("urunKayit") as String
                                    var urunTutarKayit = document.get("urunTutarKayit") as Number
                                    var  Tarih= LocalDate.parse(timestampToDate(tarih), DateTimeFormatter.ISO_DATE)
                                    if(baslangicTarihi<=Tarih && bitisTarihi>=Tarih){

                                    }
                                }
                            }
                        }
                    }
                }
    }

    fun timestampToDate(tarih: Timestamp): String {
        var milliseconds = tarih.seconds * 1000 + tarih.nanoseconds / 1000000
        var sdf = SimpleDateFormat("yyyy-MM-dd")
        var netDate = Date(milliseconds)
        var date = sdf.format(netDate).toString()
        return date
    }


}