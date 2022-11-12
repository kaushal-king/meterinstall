package com.karebo2.teamapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
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


import com.karebo2.teamapp.databinding.FragmentSiteStartBinding
import com.karebo2.teamapp.dataclass.CodeListDataClass
import com.karebo2.teamapp.dataclass.meterData.meterauditDataModel
import com.karebo2.teamapp.dataclass.photoUploadDataClass
import com.karebo2.teamapp.utils.LoaderHelper
import com.karebo2.teamapp.sharedpreference.SharedPreferenceHelper
import com.karebo2.teamapp.utils.ConstantHelper
import com.karebo2.teamapp.utils.GsonParser
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class SiteStart : Fragment() {

    private var _binding: FragmentSiteStartBinding? = null
    private val binding get() = _binding!!
    private var mPhotoFile: File? = null
        private var PhotoVoltageConnectionFile: File? = null
    private var PhotoCurrentConnectionFile: File? = null
    private var PhotoBeforeInstallFile1: File? = null
    private var PhotoBeforeInstallFile2: File? = null
    private var PhotoBeforeInstallFile3: File? = null
    private var PhotoBeforeInstallFile4: File? = null
    private var PhotoBeforeInstallFile5: File? = null
//    private var PhototokenssFile: File? = null
    var adapterMeterStatus: ArrayAdapter<String>? = null
    var timePickerDialog: TimePickerDialog? = null
    var slectTimeField:String=""
    var calendar = Calendar.getInstance()
    var  locationn : Location? =ConstantHelper.locationn

//    private val permission = arrayOf(
//        Manifest.permission.ACCESS_FINE_LOCATION,
//        Manifest.permission.ACCESS_COARSE_LOCATION,
//
//        )
//    lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>
//    private lateinit var fusedLocationClient: FusedLocationProviderClient

    var currentSelected: meterauditDataModel?=null
    private var photoname: String=""




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



    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSiteStartBinding.inflate(
            inflater, container, false
        )
        val root: View = binding.root
        hideEveryThing()

        var data=  SharedPreferenceHelper.getInstance(requireContext()).getCurrentSelected()
        currentSelected = GsonParser.gsonParser!!.fromJson(data, meterauditDataModel::class.java)


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
//
//        LoaderHelper.showLoader(requireContext())
//
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




        binding.etSiteAddress.setText(ConstantHelper.ADDRESS)
        binding.etGpsLocation.setText(currentSelected!!.latitude.toString()+" "+currentSelected!!.longitude)
        binding.etSiteName.setText(currentSelected!!.dictionary!!.Site_Name)
        binding.etSiteManagerName.setText(currentSelected!!.dictionary!!.Contact_Manager_Name)
        binding.etSiteManagerContact.setText(currentSelected!!.dictionary!!.Contact_Manager_Contact_Number)
        binding.etSiteManagerEmail.setText(currentSelected!!.dictionary!!.Contact_Manager_Email)
        binding.etRefNo.setText(currentSelected!!.dictionary!!.Reference_Number)
        binding.etRefExpire.setText(currentSelected!!.dictionary!!.Reference_Expiry)








//        loadManufecture()
//        meterStatus()
//        setApiData()

//        binding.etMeterSerialNo.setText(ConstantHelper.SERIAL)



        binding.etArriveOnSite.setOnClickListener {
            Log.e("TAG", "etarrive: ", )
            binding.etArriveOnSite.setText("")
            slectTimeField="arrive"
            openTimePickerDialog(false)

        }

        binding.etGainAccess.setOnClickListener {
            Log.e("TAG", "gain: ", )
            binding.etGainAccess.setText("")
            slectTimeField="gain"
            openTimePickerDialog(false)

        }



        binding.btNext.setOnClickListener {

            if(binding.etGpsLocation.text.isEmpty()|| binding.etGpsLocation.equals(null)){
                Toast.makeText(requireContext(),"Enter "+binding.etGpsLocation.hint.toString(),Toast.LENGTH_SHORT).show()
            }

            else if(binding.etSiteName.text.isEmpty()|| binding.etSiteName.equals(null)){
                Toast.makeText(requireContext(),"Enter "+binding.etSiteName.hint.toString(),Toast.LENGTH_SHORT).show()
            }
            else if(binding.etSiteAddress.text.isEmpty()|| binding.etSiteAddress.equals(null)){
                Toast.makeText(requireContext(),"Enter "+binding.etSiteAddress.hint.toString(),Toast.LENGTH_SHORT).show()
            }else if(binding.etRefNo.text.isEmpty()|| binding.etRefNo.equals(null)){
                Toast.makeText(requireContext(),"Enter "+binding.etRefNo.hint.toString(),Toast.LENGTH_SHORT).show()
            }else if(binding.etRefExpire.text.isEmpty()|| binding.etRefExpire.equals(null)){
                Toast.makeText(requireContext(),"Enter "+binding.etRefExpire.hint.toString(),Toast.LENGTH_SHORT).show()
            }
            else if(binding.etSiteManagerName.text.isEmpty()|| binding.etSiteManagerName.equals(null)){
                Toast.makeText(requireContext(),"Enter "+binding.etSiteManagerName.hint.toString(),Toast.LENGTH_SHORT).show()
            }
            else if(binding.etSiteManagerContact.text.isEmpty()|| binding.etSiteManagerContact.equals(null)){
                Toast.makeText(requireContext(),"Enter "+binding.etSiteManagerContact.hint.toString(),Toast.LENGTH_SHORT).show()
            }
            else if(binding.etSiteManagerEmail.text.isEmpty()|| binding.etSiteManagerEmail.equals(null)){
                Toast.makeText(requireContext(),"Enter "+binding.etSiteManagerEmail.hint.toString(),Toast.LENGTH_SHORT).show()
            }
            else if(binding.etArriveOnSite.text.isEmpty()|| binding.etArriveOnSite.equals(null)){
                Toast.makeText(requireContext(),"Enter "+binding.etArriveOnSite.hint.toString(),Toast.LENGTH_SHORT).show()
            }
            else if(binding.etGainAccess.text.isEmpty()|| binding.etGainAccess.equals(null)){
                Toast.makeText(requireContext(),"Enter "+binding.etGainAccess.hint.toString(),Toast.LENGTH_SHORT).show()
            }
            else if(binding.etStandingTime.text.isEmpty()|| binding.etStandingTime.equals(null)){
                Toast.makeText(requireContext(),"Enter "+binding.etStandingTime.hint.toString(),Toast.LENGTH_SHORT).show()
            }

            else if(binding.spReasonDelay.selectedItemPosition==0){
                Toast.makeText(requireContext(),"Select Reason Delay",Toast.LENGTH_SHORT).show()

            }
            else if(binding.spJobType.selectedItemPosition==0){
                Toast.makeText(requireContext(),"select Job type ",Toast.LENGTH_SHORT).show()
            }
            else  if(PhotoVoltageConnectionFile==null){

                    Toast.makeText(requireContext(),"Add Voltage Connection Photo ",Toast.LENGTH_SHORT).show()
                }
            else  if(PhotoCurrentConnectionFile==null){
                Toast.makeText(requireContext(),"AAdd Current Connection Photo ",Toast.LENGTH_SHORT).show()
            }
            else{
                addInModel()
//                ConstantHelper.SERIAL=binding.etMeterSerialNo.text.toString()
                Navigation.findNavController(root).navigate(
                    R.id.action_nav_siteStart_to_nav_meterinstallation,
                )
            }



        }

        binding.btImageCurrentConnection.setOnClickListener {
            photoname = ConstantHelper.CURRENT_CONNECTION
            selectImageType(requireActivity())
        }

        binding.btImageVoltageConnection.setOnClickListener {
            photoname = ConstantHelper.VOLTAGE_CONNECTION
            selectImageType(requireActivity())
        }

        binding.btnBeforeInstall1.setOnClickListener {
            photoname = ConstantHelper.BEFORE_INSTALL1
            selectImageType(requireActivity())
        }

        binding.btnBeforeInstall2.setOnClickListener {
            photoname = ConstantHelper.BEFORE_INSTALL2
            selectImageType(requireActivity())
        }

        binding.btnBeforeInstall3.setOnClickListener {
            photoname = ConstantHelper.BEFORE_INSTALL3
            selectImageType(requireActivity())
        }

        binding.btnBeforeInstall4.setOnClickListener {
            photoname = ConstantHelper.BEFORE_INSTALL4
            selectImageType(requireActivity())
        }

        binding.btnBeforeInstall5.setOnClickListener {
            photoname = ConstantHelper.BEFORE_INSTALL5
            selectImageType(requireActivity())
        }

//        binding.btTokenImgSs.setOnClickListener {
//            photoname = "tokenss"
//            selectImageType(requireContext())
//        }

        binding.btAssistantElectricianSignatureClear.setOnClickListener{
            binding.inkAssistantElectricianSignature.clear()
        }
        binding.btBidvestTechnicianSignatureClear.setOnClickListener{
            binding.inkBidvestTechnicianSignature.clear()
        }
        binding.btInstallationElectricianSignatureClear.setOnClickListener{
            binding.inkInstallationElectricianSignature.clear()
        }

        binding.spJobType.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
              ConstantHelper.JOB_TYPE= binding.spJobType.selectedItemPosition.toString()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        })



//        binding.spKrn.setOnItemSelectedListener(object :
//            AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parentView: AdapterView<*>?,
//                selectedItemView: View?,
//                position: Int,
//                id: Long
//            ) {
//
//                if(binding.spKrn.selectedItemPosition ==0){
//                    Toast.makeText(requireContext(),"select krn",Toast.LENGTH_LONG).show()
//                    hideSignature()
//                    hideTokenssImg()
//                }
//                else if(binding.spKrn.selectedItemPosition==1){
//                    showSignature()
//                    showTokenssbtnImg()
//
//
//                }else if(binding.spKrn.selectedItemPosition==2){
//                    hideSignature()
//                    hideTokenssImg()
//                }
//
//            }
//
//            override fun onNothingSelected(parentView: AdapterView<*>?) {
//                hideSignature()
//            }
//        })
//
//
        binding.swRefWithBidvest.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
               showEveryThing()
            } else {
               hideEveryThing()
            }
        })
//


        setSignatureBoardScroll()



        return root
    }

    private fun openTimePickerDialog(is24r: Boolean) {

        timePickerDialog = TimePickerDialog(
            requireContext(),
            onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE), is24r
        )
        timePickerDialog?.setTitle("Set  Time")
        timePickerDialog?.show()

    }

    var onTimeSetListener =
        OnTimeSetListener { view, hourOfDay, minute ->

               if(slectTimeField=="gain")
               {
                   binding.etGainAccess.setText("$hourOfDay:$minute")

               }
               else if(slectTimeField=="arrive"){
                   binding.etArriveOnSite.setText("$hourOfDay:$minute")
               }




            timePickerDialog?.show()
        }


     fun setSignatureBoardScroll() {
         binding.inkAssistantElectricianSignature.setOnTouchListener(View.OnTouchListener { v, event ->
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

         binding.inkBidvestTechnicianSignature.setOnTouchListener(View.OnTouchListener { v, event ->
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

         binding.inkInstallationElectricianSignature.setOnTouchListener(View.OnTouchListener { v, event ->
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
           var PreInstallation = JSONObject()
           var InitialData = JSONObject()
           var InitialDataValidation = JSONObject()
           var WarmupTimeline = JSONObject()
           var SiteRiskAssessment = JSONObject()
           var BeforePictures = JSONObject()
           var Signatures = JSONObject()




        if(currentSelected!!.subJobCards==null ||currentSelected!!.subJobCards!!.isEmpty()) {
            ConstantHelper.TEST0123456.put("Code", "")
            ConstantHelper.TEST0123456.put("Latitude", locationn!!.latitude)
            ConstantHelper.TEST0123456.put("Longitude", locationn!!.longitude)
            ConstantHelper.TEST0123456.put("Serial","")
            ConstantHelper.TEST0123456.put("Id", currentSelected!!.jobCardId)
        }else{
            ConstantHelper.TEST0123456.put("Code", "")
            ConstantHelper.TEST0123456.put("Latitude", locationn!!.latitude)
            ConstantHelper.TEST0123456.put("Longitude", locationn!!.longitude)
            ConstantHelper.TEST0123456.put("Serial", "")
            ConstantHelper.TEST0123456.put("Id", ConstantHelper.currentSelectdSubMeter.task!!.jobCardId)
        }

        PreInstallation.put("OpenedReferencewithBidvest",binding.swRefWithBidvest.isChecked)

        InitialData.put("Site GPS Location as per Ref ",binding.etGpsLocation.text)
        InitialData.put("Site Name",binding.etSiteName.text)
        InitialData.put("Site Address",binding.etSiteAddress.text)
        InitialData.put("Ref No",binding.etRefNo.text)
        InitialData.put("Date Ref Expires",binding.etRefExpire.text)
        InitialData.put("Site Manager Name",binding.etSiteManagerName.text)
        InitialData.put("Site Manager Contact Number",binding.etSiteManagerContact.text)
        InitialData.put("Site Manager Email",binding.etSiteManagerEmail.text)

        PreInstallation.put("InitialData",InitialData)

        InitialDataValidation.put("Key",binding.swDetailsCorrect.isChecked)
        InitialDataValidation.put("Value",binding.etComment.text)

        PreInstallation.put("InitialDataValidation",InitialDataValidation)

        WarmupTimeline.put("Arrived on Site",binding.etArriveOnSite.text)
        WarmupTimeline.put("Gained Access to Work",binding.etGainAccess.text)
        PreInstallation.put("WarmupTimeline",WarmupTimeline)

        PreInstallation.put("ReasonForDelay",binding.spReasonDelay.selectedItem.toString())


        SiteRiskAssessment.put("Have all the live working hazards been identified ?",binding.swAllHazardsIdentified.isChecked)
        SiteRiskAssessment.put("Is the isolation points identified for the current connection ?",binding.swIsolationPathCurrent.isChecked)
        SiteRiskAssessment.put("Have we identified what could potentially go wrong in each step of the installation and mitigated this risk ?",binding.swIdentifyGoWrong.isChecked)
        SiteRiskAssessment.put("Are all team members wearing PPE ?",binding.swWearingPpe.isChecked)
        SiteRiskAssessment.put("Have we identified an emergency exit route if there is an issue ?",binding.swEmergencyExit.isChecked)
        SiteRiskAssessment.put("Do we have emergency contact numbers available if there is an issue ?",binding.swEmergencyContact.isChecked)
        SiteRiskAssessment.put("Is the Site Generally Safe to Work On ?",binding.swSiteSafe.isChecked)
        SiteRiskAssessment.put("Have we worked out the safe process to connect the voltage connections ?",binding.swSafeConnectVoltage.isChecked)
        SiteRiskAssessment.put("Do we need to switch off ?",binding.swSwitchOff.isChecked)
        PreInstallation.put("SiteRiskAssessment",SiteRiskAssessment)

        BeforePictures.put("Voltage Connection Point",ConstantHelper.voltagepointUUID)
        BeforePictures.put("Current Connection Point",ConstantHelper.currentpointUUID)
        BeforePictures.put("Picture 1",ConstantHelper.picture1UUID)
        BeforePictures.put("Picture 2",ConstantHelper.picture2UUID)
        BeforePictures.put("Picture 3",ConstantHelper.picture3UUID)
        BeforePictures.put("Picture 4",ConstantHelper.picture4UUID)
        BeforePictures.put("Picture 5",ConstantHelper.picture5UUID)
        PreInstallation.put("BeforePictures",BeforePictures)




        var drawing: Bitmap
         drawing = binding.inkInstallationElectricianSignature.getBitmap(resources.getColor(android.R.color.white))
        Signatures.put("Installation Engineer",getBitMapBase64(drawing))

         drawing = binding.inkAssistantElectricianSignature.getBitmap(resources.getColor(android.R.color.white))
        Signatures.put("Assistant to Electrician",getBitMapBase64(drawing))

         drawing = binding.inkBidvestTechnicianSignature.getBitmap(resources.getColor(android.R.color.white))
        Signatures.put("Bidvest Technician onsite",getBitMapBase64(drawing))

        PreInstallation.put("Signatures",Signatures)

        ConstantHelper.Components.put("PreInstallation",PreInstallation)
        ConstantHelper.TEST0123456.put("Components",ConstantHelper.Components)



         Log.e("json at site Start", ConstantHelper.Components.toString())
         Log.e("json at TEST0123456", ConstantHelper.TEST0123456.toString())
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

//    fun setApiData() {
//
////
//        var data= SharedPreferenceHelper.getInstance(requireContext()).getMeterData()
//        var jsonData=  GsonParser.gsonParser!!.fromJson(data, MeterDataModel::class.java)
//            if(jsonData!=null){
//
//
//                // pre paid switch set
//                Log.e("TAG", "setApiData: " + jsonData.MeterType)
//                if(jsonData?.MeterType=="Prepaid"){
//                    binding.swPrepraid.isChecked = true
//                }
//
//                //set krn spinner
//
//                Log.e("TAG", "setApiData: "+jsonData?.KRN, )
//                if(jsonData?.KRN==1){
//                    binding.spKrn.setSelection(1)
//                }else if(jsonData?.KRN==2){
//                    binding.spKrn.setSelection(2)
//                }else{
//                    binding.spKrn.setSelection(0)
//                }
//
//
//
//
//                //set model and menufecture
//                var codelist = SharedPreferenceHelper.getInstance(requireContext()).getCodeList()
//                val codeListdata = GsonParser.gsonParser!!.fromJson(codelist, CodeListDataClass::class.java)
//                codeListdata.ElectricityMeter.forEach {
//                    //Log.e("TAG", "setApiData Model1: ${it.Model}", )
//                    if(it.Code==jsonData?.Model){
//                        Log.e("TAG", "setApiData Model: ${it.Manufacturer}")
//                        binding.spManufacturer.setSelection(spinnerManufacture!!.getPosition(it.Manufacturer))
//                    }
//                }
//            }
//
//
//
//
//     }


    private var activityResultLauncher: ActivityResultLauncher<Intent> =

        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.e("Photo", mPhotoFile.toString())
                Log.e("TAG", "photoname $photoname")
                if (photoname == ConstantHelper.CURRENT_CONNECTION) {
                    loadCurrentConnectionImage()

                } else if (photoname == ConstantHelper.VOLTAGE_CONNECTION) {
                    loadVoltageConnectionImage()

                } else if (photoname == ConstantHelper.BEFORE_INSTALL1) {

                    loadBeforeInstallImages1()

                }else if (photoname == ConstantHelper.BEFORE_INSTALL2) {

                    loadBeforeInstallImages2()

                }else if (photoname == ConstantHelper.BEFORE_INSTALL3) {

                    loadBeforeInstallImages3()

                }else if (photoname == ConstantHelper.BEFORE_INSTALL4) {

                    loadBeforeInstallImages4()

                }else if (photoname == ConstantHelper.BEFORE_INSTALL5) {

                    loadBeforeInstallImages5()

                }





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
        outState.putString("imagePath", mPhotoFile.toString())
        outState.putString("photoname",photoname)
        Log.e("TAG", "onSaveInstanceState: ")
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        setImageFile(savedInstanceState?.get("imagePath").toString())
        setPhotoname(savedInstanceState?.get("photoname").toString())
        Log.e("TAG", "onRestoreInstanceState: " + savedInstanceState?.get("imagePath"))
        Log.e("TAG", "onRestoreInstanceState photoname: " + savedInstanceState?.get("photoname"))
        super.onViewStateRestored(savedInstanceState)
    }


    fun getImageFile(): File {
        return mPhotoFile!!
    }

    fun setImageFile(path: String) {
        mPhotoFile = File(path)
    }

    fun setPhotoname(name: String) {
        photoname = name
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
                if (photoname == "tokenss") {
                    Log.e("TAG", "selectImageType: Gallary")
                    dispatchGalleryIntent()
                } else {
                    Log.e("TAG", "selectImageType: Camera")
                    dispatchTakePictureIntent()
                }

            }
            else -> {
                Toast.makeText(requireContext(), " Allow the Storage Permission", Toast.LENGTH_LONG)
                    .show()
                activityCameraResultLauncher.launch(permission)
            }
        }

    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            Log.e("permission", "hasPermissions: $permissions")
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }


    private var activityResultLauncherGallary: ActivityResultLauncher<Intent> =

        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                var result = result.data
                var selectedImage: Uri = result?.data!!
                mPhotoFile = File(getRealPathFromUri(selectedImage))

                val sourcePath = requireContext().getExternalFilesDir(null).toString()
                Log.e("Photo2", mPhotoFile.toString())

                try {
                    copyFileStream(
                        File(sourcePath + "/" + mPhotoFile!!.name),
                        selectedImage,
                        requireContext()
                    )
                }catch (e:Exception){

                }

                Log.e("Photo2", mPhotoFile.toString())
                //loadTokenImagess()

            }


        }


    @Throws(IOException::class)
    private fun copyFileStream(dest: File, uri: Uri, context: Context) {
        var `is`: InputStream? = null
        var os: OutputStream? = null
        try {
            `is` = context.contentResolver.openInputStream(uri)
            os = FileOutputStream(dest)
            val buffer = ByteArray(1024)
            var length: Int
            while (`is`!!.read(buffer).also { length = it } > 0) {
                os.write(buffer, 0, length)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            `is`!!.close()
            os!!.close()
            Log.e("TAG", "$dest uri")
            mPhotoFile = dest

        }
    }

    private fun dispatchGalleryIntent() {

        val pickPhoto = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        activityResultLauncherGallary.launch(pickPhoto)
//        startActivityForResult(
//            pickPhoto,
//            REQUEST_GALLERY_PHOTO
//        )
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
            File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "KareboForm"
            )
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            Log.e("TAG", "failed to create directory")
        }
        val image = File(storageDir.path + File.separator + imageFileName)
        Log.e("TAG", image.toString())
        return image
    }


    fun getRealPathFromUri(contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = requireContext().contentResolver.query(contentUri!!, proj, null, null, null)
            assert(cursor != null)
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } finally {
            cursor?.close()
        }
    }



    //hideEverything
    fun hideEveryThing(){
       binding.llAllWidget.visibility=View.GONE
    }

    fun showEveryThing(){
        binding.llAllWidget.visibility=View.VISIBLE

    }

    //InstallationElectricianSignature
    fun  showInstallationElectricianSignature(){
        binding.inkInstallationElectricianSignature.visibility=View.VISIBLE
        binding.tvInstallationElectricianSignature.visibility=View.VISIBLE
        binding.btInstallationElectricianSignatureClear.visibility=View.VISIBLE
    }
    fun  hideInstallationElectricianSignature(){
        binding.inkInstallationElectricianSignature.visibility=View.GONE
        binding.tvInstallationElectricianSignature.visibility=View.GONE
        binding.btInstallationElectricianSignatureClear.visibility=View.GONE
    }

    //AssistantElectricianSignature
    fun  showAssistantElectricianSignature(){
        binding.inkAssistantElectricianSignature.visibility=View.VISIBLE
        binding.tvAssistantElectricianSignature.visibility=View.VISIBLE
        binding.btAssistantElectricianSignatureClear.visibility=View.VISIBLE
    }
    fun  hideAssistantElectricianSignature(){
        binding.inkAssistantElectricianSignature.visibility=View.GONE
        binding.tvAssistantElectricianSignature.visibility=View.GONE
        binding.btAssistantElectricianSignatureClear.visibility=View.GONE
    }

    //BidvestTechnicianSignature
    fun  showBidvestTechnicianSignature(){
        binding.inkBidvestTechnicianSignature.visibility=View.VISIBLE
        binding.tvBidvestTechnicianSignature.visibility=View.VISIBLE
        binding.btBidvestTechnicianSignatureClear.visibility=View.VISIBLE
    }
    fun  hideBidvestTechnicianSignature(){
        binding.inkBidvestTechnicianSignature.visibility=View.GONE
        binding.tvBidvestTechnicianSignature.visibility=View.GONE
        binding.btBidvestTechnicianSignatureClear.visibility=View.GONE
    }


    fun showCurrentImg() {
        binding.ivCurrentConnection.visibility = View.VISIBLE
    }

    fun showVoltageImg() {
        binding.ivVoltageConnection.visibility = View.VISIBLE
    }


    fun loadCurrentConnectionImage() {

        PhotoCurrentConnectionFile = File(mPhotoFile?.path!!)
        addPhoto(6)
        Log.e("TAG", "Load Current Connection File: $PhotoCurrentConnectionFile")
        Glide.with(requireContext())
            .load(PhotoCurrentConnectionFile)
            .into(binding.ivCurrentConnection)
        showCurrentImg()

    }

    fun loadVoltageConnectionImage() {

        PhotoVoltageConnectionFile = File(mPhotoFile?.path!!)
       addPhoto(7)
        Log.e("TAG", "Load Voltage Connection File: $PhotoVoltageConnectionFile")
        Glide.with(requireContext())
            .load(PhotoVoltageConnectionFile)
            .into(binding.ivVoltageConnection)
        showVoltageImg()

    }




    fun showBeforeInstallImage() {

        if(PhotoBeforeInstallFile1!=null || PhotoBeforeInstallFile2!=null||PhotoBeforeInstallFile3!=null){
            binding.llBeforeInstallImages.visibility=View.VISIBLE
            if(PhotoBeforeInstallFile1!=null ){
                binding.ivBeforeInsatll1.visibility=View.VISIBLE
            }
            if(PhotoBeforeInstallFile2!=null ){
                binding.ivBeforeInsatll2.visibility=View.VISIBLE
            }
            if(PhotoBeforeInstallFile3!=null ){
                binding.ivBeforeInsatll3.visibility=View.VISIBLE
            }
        }


        if(PhotoBeforeInstallFile4!=null || PhotoBeforeInstallFile5!=null){
            binding.llBeforeInstallImages2.visibility=View.VISIBLE
            if(PhotoBeforeInstallFile4!=null ){
                binding.ivBeforeInsatll4.visibility=View.VISIBLE
            }
            if(PhotoBeforeInstallFile5!=null ){
                binding.ivBeforeInsatll5.visibility=View.VISIBLE
            }

        }


    }

    fun loadBeforeInstallImages1() {

        PhotoBeforeInstallFile1 = File(mPhotoFile?.path!!)
        addPhoto(1)
        Log.e("TAG", "Load Voltage Connection File: $PhotoBeforeInstallFile1")
        Glide.with(requireContext())
            .load(PhotoBeforeInstallFile1)
            .into(binding.ivBeforeInsatll1)
        showBeforeInstallImage()

    }
    fun loadBeforeInstallImages2() {

        PhotoBeforeInstallFile2 = File(mPhotoFile?.path!!)
        addPhoto(2)
        Log.e("TAG", "Load Voltage Connection File: $PhotoBeforeInstallFile2")
        Glide.with(requireContext())
            .load(PhotoBeforeInstallFile2)
            .into(binding.ivBeforeInsatll2)
        showBeforeInstallImage()

    }
    fun loadBeforeInstallImages3() {

        PhotoBeforeInstallFile3 = File(mPhotoFile?.path!!)
        addPhoto(3)
        Log.e("TAG", "Load Voltage Connection File: $PhotoBeforeInstallFile3")
        Glide.with(requireContext())
            .load(PhotoBeforeInstallFile3)
            .into(binding.ivBeforeInsatll3)
        showBeforeInstallImage()

    }

    fun loadBeforeInstallImages4() {

        PhotoBeforeInstallFile4 = File(mPhotoFile?.path!!)
        addPhoto(4)
        Log.e("TAG", "Load Voltage Connection File: $PhotoBeforeInstallFile4")
        Glide.with(requireContext())
            .load(PhotoBeforeInstallFile4)
            .into(binding.ivBeforeInsatll4)
        showBeforeInstallImage()

    }

    fun loadBeforeInstallImages5() {

        PhotoBeforeInstallFile5 = File(mPhotoFile?.path!!)
        addPhoto(5)
        Log.e("TAG", "Load Voltage Connection File: $PhotoBeforeInstallFile5")
        Glide.with(requireContext())
            .load(PhotoBeforeInstallFile5)
            .into(binding.ivBeforeInsatll5)
        showBeforeInstallImage()

    }



//    fun loadManufecture() {
//        var codelist = SharedPreferenceHelper.getInstance(requireContext()).getCodeList()
//        val data = GsonParser.gsonParser!!.fromJson(codelist, CodeListDataClass::class.java)
//        var manufecture: MutableList<String> = mutableListOf()
//        data.ElectricityMeter.forEach {
//            if (!manufecture.contains(it.Manufacturer)) {
//                manufecture.add(it.Manufacturer)
//            }
//
//        }
//
//        Log.e("TAG", "Question: " + manufecture.toString())
//
//
//
//        spinnerManufacture = ArrayAdapter<String>(
//            requireContext(),
//            android.R.layout.simple_spinner_item,
//            manufecture
//        )
//
//        spinnerManufacture?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.spManufacturer.adapter = spinnerManufacture
//    }
//
//    fun loadModel() {
//        var codelist = SharedPreferenceHelper.getInstance(requireContext()).getCodeList()
//        val data = GsonParser.gsonParser!!.fromJson(codelist, CodeListDataClass::class.java)
//        var model: MutableList<String> = mutableListOf()
//        data.ElectricityMeter.forEach {
//
//            if (it.Manufacturer == binding.spManufacturer.selectedItem) {
//                if (!model.contains(it.Model)) {
//                    model.add(it.Model)
//                }
//
//            }
//
//        }
//
//        Log.e("TAG", "Question: " + model.toString())
//
//        SpinnerModel =
//            ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, model)
//        SpinnerModel?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.spModel.adapter = SpinnerModel
//    }
//
//
//    fun findMeterCode() :String{
//        var codelist = SharedPreferenceHelper.getInstance(requireContext()).getCodeList()
//        val data = GsonParser.gsonParser!!.fromJson(codelist, CodeListDataClass::class.java)
//        var code:String=""
//        data.ElectricityMeter.forEach {
//            if(it.Manufacturer==binding.spManufacturer.selectedItem
//                && it.Model==binding.spModel.selectedItem){
//                Log.e("TAG", "findMeterCode: "+it.Code)
//                code=it.Code
//            }
//
//
//        }
//
//        return code
//    }
//
//
//    fun meterStatus(){
//        var codelist=  SharedPreferenceHelper.getInstance(requireContext()).getCodeList()
//        val data = GsonParser.gsonParser!!.fromJson(codelist, CodeListDataClass::class.java)
////        Log.e("TAG", "Question: " + data.Toolbox.toString())
//
//        var finalData : MutableList <String> = data.MeterStatus as MutableList<String>
//        finalData.add(0,"Meter Status")
//        adapterMeterStatus = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, finalData)
//        adapterMeterStatus?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.spMeterStatus.adapter = adapterMeterStatus
//    }
//




    fun addPhoto(type:Int) {


        LoaderHelper.showLoader(requireContext())




        val newUUID = UUID.randomUUID()

//        val client = ApiClient()
//        val api = client.getClient()?.create(Api::class.java)
        var base64Image: String=""
           if(type==1){
                base64Image = ConstantHelper.getBase64(PhotoBeforeInstallFile1!!)
           }else if(type==2){
               base64Image = ConstantHelper.getBase64(PhotoBeforeInstallFile2!!)
           }else if(type==3){
               base64Image = ConstantHelper.getBase64(PhotoBeforeInstallFile3!!)
           }else if(type==4){
               base64Image = ConstantHelper.getBase64(PhotoBeforeInstallFile4!!)
           }else if(type==5){
               base64Image = ConstantHelper.getBase64(PhotoBeforeInstallFile5!!)
           }else if(type==6){
               base64Image = ConstantHelper.getBase64(PhotoCurrentConnectionFile!!)
           }else if(type==7){
               base64Image = ConstantHelper.getBase64(PhotoVoltageConnectionFile!!)
           }



//        Log.e("TAG", "addAuditPhoto64: "+base64Image, )

        var body = JSONObject()
        var geoLocation = JSONObject()
        body.put("image",base64Image)
        body.put("type",type)

        geoLocation.put("latitude",locationn?.latitude)
        geoLocation.put("longitude",locationn?.longitude)
        geoLocation.put("accuracy",locationn?.accuracy)

        var sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
        var formattedDate = sdf.format(Date())
        geoLocation.put("timestamp",formattedDate)
        body.put("geoLocation",geoLocation)

        body.put("score",0)
        Log.e("TAG", "photo add body: " + body.toString())


        ConstantHelper.photoList.add( photoUploadDataClass( newUUID.toString(),body.toString()) )
        LoaderHelper.dissmissLoader()
        if(type==1){
            ConstantHelper.picture1UUID=newUUID.toString()

        }else if(type==2){
            ConstantHelper.picture2UUID=newUUID.toString()

        }else if(type==3){
            ConstantHelper.picture3UUID=newUUID.toString()

        }else if(type==4){
            ConstantHelper.picture4UUID=newUUID.toString()

        }else if(type==5){
            ConstantHelper.picture5UUID=newUUID.toString()

        }else if(type==6){
            ConstantHelper.currentpointUUID=newUUID.toString()

        }else if(type==7){
            ConstantHelper.voltagepointUUID=newUUID.toString()

        }



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
                    R.id.action_nav_siteStart_to_nav_meteraudit
                )
                true
            }
            R.id.action_logout -> {

                SharedPreferenceHelper.getInstance(requireContext()).clearData()
                Navigation.findNavController(binding.root).navigate(
                    R.id.action_nav_siteStart_to_nav_about
                )
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item);
    }




}