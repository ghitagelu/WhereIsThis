package com.example.whereisthis

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.opencsv.CSVWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {

    var csv =        Environment.getExternalStorageDirectory().path + "/Locations.csv" // Here csv file name is MyCsvFile.csv
    val STORAGE_RQ = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Init
        val btn_convert = findViewById(R.id.button_convert) as Button
        val btn_save_csv = findViewById(R.id.button_save_csv) as Button
        val textView_latitude = findViewById(R.id.editText_decimal_Latitude) as TextView
        val textView_longitude = findViewById(R.id.editText_decimal_Longitude) as TextView
        val textView_dms = findViewById(R.id.textView_DMS) as TextView
        var ID_value:Int = 0
        var DD_long_value:String = ""
        var DD_lat_value:String = ""
        var DMS_value:String = ""
        checkForPermision(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE, "storage", STORAGE_RQ)
//        checkForPermision(android.Manifest.permission.READ_EXTERNAL_STORAGE, "storage", STORAGE_RQ)

        val isNewFileCreated :Boolean = File(csv).createNewFile()

        if(isNewFileCreated){
            println("$csv is created successfully.")
        } else{
            println("already exists.")
        }

        btn_save_csv.setOnClickListener {
            btn_convert.performClick()

            DMS_value = textView_dms.text.toString()
            val output = ID_value.toString()+",Lat:"+DD_lat_value+"Long:"+DD_long_value+",DMS:"+DMS_value

//            checkForPermision(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, "storage", STORAGE_RQ)
            writeCSV(ID_value,"Lat:"+DD_lat_value,"Long:"+DD_long_value, DMS_value)
            ID_value++
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
        val result :String = lat_degrees.toString()+""+lat_minutes.toString()+"'"+lat_seconds.toString()+"''"+"N   "+long_degrees.toString()+"°"+long_minutes.toString()+"'"+long_seconds.toString()+"''"+ "E"
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


    fun writeCSV(ID:Int, lat :String,long :String, DMS: String)
    {
        var writer : CSVWriter? = null;

        try {
            writer = CSVWriter(FileWriter(csv))
            val data: MutableList<Array<String>> = ArrayList()
            data.add(arrayOf(ID.toString(), lat,long,DMS))
            writer.writeAll(data) // data is adding to csv
            try {
                writer.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        } catch (e: IOException) {
            Toast.makeText(
                this@MainActivity,
                "FAiled",

                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }

    }

    fun checkForPermision(permission:String, name:String, requestCode:Int)
    {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            when{
                ContextCompat.checkSelfPermission(applicationContext, permission) == PackageManager.PERMISSION_GRANTED ->{
                    Toast.makeText(
                        this@MainActivity,
                        "$name permission granted",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                shouldShowRequestPermissionRationale(permission) -> showDialog(permission,name,requestCode)
                else -> ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)


            }
        }

    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {

        fun innerCheck(name:String)
       {
           if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
           {
               Toast.makeText(
                   this@MainActivity,
                   "$name Permission refused",
                   Toast.LENGTH_SHORT
               ).show()
           }else
           {
               Toast.makeText(
                   this@MainActivity,
                   "$name Permission granted",
                   Toast.LENGTH_SHORT
               ).show()
           }
       }
        when(requestCode){
            STORAGE_RQ -> innerCheck("storage")

        }
    }

    fun showDialog(permission:String, name:String, requestCode:Int){
        val builder = AlertDialog.Builder(this)

        builder.apply {
            setMessage("Permission to acces your $name is required to use this app")
            setTitle("Permission required")
            setPositiveButton("OK"){
                dialog, which ->
                ActivityCompat.requestPermissions( this@MainActivity, arrayOf(permission),requestCode)
            }
        }
        val dialog = builder.create()
        dialog.show()
    }
}