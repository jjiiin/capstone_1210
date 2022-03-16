package com.capstone_design.a1209_app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.*


//도로명 주소api로 주소 찾기
class WebSearchActivity : AppCompatActivity() {
    var placesClient: PlacesClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_search)

        if (!Places.isInitialized()) {
            //혜경 places api 코드
            Places.initialize(applicationContext, "AIzaSyB0OunEJ4_hunrp-55YY92DIn3ZZi8oNAY", Locale.KOREA)

            //지인님 api 코드
            //Places.initialize(applicationContext, "AIzaSyCtnkZceEqVUR4_aTLTP2gPtAZcnhe6fjE", Locale.KOREA)
        }
        placesClient = Places.createClient(this)

        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?
        autocompleteFragment!!.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )
        )


        autocompleteFragment?.setOnPlaceSelectedListener(object :PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place) {
                Toast.makeText(applicationContext, "" + p0!!.name + p0!!.latLng, Toast.LENGTH_LONG)
                    .show()
                val address = p0.address.toString()
                val name = p0.name.toString()
                val resultIntent = Intent()
                val latlong = "${p0.latLng?.latitude!!}::${p0.latLng?.longitude!!}"



                Toast.makeText(this@WebSearchActivity, latlong + address + name, Toast.LENGTH_LONG).show()
                //name빼고 뜸 그럼 다행이구먼!




            }

            override fun onError(p0: Status) {
                Log.d("error_api",p0.toString())
            }
        })
    }




}



























