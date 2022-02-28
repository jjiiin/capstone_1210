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
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.capstone_design.a1209_app.board.BoardWirteActivity
import com.capstone_design.a1209_app.dataModels.addressData
import com.capstone_design.a1209_app.dataModels.dataModel
import com.capstone_design.a1209_app.databinding.FragmentMapHomeBinding
import com.capstone_design.a1209_app.fragment.HomeFragment
import com.capstone_design.a1209_app.utils.FBRef
import com.capstone_design.map_test.FragmentListener
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.jar.Manifest


class MapHomeFragment : Fragment(), FragmentListener, OnMapReadyCallback {
    private  lateinit var binding : FragmentMapHomeBinding
    private lateinit var mFragmentListener: FragmentListener
    val viewPagerList= mutableListOf<dataModel>()
    private lateinit var auth: FirebaseAuth
    private lateinit var mView: MapView
    private lateinit var mMap:GoogleMap
    lateinit var mainActivity: MainActivity
    private lateinit var myLatLng:LatLng
    private lateinit var cardView:CardView

    //viewpager
    private var bannerPosition = Int.MAX_VALUE/2


    private val intervalTime = 1500.toLong()

    val permission=arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION)
    val PERM_FLAG=99
    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity =context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_map_home, container, false)

        auth = Firebase.auth
        val database = Firebase.database
        val schRef:DatabaseReference =
            database.getReference("users").child(auth.currentUser?.uid.toString()).child("address")
        schRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (DataModel in snapshot.children) {
                    val item = DataModel.getValue(addressData::class.java)
                    if (item != null) {
                        if(item.set=="1")
                            binding.addressTv.text=item.address
                        Log.d("mhf","호출")
                        //좌표 가져와서 지도에 초점 맞추기
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        binding.mapGo.setOnClickListener {
            val intent = Intent(context, AddressSearchActivity::class.java).putExtra("mhf","1")
            startActivity(intent)
        }
        mView=binding.mapView
        binding.writeBtn.setOnClickListener {
            val intent = Intent(context, BoardWirteActivity::class.java)
            startActivity(intent)
        }
        mView.onCreate(savedInstanceState)


        //viewpager
//
//        val viewPagerAdpater=activity.let {bannerAdapter(viewPagerList)}
//        val boardRef :DatabaseReference= database.getReference("map_contents")
//        boardRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for (DataModel in snapshot.children) {
//                    val item = DataModel.getValue(dataModel::class.java)
//                    if (item != null) {
//                        viewPagerList.add(item)
//                    }
//                    viewPagerAdpater?.notifyDataSetChanged()
//
//                    }
//                }
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })
//        viewPager=binding.viewPager
//        viewPager.orientation= ViewPager2.ORIENTATION_HORIZONTAL
//        viewPager.setCurrentItem(bannerPosition,false)
        cardView=binding.cardView







        if(isPermitted()){
            //onMapReady함수 호출
            Log.d("mhf","startProcess")
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


        auth = Firebase.auth
        val database = Firebase.database
        val schRef :DatabaseReference= database.getReference("users").child(auth.currentUser?.uid.toString()).child("address")
        schRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (DataModel in snapshot.children) {
                    val item = DataModel.getValue(addressData::class.java)
                    if (item != null) {
                        if (item.set=="1") {
                            val myLocation=LatLng(item.lat.toDouble(),item.lng.toDouble())
                            val discripter = getMarkerDrawable(R.drawable.marker)
                            val marker = MarkerOptions()
                                .position(myLocation)
                                .title(item.name)
                                .icon(discripter)
                            //Log.d("item",item.toString())
                            val cameraOption = CameraPosition.Builder()
                                .target(myLocation)//현재 위치로 바꿀 것
                                .zoom(17f)
                                .build()
                            val camera = CameraUpdateFactory.newCameraPosition(cameraOption)

//                            mMap.addMarker(marker)
                            mMap.moveCamera(camera)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        val boardRef :DatabaseReference= database.getReference("map_contents")
        boardRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (DataModel in snapshot.children) {
                    val item = DataModel.getValue(dataModel::class.java)
                    if (item != null) {
                        val latLng=LatLng(item.lat.toDouble(),item.lng.toDouble())
                        val discripter = getMarkerDrawable(R.drawable.marker)
                        val markerOptions = MarkerOptions()
                            .position(latLng)
                            .icon(discripter)
                        val marker: Marker? =mMap!!.addMarker(markerOptions)
                        marker!!.tag=item.title+"/"+item.category+"/"+
                                item.place+"/"+
                                item.time+"/"+item.fee+"/"+item.person+"/"+item.lat+"/"+item.lng

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        //마커 클릭 시 카드 뷰 보이게 하기
        mMap!!.setOnMarkerClickListener (object :GoogleMap.OnMarkerClickListener{
            override fun onMarkerClick(p0: Marker): Boolean {
                val title: TextView =cardView.findViewById(R.id.item_title)
                val place: TextView =cardView.findViewById(R.id.item_place)
                val time: TextView =cardView.findViewById(R.id.item_time)
                val fee: TextView =cardView.findViewById(R.id.item_fee)
                val person: TextView =cardView.findViewById(R.id.item_person)
                val img: ImageView =cardView.findViewById(R.id.item_image)
                var arr=p0.tag.toString().split("/")
                title.text=arr[0]
                place.text=arr[2]
                time.text=arr[3]
                fee.text=arr[4]
                person.text=arr[5]
                when(arr[1]){
                    "asian"->img.setImageResource(R.drawable.asian)
                    "bun"->img.setImageResource(R.drawable.bun)
                    "bento"->img.setImageResource(R.drawable.bento)
                    "chicken"->img.setImageResource(R.drawable.chicken)
                    "pizza"->img.setImageResource(R.drawable.pizza)
                    "fastfood"->img.setImageResource(R.drawable.fastfood)
                    "japan"->img.setImageResource(R.drawable.japan)
                    "korean"->img.setImageResource(R.drawable.korean)
                    "cafe"->img.setImageResource(R.drawable.cafe)
                    "chi"->img.setImageResource(R.drawable.china)
                }
                cardView.visibility=View.VISIBLE
                return false
            }


        })
        mMap!!.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
            override fun onMapClick(latLng: LatLng) {
                cardView.visibility = View.GONE
            }
        })




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
        bitmapDrawable=mainActivity.resources.getDrawable(drawableId)as BitmapDrawable

        //마커 크기 변환(크게)
        val scaleBitmap= Bitmap.createScaledBitmap(bitmapDrawable.bitmap,150,235,false)
        return BitmapDescriptorFactory.fromBitmap(scaleBitmap)
    }


}