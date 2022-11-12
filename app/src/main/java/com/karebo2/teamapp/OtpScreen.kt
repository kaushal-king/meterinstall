package com.karebo2.teamapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.Navigation
import com.karebo2.teamapp.Api.Api
import com.karebo2.teamapp.Api.ApiClient
import com.karebo2.teamapp.databinding.FragmentOtpScreenBinding
import com.karebo2.teamapp.dataclass.meterData.meterauditDataModel
import com.karebo2.teamapp.utils.GsonParser
import com.karebo2.teamapp.utils.LoaderHelper
import com.karebo2.teamapp.sharedpreference.SharedPreferenceHelper
import com.karebo2.teamapp.utils.ConstantHelper
import com.the.firsttask.utils.NetworkUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OtpScreen : Fragment() {


    private var _binding: FragmentOtpScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOtpScreenBinding.inflate(
            inflater,container,false)

        val root: View = binding.root

        val args = arguments
        var msg=""
        if(args != null){
             msg = args!!.getString("msg")!!
        }
        else{
            msg="You Already have PIN No need to Submit"
        }


        binding.tvOtpSentMessage2.text=msg

        binding.llResendOtp.setOnClickListener{
            resendOtp()
        }


        binding.btLoadJobCard.setOnClickListener{
            if( binding.etOtp.text.isEmpty()){
                Toast.makeText(requireContext(),"Enter PIN", Toast.LENGTH_LONG).show()
            }
            else{
//                loadMeter(binding.etOtp.text.toString(),root)
                loadJobCard(binding.etOtp.text.toString(),root)



            }

        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(SharedPreferenceHelper.getInstance(requireContext()).getOtp()!="null"){
            binding.etOtp.setText(SharedPreferenceHelper.getInstance(requireContext()).getOtp())
//            loadMeter(binding.etOtp.text.toString(),root)

            if(NetworkUtils.isConnected==false){
                Log.e("TAG", "onCreateView: "+"otp is present", )
                try {
                    Navigation.findNavController(binding.root).navigate(
                        R.id.action_nav_otpscreen_to_nav_meteraudit,
                    )
                }catch (e:Exception){
                    Log.e("TAG", "onCreateView: "+e.localizedMessage.toString(), )
                }
            }else{
                loadJobCard(binding.etOtp.text.toString(),binding.root)
                Log.e("TAG", "Stored PIN: "+ SharedPreferenceHelper.getInstance(requireContext()).getOtp() )
            }



        }

    }

//    fun navigateMeterAudit(root:View){
//        val bundle = Bundle()
//        bundle.putString("otp", binding.etOtp.text.toString())
//        if(SharedPreferenceHelper.getInstance(requireContext()).getJobCard()!="null"){
//            var job=SharedPreferenceHelper.getInstance(requireContext()).getJobCard()
//            var joblist=GsonParser.gsonParser!!.fromJson(job, listjobcard::class.java)
//            Log.e("TAG", "joblist.jobcard: "+joblist.jobcard.toString(), )
//            ConstantHelper.list= joblist.jobcard!!
//
//        }
//        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_drawer).navigate(
//            R.id.action_nav_otpscreen_to_nav_meteraudit,bundle
//        )
//
//
////        Navigation.findNavController(root).navigate(
////
////        )
//    }



//    fun loadMeter(otp:String,root:View){
//
//        val client = ApiClient()
//        val api = client.getClient()?.create(Api::class.java)
//        val call = api?.MeterList(otp.toInt(),true)
//        call?.enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(
//                call: Call<ResponseBody>,
//                response: Response<ResponseBody>
//            ) {
//
//                if(response.isSuccessful){
//                    var statuscode=response.code()
//                    Log.e("TAG", "Statuscode of loadMeter " + statuscode)
//
//                    if(statuscode==200){
//
//                        SharedPreferenceHelper.getInstance(requireContext()).setAllMeterCode(response.body()?.string())
//                        Toast.makeText(requireContext(),"successFull load Meter", Toast.LENGTH_SHORT)
//                            .show()
//                        Log.e("TAG", "loadMeter: "+response.body()?.string(), )
//
//                    }
//                    else    {
//                        LoaderHelper.dissmissLoader()
//                        Log.e("TAG", "loadMeter: "+response.body()?.string(), )
//                        Toast.makeText(requireContext(),"some error occured"+ response.body()?.string(), Toast.LENGTH_SHORT)
//                            .show()
//                    }
//
//
//                }
//                else{
////                    Log.e("TAG", "AddToolBox :"+response.body()?.string(), )
////                    Log.e("TAG", "AddToolBox :"+response.errorBody()?.string(), )
//                    LoaderHelper.dissmissLoader()
//                    Log.e("TAG", "loadMeter: "+response.errorBody()?.string(), )
//                    Toast.makeText(requireContext(),
//                        "some error occured "+response.errorBody()?.string(), Toast.LENGTH_SHORT)
//                        .show()
//                }
//
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                LoaderHelper.dissmissLoader()
//                Log.e("TAG", "onFailure: "+t.localizedMessage, )
//                Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT)
//                    .show()
//            }
//
//        })
//
//
//    }




     fun loadJobCard(otp:String,root:View) {

         LoaderHelper.showLoader(requireContext())
         val client = ApiClient()
         val api = client.getClient()?.create(Api::class.java)
         val call = api?.checkOtp(otp)
         call?.enqueue(object : Callback<List<meterauditDataModel>> {
             override fun onResponse(
                 call: Call<List<meterauditDataModel>>,
                 response: Response<List<meterauditDataModel>>
             ) {

                 if(response.isSuccessful){

                     var statuscode=response.code()
                     Log.e("TAG", "Statuscode of LoadjobCard " + statuscode)

                     if(statuscode==200){

                         Log.e("TAG", "onResponse: "+response.body().toString(), )
                         SharedPreferenceHelper.getInstance(requireContext()).setOtp(otp)


                         compareAndSave(response.body()!!)


//                         ConstantHelper.list= response.body()!!
//
//                         val JsonString: String =
//                             GsonParser.gsonParser!!.toJson(response.body()!!)
//                         SharedPreferenceHelper.getInstance(requireContext()).setJobCard(JsonString)


                         try {
                             val inputMethodManager: InputMethodManager =
                                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                             inputMethodManager.hideSoftInputFromWindow(root!!.windowToken, 0)
                         } catch (e: Exception) {
                             print("exception is" + e.stackTraceToString())
                         }

                         try {
                             Navigation.findNavController(root).navigate(
                                 R.id.action_nav_otpscreen_to_nav_meteraudit,
                             )
                         }catch (e:Exception){

                         }




                     }
                     else    {

                         LoaderHelper.dissmissLoader()
                         Toast.makeText(requireContext(), "error occured", Toast.LENGTH_SHORT)
                             .show()
                     }


                 }
                 else{
                     LoaderHelper.dissmissLoader()
                     SharedPreferenceHelper.getInstance(requireContext()).setOtp("null")

                     try {

                         Navigation.findNavController(root).navigate(
                             R.id.action_nav_otpscreen_to_nav_question,
                         )


                     }catch (e:Exception){

                     }

                     Toast.makeText(requireActivity(),
                         response.errorBody()?.string(), Toast.LENGTH_SHORT)
                         .show()

                 }

             }

             override fun onFailure(call: Call<List<meterauditDataModel>>, t: Throwable) {
                 LoaderHelper.dissmissLoader()
                 Toast.makeText(requireActivity(), "Network Error", Toast.LENGTH_SHORT)
                     .show()
             }

         })


    }




    fun resendOtp(){
        LoaderHelper.showLoader(requireContext())
        var  teamkey= SharedPreferenceHelper.getInstance(requireContext()).getTeamKey()


        val client = ApiClient()
        val api = client.getClient()?.create(Api::class.java)
        val call = api?.resendPin(teamkey,ConstantHelper.APP_NAME)
        call?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                if(response.isSuccessful){

                    var statuscode=response.code()
                    Log.e("TAG", "Statuscode of Resendpin " + statuscode)

                    if(statuscode==200){

                        LoaderHelper.dissmissLoader()
                        binding.tvOtpSentMessage2.text=response.body()?.string()
                    }
                    else    {
                        LoaderHelper.dissmissLoader()
                        Toast.makeText(requireContext(), response.body()?.string(), Toast.LENGTH_SHORT)
                            .show()
                    }


                }
                else{
                    LoaderHelper.dissmissLoader()
                    Toast.makeText(requireContext(),
                        response.errorBody()?.string(), Toast.LENGTH_SHORT)
                        .show()
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                LoaderHelper.dissmissLoader()
                Toast.makeText(requireActivity(), "Network Error", Toast.LENGTH_SHORT)
                    .show()
            }

        })

    }



    fun compareAndSave(body: List<meterauditDataModel>) {



        var completeJobNumber= SharedPreferenceHelper.getInstance(requireContext()).getCompleteJobId()

        var completeJobNumberList: Array<String>?=null
        if(completeJobNumber!="null"){
            completeJobNumberList= GsonParser.gsonParser!!.fromJson(
                completeJobNumber,
                Array<String>::class.java
            )
        }

        var list: MutableList<String> = mutableListOf()
        if(completeJobNumberList!=null){
            list= completeJobNumberList.toList() as MutableList<String>
        }

        var newList:MutableList<meterauditDataModel> = mutableListOf()

        body.forEach {
            if(!list.contains(it.jobCardId)){

                newList.add(it)
            }
        }




        ConstantHelper.list= newList

        val JsonString: String =
            GsonParser.gsonParser!!.toJson(newList)
        SharedPreferenceHelper.getInstance(requireContext()).setJobCard(JsonString)




    }
}