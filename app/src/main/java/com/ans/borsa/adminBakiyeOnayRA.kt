package com.ans.borsa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class adminBakiyeOnayRA(private val addbakiyeTextArray: ArrayList<String>,
                        private val addbakiyeOnayArray: ArrayList<String>,
                        private val UserEmailArray: ArrayList<String>)
                        : RecyclerView.Adapter<adminBakiyeOnayRA.BakiyeHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BakiyeHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.admin_bakiye_onay_ra,parent,false)
        return BakiyeHolder(view)
    }

    override fun onBindViewHolder(holder: BakiyeHolder, position: Int) {
            holder.recycleraddbakiyeText?.text = addbakiyeTextArray[position]
            holder.recyclerUserEmail?.text = UserEmailArray[position]
            holder.recycleraddbakiyeOnay?.text = addbakiyeOnayArray[position]

    }


    override fun getItemCount(): Int {
        return UserEmailArray.size

    }

    inner class BakiyeHolder(view : View) : RecyclerView.ViewHolder(view) {
        var recycleraddbakiyeText: TextView? = null
        var recycleraddbakiyeOnay : TextView? = null
        var recyclerUserEmail : TextView? = null

        init{
            recycleraddbakiyeText = view.findViewById(R.id.recycleraddbakiyeText)
           recycleraddbakiyeOnay = view.findViewById(R.id.recycleraddbakiyeOnay)
            recyclerUserEmail = view.findViewById(R.id.recyclerUserEmail)
        }

    }


}





