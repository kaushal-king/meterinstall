package com.karebo2.teamapp


import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.karebo2.teamapp.Api.Api
import com.karebo2.teamapp.Api.ApiClient
import com.karebo2.teamapp.databinding.FragmentSignOffBinding
import com.karebo2.teamapp.dataclass.meterData.meterauditDataModel
import com.karebo2.teamapp.roomdata.RoomDb
import com.karebo2.teamapp.roomdata.mainbody
import com.karebo2.teamapp.roomdata.photobody
import com.karebo2.teamapp.sharedpreference.SharedPreferenceHelper
import com.karebo2.teamapp.utils.ConstantHelper
import com.karebo2.teamapp.utils.GsonParser
import com.karebo2.teamapp.utils.LoaderHelper
import com.the.firsttask.utils.NetworkUtils
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class SignOff : Fragment() {


    private var _binding: FragmentSignOffBinding? = null
    private val binding get() = _binding!!

    var currentSelected:meterauditDataModel?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignOffBinding.inflate(
            inflater,container,false)
        val root: View = binding.root

        var data=  SharedPreferenceHelper.getInstance(requireContext()).getCurrentSelected()
        currentSelected = GsonParser.gsonParser!!.fromJson(data, meterauditDataModel::class.java)







        binding.btnBidvestTechSignatureClear.setOnClickListener{
            binding.inkBidvestTechSignature.clear()
        }
        binding.btnKareboTechSignatureClear.setOnClickListener{
            binding.inkKareboTechSignature.clear()
        }

        setSignatureBoardScroll()


        binding.btSubmit.setOnClickListener{

           addInModel()

            val bundle = Bundle()

//            addAllPhoto(root)
            Navigation.findNavController(root).navigate(
                R.id.action_nav_signOff_to_nav_meterlocation,bundle

                )
        }
        return root
    }



//    fun addAllPhoto(root: View){
//
//        LoaderHelper.showLoader(requireContext())
//
//        if(NetworkUtils.isConnected==false){
//
//            val photobodyDao=RoomDb.getAppDatabase((requireContext()))?.photobodydao()
//
//            ConstantHelper.photoList.forEach {
//
////            requests.add( api?.addPhoto64(it.uuid,it.bodyy)!!)
//                photobodyDao?.addphotobody(photobody(it.uuid,it.bodyy))
//                Log.e("TAG", "addAllPhoto uuid: ${it.uuid}", )
////            Log.e("TAG", "addAllPhoto body: ${it.bodyy}", )
//            }
//
//            LoaderHelper.dissmissLoader()
//            activity?.runOnUiThread {
//                submitMeter(root)
//
//            }
//
//        }else{
//
//            val client = ApiClient()
//            val api = client.getClient()?.create(Api::class.java)
//
//            val requests =  mutableListOf<Observable<ResponseBody>>()
//
//            ConstantHelper.photoList.forEach {
//
//                requests.add( api?.addPhoto64(it.uuid,it.bodyy)!!)
//                Log.e("TAG", "addAllPhoto uuid: ${it.uuid}", )
////               Log.e("TAG", "addAllPhoto body: ${it.bodyy}", )
//            }
//
//
//
//
//            Observable.merge(requests)
//                .take(requests.size.toLong())
//                // executed when the channel is closed or disposed
//                .doFinally {
//                    Log.e("TAG", "addAllPhoto final: ", )
//
//                    ConstantHelper. photoList = mutableListOf()
//                    LoaderHelper.dissmissLoader()
//                    activity?.runOnUiThread {
//                        submitMeter(root)
//
//                    }
////
//                }
//                .subscribeOn(Schedulers.io())
//                // it's a question if you want to observe these on main thread, depends on context of your application
//                .subscribe(
//                    { ResponseBody ->
//                        // here you get both the deviceId and the responseBody
//                        Log.e("TAG", "addAllPhoto responce: "+ResponseBody.string(), )
//
//
//
//                        if (ResponseBody == null ) {
//                            Log.e("TAG", "addAllPhoto responce: "+ ResponseBody?.string(), )
//
//                            // request for this deviceId failed, handle it
//                        }
//                    },
//                    { error ->
//                        Log.e("TAG", "Throwable: " + error)
//                    }
//                )
//
//
//        }
//
//
//
//
//    }
//
//
//
//
//
//    fun submitMeter(root:View){
//
//
//        LoaderHelper.showLoader(requireContext())
//
//        // Log.e("TAG", "submitMeter: "+ConstantHelper.submitMeterDataJSON.toString() )
//
//
//        var jobCardId=""
//
//        if(currentSelected!!.subJobCards==null ||currentSelected!!.subJobCards!!.isEmpty())
//            {
//                jobCardId= currentSelected!!.jobCardId.toString();
//            }else{
//                jobCardId=ConstantHelper.currentSelectdSubMeter.task!!.jobCardId
//        }
//
//
//
//
//            if(NetworkUtils.isConnected==false){
//            val mainbodyDao= RoomDb.getAppDatabase((requireContext()))?.mainbodydao()
//            mainbodyDao?.addMainBody(
//                mainbody(
//                    jobCardId,
//                ConstantHelper.submitJobCardDataJSON.toString())
//            )
//
//
//            Toast.makeText(requireContext(),"successFull Added offline", Toast.LENGTH_SHORT)
//                .show()
//            Log.e("TAG", "submitmeter: offline ", )
//
//
//
//            setJobId()
//            LoaderHelper.dissmissLoader()
//
//            ConstantHelper. submitJobCardDataJSON = JSONObject()
//            ConstantHelper. Meters = JSONObject()
//            ConstantHelper. TEST0123456 = JSONObject()
//            ConstantHelper. Components = JSONObject()
//            ConstantHelper. Feedback = JSONObject()
//            ConstantHelper. Duration = JSONObject()
//
//            ConstantHelper. photoList = mutableListOf()
//
//            val bundle = Bundle()
//            bundle.putString("data","from signature" )
//                try {
//                    Navigation.findNavController(root).navigate(
//                        R.id.action_nav_signOff_to_nav_meteraudit,bundle
//
//                    )
//                }catch (e:Exception){
//
//                }
//
//
//        }
//        else{
//            val client = ApiClient()
//            val api = client.getClient()?.create(Api::class.java)
//            val call = api?.submitMeter2(ConstantHelper.submitJobCardDataJSON.toString())
//            call?.enqueue(object : Callback<ResponseBody> {
//                override fun onResponse(
//                    call: Call<ResponseBody>,
//                    response: Response<ResponseBody>
//                ) {
//
//                    if(response.isSuccessful){
//                        var statuscode=response.code()
//                        Log.e("TAG", "Statuscode of Photo " + statuscode)
//
//                        if(statuscode==200){
//
//
//                            setJobId()
//                            LoaderHelper.dissmissLoader()
//                            ConstantHelper. submitJobCardDataJSON = JSONObject()
//                            ConstantHelper. Meters = JSONObject()
//                            ConstantHelper. TEST0123456 = JSONObject()
//                            ConstantHelper. Components = JSONObject()
//                            ConstantHelper. Feedback = JSONObject()
//                            ConstantHelper. Duration = JSONObject()
//
//                            ConstantHelper. photoList = mutableListOf()
//
//
//
//                            Toast.makeText(requireContext(),"successFull Added", Toast.LENGTH_SHORT)
//                                .show()
//                            Log.e("TAG", "submitmeter: "+response.body()?.string(), )
//
//
//                            val bundle = Bundle()
//                            bundle.putString("data","from signature" )
//                            try {
//                                Navigation.findNavController(root).navigate(
//                                    R.id.action_nav_signOff_to_nav_meteraudit,bundle
//
//                                )
//                            }catch (e:Exception){
//
//                            }
//
//
//                        }
//                        else    {
//                            LoaderHelper.dissmissLoader()
//                            Log.e("TAG", "submitmeter2: "+response.body()?.string(), )
//                            Toast.makeText(requireContext(),"some error occured"+ response.body()?.string(), Toast.LENGTH_SHORT)
//                                .show()
//                        }
//
//
//                    }
//                    else{
////                    Log.e("TAG", "AddToolBox :"+response.body()?.string(), )
////                    Log.e("TAG", "AddToolBox :"+response.errorBody()?.string(), )
//                        LoaderHelper.dissmissLoader()
//                        Log.e("TAG", "submitmeter3: "+response.errorBody()?.string(), )
//                        Toast.makeText(requireContext(),
//                            "some error occured"+response.errorBody()?.string(), Toast.LENGTH_SHORT)
//                            .show()
//                    }
//
//                }
//
//                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
////                    LoaderHelper.dissmissLoader()
//                    Log.e("TAG", "onFailure: "+t.localizedMessage, )
////                    Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT)
////                        .show()
//
//
//                    val mainbodyDao= RoomDb.getAppDatabase((requireContext()))?.mainbodydao()
//                    mainbodyDao?.addMainBody(
//                        mainbody(
//                            jobCardId,
//                            ConstantHelper.submitJobCardDataJSON.toString())
//                    )
//
//
//                    Toast.makeText(requireContext(),"successFull Added offline", Toast.LENGTH_SHORT)
//                        .show()
//                    Log.e("TAG", "submitmeter: offline ", )
//
//
//
//                    setJobId()
//                    LoaderHelper.dissmissLoader()
//
//                    ConstantHelper. submitJobCardDataJSON = JSONObject()
//                    ConstantHelper. Meters = JSONObject()
//                    ConstantHelper. TEST0123456 = JSONObject()
//                    ConstantHelper. Components = JSONObject()
//                    ConstantHelper. Feedback = JSONObject()
//                    ConstantHelper. Duration = JSONObject()
//
//                    ConstantHelper. photoList = mutableListOf()
//
//                    val bundle = Bundle()
//                    bundle.putString("data","from signature" )
//                    try {
//                        Navigation.findNavController(root).navigate(
//                            R.id.action_nav_signOff_to_nav_meteraudit,bundle
//
//                        )
//                    }catch (e:Exception){
//
//                    }
//
//                }
//
//            })
//
//        }
//
////
//
//    }





    fun setSignatureBoardScroll() {
        binding.inkBidvestTechSignature.setOnTouchListener(View.OnTouchListener { v, event ->
            val action = event.action
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    // Disallow ScrollView to intercept touch events.
                    binding.scrView.requestDisallowInterceptTouchEvent(true)
                    // Disable touch on transparent view
                    false
                }
                MotionEvent.ACTION_UP -> {
                    // Allow ScrollView to intercept touch events.
                    binding.scrView.requestDisallowInterceptTouchEvent(false)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    binding.scrView.requestDisallowInterceptTouchEvent(true)
                    false
                }
                else -> true
            }
        })

        binding.inkKareboTechSignature.setOnTouchListener(View.OnTouchListener { v, event ->
            val action = event.action
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    // Disallow ScrollView to intercept touch events.
                    binding.scrView.requestDisallowInterceptTouchEvent(true)
                    // Disable touch on transparent view
                    false
                }
                MotionEvent.ACTION_UP -> {
                    // Allow ScrollView to intercept touch events.
                    binding.scrView.requestDisallowInterceptTouchEvent(false)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    binding.scrView.requestDisallowInterceptTouchEvent(true)
                    false
                }
                else -> true
            }
        })



    }


     fun addInModel() {
         var SignoffSheet = JSONObject()
         var Feedback = JSONObject()
         var Signatures = JSONObject()

         Feedback.put("Is the Power Restored and Site in Order ?",binding.swPowerRestore.isChecked)
         Feedback.put("Are you happy with our staff ?",binding.swHappyWithStaff.isChecked)
         Feedback.put("Has the job been completed safely ?",binding.swCompleteSafely.isChecked)
         Feedback.put("All in order ?",binding.swInOrder.isChecked)
         SignoffSheet.put("Feedback",Feedback)

         var drawing: Bitmap
         drawing = binding.inkKareboTechSignature.getBitmap(resources.getColor(android.R.color.white))
         Signatures.put("Installation Engineer",getBitMapBase64(drawing))


         drawing = binding.inkBidvestTechSignature.getBitmap(resources.getColor(android.R.color.white))
         Signatures.put("Bidvest Technician onsite",getBitMapBase64(drawing))
         SignoffSheet.put("Signatures",Signatures)



         val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
         val formattedDate = sdf.format(Date())
         ConstantHelper.Duration.put("Value",formattedDate)

         ConstantHelper.submitJobCardDataJSON.put("Duration",ConstantHelper.Duration)

         ConstantHelper.Components.put("SignoffSheet",SignoffSheet)
         ConstantHelper.TEST0123456.put("Components",ConstantHelper.Components)


         ConstantHelper.Meters.put(ConstantHelper.SERIAL,ConstantHelper.TEST0123456)
         ConstantHelper.submitJobCardDataJSON.put("Meters",ConstantHelper.Meters)





    }

    fun getBitMapBase64(drawing:Bitmap): Any {
        var base64 = JSONObject.NULL
        try {

            base64 = ConstantHelper.bitmapToBase64(drawing)
        }
        catch (e:Exception){
        }

        return base64



    }





//    fun setJobId(){
//        var completeJobNumber=SharedPreferenceHelper.getInstance(requireContext()).getCompleteJobId()
//        var jobid=""
//
//        if(currentSelected!!.subJobCards==null ||currentSelected!!.subJobCards!!.isEmpty())
//        {
//            jobid= currentSelected!!.jobCardId.toString();
//        }else{
//            jobid=ConstantHelper.currentSelectdSubMeter.task!!.jobCardId.toString()
//        }
//
//
//        var completeJobNumberList: Array<String>?=null
//        if(completeJobNumber!="null"){
//            completeJobNumberList= GsonParser.gsonParser!!.fromJson(
//                completeJobNumber,
//                Array<String>::class.java
//            )
//        }
//
//        var list: MutableList<String> = mutableListOf()
//        if(completeJobNumberList!=null){
//            list= completeJobNumberList.toMutableList()
//        }
//        Log.e("TAG", "setJobId: "+list.toString(), )
//        list.add(jobid)
//
//
//        val JsonString: String = GsonParser.gsonParser!!.toJson(list)
//        SharedPreferenceHelper.getInstance(requireContext()).setCompleteJobId(JsonString)
//
//
////
////        var newList:MutableList<meterauditDataModel> = mutableListOf()
////
////        ConstantHelper.list.forEach {
////            if(!list.contains(it.jobCardId)){
////
////                newList.add(it)
////            }
////        }
////
////        ConstantHelper.list=newList
//
//    }
//




    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.drawer, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_jobcard -> {
                ConstantHelper. submitJobCardDataJSON = JSONObject()
                ConstantHelper. Meters = JSONObject()
                ConstantHelper. TEST0123456 = JSONObject()
                ConstantHelper. Components = JSONObject()
                ConstantHelper. Feedback = JSONObject()
                ConstantHelper. Duration = JSONObject()

                ConstantHelper. photoList = mutableListOf()

                ConstantHelper.SERIAL =  ""
                ConstantHelper. PropertyPictureUUID=""
                ConstantHelper. voltagepointUUID=""
                ConstantHelper. currentpointUUID=""
                ConstantHelper. picture1UUID=""
                ConstantHelper. picture2UUID=""
                ConstantHelper. picture3UUID=""
                ConstantHelper. picture4UUID=""
                ConstantHelper. picture5UUID=""

                ConstantHelper. voltagepointafterUUID=""
                ConstantHelper. currentpointafterUUID=""
                ConstantHelper. picture1afterUUID=""
                ConstantHelper. picture2afterUUID=""
                ConstantHelper. picture3afterUUID=""
                ConstantHelper. picture4afterUUID=""
                ConstantHelper. picture5afterUUID=""


                ConstantHelper. PhotoSmsConfigFileUUID=""
                ConstantHelper. PhotoGprsSignalFileUUID=""

                Navigation.findNavController(binding.root).navigate(
                    R.id.action_nav_signOff_to_nav_meteraudit
                )
                true
            }
            R.id.action_logout -> {

                SharedPreferenceHelper.getInstance(requireContext()).clearData()
                Navigation.findNavController(binding.root).navigate(
                    R.id.action_nav_signOff_to_nav_about
                )
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item);
    }

}