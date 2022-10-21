package com.karebo2.teamapp

import android.Manifest
import android.app.Activity
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
import com.karebo2.teamapp.databinding.FragmentUpdateSiteDetailsBinding
import com.karebo2.teamapp.dataclass.photoUploadDataClass
import com.karebo2.teamapp.sharedpreference.SharedPreferenceHelper
import com.karebo2.teamapp.utils.ConstantHelper
import com.karebo2.teamapp.utils.LoaderHelper
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class UpdateSiteDetails : Fragment() {


    private var _binding: FragmentUpdateSiteDetailsBinding? = null
    private val binding get() = _binding!!
    private var mPhotoFile: File? = null
    private var PhotoVoltageConnectionFile: File? = null
    private var PhotoCurrentConnectionFile: File? = null
    private var PhotoAfterInstallFile1: File? = null
    private var PhotoAfterInstallFile2: File? = null
    private var PhotoAfterInstallFile3: File? = null
    private var PhotoAfterInstallFile4: File? = null
    private var PhotoAfterInstallFile5: File? = null
    lateinit var photoname: String
    var  locationn : Location? =null


    private val permission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,

        )
    lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var fusedLocationClient: FusedLocationProviderClient




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateSiteDetailsBinding.inflate(
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
                locationn=location
                LoaderHelper.dissmissLoader()

            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(),"Oops location failed to Fetch: $exception",Toast.LENGTH_SHORT).show()
                Log.e("Location", "Oops location failed with exception: $exception")
                LoaderHelper.dissmissLoader()
            }










        hideEveryThing()

        binding.swRefWithBidvest.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                showEveryThing()
            } else {
                hideEveryThing()
            }
        })




        binding.btImageCurrentConnection.setOnClickListener {
            photoname = ConstantHelper.CURRENT_CONNECTION
            selectImageType(requireContext())
        }

        binding.btImageVoltageConnection.setOnClickListener {
            photoname = ConstantHelper.VOLTAGE_CONNECTION
            selectImageType(requireContext())
        }

        binding.btnAfterInstall1.setOnClickListener {
            photoname = ConstantHelper.AFTER_INSTALL1
            selectImageType(requireContext())
        }

        binding.btnAfterInstall2.setOnClickListener {
            photoname = ConstantHelper.AFTER_INSTALL2
            selectImageType(requireContext())
        }

        binding.btnAfterInstall3.setOnClickListener {
            photoname = ConstantHelper.AFTER_INSTALL3
            selectImageType(requireContext())
        }

        binding.btnAfterInstall4.setOnClickListener {
            photoname = ConstantHelper.AFTER_INSTALL4
            selectImageType(requireContext())
        }

        binding.btnAfterInstall5.setOnClickListener {
            photoname = ConstantHelper.AFTER_INSTALL5
            selectImageType(requireContext())
        }

        binding.btnBidvestTechSignatureClear1.setOnClickListener{
            binding.inkBidvestTechSignature1.clear()
        }
        binding.btnKareboTechSignatureClear1.setOnClickListener{
            binding.inkKareboTechSignature1.clear()
        }

        setSignatureBoardScroll()


        binding.btNext.setOnClickListener{
            if(PhotoVoltageConnectionFile==null){

                Toast.makeText(requireContext(),"Add Voltage Connection Photo ",Toast.LENGTH_SHORT).show()
            }
            else  if(PhotoCurrentConnectionFile==null){
                Toast.makeText(requireContext(),"AAdd Current Connection Photo ",Toast.LENGTH_SHORT).show()
            }else{
                addInModel()
                Navigation.findNavController(root).navigate(
                    R.id.action_nav_updateSiteDetails_to_nav_signOff,

                    )
            }



        }
        return root
    }


    fun setSignatureBoardScroll() {
        binding.inkBidvestTechSignature1.setOnTouchListener(View.OnTouchListener { v, event ->
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

        binding.inkKareboTechSignature1.setOnTouchListener(View.OnTouchListener { v, event ->
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

                } else if (photoname == ConstantHelper.AFTER_INSTALL1) {

                    loadAfterInstallImages1()

                }else if (photoname == ConstantHelper.AFTER_INSTALL2) {

                    loadAfterInstallImages2()

                }else if (photoname == ConstantHelper.AFTER_INSTALL3) {

                    loadAfterInstallImages3()

                }else if (photoname == ConstantHelper.AFTER_INSTALL4) {

                    loadAfterInstallImages4()

                }else if (photoname == ConstantHelper.AFTER_INSTALL5) {

                    loadAfterInstallImages5()

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
        Log.e("TAG", "onSaveInstanceState: ")
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        setImageFile(savedInstanceState?.get("imagePath").toString())
        Log.e("TAG", "onRestoreInstanceState: " + savedInstanceState?.get("imagePath"))
        super.onViewStateRestored(savedInstanceState)
    }


    fun getImageFile(): File {
        return mPhotoFile!!
    }

    fun setImageFile(path: String) {
        mPhotoFile = File(path)
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




    fun showAfterInstallImage() {

        if(PhotoAfterInstallFile1!=null || PhotoAfterInstallFile2!=null||PhotoAfterInstallFile3!=null){
            binding.llAfterInstallImages.visibility=View.VISIBLE
            if(PhotoAfterInstallFile1!=null ){
                binding.ivAfterInsatll1.visibility=View.VISIBLE
            }
            if(PhotoAfterInstallFile2!=null ){
                binding.ivAfterInsatll2.visibility=View.VISIBLE
            }
            if(PhotoAfterInstallFile3!=null ){
                binding.ivAfterInsatll3.visibility=View.VISIBLE
            }
        }


        if(PhotoAfterInstallFile4!=null || PhotoAfterInstallFile5!=null){
            binding.llAfterInstallImages2.visibility=View.VISIBLE
            if(PhotoAfterInstallFile4!=null ){
                binding.ivAfterInsatll4.visibility=View.VISIBLE
            }
            if(PhotoAfterInstallFile5!=null ){
                binding.ivAfterInsatll5.visibility=View.VISIBLE
            }

        }


    }

    fun loadAfterInstallImages1() {

        PhotoAfterInstallFile1 = File(mPhotoFile?.path!!)
        addPhoto(1)
        Log.e("TAG", "Load Voltage Connection File: $PhotoAfterInstallFile1")
        Glide.with(requireContext())
            .load(PhotoAfterInstallFile1)
            .into(binding.ivAfterInsatll1)
        showAfterInstallImage()

    }
    fun loadAfterInstallImages2() {

        PhotoAfterInstallFile2 = File(mPhotoFile?.path!!)
        addPhoto(2)
        Log.e("TAG", "Load Voltage Connection File: $PhotoAfterInstallFile2")
        Glide.with(requireContext())
            .load(PhotoAfterInstallFile2)
            .into(binding.ivAfterInsatll2)
        showAfterInstallImage()

    }
    fun loadAfterInstallImages3() {

        PhotoAfterInstallFile3 = File(mPhotoFile?.path!!)
        addPhoto(3)
        Log.e("TAG", "Load Voltage Connection File: $PhotoAfterInstallFile3")
        Glide.with(requireContext())
            .load(PhotoAfterInstallFile3)
            .into(binding.ivAfterInsatll3)
        showAfterInstallImage()

    }

    fun loadAfterInstallImages4() {

        PhotoAfterInstallFile4 = File(mPhotoFile?.path!!)
        addPhoto(4)
        Log.e("TAG", "Load Voltage Connection File: $PhotoAfterInstallFile4")
        Glide.with(requireContext())
            .load(PhotoAfterInstallFile4)
            .into(binding.ivAfterInsatll4)
        showAfterInstallImage()

    }

    fun loadAfterInstallImages5() {

        PhotoAfterInstallFile5 = File(mPhotoFile?.path!!)
        addPhoto(5)
        Log.e("TAG", "Load Voltage Connection File: $PhotoAfterInstallFile5")
        Glide.with(requireContext())
            .load(PhotoAfterInstallFile5)
            .into(binding.ivAfterInsatll5)
        showAfterInstallImage()

    }











     fun addInModel() {
         var FinaliseInstallation = JSONObject()
         var AfterPictures = JSONObject()
         var Signatures = JSONObject()


         FinaliseInstallation.put("BidvestReferenceClosed",binding.swRefWithBidvest.isChecked)

         AfterPictures.put("Voltage Connection Point",ConstantHelper.voltagepointafterUUID)
         AfterPictures.put("Current Connection Point",ConstantHelper.currentpointafterUUID)
         AfterPictures.put("Picture 1",ConstantHelper.picture1afterUUID)
         AfterPictures.put("Picture 2",ConstantHelper.picture2afterUUID)
         AfterPictures.put("Picture 3",ConstantHelper.picture3afterUUID)
         AfterPictures.put("Picture 4",ConstantHelper.picture4afterUUID)
         AfterPictures.put("Picture 5",ConstantHelper.picture5afterUUID)
         FinaliseInstallation.put("AfterPictures",AfterPictures)


         var drawing: Bitmap
         drawing = binding.inkKareboTechSignature1.getBitmap(resources.getColor(android.R.color.white))
         Signatures.put("Installation Engineer",getBitMapBase64(drawing))


         drawing = binding.inkBidvestTechSignature1.getBitmap(resources.getColor(android.R.color.white))
         Signatures.put("Bidvest Technician onsite",getBitMapBase64(drawing))
         FinaliseInstallation.put("Signatures",Signatures)


         ConstantHelper.Components.put("FinaliseInstallation",FinaliseInstallation)
         ConstantHelper.TEST0123456.put("Components",ConstantHelper.Components)



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




    fun hideEveryThing(){
        binding.llAllWidget.visibility=View.GONE
    }

    fun showEveryThing(){
        binding.llAllWidget.visibility=View.VISIBLE

    }





    fun addPhoto(type:Int) {


        LoaderHelper.showLoader(requireContext())




        val newUUID = UUID.randomUUID()

//        val client = ApiClient()
//        val api = client.getClient()?.create(Api::class.java)
        var base64Image: String=""
        if(type==1){
            base64Image = ConstantHelper.getBase64(PhotoAfterInstallFile1!!)
        }else if(type==2){
            base64Image = ConstantHelper.getBase64(PhotoAfterInstallFile2!!)
        }else if(type==3){
            base64Image = ConstantHelper.getBase64(PhotoAfterInstallFile3!!)
        }else if(type==4){
            base64Image = ConstantHelper.getBase64(PhotoAfterInstallFile4!!)
        }else if(type==5){
            base64Image = ConstantHelper.getBase64(PhotoAfterInstallFile5!!)
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
            ConstantHelper.picture1afterUUID=newUUID.toString()

        }else if(type==2){
            ConstantHelper.picture2afterUUID=newUUID.toString()

        }else if(type==3){
            ConstantHelper.picture3afterUUID=newUUID.toString()

        }else if(type==4){
            ConstantHelper.picture4afterUUID=newUUID.toString()

        }else if(type==5){
            ConstantHelper.picture5afterUUID=newUUID.toString()

        }else if(type==6){
            ConstantHelper.currentpointafterUUID=newUUID.toString()

        }else if(type==7){
            ConstantHelper.voltagepointafterUUID=newUUID.toString()

        }



    }





    override fun onCreate(savedInstanceState: Bundle?) {
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
                    R.id.action_nav_updateSiteDetails_to_nav_meteraudit
                )
                true
            }
            R.id.action_logout -> {

                SharedPreferenceHelper.getInstance(requireContext()).clearData()
                Navigation.findNavController(binding.root).navigate(
                    R.id.action_nav_updateSiteDetails_to_nav_about
                )
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item);
    }

}