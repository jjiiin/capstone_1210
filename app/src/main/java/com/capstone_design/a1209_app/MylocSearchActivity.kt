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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.capstone_design.a1209_app.databinding.ActivityMylocSearchBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.Api
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import net.daum.android.map.MapView
import java.io.IOError
import java.io.IOException
import java.util.concurrent.TimeUnit

class MylocSearchActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    private lateinit var mView: com.google.android.gms.maps.MapView
    val permission=arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION)
    val PERM_FLAG=99
    private var mMap: GoogleMap? =null
    internal lateinit var mLastLocation: Location
    internal var mCurrentLocationMarker:Marker?=null
    internal var mGoogleApiClient:GoogleApiClient?=null
    internal lateinit var mLocationRequest: LocationRequest
    var myloc=false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myloc_search)


        val mapFragment=supportFragmentManager
            .findFragmentById(R.id.mapView)as SupportMapFragment
        mapFragment.getMapAsync(this)
        Toast.makeText(this, "맵뷰",Toast.LENGTH_LONG).show()
//        if(isPermitted()){
//            //onMapReady함수 호출
//            startProcess()
//        }else{
//            ActivityCompat.requestPermissions(this,permission,PERM_FLAG)
//        }

        val myLocBtn:ImageView=findViewById(R.id.myloc)
        myLocBtn.setOnClickListener{
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            setUpdateLocationListener()
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap=p0
        //현재 내 위치 가져오는
        if(myloc) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            setUpdateLocationListener()
        }
        //영어 유튜브
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
//        val sydney=LatLng(-34.0,151.0)
//        mMap!!.addMarker(MarkerOptions().position(sydney).title("sydney"))
//        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))
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

        mMap!!.clear()
        mCurrentLocationMarker=mMap!!.addMarker(markerOptions)
//        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
//        mMap!!.moveCamera(CameraUpdateFactory.zoomTo(25f))
//        val cameraOption = CameraPosition.Builder()
//            .target(latLng)//검색
//            .zoom(17f)
//            .build()
//        val camera=CameraUpdateFactory.newCameraPosition(cameraOption)
//        mMap?.moveCamera(camera)
        if(mGoogleApiClient!=null){
            LocationServices.getFusedLocationProviderClient(this)
        }

    }

    override fun onConnected(p0: Bundle?) {
        mLocationRequest= LocationRequest()
        mLocationRequest.interval=1000
        mLocationRequest.fastestInterval=1000
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
    fun myLocation(view: View){
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            setUpdateLocationListener()

    }

    //검색한 위치
    fun searchLocation(view: View){
        val locationSearch: EditText=findViewById(R.id.searchTv)
        var location:String
        location=locationSearch.text.toString().trim()

//        val setTextView:TextView=findViewById(R.id.setTv)
        var locText:String
//        val setBtn:Button=findViewById(R.id.setBtn)//상세정보 설정하는 액티비티로 넘어가기


        var addressList:List<Address>?=null
        if(location==null||location==""){
            Toast.makeText(this,"주소입력",Toast.LENGTH_LONG).show()
        }else{
            val geoCoder=Geocoder(this)
            try {
               addressList=geoCoder.getFromLocationName(location,1)
            }catch (e:IOException){
                e.printStackTrace()
            }

            val address=addressList!![0]
            Log.d("address",address.toString())
            val latLng=LatLng(address.latitude,address.longitude)
            locText=address.getAddressLine(0).toString()
            bottomSheet(locText)
//            locText=locText.replace("대한민국","")

//            setTextView.text=locText
//            setBtn.setOnClickListener {
//                val intent= Intent(this, DetailAddressActivity::class.java).putExtra("주소",locText)
//                intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                startActivity(intent)
//            }
            //Log.d("address_index",address_index.toString())//대한민국 서울특별시 노원구 화랑로 621
            mMap!!.clear()

            val discripter=getMarkerDrawable(R.drawable.marker)
            val marker=MarkerOptions()
                .position(latLng)
                .title(location)
                .icon(discripter)
            mMap!!.addMarker(marker)
            val cameraOption = CameraPosition.Builder()
                .target(latLng)//현재 위치로 바꿀 것
                .zoom(17f)
                .build()
            val camera=CameraUpdateFactory.newCameraPosition(cameraOption)

            mMap!!.animateCamera(camera)
        }

    }

    //내 위치를 가져오는 코드
    lateinit var fusedLocationClient:FusedLocationProviderClient
    lateinit var locationCallback:LocationCallback
    @SuppressLint("MissingPermission")
    fun setUpdateLocationListener(){
        val locationRequest=LocationRequest.create()
        locationRequest.run{
            priority=LocationRequest.PRIORITY_HIGH_ACCURACY
            interval=1000
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
            Toast.makeText(this, "좌표를 변환하지 못했습니다.", Toast.LENGTH_LONG).show()
        }
        val address=mResultList!![0]
        val locText=address.getAddressLine(0).toString()
        if (mResultList != null) {
            bottomSheet(locText)
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
    fun bottomSheet(loc:String){
        val setTextView:TextView=findViewById(R.id.setTv)
        val setBtn:Button=findViewById(R.id.setBtn)//상세정보 설정하는 액티비티로 넘어가기
        var locText:String = loc
        locText=locText.replace("대한민국","")
        setTextView.text=locText
        setBtn.setOnClickListener {
            val intent= Intent(this, DetailAddressActivity::class.java).putExtra("주소",locText)
            intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }


}