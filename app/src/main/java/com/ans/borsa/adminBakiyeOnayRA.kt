package com.ans.borsa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class adminBakiyeOnayRA(private val addbakiyeTextArray: ArrayList<Double>,
                        private val addbakiyeOnayArray: ArrayList<Number>,
                        private val UserEmailArray: ArrayList<String>)
                        : RecyclerView.Adapter<adminBakiyeOnayRA.PostHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.admin_bakiye_onay_ra,parent,false)

        return PostHolder(view)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.recycleraddbakiyeText?.text = addbakiyeTextArray[position].toString()
        holder.recycleraddbakiyeOnay?.text = addbakiyeOnayArray[position].toString()
        holder.recyclerUserEmail?.text = UserEmailArray[position]

    }


    override fun getItemCount(): Int {
        return UserEmailArray.size
    }

    class PostHolder(view : View) : RecyclerView.ViewHolder(view) {
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


