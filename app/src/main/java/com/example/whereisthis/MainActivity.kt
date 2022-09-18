package com.example.whereisthis

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.Uri.fromParts
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.opencsv.CSVWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.net.URI
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {

    var csv =        Environment.getExternalStorageDirectory().path + "/Locations.csv" // Here csv file name is MyCsvFile.csv
    val STORAGE_RQ = 101
    val data: MutableList<Array<String>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        //Init
        val btn_convert = findViewById(R.id.button_convert) as Button
        val btn_save_csv = findViewById(R.id.button_save_csv) as Button
        val btn_save_item = findViewById(R.id.button_save_item) as Button
        val textView_latitude = findViewById(R.id.editText_decimal_Latitude) as TextView
        val textView_longitude = findViewById(R.id.editText_decimal_Longitude) as TextView
        val textView_dms = findViewById(R.id.textView_DMS) as TextView
        var ID_value:Int = 0
        var DD_long_value:String = ""
        var DD_lat_value:String = ""
        var DMS_value:String = ""
//        checkForPermision(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE, "storage", STORAGE_RQ)
//        checkForPermision(android.Manifest.permission.READ_EXTERNAL_STORAGE, "storage", STORAGE_RQ)



        btn_save_item.setOnClickListener{
            btn_convert.performClick()
            DMS_value = textView_dms.text.toString()
            data.add(arrayOf(ID_value.toString(),DD_lat_value,DD_long_value, DMS_value))

            Toast.makeText(
                this@MainActivity,
                " Data No.$ID_value saved !",
                Toast.LENGTH_SHORT
            ).show()
            ID_value++
        }
        btn_save_csv.setOnClickListener {

            if(checkPermission()){
                Toast.makeText(
                    this@MainActivity,
                    "Please wait, file is generating...",
                    Toast.LENGTH_SHORT
                ).show()

                writeCSV()

                val builder = VmPolicy.Builder()
                StrictMode.setVmPolicy(builder.build())
                val emailIntent = Intent(Intent.ACTION_SEND)
                emailIntent.type = "text/plain"
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("email@example.com"))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "subject here")
                emailIntent.putExtra(Intent.EXTRA_TEXT, "body text")

                val file = File(csv)
                val uri = Uri.fromFile(file)
                emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
                startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"))
            }else
            {
                requestPermission()
                btn_save_csv.performClick()
            }
        }

        btn_convert.setOnClickListener {

            if(textView_latitude.text.isNotEmpty() && textView_longitude.text.isNotEmpty())
            {
                val latitude : Double = (textView_latitude.text.toString()).toDouble()
                val longitude : Double = (textView_longitude.text.toString()).toDouble()

                DD_long_value = longitude.toString()
                DD_lat_value =latitude.toString()

                convert_to_dms(latitude, longitude)

            }

            if(textView_longitude.text.isEmpty()&& textView_latitude.text.isEmpty()){
                Toast.makeText(
                    this@MainActivity,
                    "Please input Latitude and Longitude",
                    Toast.LENGTH_SHORT
                ).show()

            } else if(textView_longitude.text.isEmpty()){
                Toast.makeText(
                    this@MainActivity,
                    "Please input Longitude",
                    Toast.LENGTH_SHORT
                ).show()
            } else if(textView_latitude.text.isEmpty()){
                Toast.makeText(
                    this@MainActivity,
                    "Please input Latitude",
                    Toast.LENGTH_SHORT
                ).show()
            }

//            val result :String = "10°"+latitude+"'"+"22''"+"N   "   +"10°"+longitude+"'"+"22''"+ "S"
//            textView_dms.text = result
//            Toast.makeText(
//                this@MainActivity,
//                result,
//
//                Toast.LENGTH_SHORT
//            ).show()
        }

        data.add(arrayOf("ID","Latitude","Longitude", "DMS_value"))
        btn_convert.performClick()
    }

    fun convert_to_dms(value_latitude : Double, value_longitude: Double)
    {

        val lat_degrees = value_latitude.toInt()
        val lat_minutes = ((value_latitude - lat_degrees)*60).toInt()
        val lat_seconds_result =(value_latitude - lat_degrees - (lat_minutes.toDouble()/60)) * 3600
        val lat_seconds = (lat_seconds_result * 100.0).roundToInt() / 100.0

        val long_degrees = value_longitude.toInt()
        val long_minutes = ((value_longitude - long_degrees)*60).toInt()
        val long_seconds_result =(value_longitude - long_degrees - (long_minutes.toDouble()/60)) * 3600
        val long_seconds = (long_seconds_result * 100.0).roundToInt() / 100.0


        val textView_dms = findViewById(R.id.textView_DMS) as TextView
        val result :String = lat_degrees.toString()+"°"+lat_minutes.toString()+"'"+lat_seconds.toString()+"''"+"N   "+long_degrees.toString()+"°"+long_minutes.toString()+"'"+long_seconds.toString()+"''"+ "E"
        textView_dms.text = result
//
//
//            Toast.makeText(
//                this@MainActivity,
//                testing.toString(),
//
//                Toast.LENGTH_SHORT
//            ).show()
    }


    fun writeCSV()
    {
        var writer : CSVWriter? = null;

        try {
            writer = CSVWriter(FileWriter(csv))
            writer.writeAll(data) // data is adding to csv
            try {
                writer.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        } catch (e: IOException) {
            Toast.makeText(
                this@MainActivity,
                "Permissions not granted",

                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }

    }

    fun requestPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R)
        {
            try{
            val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package",this.packageName, null)
                intent.data = uri
                storageActivityResultLauncher.launch(intent)

            }catch(e:Exception){
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                storageActivityResultLauncher.launch(intent)
            }
        }else{
            //Android below 11
            ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_RQ
            )
        }
    }
    val storageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            if(Environment.isExternalStorageManager()){
                val isNewFileCreated :Boolean = File(csv).createNewFile()

                if(isNewFileCreated){
                    println("$csv is created successfully.")
                } else{
                    println("already exists.")
                }
            }else{

            }


        }else
        {
            //Android below 11
        }
    }

    fun checkPermission():Boolean{
        return if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            //Android is 11 or above
            Environment.isExternalStorageManager()
        }else{
            //Android below 11
            val write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == STORAGE_RQ)
        {
            if (grantResults.isNotEmpty()){
                val write = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val read = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if(read && write ){
                    val isNewFileCreated :Boolean = File(csv).createNewFile()

                    if(isNewFileCreated){
                        println("$csv is created successfully.")
                    } else{
                        println("already exists.")
                    }
                }else{

                }

            }
        }
    }

}