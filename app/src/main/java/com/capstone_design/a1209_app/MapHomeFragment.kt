package com.capstone_design.a1209_app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.databinding.DataBindingUtil
import com.capstone_design.a1209_app.board.BoardWirteActivity
import com.capstone_design.a1209_app.databinding.FragmentMapHomeBinding
import com.capstone_design.a1209_app.fragment.HomeFragment
import com.capstone_design.map_test.FragmentListener
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.ktx.userProfileChangeRequest
import java.util.jar.Manifest


class MapHomeFragment : Fragment(), FragmentListener, OnMapReadyCallback {
    private  lateinit var binding : FragmentMapHomeBinding
    private lateinit var mFragmentListener: FragmentListener
    private lateinit var mView: MapView
    private lateinit var mMap:GoogleMap
    lateinit var mainActivity: MainActivity

    val permission=arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION)
    val PERM_FLAG=99
    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity =context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_map_home, container, false)

        mView=binding.mapView
        binding.writeBtn.setOnClickListener {
            val intent = Intent(context, BoardWirteActivity::class.java)
            startActivity(intent)
        }
        mView.onCreate(savedInstanceState)

        if(isPermitted()){
            //onMapReady함수 호출
            startProcess()
        }else{
            requestPermissions(permission,PERM_FLAG)
        }

        //글목록으로 이동
        binding.btn.setOnClickListener {
           //버튼 누르면
            mFragmentListener = parentFragment as HomeFragment
            mFragmentListener.onReceivedData(1)
        }
        return binding.root
    }

    override fun onReceivedData(data: Int) {
    }

    fun startProcess(){
        mView.getMapAsync(this)
    }

    fun isPermitted():Boolean{
        for(perm in permission){
            if(checkSelfPermission(mainActivity,perm)!=PERMISSION_GRANTED){
                return false
            }
        }
        return true
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap=googleMap
        fusedLocationClient=LocationServices.getFusedLocationProviderClient(mainActivity)
        setUpdateLocationListener()
//        val location = LatLng(37.568291,126.997780)//위도 경도
//        //마커 생성하기기
//       val discripter=getMarkerDrawable(R.drawable.marker)
//
//        //마커-지도에 표시하기
//        val marker=MarkerOptions()
//            .position(location)
//            .title("치킨 먹을 사람")
//            .icon(discripter)
//        googleMap.addMarker(marker)
//
//
//        //카메라 위치
//        val cameraOption = CameraPosition.Builder()
//            .target(location)//현재 위치로 바꿀 것
//            .zoom(19f)
//            .build()
//        val camera=CameraUpdateFactory.newCameraPosition(cameraOption)
//
//        googleMap.moveCamera(camera)
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
    fun setLastLocation(location: Location){
        val myLocation=LatLng(location.latitude,location.longitude)
        val discripter=getMarkerDrawable(R.drawable.marker)
        val marker=MarkerOptions()
            .position(myLocation)
            .title("I'm here")
            .icon(discripter)

        val cameraOption = CameraPosition.Builder()
            .target(myLocation)//현재 위치로 바꿀 것
            .zoom(19f)
            .build()
        val camera=CameraUpdateFactory.newCameraPosition(cameraOption)

        mMap.clear()
        mMap.addMarker(marker)
        mMap.moveCamera(camera)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERM_FLAG->{
                var check=true
                for(grant in grantResults){
                    if(grant!= PERMISSION_GRANTED){
                        check = false
                        break
                    }
                }
                if(check){
                    startProcess()
                }else{
                    Toast.makeText(mainActivity, "권한을 승인해야지만 앱 사용가능",Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    override fun onStart() {
        super.onStart()
        mView.onStart()
    }
    override fun onStop() {
        super.onStop()
        mView.onStop()
    }
    override fun onResume() {
        super.onResume()
        mView.onResume()
    }
    override fun onPause() {
        super.onPause()
        mView.onPause()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        mView.onLowMemory()
    }
    override fun onDestroy() {
        mView.onDestroy()
        super.onDestroy()
    }

    fun getMarkerDrawable(drawableId:Int):BitmapDescriptor{
        //마커 아이콘 만들기
        var bitmapDrawable:BitmapDrawable
        bitmapDrawable=resources.getDrawable(drawableId)as BitmapDrawable

        //마커 크기 변환(크게)
        val scaleBitmap= Bitmap.createScaledBitmap(bitmapDrawable.bitmap,150,235,false)
        return BitmapDescriptorFactory.fromBitmap(scaleBitmap)
    }


}