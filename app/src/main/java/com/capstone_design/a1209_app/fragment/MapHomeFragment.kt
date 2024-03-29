package com.capstone_design.a1209_app.fragment

import com.capstone_design.a1209_app.fragment.HomeFragment
import com.capstone_design.a1209_app.fragment.MiniListFragment


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.VolleyLog
import com.capstone_design.a1209_app.Adapter.bannerAdapter
import com.capstone_design.a1209_app.AddressSearchActivity
import com.capstone_design.a1209_app.DetailActivity
import com.capstone_design.a1209_app.MainActivity
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.board.BoardWirteActivity
import com.capstone_design.a1209_app.dataModels.UserData
import com.capstone_design.a1209_app.dataModels.addressData
import com.capstone_design.a1209_app.dataModels.dataModel
import com.capstone_design.a1209_app.dataModels.kwNotiData
import com.capstone_design.a1209_app.databinding.FragmentMapHomeBinding
import com.capstone_design.a1209_app.fcm.NotiModel
import com.capstone_design.a1209_app.fcm.PushNotification
import com.capstone_design.a1209_app.fcm.RetrofitInstance
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef
import com.capstone_design.map_test.FragmentListener
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.NumberFormatException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class MapHomeFragment : Fragment(), FragmentListener, OnMapReadyCallback {
    private lateinit var binding: FragmentMapHomeBinding
    private lateinit var mFragmentListener: FragmentListener
    private var kwNotiList = mutableListOf<kwNotiData>()
    private var kwNotiList_ = mutableListOf<String>()
    private var kwNotiList_temp = mutableListOf<String>()
    private lateinit var auth: FirebaseAuth
    private lateinit var mView: MapView
    private lateinit var mMap: GoogleMap
    lateinit var mainActivity: MainActivity
    private lateinit var myLatLng: LatLng
    private lateinit var cardView: CardView
    private lateinit var viewPager2: ViewPager2
    private lateinit var springDotsIndicator: SpringDotsIndicator
    private lateinit var myLocation: Location
    private val categoryList = mutableListOf<Button>()

    private var category = "all"
    private var cnt = 0
    private var items = mutableListOf<dataModel>()
    private val itemsKeyList = mutableListOf<String>()

    private var keywordList = mutableListOf<String>()
    private var contentList = mutableListOf<dataModel>()

    val database = Firebase.database

    //viewpager
    private var bannerPosition = Int.MAX_VALUE / 2
    private val intervalTime = 1500.toLong()

    //Button
    var cate_all: Button? = null
    var cate_kor: Button? = null
    var cate_asian: Button? = null
    var cate_bun: Button? = null
    var cate_jap: Button? = null
    var cate_chicken: Button? = null
    var cate_pizza: Button? = null
    var cate_fast: Button? = null
    var cate_bento: Button? = null
    var cate_cafe: Button? = null
    var cate_chi: Button? = null

    val permission = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val PERM_FLAG = 99
    var keywordSwitch: Boolean = true

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map_home, container, false)
        getKeywordSwitch()
        auth = Firebase.auth
        val database = Firebase.database
        val schRef: DatabaseReference =
            database.getReference("users").child(auth.currentUser?.uid.toString()).child("address")
        schRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (DataModel in snapshot.children) {
                    val item = DataModel.getValue(addressData::class.java)
                    if (item != null) {
                        if (item.set == "1")
                            binding.addressTv.text = item.address
                        Log.d("mhf", "호출")
                        //좌표 가져와서 지도에 초점 맞추기
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        viewPager2 = binding.viewPager
        springDotsIndicator = binding.springDotsIndicator

        //버튼 클릭시 category에 값 할당하기
        cate_all = binding.categoryAll
        categoryList.add(cate_all!!)
        cate_kor = binding.categoryKor
        categoryList.add(cate_kor!!)
        cate_asian = binding.categoryAsian
        categoryList.add(cate_asian!!)
        cate_bun = binding.categoryBun
        categoryList.add(cate_bun!!)
        cate_jap = binding.categoryJap
        categoryList.add(cate_jap!!)
        cate_chicken = binding.categoryChicken
        categoryList.add(cate_chicken!!)
        cate_pizza = binding.categoryPizza
        categoryList.add(cate_pizza!!)
        cate_fast = binding.categoryFast
        categoryList.add(cate_fast!!)
        cate_bento = binding.categoryDo
        categoryList.add(cate_bento!!)
        cate_cafe = binding.categoryCafe
        categoryList.add(cate_cafe!!)
        cate_chi = binding.categoryChi
        categoryList.add(cate_chi!!)


        //키워드 알림
        // 1. 키워드 리스트 가져오기
        // 2. map-contents에서 item.title 만 놓고 비교하기
        val kwRef = database.getReference("users").child(auth.currentUser!!.uid).child("keyword")
        kwRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (DataModel in snapshot.children) {
                    val item = DataModel.getValue(String::class.java)
                    if (item != null) {
                        keywordList.add(item)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        //키워드 알림 목록 받아오기
        FBRef.usersRef.child(Auth.current_uid).child("kwNoti").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                kwNotiList_.clear()
                for (data in snapshot.children) {
                    var item=data.getValue(kwNotiData::class.java)!!
                    if(item!=null){
                        kwNotiList_.add(item.roomKey)
                        kwNotiList_temp.add(item.roomKey)
                        Log.d("kwnoti_",kwNotiList_.toString())
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        //키워드 알림 - 키워드 리스트와 글 목록 비교하기
        //키워드 알림 켜져있을때만(switch_enterNoti) 키워드 알림보내기
        FBRef.usersRef.child(Auth.current_uid).child("switch_kwNoti").get()
            .addOnSuccessListener {
                if(it.getValue() == null){
                    keywordSwitch = true
                }else{
                    keywordSwitch = it.getValue() as Boolean
                }
                if (keywordSwitch == true) {
                    FBRef.board.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (DataModel in snapshot.children) {
                                val item = DataModel.getValue(dataModel::class.java)
                                if (item != null) {
                                    for (i in keywordList) {
                                        if (item.title.contains(i)) {
                                            if(kwNotiList_.isEmpty()){
                                                notification(i, item.title, item.chatroomkey)
                                                kwNotiList_temp.add(item.chatroomkey)
                                            }else {
                                                if(compareKwnoti(item.chatroomkey)){
                                                    notification(i, item.title, item.chatroomkey)
                                                    kwNotiList_temp.add(item.chatroomkey)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                }
            }


        binding.mapGo.setOnClickListener {
            val intent = Intent(context, AddressSearchActivity::class.java).putExtra("mhf", "1")
                .putExtra("page", "MapHomeFragment")
            startActivity(intent)
        }
        mView = binding.mapView
        binding.writeBtn.setOnClickListener {
            val intent = Intent(context, BoardWirteActivity::class.java)
            startActivity(intent)
        }
        mView.onCreate(savedInstanceState)

        //현재 내 위치 가져오는

        if (isPermitted()) {
            //onMapReady함수 호출
            Log.d("mhf", "startProcess")
            startProcess()
        } else {
            requestPermissions(permission, PERM_FLAG)
        }

        //글목록으로 이동
        binding.btn.setOnClickListener {
            //버튼 누르면
            mFragmentListener = parentFragment as HomeFragment
            mFragmentListener.onReceivedData(1)

        }
        return binding.root
    }

    //글목록-작성 세트 margin값 바꾸는 함수
    fun changeMargin(set: Int) {
        if (set == 1) {
            val linearLayout: LinearLayout = binding.boardSet
            val params = linearLayout.layoutParams as ConstraintLayout.LayoutParams
            params.bottomMargin = 720
            linearLayout.layoutParams = params
        } else {
            val linearLayout: LinearLayout = binding.boardSet
            val params = linearLayout.layoutParams as ConstraintLayout.LayoutParams
            params.bottomMargin = 55
            linearLayout.layoutParams = params
        }
    }

    override fun onReceivedData(data: Int) {
    }

    fun startProcess() {
        mView.getMapAsync(this)
    }

    fun isPermitted(): Boolean {
        for (perm in permission) {
            if (checkSelfPermission(mainActivity, perm) != PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.clear()
        viewPager2.visibility = View.GONE
        springDotsIndicator.visibility = View.GONE
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
        setUpdateLocationListener2()
        markerView("all")
        buttonSelect()

        cate_all?.isSelected = true

        binding.categoryAll.setOnClickListener {
            mMap!!.clear()
            viewPager2.visibility = View.GONE
            springDotsIndicator.visibility = View.GONE
            changeMargin(0)
            markerView("all")
            buttonSelect()
            cate_all?.isSelected = true
            setUpdateLocationListener2()

        }
        binding.categoryAsian.setOnClickListener {
            mMap!!.clear()
            viewPager2.visibility = View.GONE
            springDotsIndicator.visibility = View.GONE
            changeMargin(0)
            markerView("asian")
            buttonSelect()
            cate_asian?.isSelected = true
            setUpdateLocationListener2()
        }
        binding.categoryBun.setOnClickListener {
            mMap!!.clear()
            viewPager2.visibility = View.INVISIBLE
            springDotsIndicator.visibility = View.INVISIBLE
            markerView("bun")
            buttonSelect()
            cate_bun?.isSelected = true
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
            setUpdateLocationListener2()
        }
        binding.categoryChicken.setOnClickListener {
            mMap!!.clear()
            viewPager2.visibility = View.GONE
            springDotsIndicator.visibility = View.GONE
            changeMargin(0)
            markerView("chicken")
            buttonSelect()
            cate_chicken?.isSelected = true
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
            setUpdateLocationListener2()
        }
        binding.categoryPizza.setOnClickListener {
            mMap!!.clear()
            viewPager2.visibility = View.GONE
            springDotsIndicator.visibility = View.GONE
            changeMargin(0)
            markerView("chicken")
            buttonSelect()
            cate_pizza?.isSelected = true
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
            setUpdateLocationListener2()
        }
        binding.categoryFast.setOnClickListener {
            mMap!!.clear()
            viewPager2.visibility = View.GONE
            springDotsIndicator.visibility = View.GONE
            changeMargin(0)
            markerView("fastfood")
            buttonSelect()
            cate_fast?.isSelected = true
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
            setUpdateLocationListener2()
        }
        binding.categoryJap.setOnClickListener {
            mMap!!.clear()
            viewPager2.visibility = View.GONE
            springDotsIndicator.visibility = View.GONE
            changeMargin(0)
            markerView("japan")
            buttonSelect()
            cate_jap?.isSelected = true
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
            setUpdateLocationListener2()
        }
        binding.categoryKor.setOnClickListener {
            mMap!!.clear()
            viewPager2.visibility = View.GONE
            springDotsIndicator.visibility = View.GONE
            changeMargin(0)
            markerView("korean")
            buttonSelect()
            cate_kor?.isSelected = true
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
            setUpdateLocationListener2()
        }
        binding.categoryDo.setOnClickListener {
            mMap!!.clear()
            viewPager2.visibility = View.GONE
            springDotsIndicator.visibility = View.GONE
            changeMargin(0)
            markerView("bento")
            buttonSelect()
            cate_bento?.isSelected = true
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
            setUpdateLocationListener2()

        }
        binding.categoryCafe.setOnClickListener {
            mMap!!.clear()
            viewPager2.visibility = View.GONE
            springDotsIndicator.visibility = View.GONE
            changeMargin(0)
            markerView("cafe")
            buttonSelect()
            cate_cafe?.isSelected = true
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
            setUpdateLocationListener2()

        }
        binding.categoryChi.setOnClickListener {
            mMap!!.clear()
            viewPager2.visibility = View.GONE
            springDotsIndicator.visibility = View.GONE
            changeMargin(0)
            markerView("chi")
            buttonSelect()
            cate_chi?.isSelected = true
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
            setUpdateLocationListener2()

        }
//        //내위치 항상 표시하기
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
//        setUpdateLocationListener2()

        //내위치 버튼 클릭
        binding.myloc.setOnClickListener {
            viewPager2.visibility = View.GONE
            springDotsIndicator.visibility = View.GONE
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
            setUpdateLocationListener()
            setUpdateLocationListener2()
        }

        auth = Firebase.auth
        val database = Firebase.database
        val schRef: DatabaseReference =
            database.getReference("users").child(auth.currentUser?.uid.toString()).child("address")
        schRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (DataModel in snapshot.children) {
                    val item = DataModel.getValue(addressData::class.java)
                    if (item != null) {
                        if (item.set == "1") {
                            val myLocation = LatLng(item.lat.toDouble(), item.lng.toDouble())

                            //Log.d("item",item.toString())
                            val cameraOption = CameraPosition.Builder()
                                .target(myLocation)
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

        //마커 클릭 시 카드 뷰 보이게 하기
        mMap!!.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
            override fun onMarkerClick(p0: Marker): Boolean {
                markerClick(p0.tag.toString(), p0.position)
                if (p0.tag != "mylocation") {
                    changeMargin(1)
                    viewPager2.visibility = View.VISIBLE
                    springDotsIndicator.visibility = View.VISIBLE
                    mMap!!.uiSettings.isMapToolbarEnabled=false
                }
                return false
            }


        })

        //지도 클릭시 마커 원상복구
        mMap!!.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
            override fun onMapClick(latLng: LatLng) {
                viewPager2.visibility = View.GONE
                springDotsIndicator.visibility = View.GONE
                changeMargin(0)
                Log.d("map_lat", latLng.toString())

            }
        })
    }

    //내 위치를 가져오는 코드
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback

    @SuppressLint("MissingPermission")
    fun setUpdateLocationListener() {
        val locationRequest = LocationRequest.create()
        locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0?.let {
                    for ((i, location) in it.locations.withIndex()) {
                        setLastLocation(location)

                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
        //로케이션 요청 함수 호출
    }

    @SuppressLint("MissingPermission")
    fun setUpdateLocationListener2() {
        val locationRequest = LocationRequest.create()
        locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0?.let {
                    for ((i, location) in it.locations.withIndex()) {
                        setDrawLastLocation(location)
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
        //로케이션 요청 함수 호출
    }

    fun setLastLocation(location: Location) {
        val myLocation = LatLng(location.latitude, location.longitude)
//        val discripter=getMarkerDrawable(R.drawable.marker)
//        val marker=MarkerOptions()
//            .position(myLocation)
//            .title("I'm here")
//            .icon(discripter)

        val cameraOption = CameraPosition.Builder()
            .target(myLocation)//현재 위치로 바꿀 것
            .zoom(17f)
            .build()
        val camera = CameraUpdateFactory.newCameraPosition(cameraOption)


        //mMap.addMarker(marker)
        mMap.moveCamera(camera)
    }

    fun setDrawLastLocation(location: Location) {
        val myLocation = LatLng(location.latitude, location.longitude)
        val discripter = getLocDrawable(R.drawable.mylocation)
        val marker = MarkerOptions()
            .position(myLocation)
            .icon(discripter)
        val marker2: Marker? = mMap!!.addMarker(marker)
        marker2!!.tag = "mylocation"

        viewPager2.visibility = View.GONE
        springDotsIndicator.visibility = View.GONE
        changeMargin(0)
//        val cameraOption = CameraPosition.Builder()
//            .target(myLocation)//현재 위치로 바꿀 것
//            .zoom(17f)
//            .build()
//        val camera=CameraUpdateFactory.newCameraPosition(cameraOption)

//        mMap.addMarker(marker)
//        mMap.moveCamera(camera)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERM_FLAG -> {
                var check = true
                for (grant in grantResults) {
                    if (grant != PERMISSION_GRANTED) {
                        check = false
                        break
                    }
                }
                if (check) {
                    startProcess()
                } else {
                    Toast.makeText(mainActivity, "권한을 승인해야지만 앱 사용가능", Toast.LENGTH_LONG).show()
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

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getMarkerDrawable(drawableId: Int): BitmapDescriptor? {
        //마커 아이콘 만들기
        var bitmapDrawable: BitmapDrawable
        bitmapDrawable = mainActivity.resources.getDrawable(drawableId) as BitmapDrawable

        //마커 크기 변환(크게)
        val scaleBitmap = Bitmap.createScaledBitmap(bitmapDrawable.bitmap, 130, 160, false)
        return BitmapDescriptorFactory.fromBitmap(scaleBitmap)

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getLocDrawable(drawableId: Int): BitmapDescriptor {
        //마커 아이콘 만들기
        var bitmapDrawable: BitmapDrawable
        bitmapDrawable = mainActivity.resources.getDrawable(drawableId) as BitmapDrawable

        //마커 크기 변환
        return BitmapDescriptorFactory.fromBitmap(bitmapDrawable.bitmap)
    }

    private fun buttonSelect() {
        for (i in categoryList) {
            i.isSelected = false
        }
    }

    //모든 data담아두는 List
    private var dataList = mutableListOf<dataModel>()
    private var tempList = mutableListOf<dataModel>()
    private fun markerView(category: String) {
        val boardRef: DatabaseReference = database.getReference("map_contents")
        boardRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                tempList.clear()
                itemsKeyList.clear()
                mMap.clear()
                for (data in snapshot.children) {
                    val item = data.getValue(dataModel::class.java)
                    if (item != null) {
                        if (category == "all") {
                            val latLng = LatLng(item.lat.toDouble(), item.lng.toDouble())
                            val discripter = getMarkerDrawable(R.drawable.marker)
                            val markerOptions = MarkerOptions()
                                .position(latLng)
                                .icon(discripter)
                            dataList.add(item)
                            itemsKeyList.add(data.key.toString())
                            //주소가 같은 것이 있으면 제외하기-> continue
                            var con = false
                            for (i in tempList) {
                                if (i.placeAddress == item.placeAddress) {
                                    con = true
                                    break
                                }
                            }
                            if (con) {
                                continue
                            }
                            val marker: Marker? = mMap!!.addMarker(markerOptions)
                            marker!!.tag = item.placeAddress //나중에 place=address+detail 분리하기
                            tempList.add(item)//새로운 주소 목록에 포함.
                        }
                        if (category == item.category) {
                            val latLng = LatLng(item.lat.toDouble(), item.lng.toDouble())
                            val discripter = getMarkerDrawable(R.drawable.marker)
                            val markerOptions = MarkerOptions()
                                .position(latLng)
                                .icon(discripter)
                            dataList.add(item)
                            itemsKeyList.add(data.key.toString())
                            //주소가 같은 것이 있으면 제외하기-> continue
                            var con = false
                            for (i in tempList) {
                                if (i.placeAddress == item.placeAddress) {
                                    con = true
                                }
                            }
                            if (con) {
                                continue
                            }
                            val marker: Marker? = mMap!!.addMarker(markerOptions)
                            marker!!.tag = item.placeAddress //나중에 place=address+detail 분리하기
                            tempList.add(item)//새로운 주소 목록에 포함.
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun markerClick(tag: String, position: LatLng) {
        //마커 클릭 됐을 때 마커 디자인 바꾸기
        if (tag != "mylocation") {
            val discripter = getMarkerDrawable(R.drawable.marker_select)
            val markerOptions = MarkerOptions()
                .position(position)
                .icon(discripter)
            val marker: Marker? = mMap!!.addMarker(markerOptions)
            marker!!.tag = "select"
        }
        //마커가 클릭 됐을 때 같은 주소끼리 배열로 만들어서 viewPager에 보이게 하기
        //같은 placeAddress를 배열로 묶기
        val cardList = ArrayList<dataModel>()
        //val itemKey= mutableListOf<String>()
        val itemKey = ArrayList<String>()
        for (i in 0 until dataList.size) {
            if (dataList[i].placeAddress == tag) {
                cardList.add(dataList[i])
                itemKey.add(itemsKeyList[i])
            }
        }
        Log.d("keyitemkey", cardList.toString())
//        viewPager2.adapter=bannerAdapter(cardList)
        val vpAdapter = bannerAdapter(cardList, this, mainActivity)
        viewPager2.adapter = vpAdapter
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        springDotsIndicator.setViewPager2(viewPager2)

        //viewPager2 클릭 이벤트
        vpAdapter.setItemClickListener(object : bannerAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val intent = Intent(context, DetailActivity::class.java)
                //firebase에 있는 board에 대한 데이터의 id를 가져오기
                intent.putExtra("key", itemKey[position])
                Log.d("keyitemkey", cardList.toString())
                Log.d("keyitem", itemKey.toString())
                Log.d("keyposition", position.toString())
                startActivity(intent)
            }

        })

        //viewpager2의 버튼 클릭 이벤트

        vpAdapter.setBtnClickListener(object : bannerAdapter.OnBtnClickListener {
            override fun onClick(v: View, position: Int) {
                var fragment2 = MiniListFragment()
                var bundle = Bundle()
                bundle.putParcelableArrayList("list", cardList)
                bundle.putStringArrayList("keyList", itemKey)
                fragment2.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌
                parentFragmentManager
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.home, fragment2)
                    .commit()

//                childFragmentManager.beginTransaction()
//                    .replace(R.id.mhfLayout,fragment2).addToBackStack(null).commit()

            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun notification(keyword: String, title: String, roomKey:String) {
        Log.d("kwnoti", keyword)
        val time = Calendar.getInstance().time
        /*  val current = LocalDateTime.now()
          val formatter = DateTimeFormatter.ISO_DATE
          val formatted = current.format(formatter)*/
        val notiModel = NotiModel("Saveat - 키워드알림", "\"${keyword}\" 배달 쉐어가 오픈됐습니다.", time)
        val kwnoti = kwNotiData("\"${keyword}\" 배달 쉐어가 오픈됐습니다.", time,title,roomKey)

        FBRef.usersRef.child(auth.currentUser?.uid.toString()).child("kwNoti").push()
            .setValue(kwnoti)
        val token = true
        if (token) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(VolleyLog.TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                Log.d("kwnoti", token)
                val pushModel = PushNotification(notiModel, "${token}")
                testPush(pushModel)

                // Log and toast
                Log.e("token", token.toString())
            })
        }
    }

    private fun testPush(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        Log.d("kwnoti_test", notification.toString())
        Log.d("pushNoti", notification.toString())
        RetrofitInstance.api.postNotification(notification)
    }

    fun getKeywordSwitch() {
        FBRef.usersRef.child(Auth.current_uid).get()
            .addOnSuccessListener {
                val data = it.getValue<UserData>()
                keywordSwitch = data!!.switch_kwNoti
            }
    }

    fun compareKwnoti(kwnoti:String):Boolean{
        var check=false
        if(!kwNotiList_temp.contains(kwnoti)){
            check=true
        }

        return check

    }



}