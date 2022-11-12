package com.karebo2.teamapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.karebo2.teamapp.databinding.FragmentAuditPhotoBinding
import com.karebo2.teamapp.dataclass.photoUploadDataClass
import com.karebo2.teamapp.utils.LoaderHelper
import com.karebo2.teamapp.sharedpreference.SharedPreferenceHelper
import com.karebo2.teamapp.utils.ConstantHelper
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors


class AuditPhoto : Fragment() {


    private var _binding: FragmentAuditPhotoBinding? = null
    private val binding get() = _binding!!
    var imageCapture: ImageCapture? = null
    var photouri: Uri? = null
    var mPhotoFile: File? = null
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var imagePreview: Preview? = null

    var  locationn : Location? =null

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
        _binding = FragmentAuditPhotoBinding.inflate(
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




        binding.tvAddress.text = ConstantHelper.ADDRESS

//        binding.ivPhoto.setOnClickListener{
//            selectImageType()
//        }


        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions(
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }


        binding.btTakePicture.setOnClickListener{
            if (binding.btTakePicture.getText() == "RETAKE PICTURE") {
                notshownext()
            } else if (binding.btTakePicture.getText() == "TAKE PICTURE") {
                takePhoto()
            }
        }

        binding.btNext.setOnClickListener{
            if(mPhotoFile!=null){
//                val bundle = Bundle()
//                val JsonString: String =
//                    GsonParser.gsonParser!!.toJson(data)
//                bundle.putString("data", JsonString)
//                addInModel()
                try {
                    Navigation.findNavController(root).navigate(
                        R.id.action_nav_auditphoto_to_nav_accessstatus
                    )
                }catch (e:Exception){

                }



            }else   {
                Toast.makeText(requireContext(), "select photo", Toast.LENGTH_LONG).show()

            }
        }

        return root
    }

//     fun addInModel() {
//
//    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        cameraProviderFuture.addListener({

            imagePreview = Preview.Builder().apply {
                setTargetAspectRatio(AspectRatio.RATIO_16_9)
                setTargetRotation(binding.camPrevPhoto.display.rotation)
            }.build()


            imageCapture = ImageCapture.Builder().apply {
                setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                setFlashMode(ImageCapture.FLASH_MODE_AUTO)
            }.build()

            val cameraProvider = cameraProviderFuture.get()
            imagePreview?.setSurfaceProvider(binding.camPrevPhoto.surfaceProvider)
            val camera = cameraProvider.bindToLifecycle(this, cameraSelector,imageCapture, imagePreview)
            binding.camPrevPhoto.implementationMode = PreviewView.ImplementationMode.COMPATIBLE

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                requestPermission()

            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            REQUIRED_PERMISSIONS,
            REQUEST_CODE_PERMISSIONS
        )
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }


    private fun takePhoto() {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File =
            File(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "karebo")
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            Log.e("karebo", "failed to create directory")
        }
        val photofile = File(storageDir.path + File.separator + imageFileName)
        Log.e("our file", photofile.toString())


        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photofile).build()
        imageCapture?.takePicture(outputFileOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
               // val msg = "Photo capture succeeded: ${photofile.absolutePath}"
                Log.e("TAG", "onImageSaved: " + "success")

                photouri = FileProvider.getUriForFile(
                    requireContext(), BuildConfig.APPLICATION_ID + ".provider",
                    photofile
                )
                try {
                    // mPhotoFile = mCompressor.compressToFile(photofile)
                    mPhotoFile = photofile
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                mPhotoFile = File(mPhotoFile!!.path)
                addAuditPhoto()

//                shownext()
            }

            override fun onError(exception: ImageCaptureException) {
                val msg = "Photo capture failed: ${exception.message}"
                Log.e("TAG", "Photo capture failed:" + exception.localizedMessage)

            }
        })


    }



    fun addAuditPhoto() {

        activity?.runOnUiThread(Runnable {
        LoaderHelper.showLoader(requireContext())
        })
        shownext()


        val newUUID = UUID.randomUUID()

//        val client = ApiClient()
//        val api = client.getClient()?.create(Api::class.java)


        val base64Image: String = ConstantHelper.getBase64(mPhotoFile!!)

        Log.e("TAG", "addAuditPhoto64: " + base64Image)

        var body = JSONObject()
        var geoLocation = JSONObject()


        body.put("image",base64Image)
        body.put("type",0)

        geoLocation.put("latitude",locationn?.latitude)
        geoLocation.put("longitude",locationn?.longitude)
        geoLocation.put("accuracy",locationn?.accuracy)
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
        val formattedDate = sdf.format(Date())
        geoLocation.put("timestamp",formattedDate)

        body.put("geoLocation",geoLocation)

        body.put("score",0)

        Log.e("TAG", "addAuditPhoto body: " + body.toString())

        ConstantHelper.photoList.add( photoUploadDataClass( newUUID.toString(),body.toString()) )
        LoaderHelper.dissmissLoader()
        ConstantHelper.PropertyPictureUUID=newUUID.toString()
        shownext2()

//        val call = api?.addPhoto64(newUUID.toString(),body.toString())
//        call?.enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(
//                call: Call<ResponseBody>,
//                response: Response<ResponseBody>
//            ) {
//
//                if(response.isSuccessful){
//                    var statuscode=response.code()
//                    Log.e("TAG", "Statuscode of AuditPhoto " + statuscode)
//
//                    if(statuscode==200){
//
//                        LoaderHelper.dissmissLoader()
//                        ConstantHelper.PropertyPictureUUID=newUUID.toString()
//                        shownext2()
//                        Log.e("TAG", "Audotphoto: "+response.body()?.string(), )
//
//                    }
//                    else    {
//                        LoaderHelper.dissmissLoader()
//                        ConstantHelper.PropertyPictureUUID=""
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
//                    ConstantHelper.PropertyPictureUUID=""
//                    Toast.makeText(requireContext(),
//                        response.errorBody()?.string(), Toast.LENGTH_SHORT)
//                        .show()
//                }
//
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                LoaderHelper.dissmissLoader()
//                ConstantHelper.PropertyPictureUUID=""
//                Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT)
//                    .show()
//            }
//
//        })

    }


    fun shownext() {


        activity?.runOnUiThread(Runnable {
            Glide.with(requireContext())
                .load(mPhotoFile)
                .into(binding.ivPhoto)
//            binding.btTakePicture.setText("RETAKE PICTURE")
            binding.camPrevPhoto.visibility = View.INVISIBLE
            binding.ivPhoto.setVisibility(View.VISIBLE)
//            binding.btNext.setVisibility(View.VISIBLE)
        })


    }


    fun shownext2() {


        activity?.runOnUiThread(Runnable {
            Glide.with(requireContext())
                .load(mPhotoFile)
                .into(binding.ivPhoto)
            binding.btTakePicture.setText("RETAKE PICTURE")
            binding.camPrevPhoto.visibility = View.INVISIBLE
            binding.ivPhoto.setVisibility(View.VISIBLE)
            binding.btNext.setVisibility(View.VISIBLE)
        })


    }

    fun notshownext() {
        binding.btTakePicture.setText("TAKE PICTURE")
        binding.camPrevPhoto.visibility = View.VISIBLE
        binding.ivPhoto.setVisibility(View.INVISIBLE)
        binding.btNext.setVisibility(View.INVISIBLE)
    }



    private fun requestlocationPermission() {

        when {
            hasPermissions(requireContext(), *permission) -> {
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
    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    private fun locationPermission() {
        locationPermissionLauncher.launch(permission)
    }







    fun findAddress(latitude:Double,longitude:Double):String
    {
        var addresses: List<Address?> = listOf()
        var geocoder = Geocoder(requireContext(), Locale.getDefault())
        var returnAddress:String

        try {
            addresses = geocoder.getFromLocation(latitude ,longitude , 1);

        }catch (e:Exception){

        }
        if(addresses != null && !addresses.isEmpty()){
            var address=addresses[0]?.getAddressLine(0)
            address=address?.replace(", South Africa","")
            address=address?.replace("South Africa","")
            returnAddress=address!!
        }
        else{
            returnAddress= "No Address on "+ "("+latitude+","+longitude+")";
        }

        return returnAddress


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
                    R.id.action_nav_auditphoto_to_nav_meteraudit
                )
                true
            }
            R.id.action_logout -> {
                SharedPreferenceHelper.getInstance(requireContext()).clearData()
                Navigation.findNavController(binding.root).navigate(
                    R.id.action_nav_auditphoto_to_nav_about
                )

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
       return super.onOptionsItemSelected(item);
    }



}