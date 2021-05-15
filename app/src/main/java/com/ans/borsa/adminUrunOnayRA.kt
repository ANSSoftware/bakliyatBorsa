package com.ans.borsa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class adminUrunOnayRA(private val addUrunTextArray: ArrayList<String>,
                      private val addUrunOnayArray: ArrayList<String>,
                      private val addUrunSayiArray: ArrayList<String>,
                      private val UserEmailArray: ArrayList<String>,
                      private val addUrunTutarArray: ArrayList<String>)
    : RecyclerView.Adapter<adminUrunOnayRA.UrunHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UrunHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.admin_urun_onay_ra, parent, false)
        return UrunHolder(view)
    }

    override fun onBindViewHolder(holder: UrunHolder, position: Int) {
        holder.recycleraddUrunText?.text = addUrunTextArray[position]
        holder.recyclerUserEmail?.text = UserEmailArray[position]
        holder.recycleraddUrunOnay?.text = addUrunOnayArray[position]
        holder.recycleraddUrunSayi?.text = addUrunSayiArray[position]
        holder.recycleraddUrunTutar?.text = addUrunTutarArray[position]
    }


    override fun getItemCount(): Int {
        return UserEmailArray.size

    }

    inner class UrunHolder(view: View) : RecyclerView.ViewHolder(view) {
        var recycleraddUrunText: TextView? = null
        var recycleraddUrunOnay: TextView? = null
        var recyclerUserEmail: TextView? = null
        var recycleraddUrunSayi: TextView? = null
        var recycleraddUrunTutar: TextView? = null

        init {
            recycleraddUrunText = view.findViewById(R.id.recycleraddurunText)
            recyclerUserEmail = view.findViewById(R.id.recyclerUserEmailUrun)
            recycleraddUrunSayi = view.findViewById(R.id.recycleraddurunMiktar)
            recycleraddUrunTutar = view.findViewById(R.id.recycleradduruntutar)
        }

    }


}


