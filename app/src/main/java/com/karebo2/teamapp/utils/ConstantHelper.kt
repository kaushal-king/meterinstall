package com.karebo2.teamapp.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.util.Base64
import android.util.Log
import com.karebo2.teamapp.dataclass.meterData.SubJobCard
import com.karebo2.teamapp.dataclass.meterData.meterauditDataModel
import com.karebo2.teamapp.dataclass.photoUploadDataClass
import org.json.JSONObject
import java.io.*


object ConstantHelper {


    const val APP_NAME="mi"
    const val NETWORK_CONNECT="connect"
    const val NETWORK_LOST="lost"
    const val NOTIFICATION_CHANNEL_ID="com.the"
    const val SHARED_PREFERENCE_ID="MyPrefsTheme"

    const val CURRENT_CONNECTION="CurrentConnection"
    const val VOLTAGE_CONNECTION="VoltageConnection"

    const val BEFORE_INSTALL1="BeforeInsatall1"
    const val BEFORE_INSTALL2="BeforeInsatall2"
    const val BEFORE_INSTALL3="BeforeInsatall3"
    const val BEFORE_INSTALL4="BeforeInsatall4"
    const val BEFORE_INSTALL5="BeforeInsatall5"


    const val AFTER_INSTALL1="AfterInsatall1"
    const val AFTER_INSTALL2="AfterInsatall2"
    const val AFTER_INSTALL3="AfterInsatall3"
    const val AFTER_INSTALL4="AfterInsatall4"
    const val AFTER_INSTALL5="AfterInsatall5"

    const val CONFIG_SMS="ConfigSms"
    const val GPRS_SIGNAL="GprsSignal"
    const val ONTECH_CONFIRM="ontechconfirm"
    const val PDF_COMPLETE="Pdf_Complete"

    const val SIM_CARD="SimCard"

    var ADDRESS=""
    var SERIAL=""
    var JOB_TYPE=""

    var PropertyPictureUUID=""
    var  locationn : Location? =null


    var voltagepointUUID=""
    var currentpointUUID=""
    var picture1UUID=""
    var picture2UUID=""
    var picture3UUID=""
    var picture4UUID=""
    var picture5UUID=""

    var voltagepointafterUUID=""
    var currentpointafterUUID=""
    var picture1afterUUID=""
    var picture2afterUUID=""
    var picture3afterUUID=""
    var picture4afterUUID=""
    var picture5afterUUID=""


    var PhotoSmsConfigFileUUID=""
    var PhotoGprsSignalFileUUID=""
    var PhotoOntechFileUUID=""
    var PhotopdfhexUUID=""

//    lateinit var currentSelectd: meterauditDataModel
    lateinit var currentSelectdSubMeter: SubJobCard


    var submitJobCardDataJSON = JSONObject()
    var Meters = JSONObject()
    var TEST0123456 = JSONObject()



    var Components = JSONObject()
    var Feedback = JSONObject()
    var Duration = JSONObject()



     var list: List<meterauditDataModel> =listOf()
     var subMeterlist: List<SubJobCard> =listOf()


     var photoList: MutableList<photoUploadDataClass> = mutableListOf()


    fun getBase64(imgFile: File):String{
        var base64: String=""
        if (imgFile.exists() && imgFile.length() > 0) {
            val bm = BitmapFactory.decodeFile(imgFile.path)
            val bOut = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, 8, bOut)
            val base64Image: String = Base64.encodeToString(bOut.toByteArray(), Base64.DEFAULT)
            base64=base64Image
        }
        return base64

    }

    fun bitmapToBase64(bitmap: Bitmap):String{
        var base64: String=""

            val bm = bitmap
            val bOut = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, 10, bOut)
            val base64Image: String = Base64.encodeToString(bOut.toByteArray(), Base64.DEFAULT)
            base64=base64Image

        return base64

    }


    fun fileToBase64(yourFile: File): String? {
        val size = yourFile.length().toInt()
        val bytes = ByteArray(size)

        try {
            val buf = BufferedInputStream(FileInputStream(yourFile))
            buf.read(bytes, 0, bytes.size)
            buf.close()
        } catch (e: FileNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }


//    @Throws(IOException::class)
//    private fun encodeFileToBase64Binary(fileName: File): String? {
//        val bytes: ByteArray = loadFile(fileName)
//        val encoded: ByteArray = Base64.encodeBase64(bytes)
//        return String(encoded)
//    }


}