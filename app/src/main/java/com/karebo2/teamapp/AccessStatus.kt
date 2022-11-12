package com.karebo2.teamapp

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.karebo2.teamapp.Api.Api
import com.karebo2.teamapp.Api.ApiClient
import com.karebo2.teamapp.adapter.jobnumberAdapter
import com.karebo2.teamapp.databinding.FragmentAccessStatusBinding
import com.karebo2.teamapp.dataclass.CodeListDataClass
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
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class AccessStatus : Fragment() {


    private var _binding: FragmentAccessStatusBinding? = null
    private val binding get() = _binding!!
    var adapterSiteStatus: ArrayAdapter<String>? = null
    var list:MutableList<String> = mutableListOf()
    private var adapter: jobnumberAdapter? = null
    var data= meterauditDataModel()




    var currentSelected:meterauditDataModel?=null

    private val permission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,

        )
    lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        locationPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (!permissions.containsValue(false)) {
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
//                        mMap.isMyLocationEnabled = true
                    }
                }
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccessStatusBinding.inflate(
            inflater,container,false)
        val root: View = binding.root




        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {


        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestlocationPermission()
            }
        }
        Log.e("TAG", "location: ")
        LoaderHelper.showLoader(requireContext())
        val cancellationTokenSource = CancellationTokenSource()
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
                Log.e("Location", "location is found: $location")
                ConstantHelper.locationn=location
                LoaderHelper.dissmissLoader()

            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(),"Oops location failed to Fetch: $exception",Toast.LENGTH_SHORT).show()
                Log.e("Location", "Oops location failed with exception: $exception")
                val loc = Location("dummyprovider")
                currentSelected?.latitude?.let { loc.setLatitude(it) }
                currentSelected?.longitude?.let { loc.setLongitude(it) }
                ConstantHelper.locationn=loc
                LoaderHelper.dissmissLoader()
            }


        var data=  SharedPreferenceHelper.getInstance(requireContext()).getCurrentSelected()
        currentSelected = GsonParser.gsonParser!!.fromJson(data, meterauditDataModel::class.java)


        accessStatus()
        siteStatus()

        loadCompleteJobNumber()

        val args = arguments
        if(args!=null){
            val destination = args!!.getString("data")
            if(destination=="from signature"){
                binding.spAccessStatus.setSelection(1)
            }
        }


        binding.tvAddress.text = ConstantHelper.ADDRESS


        binding.btSubmit.setOnClickListener{

            if(binding.spAccessStatus.selectedItemPosition==2||
                binding.spAccessStatus.selectedItemPosition==3||
                binding.spAccessStatus.selectedItemPosition==4||
                binding.spAccessStatus.selectedItemPosition==5){
                addInModel()
            }



            showConfirmDialog(root)

        }



        binding.btNext.setOnClickListener{
//            val bundle = Bundle()
//            val JsonString: String =
//                GsonParser.gsonParser!!.toJson(data)
//            bundle.putString("data", JsonString)
            if(binding.spSiteStatus.selectedItemPosition==0){
                Toast.makeText(requireContext(),"select site status",Toast.LENGTH_SHORT ).show()
            }
            else{
                addInModel()

                try{
                    Navigation.findNavController(root).navigate(
                        R.id.action_nav_accessstatus_to_nav_siteStart
                    )
                }
                catch (e:Exception){

                }

            }

        }
        binding.spAccessStatus.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.e("TAG", "onItemSelected: "+binding.spAccessStatus.selectedItemPosition )
                if(binding.spAccessStatus.selectedItemPosition ==0){
                    binding.btNext.visibility=View.INVISIBLE
                    binding.btSubmit.visibility=View.INVISIBLE
                    binding.etName.visibility=View.INVISIBLE
                    binding.etNumber.visibility=View.INVISIBLE
                  Toast.makeText(requireContext(),"select access Status",Toast.LENGTH_LONG).show()
                }
                else if(binding.spAccessStatus.selectedItemPosition ==1){
                    binding.btNext.visibility=View.VISIBLE
                    binding.etName.visibility=View.INVISIBLE
                    binding.etNumber.visibility=View.INVISIBLE


                }

                else if(binding.spAccessStatus.selectedItemPosition ==3 ){
                    binding.btSubmit.visibility=View.VISIBLE
                    binding.btNext.visibility=View.INVISIBLE
                    binding.etName.visibility=View.INVISIBLE
                    binding.etNumber.visibility=View.INVISIBLE
                }

                else if(binding.spAccessStatus.selectedItemPosition ==2 ||
                    binding.spAccessStatus.selectedItemPosition ==4 ||
                    binding.spAccessStatus.selectedItemPosition ==5
                        ){
                    binding.btNext.visibility=View.INVISIBLE
                    binding.btSubmit.visibility=View.VISIBLE
                    binding.etName.visibility=View.VISIBLE
                    binding.etNumber.visibility=View.VISIBLE
                }


            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    return root

    }







    private fun requestlocationPermission() {

        when {
            hasPermissions2(requireContext(), *permission) -> {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {

                }
            }
            else -> {
                Toast.makeText(requireContext(), " Allow the  Permission", Toast.LENGTH_LONG).show()
                locationPermission()
            }
        }

    }
    private fun hasPermissions2(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    private fun locationPermission() {
        locationPermissionLauncher.launch(permission)
    }


    fun addInModel() {

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
        val formattedDate = sdf.format(Date())

        var Task = JSONObject()

        if(currentSelected!!.subJobCards==null ||currentSelected!!.subJobCards!!.isEmpty()){
            Task.put("JobCardId", currentSelected!!.jobCardId)
            Task.put("Vertices", JSONArray(currentSelected!!.vertices) )
            Task.put("SGCode", currentSelected!!.sgCode)
            Task.put("ParcelAddress",currentSelected!!.parcelAddress)
            Task.put("PostedOn",currentSelected!!.postedOn)
            Task.put("Latitude", currentSelected!!.latitude)
            Task.put("Longitude", currentSelected!!.longitude)
            Task.put("Project", currentSelected!!.project)
            Task.put("Team", currentSelected!!.team)
            Task.put("CardType", currentSelected!!.cardType)
            Task.put("Municipality", currentSelected!!.municipality)
            Task.put("SubJobCards", currentSelected!!.subJobCards)
            Task.put("SubJobCardIds", currentSelected!!.subJobCards)
            Task.put("HideInInbox", currentSelected!!.hideInInbox)

        }else{
            Task.put("JobCardId", ConstantHelper.currentSelectdSubMeter.task!!.jobCardId)
            Task.put("Vertices", JSONArray(ConstantHelper.currentSelectdSubMeter.task!!.vertices) )
            Task.put("SGCode", ConstantHelper.currentSelectdSubMeter.task!!.sgCode)
            Task.put("ParcelAddress",ConstantHelper.currentSelectdSubMeter.task!!.parcelAddress)
            Task.put("PostedOn",ConstantHelper.currentSelectdSubMeter.task!!.postedOn)
            Task.put("Latitude", ConstantHelper.currentSelectdSubMeter.task!!.latitude)
            Task.put("Longitude", ConstantHelper.currentSelectdSubMeter.task!!.longitude)
            Task.put("Project", ConstantHelper.currentSelectdSubMeter.task!!.project)
            Task.put("Team", ConstantHelper.currentSelectdSubMeter.task!!.team)
            Task.put("CardType", ConstantHelper.currentSelectdSubMeter.task!!.cardType)
            Task.put("Municipality", ConstantHelper.currentSelectdSubMeter.task!!.municipality)
            Task.put("SubJobCards", ConstantHelper.currentSelectdSubMeter.task!!.subJobCards)
            Task.put("SubJobCardIds", ConstantHelper.currentSelectdSubMeter.task!!.subJobCards)
            Task.put("HideInInbox", ConstantHelper.currentSelectdSubMeter.task!!.hideInInbox)

        }



        ConstantHelper.submitJobCardDataJSON.put("Task",Task)
        Log.e("TAG", "addInModel:timestemp " + formattedDate)
        ConstantHelper.Duration.put("Key",formattedDate)










        var Access = JSONObject()
         var Location = JSONObject()
         var ContactInfo = JSONObject()
         var Picture = JSONObject()


         Log.e("TAG", "addInModel:timestemp " + formattedDate)
         Access.put("Timestamp",formattedDate)

         Location.put("Latitude",ConstantHelper.locationn?.latitude)
         Location.put("Longitude",ConstantHelper.locationn?.longitude)
         Location.put("Accuracy",ConstantHelper.locationn?.accuracy)
         Location.put("Timestamp",formattedDate)

         Access.put("Location",Location)



         Access.put("Access",findAccess())
        if(currentSelected!!.subJobCards==null ||currentSelected!!.subJobCards!!.isEmpty())
            {
                Access.put("JobCardId", currentSelected!!.jobCardId)
                Access.put("Team", currentSelected!!.team)
            }else{
            Access.put("JobCardId", ConstantHelper.currentSelectdSubMeter.task!!.jobCardId)
            Access.put("Team", ConstantHelper.currentSelectdSubMeter.task!!.team)
        }



         ContactInfo.put("Full Name",binding.etName.text.toString())
         ContactInfo.put("Mobile Number",binding.etNumber.text.toString())
         Access.put("ContactInfo",ContactInfo)

         Picture.put("Key",0)
         Picture.put("Value", ConstantHelper.PropertyPictureUUID)
         Access.put("Picture",Picture)


         ConstantHelper.submitJobCardDataJSON.put("Access",Access)


         Log.e("json at access", ConstantHelper.submitJobCardDataJSON.toString())







     }

    fun findAccess():Int{
        var i=0
        if(binding.spAccessStatus.selectedItemPosition==1){
            i=3
        }else if(binding.spAccessStatus.selectedItemPosition==2){
            i=4
        }else if(binding.spAccessStatus.selectedItemPosition==3){
            i=0
        }else if(binding.spAccessStatus.selectedItemPosition==4){
            i=1
        }
        else if(binding.spAccessStatus.selectedItemPosition==5){
            i=2
        }

        return  i
    }

    fun loadCompleteJobNumber() {



        val keys = ConstantHelper.Meters.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            list.add(key)

        }

         adapter = jobnumberAdapter(
             list,
             requireActivity()
         )
         binding.rvJobnum.adapter = adapter
         binding.rvJobnum.adapter?.notifyDataSetChanged()
    }


    fun siteStatus(){
        var codelist=  SharedPreferenceHelper.getInstance(requireContext()).getCodeList()
        val data = GsonParser.gsonParser!!.fromJson(codelist, CodeListDataClass::class.java)
//        Log.e("TAG", "Question: " + data.Toolbox.toString())
        var finalData : MutableList <String> = data.SiteStatus as MutableList<String>
        finalData.add(0,"Site Status")
        adapterSiteStatus = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, finalData)
        adapterSiteStatus?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spSiteStatus.adapter = adapterSiteStatus
    }



    fun accessStatus(){
        var codelist=  SharedPreferenceHelper.getInstance(requireContext()).getCodeList()
        val data = GsonParser.gsonParser!!.fromJson(codelist, CodeListDataClass::class.java)
//        Log.e("TAG", "Question: " + data.Toolbox.toString())

        var finalData : MutableList <String> = data.SiteAccess as MutableList<String>
        finalData.add(0,"Site Access")
        adapterSiteStatus = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, finalData)
        adapterSiteStatus?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spAccessStatus.adapter = adapterSiteStatus
    }




fun showConfirmDialog(root:View){
    var alert: AlertDialog? = null
    val builder = AlertDialog.Builder(requireContext())
    val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val v = inflater.inflate(R.layout.dialog_submitmeter, null)
    builder.setView(v)
    builder.setCancelable(false)
    alert = builder.create()

    val okButton = v.findViewById<Button>(R.id.bt_submit)
    val cancelButton = v.findViewById<Button>(R.id.bt_cancel)
    val title = v.findViewById<TextView>(R.id.tv_title_submit_dialog)

    title.setText("Do You want to submit Job Card?")

    okButton.setOnClickListener{
        SharedPreferenceHelper.getInstance(requireContext()).setJobNumber("0")
        alert.dismiss()
        submitMeter(root)
//        addAllPhoto(root)
    }

    cancelButton.setOnClickListener{
        alert.dismiss()
    }

    alert?.show()


}

    fun submitMeter(root:View){


        LoaderHelper.showLoader(requireContext())

        // Log.e("TAG", "submitMeter: "+ConstantHelper.submitMeterDataJSON.toString() )


        var jobCardId=""

        if(currentSelected!!.subJobCards==null ||currentSelected!!.subJobCards!!.isEmpty())
        {
            jobCardId= currentSelected!!.jobCardId.toString();
        }else{
            jobCardId=ConstantHelper.currentSelectdSubMeter.task!!.jobCardId
        }




        if(NetworkUtils.isConnected==false){
            val mainbodyDao= RoomDb.getAppDatabase((requireContext()))?.mainbodydao()
            mainbodyDao?.addMainBody(
                mainbody(
                    jobCardId,
                    ConstantHelper.submitJobCardDataJSON.toString())
            )


            Toast.makeText(requireContext(),"successFull Added offline", Toast.LENGTH_SHORT)
                .show()
            Log.e("TAG", "submitmeter: offline ")



            // setJobId()
            LoaderHelper.dissmissLoader()

            ConstantHelper. submitJobCardDataJSON = JSONObject()
            ConstantHelper. Meters = JSONObject()
            ConstantHelper. TEST0123456 = JSONObject()
            ConstantHelper. Components = JSONObject()
            ConstantHelper. Feedback = JSONObject()
            ConstantHelper. Duration = JSONObject()

            ConstantHelper. photoList = mutableListOf()

            val bundle = Bundle()
            bundle.putString("data","from signature" )
            try {
                Navigation.findNavController(root).navigate(
                    R.id.action_nav_accessstatus_to_nav_meteraudit,bundle

                )
            }catch (e:Exception){

            }


        }
        else{
            val client = ApiClient()
            val api = client.getClient()?.create(Api::class.java)
            val call = api?.submitMeter2(ConstantHelper.submitJobCardDataJSON.toString())
            call?.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    if(response.isSuccessful){
                        var statuscode=response.code()
                        Log.e("TAG", "Statuscode of Photo " + statuscode)

                        if(statuscode==200){


                            //setJobId()
                            LoaderHelper.dissmissLoader()
                            ConstantHelper. submitJobCardDataJSON = JSONObject()
                            ConstantHelper. Meters = JSONObject()
                            ConstantHelper. TEST0123456 = JSONObject()
                            ConstantHelper. Components = JSONObject()
                            ConstantHelper. Feedback = JSONObject()
                            ConstantHelper. Duration = JSONObject()

                            ConstantHelper. photoList = mutableListOf()



                            Toast.makeText(requireContext(),"successFull Added", Toast.LENGTH_SHORT)
                                .show()
                            Log.e("TAG", "submitmeter: " + response.body()?.string())


                            val bundle = Bundle()
                            bundle.putString("data","from signature" )
                            try{
                                Navigation.findNavController(root).navigate(
                                    R.id.action_nav_accessstatus_to_nav_meteraudit,bundle

                                )
                            }catch (e:Exception){

                            }



                        }
                        else    {
                            LoaderHelper.dissmissLoader()
                            Log.e("TAG", "submitmeter2: " + response.body()?.string())
                            Toast.makeText(requireContext(),"some error occured"+ response.body()?.string(), Toast.LENGTH_SHORT)
                                .show()
                        }


                    }
                    else{
//                    Log.e("TAG", "AddToolBox :"+response.body()?.string(), )
//                    Log.e("TAG", "AddToolBox :"+response.errorBody()?.string(), )
                        LoaderHelper.dissmissLoader()
                        Log.e("TAG", "submitmeter3: " + response.errorBody()?.string())
                        Toast.makeText(requireContext(),
                            "some error occured"+response.errorBody()?.string(), Toast.LENGTH_SHORT)
                            .show()
                    }

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    LoaderHelper.dissmissLoader()
                    Log.e("TAG", "onFailure: " + t.localizedMessage)
                    Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT)
                        .show()
                }

            })

        }

//

    }



    fun addAllPhoto(root: View) {

        LoaderHelper.showLoader(requireContext())

        if (NetworkUtils.isConnected == false) {

            val photobodyDao = RoomDb.getAppDatabase((requireContext()))?.photobodydao()

            ConstantHelper.photoList.forEach {

//            requests.add( api?.addPhoto64(it.uuid,it.bodyy)!!)
                photobodyDao?.addphotobody(photobody(it.uuid, it.bodyy))
                Log.e("TAG", "addAllPhoto uuid: ${it.uuid}")
//            Log.e("TAG", "addAllPhoto body: ${it.bodyy}", )
            }

            LoaderHelper.dissmissLoader()
            activity?.runOnUiThread {
                submitMeter(root)

            }

        } else {

            val client = ApiClient()
            val api = client.getClient()?.create(Api::class.java)

            val requests = mutableListOf<Observable<ResponseBody>>()

            ConstantHelper.photoList.forEach {

                requests.add(api?.addPhoto64(it.uuid, it.bodyy)!!)
                Log.e("TAG", "addAllPhoto uuid: ${it.uuid}")
//               Log.e("TAG", "addAllPhoto body: ${it.bodyy}", )
            }


            Observable.merge(requests)
                .take(requests.size.toLong())
                // executed when the channel is closed or disposed
                .doFinally {
                    Log.e("TAG", "addAllPhoto final: ")

                    ConstantHelper.photoList = mutableListOf()
                    LoaderHelper.dissmissLoader()
                    activity?.runOnUiThread {
                        submitMeter(root)

                    }
                }
        }
    }
//
//    val okButton = v.findViewById<Button>(R.id.bt_submit)
//    val cancelButton = v.findViewById<Button>(R.id.bt_cancel)
//    val title = v.findViewById<TextView>(R.id.tv_title_submit_dialog)
//
//    title.setText("Do You want to submit Job Card?")
//
//    okButton.setOnClickListener{
//        SharedPreferenceHelper.getInstance(requireContext()).setJobNumber("0")
//        alert.dismiss()
////        submitMeter(root)
////        addAllPhoto(root)
//    }
//
//    cancelButton.setOnClickListener{
//        alert.dismiss()
//    }
//
//    alert?.show()
//
//
//}
//







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
                    R.id.action_nav_accessstatus_to_nav_meteraudit
                )
                true
            }
            R.id.action_logout -> {

                SharedPreferenceHelper.getInstance(requireContext()).clearData()
                Navigation.findNavController(binding.root).navigate(
                    R.id.action_nav_accessstatus_to_nav_about
                )
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item);
    }
}