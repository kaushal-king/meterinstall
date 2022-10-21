package com.karebo2.teamapp.adapter

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.karebo2.teamapp.R
import com.karebo2.teamapp.dataclass.meterData.SubJobCard
import com.karebo2.teamapp.dataclass.meterData.meterauditDataModel
import com.karebo2.teamapp.utils.ConstantHelper
import java.util.*

class meterAuditadapter2(private val mList: List<SubJobCard>,
                         var mCtx: Context,
                         var view2: View
) : RecyclerView.Adapter<meterauditadapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): meterauditadapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_meteraudit, parent, false)
        val viewHolder = meterauditadapter.ViewHolder(view)
        viewHolder.card.setOnClickListener{
//            val bundle = Bundle()
//            val JsonString: String =
//                GsonParser.gsonParser!!.toJson(mList[viewHolder.adapterPosition])
//            bundle.putString("data", JsonString)


            if(mList[viewHolder.adapterPosition].task?.parcelAddress==null) {
                var address = findAddress(mList[viewHolder.adapterPosition])
                ConstantHelper.ADDRESS = address
            }else{
                ConstantHelper.ADDRESS= mList[viewHolder.adapterPosition].task?.parcelAddress.toString()

            }



            ConstantHelper.currentSelectdSubMeter=mList[viewHolder.adapterPosition]

            Navigation.findNavController(view2).navigate(
                R.id.action_nav_subMeter_to_nav_accessstatus
            )

        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: meterauditadapter.ViewHolder, position: Int) {
        var addresses: List<Address?>
        var geocoder = Geocoder(mCtx, Locale.getDefault())

        val item = mList[position]
        if(item.task?.parcelAddress==null){
            var address=findAddress(item)
            holder.address.text =address
        }
        else{
            holder.address.text =item.task?.parcelAddress
        }




        holder.date.text=item.task?.team
    }

    override fun getItemCount(): Int {
        return mList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var address: TextView = itemView.findViewById(R.id.tv_address)
        var date: TextView = itemView.findViewById(R.id.tv_date)
        var card: ConstraintLayout = itemView.findViewById(R.id.card_meter_audit)

    }



    fun findAddress(listItem:SubJobCard):String
    {
        var addresses: List<Address?> = listOf()
        var geocoder = Geocoder(mCtx, Locale.getDefault())
        var returnAddress:String

        val item = listItem
        try {
            addresses = geocoder.getFromLocation(item.task?.latitude as Double, item.task?.longitude as Double, 1);

        }catch (e:Exception){

        }
        if(addresses != null && !addresses.isEmpty()){
            var address=addresses[0]?.getAddressLine(0)
            address=address?.replace(", South Africa","")
            address=address?.replace("South Africa","")
            returnAddress=address!!
        }
        else{
            returnAddress= "No Address on "+ "("+item.task?.latitude+","+item.task?.longitude+")";
        }

        return returnAddress
    }
}