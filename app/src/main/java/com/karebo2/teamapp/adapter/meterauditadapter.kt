package com.karebo2.teamapp.adapter

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.karebo2.teamapp.R
import com.karebo2.teamapp.dataclass.meterData.meterauditDataModel
import com.karebo2.teamapp.utils.ConstantHelper
import java.util.*


class meterauditadapter(private val mList: List<meterauditDataModel>,
                        var mCtx: Context,
                        var view2: View
) : RecyclerView.Adapter<meterauditadapter.ViewHolder>()  {





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_meteraudit, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.card.setOnClickListener{
//            val bundle = Bundle()
//            val JsonString: String =
//                GsonParser.gsonParser!!.toJson(mList[viewHolder.adapterPosition])
//            bundle.putString("data", JsonString)

            if(mList[viewHolder.adapterPosition].parcelAddress==null){

                    var address=findAddress(mList[viewHolder.adapterPosition])
                    ConstantHelper.ADDRESS=address
                }
                else{
                    ConstantHelper.ADDRESS= mList[viewHolder.adapterPosition].parcelAddress.toString()
                }



            ConstantHelper.currentSelectd=mList[viewHolder.adapterPosition]

            if(mList[viewHolder.adapterPosition].subJobCards==null || mList[viewHolder.adapterPosition].subJobCards!!.isEmpty() ){


                Navigation.findNavController(view2).navigate(
//                R.id.action_nav_meteraudit_to_nav_auditphoto
                    R.id.action_nav_meteraudit_to_nav_accessstatus
                )
            }else{

                ConstantHelper.subMeterlist= mList[viewHolder.adapterPosition].subJobCards!!

                Navigation.findNavController(view2).navigate(
//                R.id.action_nav_meteraudit_to_nav_auditphoto
                    R.id.action_nav_meteraudit_to_nav_subMeter
                )
            }




        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var addresses: List<Address?>
        var geocoder = Geocoder(mCtx, Locale.getDefault())

        val item = mList[position]

        if(item.parcelAddress==null) {
            var address = findAddress(item)
            holder.address.text = address
        }else{
            holder.address.text =item.parcelAddress
        }
//        addresses = geocoder.getFromLocation(item.latitude as Double, item.longitude as Double, 1);
//        if(addresses != null && !addresses.isEmpty()){
//            var address=addresses[0]?.getAddressLine(0)
//            address=address?.replace(", South Africa","")
//            address=address?.replace("South Africa","")
//            holder.address.text =address
//        }
//        else{
//            holder.address.text = "No Address on "+ "("+item.latitude+","+item.longitude+")";
//        }
        holder.date.text=item.team
    }

    override fun getItemCount(): Int {
        return mList.size

    }



    fun findAddress(listItem:meterauditDataModel):String
    {
        var addresses: List<Address?> = listOf()
        var geocoder = Geocoder(mCtx, Locale.getDefault())
        var returnAddress:String

        val item = listItem
        try {
            addresses = geocoder.getFromLocation(item.latitude as Double, item.longitude as Double, 1);

        }catch (e:Exception){

        }
        if(addresses != null && !addresses.isEmpty()){
            var address=addresses[0]?.getAddressLine(0)
            address=address?.replace(", South Africa","")
            address=address?.replace("South Africa","")
            returnAddress=address!!
        }
        else{
            returnAddress= "No Address on "+ "("+item.latitude+","+item.longitude+")";
        }

        return returnAddress
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var address: TextView = itemView.findViewById(R.id.tv_address)
        var date: TextView = itemView.findViewById(R.id.tv_date)
        var card: ConstraintLayout = itemView.findViewById(R.id.card_meter_audit)

    }
}