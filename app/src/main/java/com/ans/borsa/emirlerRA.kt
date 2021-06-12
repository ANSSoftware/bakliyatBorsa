package com.ans.borsa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class emirlerRA(private val addEmirUrunTextArray: ArrayList<String>,
                      private val addEmirOnayArray: ArrayList<String>,
                      private val addUrunSayiArray: ArrayList<String>,
                      private val UserEmailArray: ArrayList<String>,
                      private val addUrunTutarArray: ArrayList<String>)
    : RecyclerView.Adapter<emirlerRA.EmirHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmirHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.emirler_ra, parent, false)
        return EmirHolder(view)
    }

    override fun onBindViewHolder(holder: EmirHolder, position: Int) {
        holder.recycleraddEmirUrunText?.text = addEmirUrunTextArray[position]
        holder.recyclerUserEmail?.text = UserEmailArray[position]
        holder.recycleraddEmirOnay?.text = addEmirOnayArray[position]
        holder.recycleraddUrunSayi?.text = addUrunSayiArray[position]
        holder.recycleraddUrunTutar?.text = addUrunTutarArray[position]
    }


    override fun getItemCount(): Int {
        return UserEmailArray.size

    }

    inner class EmirHolder(view: View) : RecyclerView.ViewHolder(view) {
        var recycleraddEmirUrunText: TextView? = null
        var recycleraddEmirOnay: TextView? = null
        var recyclerUserEmail: TextView? = null
        var recycleraddUrunSayi: TextView? = null
        var recycleraddUrunTutar: TextView? = null

        init {
            recycleraddEmirUrunText = view.findViewById(R.id.recycleraddurunEmirText)
            recycleraddUrunSayi = view.findViewById(R.id.recycleraddurunEmirMiktar)
            recycleraddUrunTutar = view.findViewById(R.id.recycleraddurunEmirtutar)
            recycleraddEmirOnay = view.findViewById(R.id.recycleraddurunEmironay)
        }

    }


}


