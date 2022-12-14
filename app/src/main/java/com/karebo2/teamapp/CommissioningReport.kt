package com.karebo2.teamapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.*
import android.widget.AdapterView
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
import com.karebo2.teamapp.databinding.FragmentCommissioningReportBinding
import com.karebo2.teamapp.dataclass.photoUploadDataClass
import com.karebo2.teamapp.utils.ConstantHelper
import com.karebo2.teamapp.utils.LoaderHelper
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class CommissioningReport : Fragment() {


    private var _binding: FragmentCommissioningReportBinding? = null
    private val binding get() = _binding!!
    private var mPhotoFile: File? = null
    private var PdfHexingFile: File? = null
    private var PhotoSmsConfigFile: File? = null
    private var PhotoGprsSignalFile: File? = null
    private var PhotoOntechFile: File? = null
     var photoname: String=""
    var  locationn : Location? =ConstantHelper.locationn

//    var jsonData: MeterDataModel? =null
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
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommissioningReportBinding.inflate(
            inflater,container,false)
        val root: View = binding.root
//        requestlocationPermission()


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
//        Log.e("TAG", "location: ", )
//        LoaderHelper.showLoader(requireContext())
//        val cancellationTokenSource = CancellationTokenSource()
//        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
//            .addOnSuccessListener { location ->
//            Log.e("Location", "location is found: $location")
//                locationn=location
////                findAddress(location)
//                LoaderHelper.dissmissLoader()
//
//        }
//            .addOnFailureListener { exception ->
//                Toast.makeText(requireContext(),"Oops location failed to Fetch: $exception",Toast.LENGTH_SHORT).show()
//                Log.e("Location", "Oops location failed with exception: $exception")
//                LoaderHelper.dissmissLoader()
//            }
//



        binding.btnConfigSms.setOnClickListener {
            photoname = ConstantHelper.CONFIG_SMS
            selectImageType(requireContext())
        }

        binding.btnGprsSignal.setOnClickListener {
            photoname = ConstantHelper.GPRS_SIGNAL
            selectImageType(requireContext())
        }
        binding.btnOntechConfirm.setOnClickListener {
            photoname = ConstantHelper.ONTECH_CONFIRM
            selectImageType(requireContext())
        }

        binding.btnPdfFile.setOnClickListener{
            photoname=ConstantHelper.PDF_COMPLETE
            selectImageType(requireContext())
        }



        // set visibility of Spinner Element
        binding.spPdfComplete.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                if(binding.spPdfComplete.selectedItemPosition ==2){
//                    binding.ivPdfHex.visibility=View.GONE
                    binding.btnPdfFile.visibility=View.GONE

                }else{
//                    if(PdfHexingFile!=null){
//                        binding.ivPdfHex.visibility=View.VISIBLE
//                    }

                    binding.btnPdfFile.visibility=View.VISIBLE
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
//                binding.ivPdfHex.visibility=View.GONE
                binding.btnPdfFile.visibility=View.GONE
            }
        })

        binding.spConfigSms.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                if(binding.spConfigSms.selectedItemPosition ==2){
                    binding.ivConfigSms.visibility=View.GONE
                    binding.btnConfigSms.visibility=View.GONE

                }else{
                    if(PhotoSmsConfigFile!=null){
                        binding.ivConfigSms.visibility=View.VISIBLE
                    }
                    binding.btnConfigSms.visibility=View.VISIBLE
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                binding.ivConfigSms.visibility=View.GONE
                binding.btnConfigSms.visibility=View.GONE
            }
        })

        binding.spRemoteRead.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                if(binding.spRemoteRead.selectedItemPosition ==2){
                    binding.etNameOntecTech.visibility=View.GONE


                }else{
                    binding.etNameOntecTech.visibility=View.VISIBLE

                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                binding.etNameOntecTech.visibility=View.GONE
            }
        })



        //comment Box visibilty Handle
        binding.swMeterInstall.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
              binding.etCommentMeterInstall.visibility=View.GONE
            } else {
                binding.etCommentMeterInstall.visibility=View.VISIBLE
            }
        })

        binding.swMeterConfigure.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.etCommentMeterConfigure.visibility=View.GONE
            } else {
                binding.etCommentMeterConfigure.visibility=View.VISIBLE
            }
        })

        binding.swModemConfigure.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.etCommentModemConfigure.visibility=View.GONE
            } else {
                binding.etCommentModemConfigure.visibility=View.VISIBLE
            }
        })

        binding.swIpInfoProvide.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.etCommentIpInfoProvide.visibility=View.GONE
            } else {
                binding.etCommentIpInfoProvide.visibility=View.VISIBLE
            }
        })

        binding.swPowerUp.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.etCommentPowerUp.visibility=View.GONE
            } else {
                binding.etCommentPowerUp.visibility=View.VISIBLE
            }
        })



//        binding.btNext.setOnClickListener{
//            Navigation.findNavController(root).navigate(
//                     R.id.action_nav_commissioningReport_to_nav_updateSiteDetails,
//                     )
//        }





        binding.btNext.setOnClickListener{

            if( binding.etRedPhaseVoltage.text.isEmpty()  ||binding.etRedPhaseVoltage.text.equals(null)){
            Toast.makeText(requireContext(),"Add Red Phase Voltage",Toast.LENGTH_SHORT).show()

            }else if( binding.etWhitePhaseVoltage.text.isEmpty()  ||binding.etWhitePhaseVoltage.text.equals(null)){
                Toast.makeText(requireContext(),"Add White Phase Voltage ",Toast.LENGTH_SHORT).show()

            }else if( binding.etBluePhaseVoltage.text.isEmpty()  ||binding.etBluePhaseVoltage.text.equals(null)){
                Toast.makeText(requireContext(),"Add Blue Phase Voltage ",Toast.LENGTH_SHORT).show()

            }
            else if( binding.etRedPhaseVoltageCurrent.text.isEmpty()  ||binding.etRedPhaseVoltageCurrent.text.equals(null)){
                Toast.makeText(requireContext(),"Add current Red Phase Voltage",Toast.LENGTH_SHORT).show()

            }
            else if( binding.etWhitePhaseVoltageCurrent.text.isEmpty()  ||binding.etWhitePhaseVoltageCurrent.text.equals(null)){
                Toast.makeText(requireContext(),"Add current white Phase Voltage",Toast.LENGTH_SHORT).show()

            }else if( binding.etBluePhaseVoltageCurrent.text.isEmpty()  ||binding.etBluePhaseVoltageCurrent.text.equals(null)){
                Toast.makeText(requireContext(),"Add current blue Phase Voltage",Toast.LENGTH_SHORT).show()

            }
            else if(binding.spPdfComplete.selectedItemPosition==0){
                Toast.makeText(requireContext(),"Select hex pdf complete",Toast.LENGTH_SHORT).show()

            } else if(binding.spConfigSms.selectedItemPosition==0){
                Toast.makeText(requireContext(),"Select config sms successfully",Toast.LENGTH_SHORT).show()

            } else if(binding.spGprsSignal.selectedItemPosition==0){
                Toast.makeText(requireContext(),"Select GPRS signal strength",Toast.LENGTH_SHORT).show()

            } else if(binding.spRemoteRead.selectedItemPosition==0){
                Toast.makeText(requireContext(),"Select remote read possible",Toast.LENGTH_SHORT).show()

            }
            else  if(PhotoGprsSignalFile==null){
                Toast.makeText(requireContext(),"Add Gprs Signal Photo ",Toast.LENGTH_SHORT).show()
            }else  if(PhotoOntechFile==null){
                Toast.makeText(requireContext(),"Add Ontech confirm Photo ",Toast.LENGTH_SHORT).show()
            }
            else  if(PhotoSmsConfigFile==null){
                Toast.makeText(requireContext(),"Add SmsConfig Photo ",Toast.LENGTH_SHORT).show()
            }else  if(PdfHexingFile==null){
                Toast.makeText(requireContext(),"Add hexing pdf",Toast.LENGTH_SHORT).show()
            }
            else{
                 addInModel()
                try {
                    Navigation.findNavController(root).navigate(
                        R.id.action_nav_commissioningReport_to_nav_updateSiteDetails,
                    )
                }catch (e:Exception){

                }

             }



        }
        return root
    }

     fun addInModel() {
         var Commissioning = JSONObject()
         var HexingPDFFile = JSONObject()
         var ItronConfigSMSSuccessfull = JSONObject()
         var GPRSSignalStrength = JSONObject()
         var OntecTechnicalConfirmation = JSONObject()
         var MeterInstalled = JSONObject()
         var ModemConfigured = JSONObject()
         var APNConfirmed = JSONObject()
         var MeterConfigured = JSONObject()
         var IPInfoProvided = JSONObject()
         var ModelAndMeterPoweredUp = JSONObject()


         Commissioning.put("RedPhaseVoltage",binding.etRedPhaseVoltage.text)
         Commissioning.put("WhitePhaseVoltage",binding.etWhitePhaseVoltage.text)
         Commissioning.put("BluePhaseVoltage",binding.etBluePhaseVoltage.text)
         Commissioning.put("RedPhaseCurrent",binding.etRedPhaseVoltageCurrent.text)
         Commissioning.put("WhitePhaseCurrent",binding.etWhitePhaseVoltageCurrent.text)
         Commissioning.put("BluePhaseCurrent",binding.etBluePhaseVoltageCurrent.text)


         HexingPDFFile.put("Key",binding.spPdfComplete.selectedItem)
         var pdfBase64file=ConstantHelper.fileToBase64(PdfHexingFile!!)
         HexingPDFFile.put("Value",ConstantHelper.PhotopdfhexUUID)
         Commissioning.put("HexingPDFFile",HexingPDFFile)


         ItronConfigSMSSuccessfull.put("Key",binding.spConfigSms.selectedItem)
         ItronConfigSMSSuccessfull.put("Value",ConstantHelper.PhotoSmsConfigFileUUID)
         Commissioning.put("ItronConfigSMSSuccessfull",ItronConfigSMSSuccessfull)



         GPRSSignalStrength.put("Key",binding.spGprsSignal.selectedItem)
         GPRSSignalStrength.put("Value",ConstantHelper.PhotoGprsSignalFileUUID)
         Commissioning.put("GPRSSignalStrength",GPRSSignalStrength)

         OntecTechnicalConfirmation.put("Key",binding.etNameOntecTech.text)
         OntecTechnicalConfirmation.put("Value",ConstantHelper.PhotoOntechFileUUID)
         Commissioning.put("OntecTechnicalConfirmation",OntecTechnicalConfirmation)

         Commissioning.put("LocationDescription",binding.etDescMeterLocation.text.toString())


         MeterInstalled.put("Key",binding.swMeterInstall.isChecked)
         MeterInstalled.put("Value",binding.etCommentMeterInstall.text)
         Commissioning.put("MeterInstalled",MeterInstalled)


         ModemConfigured.put("Key",binding.swModemConfigure.isChecked)
         ModemConfigured.put("Value",binding.etCommentModemConfigure.text)
         Commissioning.put("ModemConfigured",ModemConfigured)


         APNConfirmed.put("Key",binding.swModemConfigure.isChecked)
         APNConfirmed.put("Value",binding.etCommentModemConfigure.text)
         Commissioning.put("APNConfirmed",APNConfirmed)


         MeterConfigured.put("Key",binding.swMeterConfigure.isChecked)
         MeterConfigured.put("Value",binding.etCommentMeterConfigure.text)
         Commissioning.put("MeterConfigured",MeterConfigured)


         IPInfoProvided.put("Key",binding.swIpInfoProvide.isChecked)
         IPInfoProvided.put("Value",binding.etCommentIpInfoProvide.text)
         Commissioning.put("IPInfoProvided",IPInfoProvided)


         ModelAndMeterPoweredUp.put("Key",binding.swPowerUp.isChecked)
         ModelAndMeterPoweredUp.put("Value",binding.etCommentPowerUp.text)
         Commissioning.put("ModelAndMeterPoweredUp",ModelAndMeterPoweredUp)


         ConstantHelper.Components.put("Commissioning",Commissioning)

         ConstantHelper.TEST0123456.put("Components",ConstantHelper.Components)



         Log.e("json at Commissionnn", Commissioning.toString())
         Log.e("json at Commission", ConstantHelper.Components.toString())
         Log.e("json at TEST0123456", ConstantHelper.TEST0123456.toString())



//
    }


//    private fun requestlocationPermission() {
//
//        when {
//            hasPermissions(requireContext(), *permission) -> {
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
//    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
//        permissions.all {
//            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
//        }
//    private fun locationPermission() {
//        locationPermissionLauncher.launch(permission)
//    }

//
//    fun findAddress(location: Location):String
//    {
//        var addresses: List<Address?> = listOf()
//        var geocoder = Geocoder(requireContext(), Locale.getDefault())
//        var returnAddress:String
//
//        try {
//            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
//
//        }
//        catch (e:Exception){
//
//        }
//        if(addresses != null && !addresses.isEmpty()){
//            var address=addresses[0]?.getAddressLine(0)
//            address=address?.replace(", South Africa","")
//            address=address?.replace("South Africa","")
//            returnAddress=address!!
//            val addressPart = address.split(",")
////            binding.etStreetAddress.setText(addressPart[0]+","+addressPart[1])
////            binding.etPostalCode.setText(addresses[0]?.postalCode)
////            binding.etSuburbName.setText(addressPart[2])
//        }
//        else{
//            returnAddress= "No Address on "+ "("+location.latitude+","+location.longitude+")";
//        }
//        LoaderHelper.dissmissLoader()
//        return returnAddress
//
//    }




    fun loadGprsSignalImage(){
        PhotoGprsSignalFile = File(mPhotoFile?.path!!)
       addPhoto(2)
        Log.e("TAG", "Load Gprs Signal File: $PhotoGprsSignalFile")
        Glide.with(requireContext())
            .load(PhotoGprsSignalFile)
            .into(binding.ivGprsSignal)
        showGprsSignalImg()
    }

    fun showGprsSignalImg() {
        binding.ivGprsSignal.visibility = View.VISIBLE
    }


    fun loadontechConfirm(){
        PhotoOntechFile = File(mPhotoFile?.path!!)
        addPhoto(5)
        Log.e("TAG", "Load Gprs Signal File: $PhotoOntechFile")
        Glide.with(requireContext())
            .load(PhotoOntechFile)
            .into(binding.ivOntechConfirm)
        showOntechconfirmImg()
    }

    fun showOntechconfirmImg() {
        binding.ivOntechConfirm.visibility = View.VISIBLE
    }



    fun loadPdfFile(){

        PdfHexingFile = File(mPhotoFile?.path!!)
        addPhoto(3)
        Log.e("TAG", "Load SMS Photo File: $PdfHexingFile")

        binding.tvPdfName.text= "File Name:--   "+PdfHexingFile?.path.toString()
        showPdfFile()
    }

    fun showPdfFile() {
        binding.tvPdfName.visibility = View.VISIBLE
    }




    fun loadSmsConfigImages(){
        PhotoSmsConfigFile = File(mPhotoFile?.path!!)
        addPhoto(1)
        Log.e("TAG", "Load SMS Photo File: $PhotoSmsConfigFile")
        Glide.with(requireContext())
            .load(PhotoSmsConfigFile)
            .into(binding.ivConfigSms)
        showVoltageImg()
    }

//    fun loadpdfImages(){
//        PdfHexingFile = File(mPhotoFile?.path!!)
//        addPhoto(3)
//        Log.e("TAG", "Load SMS Photo File: $PdfHexingFile")
//        Glide.with(requireContext())
//            .load(PdfHexingFile)
//            .into(binding.ivPdfHex)
//        showpdfhex()
//    }
//
//    fun showpdfhex(){
//        binding.ivPdfHex.visibility=View.VISIBLE
//    }

     fun showVoltageImg() {
         binding.ivConfigSms.visibility = View.VISIBLE
    }


    private var activityResultLauncher: ActivityResultLauncher<Intent> =

        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.e("Photo", mPhotoFile.toString())
                Log.e("TAG", "photoname $photoname")
                if (photoname == ConstantHelper.GPRS_SIGNAL) {
                    loadGprsSignalImage()

                }else if (photoname == ConstantHelper.ONTECH_CONFIRM) {
                    loadontechConfirm()

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
        setPhotonamee(savedInstanceState?.get("photoname").toString())
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
    fun setPhotonamee(name: String) {
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

            hasPermissionsCamera(requireContext(), *permission) -> {
                Log.e("permission", "camera has permission ")
                if (photoname == ConstantHelper.CONFIG_SMS) {
                    Log.e("TAG", "selectImageType: Gallary")
                    dispatchGalleryIntent()
                } else if(photoname==ConstantHelper.GPRS_SIGNAL) {
                    Log.e("TAG", "selectImageType: Camera")
                    dispatchTakePictureIntent()
                }else if(photoname==ConstantHelper.ONTECH_CONFIRM) {
                    Log.e("TAG", "selectImageType: Camera")
                    dispatchTakePictureIntent()
                } else if(photoname==ConstantHelper.PDF_COMPLETE){
                  //  dispatchGalleryIntent()
                   dispatchDocumetIntent()
                }


            }
            else -> {
                Toast.makeText(requireContext(), " Allow the Storage Permission", Toast.LENGTH_LONG)
                    .show()
                activityCameraResultLauncher.launch(permission)
            }
        }

    }

    private fun hasPermissionsCamera(context: Context, vararg permissions: String): Boolean =
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

//                if(photoname!=ConstantHelper.PDF_COMPLETE){
                    loadSmsConfigImages()
//                }else{
//                    loadpdfImages()
//                }


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


    private var activityResultLauncherDocumet: ActivityResultLauncher<Intent> =

        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                var result = result.data
                var selectedDocument: Uri = result?.data!!
                Log.e("TAG", "selectedDocument: "+selectedDocument.toString(), )
                var realPath=getRealPathFromUriDocument(selectedDocument)

                if(realPath.equals(null)){
                    Toast.makeText(requireActivity(),"pdf is not selected",Toast.LENGTH_LONG).show()
                }else{

                    mPhotoFile = File(realPath)

                    val sourcePath = requireContext().getExternalFilesDir(null).toString()
                    Log.e("Photo2", mPhotoFile.toString())

                        try {
                            copyFileStream(
                                File(sourcePath + "/" + mPhotoFile!!.name),
                                selectedDocument,
                                requireContext()
                            )
                        }catch (e:Exception){

                        }

                    if(mPhotoFile!!.length()<500000){
                        Log.e("Photo2", mPhotoFile.toString())
                        Log.e("Photo3", mPhotoFile!!.length().toString())

                        loadPdfFile()
                    }else{
                        Toast.makeText(requireActivity(),"Select pdf less then 400 kb ",Toast.LENGTH_LONG).show()
                    }


                }



            }


        }

    private fun dispatchDocumetIntent() {

        val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
        pdfIntent.type = "application/pdf"
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
        activityResultLauncherDocumet.launch(pdfIntent )

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
            var r= cursor.getString(columnIndex)
            Log.e("TAG", "getRealPathFromUri: "+r, )
            cursor.getString(columnIndex)
        } finally {
            cursor?.close()
        }
    }

    fun getRealPathFromUriDocument(contentUri: Uri?): String? {

        val uriString: String = contentUri.toString()
        var pdfName: String? = null
        if (uriString.startsWith("content://")) {
            var myCursor: Cursor? = null
             try {
                // Setting the PDF to the TextView
                myCursor =requireContext().contentResolver.query(contentUri!!, null, null, null, null)
                if (myCursor != null && myCursor.moveToFirst()) {
                    pdfName = myCursor.getString(myCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    Log.e("TAG", "selectedDocument name: "+pdfName.toString(), )
                    return pdfName
                }
            } finally {
                myCursor?.close()
            }
        }
        return pdfName
    }



//    fun getRealPathFromUriDocument(contentUri: Uri?): String? {
//        var cursor: Cursor? = null
//        return try {
//            val proj = arrayOf(MediaStore.Images.Media.DATA)
//            cursor = requireContext().contentResolver.query(contentUri!!,
//                arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null)
//            assert(cursor != null)
//
//            cursor!!.moveToFirst()
//
//            val columnIndex = cursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//            val size = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE))
//
//            cursor.getString(columnIndex)
//        } finally {
//            cursor?.close()
//        }
//    }
//

    fun addPhoto(type:Int) {


        LoaderHelper.showLoader(requireContext())




        val newUUID = UUID.randomUUID()

//        val client = ApiClient()
//        val api = client.getClient()?.create(Api::class.java)
        var base64Image: String=""
        if(type==1){
            base64Image = ConstantHelper.getBase64(PhotoSmsConfigFile!!).toString()
            Log.e("TAG", "configphoto: "+base64Image, )
        }else if(type==2){
            base64Image = ConstantHelper.getBase64(PhotoGprsSignalFile!!).toString()
        }else if(type==3){
            base64Image = ConstantHelper.fileToBase64(PdfHexingFile!!).toString()
        }else if(type==5){
            base64Image = ConstantHelper.getBase64(PhotoOntechFile!!).toString()
        }



//        Log.e("TAG", "addAuditPhoto64: "+base64Image, )

        var body = JSONObject()
        var geoLocation = JSONObject()
        body.put("image",base64Image)
        body.put("type",type)

        geoLocation.put("latitude", locationn!!.latitude)
        geoLocation.put("longitude", locationn!!.longitude)
        geoLocation.put("accuracy", locationn!!.accuracy)

        var sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
        var formattedDate = sdf.format(Date())
        geoLocation.put("timestamp",formattedDate)
        body.put("geoLocation",geoLocation)

        body.put("score",0)
        Log.e("TAG", "photo add body: " + body.toString())


        ConstantHelper.photoList.add( photoUploadDataClass( newUUID.toString(),body.toString()) )
        LoaderHelper.dissmissLoader()
        if(type==1){
            ConstantHelper.PhotoSmsConfigFileUUID=newUUID.toString()

        }else if(type==2){
            ConstantHelper.PhotoGprsSignalFileUUID=newUUID.toString()

        }else if(type==3){
            ConstantHelper.PhotopdfhexUUID=newUUID.toString()

        }else if(type==5){
            ConstantHelper.PhotoOntechFileUUID=newUUID.toString()

        }

        Log.e("TAG", "addPhoto size: " + ConstantHelper.photoList.size.toString())


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
                    R.id.action_nav_commissioningReport_to_nav_meteraudit
                )
                true
            }
//            R.id.action_logout -> {
//
//                SharedPreferenceHelper.getInstance(requireContext()).clearData()
//                Navigation.findNavController(binding.root).navigate(
//                    R.id.action_nav_commissioningReport_to_nav_about
//                )
//                true
//            }

            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item);
    }
}