package com.karebo2.teamapp.meteraudit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.karebo2.teamapp.adapter.meterauditadapter
import com.karebo2.teamapp.databinding.FragmentListfragmentBinding
import com.karebo2.teamapp.dataclass.meterData.meterauditDataModel
import com.karebo2.teamapp.sharedpreference.SharedPreferenceHelper

import com.karebo2.teamapp.utils.ConstantHelper
import com.karebo2.teamapp.utils.GsonParser

class listfragment() : Fragment() {


    private var _binding: FragmentListfragmentBinding? = null
    private val binding get() = _binding!!
    private var adapter: meterauditadapter? = null
     lateinit var viw:View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListfragmentBinding.inflate(
            inflater,container,false)
        val root: View = binding.root
viw=root

        loadMeterAudit()

        return root
    }

    private fun loadMeterAudit() {
        var completeJobNumber= SharedPreferenceHelper.getInstance(requireContext()).getCompleteJobId()

        var completeJobNumberList: Array<String>?=emptyArray<String>()
        if(completeJobNumber!="null"){
            completeJobNumberList= GsonParser.gsonParser!!.fromJson(
                completeJobNumber,
                Array<String>::class.java
            )
        }

        var newList:MutableList<meterauditDataModel> = mutableListOf()
        ConstantHelper.list.forEach {
            if(!completeJobNumberList!!.contains(it.jobCardId)){
                newList.add(it)
            }
        }

        ConstantHelper.list=newList



        var list2: List<meterauditDataModel>? = ConstantHelper.list

        adapter = meterauditadapter(
            list2!!,
            requireActivity(), viw

        )
        binding.rvQuestion.adapter = adapter
        binding.rvQuestion.adapter?.notifyDataSetChanged()


    }




}