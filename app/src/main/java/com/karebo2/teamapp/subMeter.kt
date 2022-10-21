package com.karebo2.teamapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.karebo2.teamapp.adapter.meterAuditadapter2

import com.karebo2.teamapp.databinding.FragmentSubMeterBinding
import com.karebo2.teamapp.dataclass.meterData.SubJobCard
import com.karebo2.teamapp.dataclass.meterData.meterauditDataModel
import com.karebo2.teamapp.sharedpreference.SharedPreferenceHelper
import com.karebo2.teamapp.utils.ConstantHelper
import com.karebo2.teamapp.utils.GsonParser

class subMeter : Fragment() {

    private var _binding: FragmentSubMeterBinding? = null
    private val binding get() = _binding!!
    private var adapter: meterAuditadapter2? = null
    lateinit var viw:View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSubMeterBinding.inflate(
            inflater,container,false)
        val root: View = binding.root
        viw=root
        loadSubMeter()


        return root
    }

    private fun loadSubMeter() {

        var completeJobNumber= SharedPreferenceHelper.getInstance(requireContext()).getCompleteJobId()

        var completeJobNumberList: Array<String>?=null
        if(completeJobNumber!="null"){
            completeJobNumberList= GsonParser.gsonParser!!.fromJson(
                completeJobNumber,
                Array<String>::class.java
            )
        }

        var newList:MutableList<SubJobCard> = mutableListOf()
        ConstantHelper.subMeterlist.forEach {
            if(!completeJobNumberList!!.contains(it.task!!.jobCardId)){
                newList.add(it)
            }
        }

        ConstantHelper.subMeterlist=newList






        var list: List<SubJobCard>? = ConstantHelper.subMeterlist

        Log.e("TAG", "loadSubMeter: " +list.toString(), )


        adapter = meterAuditadapter2(
            list!!,
            requireActivity(), viw

        )
        binding.rvSubmeter.adapter = adapter
        binding.rvSubmeter.adapter?.notifyDataSetChanged()


    }


}