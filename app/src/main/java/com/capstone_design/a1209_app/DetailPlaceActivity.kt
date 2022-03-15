package com.capstone_design.a1209_app

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class DetailPlaceActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var latLng:LatLng
    private var mMap: GoogleMap? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_place)

        var lat= intent.getStringExtra("lat")!!.toDouble()
        var lng=intent.getStringExtra("lng")!!.toDouble()
        latLng= LatLng(lat,lng)


        val placeTv:TextView=findViewById(R.id.setTv)
        placeTv.text=intent.getStringExtra("place")

        val mapFragment=supportFragmentManager
            .findFragmentById(R.id.mapView)as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    override fun onMapReady(p0: GoogleMap) {
        mMap=p0
        mMap!!.clear()
        val discripter=getMarkerDrawable(R.drawable.marker)
        val marker= MarkerOptions()
            .position(latLng)
            .title("place search")
            .icon(discripter)
        mMap?.addMarker(marker)
        val cameraOption = CameraPosition.Builder()
            .target(latLng)//현재 위치로 바꿀 것
            .zoom(17f)
            .build()
        val camera= CameraUpdateFactory.newCameraPosition(cameraOption)
        mMap?.moveCamera(camera)
    }
    fun getMarkerDrawable(drawableId:Int): BitmapDescriptor {
        //마커 아이콘 만들기
        var bitmapDrawable: BitmapDrawable
        bitmapDrawable=resources.getDrawable(drawableId)as BitmapDrawable

        //마커 크기 변환(크게)
        val scaleBitmap= Bitmap.createScaledBitmap(bitmapDrawable.bitmap,150,235,false)
        return BitmapDescriptorFactory.fromBitmap(scaleBitmap)
    }
}