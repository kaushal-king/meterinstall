package com.karebo2.teamapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.karebo2.teamapp.databinding.FragmentMeterInstallationBinding
import com.karebo2.teamapp.dataclass.CodeListDataClass
import com.karebo2.teamapp.utils.LoaderHelper
import com.karebo2.teamapp.sharedpreference.SharedPreferenceHelper
import com.karebo2.teamapp.utils.ConstantHelper
import com.karebo2.teamapp.utils.GsonParser
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MeterInstallation : Fragment() {

    private var _binding: FragmentMeterInstallationBinding? = null
    private val binding get() = _binding!!
    private var mPhotoFile: File? = null
    private var PhotoSimCardFile: File? = null

    var spinnerManufacture: ArrayAdapter<String>? = null
    var SpinnerModel: ArrayAdapter<String>? = null

    var  locationn : Location? =ConstantHelper.locationn
     var photoname: String=""

//    private val permission = arrayOf(
//        Manifest.permission.ACCESS_FINE_LOCATION,
//        Manifest.permission.ACCESS_COARSE_LOCATION,
//
//        )
//    lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>
//    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
//        locationPermissionLauncher =
//            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
//                if (!permissions.containsValue(false)) {
//                    if (ActivityCompat.checkSelfPermission(
//                            requireContext(),
//                            Manifest.permission.ACCESS_FINE_LOCATION
//                        ) == PackageManager.PERMISSION_GRANTED
//                    ) {
////                        mMap.isMyLocationEnabled = true
//                    }
//                }
//            }
//
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMeterInstallationBinding.inflate(
            inflater,container,false)
        val root: View = binding.root

        if(ConstantHelper.JOB_TYPE=="2"){
            showSimSwap()
        }else{
            hideSimSwap()
        }

        loadManufecture()

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
//
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//
//
//        }
//        else{
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestlocationPermission()
//            }
//        }
//        Log.e("TAG", "location: ")
//        LoaderHelper.showLoader(requireContext())
//        val cancellationTokenSource = CancellationTokenSource()
//        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
//            .addOnSuccessListener { location ->
//                Log.e("Location", "location is found: $location")
//                locationn=location
//                LoaderHelper.dissmissLoader()
//
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(requireContext(),"Oops location failed to Fetch: $exception",Toast.LENGTH_SHORT).show()
//                Log.e("Location", "Oops location failed with exception: $exception")
//                LoaderHelper.dissmissLoader()
//            }


//
        binding.btnSimCard.setOnClickListener{
            photoname = ConstantHelper.SIM_CARD
            selectImageType(requireContext())
        }

        binding.spMeterManufacturer.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                loadModel()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        })




        binding.btNext.setOnClickListener{
            if(ConstantHelper.JOB_TYPE=="2"){
                if(binding.etModemSerialNumber.text.isEmpty()|| binding.etModemSerialNumber.equals(null)){
                    Toast.makeText(requireContext(),"Enter Modem serial number",Toast.LENGTH_SHORT).show()
                }
                else if(binding.etSerialNumber.text.isEmpty()|| binding.etSerialNumber.equals(null)){
                    Toast.makeText(requireContext(),"Enter serial number",Toast.LENGTH_SHORT).show()
                }
                else if(binding.etMeterYear.text.isEmpty()|| binding.etMeterYear.equals(null)){
                    Toast.makeText(requireContext(),"Enter meter year",Toast.LENGTH_SHORT).show()
                }
                else if(binding.spSupplyVoltage.selectedItemPosition==0){
                    Toast.makeText(requireContext(),"Select supply voltage",Toast.LENGTH_SHORT).show()

                }else if(binding.spPhase.selectedItemPosition==0){
                    Toast.makeText(requireContext(),"Select phase",Toast.LENGTH_SHORT).show()

                }else if(binding.spWire.selectedItemPosition==0){
                    Toast.makeText(requireContext(),"Select wire",Toast.LENGTH_SHORT).show()

                }else if(binding.spCtsUsed.selectedItemPosition==0){
                    Toast.makeText(requireContext(),"Select cts used",Toast.LENGTH_SHORT).show()

                }else if(binding.spCtType.selectedItemPosition==0){
                    Toast.makeText(requireContext(),"Select CT type",Toast.LENGTH_SHORT).show()

                }else if(binding.spCtRatioMeter.selectedItemPosition==0){
                    Toast.makeText(requireContext(),"Select CT ratio configured",Toast.LENGTH_SHORT).show()

                }else if(binding.spCtRatioSite.selectedItemPosition==0){
                    Toast.makeText(requireContext(),"Select CT ratio on site",Toast.LENGTH_SHORT).show()

                }else if(binding.spVtRatio.selectedItemPosition==0){
                    Toast.makeText(requireContext(),"Select vt ratio",Toast.LENGTH_SHORT).show()

                }else if(binding.spCircuitBreakerConnected.selectedItemPosition==0){
                    Toast.makeText(requireContext(),"Select new circuit breaker connected",Toast.LENGTH_SHORT).show()

                }else if(binding.spCircuitBreakerSize.selectedItemPosition==0){
                    Toast.makeText(requireContext(),"Select circuit breaker size",Toast.LENGTH_SHORT).show()

                }
                else if(binding.etModemManufacturer.text.isEmpty()|| binding.etModemManufacturer.equals(null)){
                    Toast.makeText(requireContext(),"Enter Modem Manufacturer",Toast.LENGTH_SHORT).show()
                }else if(binding.etModemType.text.isEmpty()|| binding.etModemType.equals(null)){
                    Toast.makeText(requireContext(),"Enter Modem Type",Toast.LENGTH_SHORT).show()
                }else if(binding.etModemConfigure.text.isEmpty()|| binding.etModemConfigure.equals(null)){
                    Toast.makeText(requireContext(),"Enter Modem configured successfully",Toast.LENGTH_SHORT).show()
                }else if(binding.etNewSimNo.text.isEmpty()|| binding.etNewSimNo.equals(null)){
                    Toast.makeText(requireContext(),"Enter New Sim Card Number No",Toast.LENGTH_SHORT).show()
                }else if(binding.etSimMsisdn.text.isEmpty()|| binding.etSimMsisdn.equals(null)){
                    Toast.makeText(requireContext(),"Enter SIM MSISDN",Toast.LENGTH_SHORT).show()
                }else if(binding.etIpPort.text.isEmpty()|| binding.etIpPort.equals(null)){
                    Toast.makeText(requireContext(),"Enter IP Port",Toast.LENGTH_SHORT).show()
                }else if(binding.etTcpPort.text.isEmpty()|| binding.etTcpPort.equals(null)){
                    Toast.makeText(requireContext(),"Enter TCP Port",Toast.LENGTH_SHORT).show()
                }
                else if(PhotoSimCardFile==null){
                    Toast.makeText(requireContext(),"select simcard photo",Toast.LENGTH_SHORT).show()
                }
                else{
                    addInModel()
                    Navigation.findNavController(root).navigate(
                        R.id.action_nav_meterinstallation_to_nav_commissioningReport,
                    )
                }

            }else{
                if(binding.etSerialNumber.text.isEmpty()|| binding.etSerialNumber.equals(null)){
                    Toast.makeText(requireContext(),"Enter serial number",Toast.LENGTH_SHORT).show()
                }
                else if(binding.etMeterYear.text.isEmpty()|| binding.etMeterYear.equals(null)){
                    Toast.makeText(requireContext(),"Enter meter year",Toast.LENGTH_SHORT).show()
                }
                else if(binding.spSupplyVoltage.selectedItemPosition==0){
                    Toast.makeText(requireContext(),"Select supply voltage",Toast.LENGTH_SHORT).show()

                }else if(binding.spPhase.selectedItemPosition==0){
                    Toast.makeText(requireContext(),"Select phase",Toast.LENGTH_SHORT).show()

                }else if(binding.spWire.selectedItemPosition==0){
                    Toast.makeText(requireContext(),"Select wire",Toast.LENGTH_SHORT).show()

                }else if(binding.spCtsUsed.selectedItemPosition==0){
                    Toast.makeText(requireContext(),"Select cts used",Toast.LENGTH_SHORT).show()

                }else if(binding.spCtType.selectedItemPosition==0){
                    Toast.makeText(requireContext(),"Select CT type",Toast.LENGTH_SHORT).show()

                }else if(binding.spCtRatioMeter.selectedItemPosition==0){
                    Toast.makeText(requireContext(),"Select CT ratio configured",Toast.LENGTH_SHORT).show()

                }else if(binding.spCtRatioSite.selectedItemPosition==0){
                    Toast.makeText(requireContext(),"Select CT ratio on site",Toast.LENGTH_SHORT).show()

                }else if(binding.spVtRatio.selectedItemPosition==0){
                    Toast.makeText(requireContext(),"Select vt ratio",Toast.LENGTH_SHORT).show()

                }else if(binding.spCircuitBreakerConnected.selectedItemPosition==0){
                    Toast.makeText(requireContext(),"Select new circuit breaker connected",Toast.LENGTH_SHORT).show()

                }else if(binding.spCircuitBreakerSize.selectedItemPosition==0){
                    Toast.makeText(requireContext(),"Select circuit breaker size",Toast.LENGTH_SHORT).show()

                }else{
                    addInModel()
                    Navigation.findNavController(root).navigate(
                        R.id.action_nav_meterinstallation_to_nav_commissioningReport,
                    )
                }







            }

        }







        return root
    }

//    private fun requestlocationPermission() {
//
//        when {
//            hasPermissions2(requireContext(), *permission) -> {
//                if (ActivityCompat.checkSelfPermission(
//                        requireContext(),
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                    ) == PackageManager.PERMISSION_GRANTED
//                ) {
//
//                }
//            }
//            else -> {
//                Toast.makeText(requireContext(), " Allow the  Permission", Toast.LENGTH_LONG).show()
//                locationPermission()
//            }
//        }
//
//    }
//    private fun hasPermissions2(context: Context, vararg permissions: String): Boolean =
//        permissions.all {
//            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
//        }
//    private fun locationPermission() {
//        locationPermissionLauncher.launch(permission)
//    }

     fun addInModel() {
         var InstallationDetails = JSONObject()
         var Modem = JSONObject()
         var code=findMeterCode()
//         var TamperPictures = JSONArray()
         ConstantHelper.SERIAL=binding.etSerialNumber.text.toString()

         ConstantHelper.TEST0123456.put("Code", code)
         ConstantHelper.TEST0123456.put("Serial",binding.etSerialNumber.text.toString())

         InstallationDetails.put("Voltage",binding.spSupplyVoltage.selectedItem.toString())
         InstallationDetails.put("PhaseCount",binding.spPhase.selectedItem.toString())
         InstallationDetails.put("WireCount",binding.spWire.selectedItem.toString())
         InstallationDetails.put("CTsUsed",binding.spCtsUsed.selectedItem.toString())
         InstallationDetails.put("CTRatioOnMeter",binding.spCtRatioMeter.selectedItem.toString())
         InstallationDetails.put("CTRatioAtSite",binding.spCtRatioSite.selectedItem.toString())
         InstallationDetails.put("VTRatio",binding.spVtRatio.selectedItem.toString())

         if(binding.spCircuitBreakerConnected.selectedItem.toString()=="yes"){
             InstallationDetails.put("NewCircuitBreaker",true)
         }else{
             InstallationDetails.put("NewCircuitBreaker",false)
         }

         InstallationDetails.put("CircuitBreakerSize",binding.spCircuitBreakerSize.selectedItem.toString())

        Modem.put("Modem Serial Number",binding.etModemSerialNumber.text.toString())
        Modem.put("Modem Manufacturer",binding.etModemManufacturer.text.toString())
        Modem.put("Modem Type",binding.etModemType.text.toString())
        Modem.put("Modem configured successfully",binding.etModemConfigure.text.toString())
        Modem.put("New Sim Card Number No",binding.etNewSimNo.text.toString())
        Modem.put("SIM MSISDN",binding.etSimMsisdn.text.toString())
        Modem.put("IP Port",binding.etIpPort.text.toString())
        Modem.put("TCP Port",binding.etTcpPort.text.toString())

         var base64Image=""
         if(PhotoSimCardFile!=null){
             base64Image = ConstantHelper.getBase64(PhotoSimCardFile!!)
         }
        Modem.put("SIM Photo",base64Image)

         InstallationDetails.put("Modem",Modem)



         ConstantHelper.Components.put("InstallationDetails",InstallationDetails)

         ConstantHelper.TEST0123456.put("Components",ConstantHelper.Components)



         Log.e("json at Details", ConstantHelper.Components.toString())
         Log.e("json at TEST0123456", ConstantHelper.TEST0123456.toString())

    }


    private var activityResultLauncher: ActivityResultLauncher<Intent> =

        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.e("Photo", mPhotoFile.toString())

                if (photoname == ConstantHelper.SIM_CARD) {
                    loadSimCardImage()

                }
                //                    else if(temperCount==2){
//                        loadTampringImage2()
//                    }else if(temperCount==3){
//                        loadTampringImage3()
//                    }



            }


        }




    private var activityCameraResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (!permissions.containsValue(false)) {
            dispatchTakePictureIntent()
        }
//        permissions.entries.forEach {
//            if(!it.value)
//            {
//                Log.e("permission", "${it.key} permission not grant", )
//            }
//          //  Log.e("DEBUG", "${it.key} = ${it.value}")
//        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("imagePath",mPhotoFile.toString())
        outState.putString("photoname",photoname)
        Log.e("TAG", "onSaveInstanceState: ")
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        setImageFile(savedInstanceState?.get("imagePath").toString())
        setPhotonamee(savedInstanceState?.get("photoname").toString())
        Log.e("TAG", "onRestoreInstanceState: " + savedInstanceState?.get("imagePath"))
        Log.e("TAG", "onRestoreInstanceState photoname: " + savedInstanceState?.get("photoname"))
        super.onViewStateRestored(savedInstanceState)
    }



    fun getImageFile(): File {
        return mPhotoFile!!
    }
    fun setImageFile(path:String) {
        mPhotoFile= File(path)
    }

    fun setPhotonamee(namee: String) {
        photoname = namee
    }


    private fun selectImageType(context: Context) {

        requestStoragePermission()

    }



    private fun requestStoragePermission() {
        val permission = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
        )
        when {

            hasPermissions(requireContext(), *permission) -> {
                Log.e("permission", "camera has permission ")

                    dispatchTakePictureIntent()


            }
            else -> {
                Toast.makeText(requireContext(), " Allow the Storage Permission", Toast.LENGTH_LONG).show()
                activityCameraResultLauncher.launch(permission)
            }
        }

    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            Log.e("permission", "hasPermissions: $permissions")
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }


    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                ex.printStackTrace()
                Toast.makeText(requireContext(), ex.localizedMessage, Toast.LENGTH_SHORT).show()
                Log.d("file not", ex.localizedMessage)
                // Error occurred while creating the File
            }
            if (photoFile != null) {

                val photoURI = FileProvider.getUriForFile(
                    requireContext(), BuildConfig.APPLICATION_ID + ".provider",
                    photoFile
                )
                mPhotoFile = photoFile
                Log.e("TAG", mPhotoFile.toString())
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                activityResultLauncher.launch(takePictureIntent)

            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File =
            File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "KareboForm")
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            Log.e("TAG", "failed to create directory")
        }
        val image = File(storageDir.path + File.separator + imageFileName)
        Log.e("TAG", image.toString())
        return image
    }







    fun showSimCardImg() {
        binding.ivSimCard.visibility = View.VISIBLE
    }


    fun loadSimCardImage() {
        PhotoSimCardFile=File(mPhotoFile?.path!!)
        //addPhoto(6)
        Log.e("TAG", "loadSimCardImage: " + PhotoSimCardFile)
        Glide.with(requireContext())
            .load(PhotoSimCardFile)
            .into(binding.ivSimCard)
        showSimCardImg()
    }




//    fun addPhoto(type:Int) {
//
//
//        LoaderHelper.showLoader(requireContext())
//
//
//
//
//        val newUUID = UUID.randomUUID()
//        val newUUID2 = UUID.randomUUID()
//        val newUUID3 = UUID.randomUUID()
//
////        val client = ApiClient()
////        val api = client.getClient()?.create(Api::class.java)
//
//
//        var id=""
//        if(temperCount==1){
//            id=newUUID.toString()
//        }else if(temperCount==2){
//            id=newUUID2.toString()
//        }else   if(temperCount==3){
//            id=newUUID3.toString()
//        }
//
//
//
//        var base64Image: String=""
//        if(type==6){
//
//
//            if(temperCount==1){
//                if(PhototampringFile!=null){
//                    base64Image = ConstantHelper.getBase64(PhototampringFile!!)
//                }
//            }else if(temperCount==2){
//                if(PhototampringFile2!=null){
//                    base64Image = ConstantHelper.getBase64(PhototampringFile2!!)
//                }
//            }else   if(temperCount==3){
//                if(PhototampringFile3!=null){
//                    base64Image = ConstantHelper.getBase64(PhototampringFile3!!)
//                }
//            }
//
//
//
//
//        }



//        Log.e("TAG", "addAuditPhoto64: "+base64Image, )

//        var body = JSONObject()
//        var geoLocation = JSONObject()
//        body.put("image",base64Image)
//        body.put("type",type)
//
//        geoLocation.put("latitude",locationn?.latitude)
//        geoLocation.put("longitude",locationn?.longitude)
//        geoLocation.put("accuracy",locationn?.accuracy)
//        var sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
//        var formattedDate = sdf.format(Date())
//        geoLocation.put("timestamp",formattedDate)
//        body.put("geoLocation",geoLocation)
//
//        body.put("score",0)
//
//        Log.e("TAG", "photo add body: " + body.toString())
//
//
//        ConstantHelper.photoList.add( photoUploadDataClass( id,body.toString()) )
//        LoaderHelper.dissmissLoader()
//        if(type==6){
//
//            if(temperCount==1){
//                ConstantHelper.TamperedWiresUUID=newUUID.toString()
//            }else if(temperCount==2){
//                ConstantHelper.TamperedWires2UUID=newUUID2.toString()
//            }else   if(temperCount==3){
//                ConstantHelper.TamperedWires3UUID=newUUID3.toString()
//            }
//
//
//
//        }


                /////      not useable below code


//
//        val call = api?.addPhoto64(id,body.toString())
//        call?.enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(
//                call: Call<ResponseBody>,
//                response: Response<ResponseBody>
//            ) {
//
//                if(response.isSuccessful){
//                    var statuscode=response.code()
//                    Log.e("TAG", "Statuscode of Photo " + statuscode)
//
//                    if(statuscode==200){
//
//                        LoaderHelper.dissmissLoader()
//                        if(type==6){
//
//                            if(temperCount==1){
//                                ConstantHelper.TamperedWiresUUID=newUUID.toString()
//                            }else if(temperCount==2){
//                                ConstantHelper.TamperedWires2UUID=newUUID2.toString()
//                            }else   if(temperCount==3){
//                                ConstantHelper.TamperedWires3UUID=newUUID3.toString()
//                            }
//
//
//
//                        }
//
//                        Log.e("TAG", "Audotphoto: " + response.body()?.string())
//
//                    }
//                    else    {
//                        LoaderHelper.dissmissLoader()
//
//                        Toast.makeText(requireContext(), response.body()?.string(), Toast.LENGTH_SHORT)
//                            .show()
//                    }
//
//
//                }
//                else{
////                    Log.e("TAG", "AddToolBox :"+response.body()?.string(), )
////                    Log.e("TAG", "AddToolBox :"+response.errorBody()?.string(), )
//                    LoaderHelper.dissmissLoader()
//
//                    Toast.makeText(requireContext(),
//                        response.errorBody()?.string(), Toast.LENGTH_SHORT)
//                        .show()
//                }
//
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                LoaderHelper.dissmissLoader()
//
//                Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT)
//                    .show()
//            }
//
//        })

//    }

    fun hideSimSwap(){
        binding.llSimSwap.visibility=View.GONE

    }

    fun  showSimSwap(){
        binding.llSimSwap.visibility=View.VISIBLE
    }


    fun loadManufecture() {
        var codelist = SharedPreferenceHelper.getInstance(requireContext()).getCodeList()
        val data = GsonParser.gsonParser!!.fromJson(codelist, CodeListDataClass::class.java)
        var manufecture: MutableList<String> = mutableListOf()
        data.ElectricityMeter.forEach {
            if (!manufecture.contains(it.Manufacturer)) {
                manufecture.add(it.Manufacturer)
            }

        }

        Log.e("TAG", "Question: " + manufecture.toString())



        spinnerManufacture = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            manufecture
        )

        spinnerManufacture?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spMeterManufacturer.adapter = spinnerManufacture
    }

    fun loadModel() {
        var codelist = SharedPreferenceHelper.getInstance(requireContext()).getCodeList()
        val data = GsonParser.gsonParser!!.fromJson(codelist, CodeListDataClass::class.java)
        var model: MutableList<String> = mutableListOf()
        data.ElectricityMeter.forEach {

            if (it.Manufacturer == binding.spMeterManufacturer.selectedItem) {
                if (!model.contains(it.Model)) {
                    model.add(it.Model)
                }

            }

        }

        Log.e("TAG", "Question: " + model.toString())

        SpinnerModel =
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, model)
        SpinnerModel?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spMeterModel.adapter = SpinnerModel
    }


    fun findMeterCode() :String{
        var codelist = SharedPreferenceHelper.getInstance(requireContext()).getCodeList()
        val data = GsonParser.gsonParser!!.fromJson(codelist, CodeListDataClass::class.java)
        var code:String=""
        data.ElectricityMeter.forEach {
            if(it.Manufacturer==binding.spMeterManufacturer.selectedItem
                && it.Model==binding.spMeterModel.selectedItem){
                Log.e("TAG", "findMeterCode: "+it.Code)
                code=it.Code
            }


        }

        return code
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
                    R.id.action_nav_meterinstallation_to_nav_meteraudit
                )
                true
            }
//            R.id.action_logout -> {
//
//                SharedPreferenceHelper.getInstance(requireContext()).clearData()
//                Navigation.findNavController(binding.root).navigate(
//                    R.id.action_nav_meterinstallation_to_nav_about
//                )
//                true
//            }

            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item);
    }



}