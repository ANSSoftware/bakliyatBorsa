package com.ans.borsa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class urunSatinAlRA(
        private val UserEmailArray: ArrayList<String>,
        private val addUrunSayiArray: ArrayList<String>,
        private val addUrunTutariArray: ArrayList<String>,
)
    : RecyclerView.Adapter<urunSatinAlRA.SatisHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SatisHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.urun_satis_ra, parent, false)
        return SatisHolder(view)
    }

    override fun onBindViewHolder(holder: SatisHolder, position: Int) {
        holder.recycleraddUrunTutari?.text = addUrunTutariArray[position]
        holder.recyclerUserEmail?.text = UserEmailArray[position]
        holder.recycleraddUrunSayi?.text = addUrunSayiArray[position]
    }


    override fun getItemCount(): Int {
        return UserEmailArray.size

    }

    inner class SatisHolder(view: View) : RecyclerView.ViewHolder(view) {
        var recycleraddUrunTutari: TextView? = null
        var recyclerUserEmail: TextView? = null
        var recycleraddUrunSayi: TextView? = null

        init {
            recyclerUserEmail = view.findViewById(R.id.recyclerUserEmail)
            recycleraddUrunSayi = view.findViewById(R.id.recyclerurunMiktar)
            recycleraddUrunTutari = view.findViewById(R.id.recyclerurunTutar)
        }

    }


}


