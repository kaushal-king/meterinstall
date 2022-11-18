package com.karebo2.teamapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.karebo2.teamapp.Api.Api
import com.karebo2.teamapp.Api.ApiClient
import com.karebo2.teamapp.databinding.FragmentMeterLocationBinding
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


class MeterLocation : Fragment() , OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    //,GoogleMap.OnMarkerDragListener

    private var _binding: FragmentMeterLocationBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    lateinit var latlong: meterauditDataModel
    lateinit var finalLatLng: LatLng
    var outsideMap=false

    lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>
    var polygonList: MutableList<LatLng> = mutableListOf()
    private val permission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,

        )
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    var currentSelected:meterauditDataModel?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true);

        locationPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (!permissions.containsValue(false)) {
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        mMap.isMyLocationEnabled = true
                    }
                }
            }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMeterLocationBinding.inflate(
            inflater,container,false)
        val root: View = binding.root

        var data=  SharedPreferenceHelper.getInstance(requireContext()).getCurrentSelected()
        currentSelected = GsonParser.gsonParser!!.fromJson(data, meterauditDataModel::class.java)



        latlong= currentSelected!!
        binding.tvAddress.text = ConstantHelper.ADDRESS







        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        binding.btNext.setOnClickListener{
//            val bundle = Bundle()
//            val JsonString: String =
//                GsonParser.gsonParser!!.toJson(latlong)
//            bundle.putString("data", JsonString)
           // addInModel()
            addAllPhoto(root)

            submitMeter(root)


        }

        //scrollFunction()






        return root
    }

//     fun addInModel() {
////
////         ConstantHelper.meterModelJson.put("Latitude",finalLatLng.latitude)
////         ConstantHelper.meterModelJson.put("Longitude",finalLatLng.longitude)
////         ConstantHelper.meterModelJson.put("Id", ConstantHelper.currentSelectd.jobCardId)
//
////
////         var MeterLocation = JSONObject()
////         var Location = JSONObject()
////         Location.put("Latitude",finalLatLng.latitude)
////         Location.put("Longitude",finalLatLng.longitude)
////         Location.put("Accuracy",0)
////         var sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
////         var formattedDate = sdf.format(Date())
////         Location.put("Timestamp",formattedDate)
////
////         MeterLocation.put("Location",Location)
////
////         if(outsideMap==false){
////             MeterLocation.put("Type",0)
////         }
////         else{
////             if(binding.spMapMarker.selectedItemPosition==0){
////                 MeterLocation.put("Type",1)
////             }else{
////                 MeterLocation.put("Type",2)
////             }
////         }
////
////         MeterLocation.put("Description",binding.etMapDescription.text.toString())
////         ConstantHelper.Components.put("MeterLocation",MeterLocation)
//////         ConstantHelper.meterModelJson.put("Components",ConstantHelper.Components)
////
////         Log.e("json at location", ConstantHelper.Components.toString())
////         Log.e("json at location", ConstantHelper.meterModelJson.toString())
//
//
//    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.clear()
//        fusedLocationClient.lastLocation
//            .addOnSuccessListener { location: Location? ->
//                updateMapLocation(location)
//            }
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestlocationPermission()
            }
        }

        finalLatLng= LatLng(latlong!!.latitude as Double,latlong!!.longitude as Double)
        mMap.addMarker(
            MarkerOptions().position(LatLng(latlong!!.latitude as Double, latlong!!.longitude as Double))
                .title(latlong!!.latitude.toString()+" "+latlong!!.longitude)
//                .draggable(true)// below line is use to add custom marker on our map.

        )?.showInfoWindow()

//
        var polyOption=PolylineOptions()

        for (coordinate in latlong!!.vertices!!) {

            Log.e("TAG", "onMapReady: "+coordinate )
            var splitt : List<String> =coordinate!!.toString().split(" ")
            Log.e("TAG", "onMapReady: "+splitt )
           var lat=splitt[0].toDouble()
           var long= splitt[1].toDouble()
            Log.e("TAG", "onMapReady: " + lat + "   " + long)
            polygonList.add(LatLng(long,lat))
            polyOption.add(LatLng(long,lat))

        }

        val color: Int = Color.argb(255, 236, 4,4 )
        polyOption.clickable(false)
        polyOption.color(resources.getColor(R.color.red))



        val polyline1 = googleMap.addPolyline(
            polyOption
        )


       updateMapLocationLatLong( LatLng(latlong!!.latitude as Double, latlong!!.longitude as Double) )

        mMap.setOnMarkerClickListener(this)
//        mMap.setOnMarkerDragListener(this)
    }

    private fun requestlocationPermission() {

        when {
            hasPermissions(requireContext(), *permission) -> {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    mMap.isMyLocationEnabled = true
                }
            }
            else -> {
                Toast.makeText(requireContext(), " Allow the  Permission", Toast.LENGTH_LONG).show()
                locationPermission()
            }
        }

    }
    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    private fun locationPermission() {


        locationPermissionLauncher.launch(permission)
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        return false
    }

    private fun updateMapLocation(location: Location?) {
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    location?.latitude ?: 0.0,
                    location?.longitude ?: 0.0
                ), 18.0f
            )
        )

    }


    private fun updateMapLocationLatLong(location: LatLng) {
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    location?.latitude ?: 0.0,
                    location?.longitude ?: 0.0
                ), 18.0f
            )
        )

    }

//    override fun onMarkerDrag(arg0: Marker) {
//        Log.e("TAG", "onMarkerDrag...")
//
//    }

//    override fun onMarkerDragEnd(arg0: Marker) {
//        Log.e(
//            "TAG",
//            "onMarkerDragStart..." + arg0.position.latitude + "..." + arg0.position.longitude
//        )
//        finalLatLng= LatLng(arg0.position.latitude,arg0.position.longitude)
//        if (!PolyUtil.containsLocation(LatLng(arg0.position.latitude,arg0.position.longitude), polygonList, false)) {
//            showField()
//        }
//        else{
//            hideField()
//        }
//
//        }
//    override fun onMarkerDragStart(arg0: Marker) {
//        Log.e(
//                    "TAG",
//                    "onMarkerDragEnd..." + arg0.position.latitude + "..." + arg0.position.longitude
//                )
//    }
//
//
//    fun showField(){
//        outsideMap=true
//        binding.linearLayout.visibility=View.VISIBLE
//
//        binding.etMapDescription.visibility=View.VISIBLE
//    }
//
//    fun hideField(){
//        outsideMap=false
//        binding.linearLayout.visibility=View.GONE
//
//        binding.etMapDescription.visibility=View.GONE
//    }



    fun addAllPhoto(root: View){

//        LoaderHelper.showLoader(requireContext())
        Log.e("TAG", "addPhoto size: "+ConstantHelper.photoList.size.toString(), )


        if(NetworkUtils.isConnected==false){

            val photobodyDao= RoomDb.getAppDatabase((requireContext()))?.photobodydao()

            ConstantHelper.photoList.forEach {

                Log.e("TAG", "addAllPhoto: "+it.uuid, )
//            requests.add( api?.addPhoto64(it.uuid,it.bodyy)!!)
                photobodyDao?.addphotobody(photobody(it.uuid,it.bodyy))
                Log.e("TAG", "addAllPhoto uuid: ${it.uuid}", )
//            Log.e("TAG", "addAllPhoto body: ${it.bodyy}", )
            }
            //submitMeter(root)

//            LoaderHelper.dissmissLoader()


        }else{

            val client = ApiClient()
            val api = client.getClient()?.create(Api::class.java)

            val requests =  mutableListOf<Observable<ResponseBody>>()

            ConstantHelper.photoList.forEach {

                requests.add( api?.addPhoto64(it.uuid,it.bodyy)!!)
                Log.e("TAG", "addAllPhoto uuid: ${it.uuid}", )

//               Log.e("TAG", "addAllPhoto body: ${it.bodyy}", )

            }




            Observable.merge(requests)
                .take(requests.size.toLong())
                // executed when the channel is closed or disposed
                .doFinally {
                    Log.e("TAG", "addAllPhoto final: ", )

                    ConstantHelper. photoList = mutableListOf()
//                    LoaderHelper.dissmissLoader()

//
                }
                .subscribeOn(Schedulers.io())
                // it's a question if you want to observe these on main thread, depends on context of your application
                .subscribe(
                    { ResponseBody ->
                        // here you get both the deviceId and the responseBody
                        Log.e("TAG", "addAllPhoto responce: "+ResponseBody.string(), )



                        if (ResponseBody == null ) {
                            Log.e("TAG", "addAllPhoto responce: "+ ResponseBody?.string(), )

                            // request for this deviceId failed, handle it
                        }
                    },
                    { error ->
                        Log.e("TAG", "Throwable: " + error)
                    }
                )


        }




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
            Log.e("TAG", "submitmeter: offline ", )



            setJobId()
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
                    R.id.action_nav_meterlocation_to_nav_meteraudit,bundle

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


                            setJobId()
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
                            Log.e("TAG", "submitmeter: "+response.body()?.string(), )


                            val bundle = Bundle()
                            bundle.putString("data","from signature" )
                            try {
                                Navigation.findNavController(root).navigate(
                                    R.id.action_nav_meterlocation_to_nav_meteraudit,bundle

                                )
                            }catch (e:Exception){

                            }


                        }
                        else    {
                            LoaderHelper.dissmissLoader()
                            Log.e("TAG", "submitmeter2: "+response.body()?.string(), )
                            Toast.makeText(requireContext(),"some error occured"+ response.body()?.string(), Toast.LENGTH_SHORT)
                                .show()
                        }


                    }
                    else{
//                    Log.e("TAG", "AddToolBox :"+response.body()?.string(), )
//                    Log.e("TAG", "AddToolBox :"+response.errorBody()?.string(), )
                        LoaderHelper.dissmissLoader()
                        Log.e("TAG", "submitmeter3: "+response.errorBody()?.string(), )
                        Toast.makeText(requireContext(),
                            "some error occured"+response.errorBody()?.string(), Toast.LENGTH_SHORT)
                            .show()
                    }

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                    LoaderHelper.dissmissLoader()
                    Log.e("TAG", "onFailure: "+t.localizedMessage, )
//                    Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT)
//                        .show()


                    val mainbodyDao= RoomDb.getAppDatabase((requireContext()))?.mainbodydao()
                    mainbodyDao?.addMainBody(
                        mainbody(
                            jobCardId,
                            ConstantHelper.submitJobCardDataJSON.toString())
                    )


                    Toast.makeText(requireContext(),"successFull Added offline", Toast.LENGTH_SHORT)
                        .show()
                    Log.e("TAG", "submitmeter: offline ", )



                    setJobId()
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
                            R.id.action_nav_meterlocation_to_nav_meteraudit,bundle

                        )
                    }catch (e:Exception){

                    }

                }

            })

        }

//

    }


    fun setJobId(){
        var completeJobNumber=SharedPreferenceHelper.getInstance(requireContext()).getCompleteJobId()
        var jobid=""

        if(currentSelected!!.subJobCards==null ||currentSelected!!.subJobCards!!.isEmpty())
        {
            jobid= currentSelected!!.jobCardId.toString();
        }else{
            jobid=ConstantHelper.currentSelectdSubMeter.task!!.jobCardId.toString()
        }


        var completeJobNumberList: Array<String>?=null
        if(completeJobNumber!="null"){
            completeJobNumberList= GsonParser.gsonParser!!.fromJson(
                completeJobNumber,
                Array<String>::class.java
            )
        }

        var list: MutableList<String> = mutableListOf()
        if(completeJobNumberList!=null){
            list= completeJobNumberList.toMutableList()
        }
        Log.e("TAG", "setJobId: "+list.toString(), )
        list.add(jobid)


        val JsonString: String = GsonParser.gsonParser!!.toJson(list)
        SharedPreferenceHelper.getInstance(requireContext()).setCompleteJobId(JsonString)


//
//        var newList:MutableList<meterauditDataModel> = mutableListOf()
//
//        ConstantHelper.list.forEach {
//            if(!list.contains(it.jobCardId)){
//
//                newList.add(it)
//            }
//        }
//
//        ConstantHelper.list=newList

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
                    R.id.action_nav_meterlocation_to_nav_meteraudit
                )
                true
            }
//            R.id.action_logout -> {
//
//                SharedPreferenceHelper.getInstance(requireContext()).clearData()
//                Navigation.findNavController(binding.root).navigate(
//                    R.id.action_nav_meterlocation_to_nav_about
//                )
//                true
//            }

            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item);
    }


}