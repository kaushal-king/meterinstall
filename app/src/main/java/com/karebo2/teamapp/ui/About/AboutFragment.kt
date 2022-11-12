package com.karebo2.teamapp.ui.About

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.karebo2.teamapp.Api.Api
import com.karebo2.teamapp.Api.ApiClient
import com.karebo2.teamapp.R
import com.karebo2.teamapp.databinding.FragmentAboutBinding
import com.karebo2.teamapp.dataclass.meterData.meterauditDataModel
import com.karebo2.teamapp.sharedpreference.SharedPreferenceHelper
import com.karebo2.teamapp.utils.GsonParser.gsonParser
import com.karebo2.teamapp.utils.LoaderHelper

import com.karebo2.teamapp.utils.ConstantHelper

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AboutFragment : Fragment() {

private var _binding: FragmentAboutBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!
var connected=false
    override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {

//    val homeViewModel =
//            ViewModelProvider(this).get(AboutViewModel::class.java)

    _binding = FragmentAboutBinding.inflate(inflater, container, false)
    val root: View = binding.root


        if(SharedPreferenceHelper.getInstance(requireContext()).getTeamKey()!="null"){
            binding.etTeamKey.setText(SharedPreferenceHelper.getInstance(requireContext()).getTeamKey())
            Log.e("TAG", "onCreateView: "+SharedPreferenceHelper.getInstance(requireContext()).getTeamKey() )
            verifyTeamKey(root)
        }



//      binding.btVerify.setOnClickListener{
//          verifyTeamKey(root)
//      }

        binding.etTeamKey.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var teamKey = p0.toString()

                verifyTeamKey(root)
            }
            override fun afterTextChanged(p0: Editable?) {
            }

        })

        return root
  }


    fun tt(){

        if(SharedPreferenceHelper.getInstance(requireContext()).getJobCard()!="null"){

            var job=SharedPreferenceHelper.getInstance(requireContext()).getJobCard()
            Log.e("TAG", "tt: "+job.toString(), )
            val joblist: Array<meterauditDataModel> = gsonParser!!.fromJson(
                job,
                Array<meterauditDataModel>::class.java
            )
            ConstantHelper.list= joblist.toList()

            Log.e("TAG", "joblist.jobcard: " + ConstantHelper.list )

        }
        LoaderHelper.dissmissLoader()
        if(SharedPreferenceHelper.getInstance(requireContext()).getTeamKey()!="null" &&
            SharedPreferenceHelper.getInstance(requireContext()).getOtp()!="null"    ){
            var  navController : NavController = NavHostFragment.findNavController(this)
            try{
                navController.navigate(R.id.action_nav_about_to_nav_meteraudit)
            }catch (e:Exception){

            }

        }


//

    }

    fun verifyTeamKey(root:View) {
//        netConnected()
//        if(!connected){
//            if(SharedPreferenceHelper.getInstance(requireContext()).getTeamKey()!="null"&&
//                SharedPreferenceHelper.getInstance(requireContext()).getOtp()!="null"){
//            tt()
//            }
//        }

            if(binding.etTeamKey.text.length==10){
                LoaderHelper.showLoader(requireContext())
                testKey(binding.etTeamKey.text.toString().toUpperCase(),root)
//
            }

    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    fun testKey(teamkey:String,root: View){

        val client = ApiClient()
        val api = client.getClient()?.create(Api::class.java)
        val call = api?.test(teamkey)
        call?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if(response.isSuccessful){
                    var statuscode=response.code()
                    Log.e("TAG", "Statuscode of TeamKey Test " + statuscode)

                    if(statuscode==200){
                        // Log.e("TAG", "onResponse: "+response.body().toString(), )
                        SharedPreferenceHelper.getInstance(requireContext()).setTeamKey(teamkey)
                        LoaderHelper.dissmissLoader()



                        if(SharedPreferenceHelper.getInstance(requireContext()).getOtp()!="null"){


                           try{
                               findNavController(root).navigate(
                                   R.id.action_nav_about_to_nav_otpscreen,
                               )
                           }catch (e:Exception){

                           }

                        }
                        else{

                            try{
                                findNavController(root).navigate(
                                    R.id.action_nav_about_to_nav_question,
                                )
                            }catch (e:Exception){

                            }

                        }

                    }
                    else    {
                        LoaderHelper.dissmissLoader()
                        Toast.makeText(requireContext(),
                            response.body().toString(), Toast.LENGTH_SHORT)
                            .show()
                    }


                }
                else{
                    Log.e("TAG", "teamkey: " + response.code())
                    LoaderHelper.dissmissLoader()
                    Toast.makeText(requireContext(),
                        "Unauthorized Access", Toast.LENGTH_SHORT)
                        .show()
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                LoaderHelper.dissmissLoader()
                Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT)
                    .show()
                tt()

            }

        })




    }



}