package com.example.whereisthis

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceControl
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.io.*
import java.lang.Exception
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import java.sql.Types.NULL
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {


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



        btn_save_csv.setOnClickListener {
            btn_convert.performClick()

            DMS_value = textView_dms.text.toString()
            val output = ID_value.toString()+",Lat:"+DD_lat_value+"Long:"+DD_long_value+",DMS:"+DMS_value
            ID_value++

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
        val result :String = lat_degrees.toString()+"°"+lat_minutes.toString()+"'"+lat_seconds.toString()+"''"+"N   "+long_degrees.toString()+"°"+long_minutes.toString()+"'"+long_seconds.toString()+"''"+ "S"
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





}