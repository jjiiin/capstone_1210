package com.capstone_design.a1209_app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.io.IOException
import java.util.*
import kotlin.properties.Delegates


class MylocSearchActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener{
    private lateinit var  autocompleteSupportFragment: AutocompleteSupportFragment
    private var markerSet:Boolean?=null
    private var mMap: GoogleMap? =null
    internal lateinit var mLastLocation: Location
    internal var mCurrentLocationMarker:Marker?=null
    internal var mGoogleApiClient:GoogleApiClient?=null
    internal lateinit var mLocationRequest: LocationRequest
    var placesClient: PlacesClient? = null
    private var page:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myloc_search)

        if (!Places.isInitialized()) {

            //혜경 places api 코드
            Places.initialize(applicationContext, "AIzaSyB0OunEJ4_hunrp-55YY92DIn3ZZi8oNAY", Locale.KOREA)

            //지인님 api 코드
            //Places.initialize(applicationContext, "AIzaSyCtnkZceEqVUR4_aTLTP2gPtAZcnhe6fjE", Locale.KOREA)
        }
        placesClient = Places.createClient(this)

       autocompleteSupportFragment = (supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
               as AutocompleteSupportFragment?)!!

        page=intent.getStringExtra("page").toString()


        val mapFragment=supportFragmentManager
            .findFragmentById(R.id.mapView)as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(p0: GoogleMap) {
        mMap=p0

        mMap!!.setOnMyLocationButtonClickListener(this)
        mMap!!.setOnMyLocationClickListener(this)
        mMap!!.uiSettings.isMyLocationButtonEnabled=false
        //현재 내 위치 가져오는
        val myLoc:ImageView=findViewById(R.id.myloc)
        myLoc.setOnClickListener {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            setUpdateLocationListener()
        }

        //지도 클릭시 마커 가져오기
        mMap!!.setOnMapClickListener{
            markerSet=false
            makeMarker(mMap!!,it)
        }

        autocompleteSupportFragment!!.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )
        )


        autocompleteSupportFragment?.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place) {
                //Toast.makeText(applicationContext, "" + p0!!.name + p0!!.latLng, Toast.LENGTH_LONG)
                    //.show()
                val address = p0.address.toString()
                val name = p0.name.toString()
                val latlong = "${p0.latLng?.latitude!!}::${p0.latLng?.longitude!!}"
                val latlng=LatLng(p0.latLng?.latitude!!,p0.latLng?.longitude!!)

                //name빼고 뜸 그럼 다행이구먼!
                mMap!!.clear()
                val discripter=getMarkerDrawable(R.drawable.marker)
                val marker=MarkerOptions()
                    .position(latlng)
                    .title("place search")
                    .icon(discripter)
                mMap?.addMarker(marker)
                val cameraOption = CameraPosition.Builder()
                    .target(latlng)//현재 위치로 바꿀 것
                    .zoom(17f)
                    .build()
                val camera=CameraUpdateFactory.newCameraPosition(cameraOption)
                mMap?.moveCamera(camera)
                bottomSheet(address,latlng)

            }

            override fun onError(p0: Status) {
                Log.d("error_api",p0.toString())
            }
        })


//        영어 유튜브
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
            ){
                buildGoogleApiClient()
                mMap!!.isMyLocationEnabled=true
            }
        }else{
            buildGoogleApiClient()
            mMap!!.isMyLocationEnabled=true
        }

    }


    fun makeMarker(googleMap: GoogleMap, latLng: LatLng){
        mMap=googleMap
        mMap!!.clear()
        val discripter=getMarkerDrawable(R.drawable.marker)
        val markerOptions=MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("touch")
        markerOptions.icon(discripter)
        mMap!!.addMarker(markerOptions)

        val cameraOption = CameraPosition.Builder()
            .target(latLng)//현재 위치로 바꿀 것
            .zoom(17f)
            .build()
        val camera=CameraUpdateFactory.newCameraPosition(cameraOption)

        mMap!!.moveCamera(camera)
                //현재 위치(좌표계)를 주소로 받아오기
        var mResultList: List<Address>? = null
        Log.d("makeMarker",mResultList.toString())
        val geoCoder=Geocoder(this)
        try{
            mResultList = geoCoder.getFromLocation(
                latLng.latitude,latLng.longitude, 1
            )
        }catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "좌표를 변환하지 못했습니다.", Toast.LENGTH_LONG).show()
        }
        if (mResultList != null) {
            val address=mResultList!![0]
            val locText=address.getAddressLine(0).toString()
            bottomSheet(locText,latLng)
        }
    }


    protected fun buildGoogleApiClient(){
        mGoogleApiClient=GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
        mGoogleApiClient!!.connect()
    }

    override fun onLocationChanged(p0: Location) {
        mLastLocation=p0
        if(mCurrentLocationMarker!=null){
            mCurrentLocationMarker!!.remove()
        }

        val latLng=LatLng(p0.latitude,p0.longitude)
        Log.d("onLocationChanged",latLng.toString())
        val markerOptions=MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("current")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        var locText:String
        var addressList:List<Address>?=null
        val geoCoder=Geocoder(this)
        try {
            addressList=geoCoder.getFromLocation(
                p0.latitude,p0.longitude, 1
            )
        }catch (e:IOException){
            e.printStackTrace()
        }

        val address=addressList!![0]
        Log.d("address",address.toString())
        locText=address.getAddressLine(0).toString()
        bottomSheet(locText,latLng)
        mMap!!.clear()
        mCurrentLocationMarker=mMap!!.addMarker(markerOptions)
//        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        //mMap!!.moveCamera(CameraUpdateFactory.zoomTo(30f))
        val cameraOption = CameraPosition.Builder()
            .target(latLng)//검색
            .zoom(17f)
            .build()
        val camera=CameraUpdateFactory.newCameraPosition(cameraOption)
        mMap?.moveCamera(camera)
        if(mGoogleApiClient!=null){
            LocationServices.getFusedLocationProviderClient(this)
        }

    }

    override fun onConnected(p0: Bundle?) {
        mLocationRequest= LocationRequest()
        mLocationRequest.interval=30000
        mLocationRequest.fastestInterval=30000
        mLocationRequest.priority=LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        )==PackageManager.PERMISSION_GRANTED){
            LocationServices.getFusedLocationProviderClient(this)
        }
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }


    //내 위치를 가져오는 코드
    lateinit var fusedLocationClient:FusedLocationProviderClient
    lateinit var locationCallback:LocationCallback
    @SuppressLint("MissingPermission")
    fun setUpdateLocationListener(){
        val locationRequest=LocationRequest.create()
        locationRequest.run{
            priority=LocationRequest.PRIORITY_HIGH_ACCURACY
//            interval=30000
        }

        locationCallback=object  : LocationCallback (){
            override fun onLocationResult(p0: LocationResult) {
                p0?.let{
                    for ((i,location)  in it.locations.withIndex()){
                        Log.d("로케이션","$i ${location.latitude},${location.longitude}")
                        setLastLocation(location)
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper())
        //로케이션 요청 함수 호출
    }
    //현재 위치 나타내는
    fun setLastLocation(location: Location){
        val myLocation=LatLng(location.latitude,location.longitude)
        Log.d("setLastLocation",myLocation.toString())
        //현재 위치(좌표계)를 주소로 받아오기
        var mResultList: List<Address>? = null
        val geoCoder=Geocoder(this)
        try{
            mResultList = geoCoder.getFromLocation(
                location.latitude,location.longitude, 1
            )
        }catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "좌표를 변환하지 못했습니다. make", Toast.LENGTH_LONG).show()
        }
        val address=mResultList!![0]
        val locText=address.getAddressLine(0).toString()
        if (mResultList != null) {
            bottomSheet(locText,myLocation)
        }

        val discripter=getMarkerDrawable(R.drawable.marker)
        val marker=MarkerOptions()
            .position(myLocation)
            .title("I'm here")
            .icon(discripter)

        val cameraOption = CameraPosition.Builder()
            .target(myLocation)//현재 위치로 바꿀 것
            .zoom(17f)
            .build()
        val camera=CameraUpdateFactory.newCameraPosition(cameraOption)

        mMap?.clear()
        mMap?.addMarker(marker)
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
    fun bottomSheet(loc:String,latLng: LatLng){
        val setTextView:TextView=findViewById(R.id.setTv)
        val setBtn:Button=findViewById(R.id.setBtn)//상세정보 설정하는 액티비티로 넘어가기
        var locText:String = loc
        locText=locText.replace("대한민국 ","")
        setTextView.text=locText
        setBtn.setOnClickListener {
            val intent= Intent(this, DetailAddressActivity::class.java)
                .putExtra("주소",locText)
                .putExtra("위도",latLng.latitude)
                .putExtra("경도",latLng.longitude)
                .putExtra("page",page)
            intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
            .show()
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this, "Current location:\n$p0", Toast.LENGTH_LONG)
            .show()
    }


}