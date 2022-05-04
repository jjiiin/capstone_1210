package com.capstone_design.a1209_app.fragment

import com.capstone_design.a1209_app.Adapter.bannerAdapter
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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.VolleyLog
import com.capstone_design.a1209_app.*
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.board.BoardWirteActivity
import com.capstone_design.a1209_app.dataModels.addressData
import com.capstone_design.a1209_app.dataModels.dataModel
import com.capstone_design.a1209_app.dataModels.kwNotiData
import com.capstone_design.a1209_app.databinding.FragmentMapHomeBinding
import com.capstone_design.a1209_app.fcm.NotiModel
import com.capstone_design.a1209_app.fcm.PushNotification
import com.capstone_design.a1209_app.fcm.RetrofitInstance
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
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


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
    private lateinit var viewPager2: ViewPager2
    private lateinit var springDotsIndicator: SpringDotsIndicator
    private lateinit var myLocation:Location

    private var category="all"
    private var cnt=0
    private var items= mutableListOf<dataModel>()
    private val itemsKeyList= mutableListOf<String>()

    private var keywordList= mutableListOf<String>()
    private var contentList= mutableListOf<dataModel>()

    val database = Firebase.database

    //viewpager
    private var bannerPosition = Int.MAX_VALUE/2
    private val intervalTime = 1500.toLong()


    val permission=arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION)
    val PERM_FLAG=99
    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity =context as MainActivity
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_map_home, container, false)

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
        viewPager2=binding.viewPager
        springDotsIndicator=binding.springDotsIndicator



        //키워드 알림
        // 1. 키워드 리스트 가져오기
        // 2. map-contents에서 item.title 만 놓고 비교하기
        val kwRef =database.getReference("users").child(auth.currentUser!!.uid).child("keyword")
        kwRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
               for(DataModel in snapshot.children){
                   val item=DataModel.getValue(String::class.java)
                   if(item!=null){
                       keywordList.add(item)
                   }
               }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        //키워드 알림 - 키워드 리스트와 글 목록 비교하기
        FBRef.board.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(DataModel in snapshot.children){
                    val item=DataModel.getValue(dataModel::class.java)
                    if(item!=null){
                        for(i in keywordList){
                            if(item.title.contains(i)){
                                notification(i,item.title)
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding.mapGo.setOnClickListener {
            val intent = Intent(context, AddressSearchActivity::class.java).putExtra("mhf","1")
                .putExtra("page","MapHomeFragment")
            startActivity(intent)
        }
        mView=binding.mapView
        binding.writeBtn.setOnClickListener {
            val intent = Intent(context, BoardWirteActivity::class.java)
            startActivity(intent)
        }
        mView.onCreate(savedInstanceState)

        //현재 내 위치 가져오는

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
        mMap!!.clear()
        viewPager2.visibility=View.GONE
        springDotsIndicator.visibility=View.GONE
        markerView("all")
        buttonColor("all")

        binding.categoryAll.setOnClickListener {
            mMap!!.clear()
            viewPager2.visibility=View.GONE
            springDotsIndicator.visibility=View.GONE
            markerView("all")
            buttonColor("all")

        }
        binding.categoryAsian.setOnClickListener {
            mMap!!.clear()
            viewPager2.visibility=View.GONE
            springDotsIndicator.visibility=View.GONE
            markerView("asian")
            buttonColor("asian")
        }
        binding.categoryBun.setOnClickListener {
            mMap!!.clear()
            viewPager2.visibility=View.INVISIBLE
            springDotsIndicator.visibility=View.INVISIBLE
            markerView("bun")
            buttonColor("bun")
        }
        binding.categoryChicken.setOnClickListener {
            mMap!!.clear()
            viewPager2.visibility=View.GONE
            springDotsIndicator.visibility=View.GONE
            markerView("chicken")
            buttonColor("chicken")
        }
        binding.categoryPizza.setOnClickListener {
            mMap!!.clear()
            viewPager2.visibility=View.GONE
            springDotsIndicator.visibility=View.GONE
            markerView("chicken")
            buttonColor("pizza")
        }
        binding.categoryFast.setOnClickListener {
            mMap!!.clear()
            viewPager2.visibility=View.GONE
            springDotsIndicator.visibility=View.GONE
            markerView("fastfood")
            buttonColor("fast")
        }
        binding.categoryJap.setOnClickListener {
            mMap!!.clear()
            viewPager2.visibility=View.GONE
            springDotsIndicator.visibility=View.GONE
            markerView("japan")
            buttonColor("japan")
        }
        binding.categoryKor.setOnClickListener {
            mMap!!.clear()
            viewPager2.visibility=View.GONE
            springDotsIndicator.visibility=View.GONE
            markerView("korean")
            buttonColor("korean")
        }
        binding.categoryDo.setOnClickListener {
            mMap!!.clear()
            viewPager2.visibility=View.GONE
            springDotsIndicator.visibility=View.GONE
            markerView("bento")
            buttonColor("bento")

        }
        binding.categoryCafe.setOnClickListener {
            mMap!!.clear()
            viewPager2.visibility=View.GONE
            springDotsIndicator.visibility=View.GONE
            markerView("cafe")
            buttonColor("cafe")

        }
        binding.categoryChi.setOnClickListener {
            mMap!!.clear()
            viewPager2.visibility=View.GONE
            springDotsIndicator.visibility=View.GONE
            markerView("chi")
            buttonColor("chi")

        }
        //내위치 항상 표시하기
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
        setUpdateLocationListener2()

        //내위치 버튼 클릭
        binding.myloc.setOnClickListener {
            viewPager2.visibility=View.GONE
            springDotsIndicator.visibility=View.GONE
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
            setUpdateLocationListener()
            setUpdateLocationListener2()
        }

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
        mMap!!.setOnMarkerClickListener (object :GoogleMap.OnMarkerClickListener{
            override fun onMarkerClick(p0: Marker): Boolean {
                markerClick(p0.tag.toString(), p0.position)
                viewPager2.visibility=View.VISIBLE
                springDotsIndicator.visibility=View.VISIBLE
                return false
            }


        })

        //지도 클릭시 마커 원상복구
        mMap!!.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
            override fun onMapClick(latLng: LatLng) {
                viewPager2.visibility = View.GONE
                springDotsIndicator.visibility = View.GONE


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
        }

        locationCallback=object  : LocationCallback (){
            override fun onLocationResult(p0: LocationResult) {
                p0?.let{
                   for ((i,location)  in it.locations.withIndex()){
                        setLastLocation(location)

                   }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper())
        //로케이션 요청 함수 호출
    }
    @SuppressLint("MissingPermission")
    fun setUpdateLocationListener2(){
        val locationRequest=LocationRequest.create()
        locationRequest.run{
            priority=LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback=object  : LocationCallback (){
            override fun onLocationResult(p0: LocationResult) {
                p0?.let{
                    for ((i,location)  in it.locations.withIndex()){
                        setDrawLastLocation(location)
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper())
        //로케이션 요청 함수 호출
    }
    fun setLastLocation(location: Location){
        val myLocation=LatLng(location.latitude,location.longitude)
//        val discripter=getMarkerDrawable(R.drawable.marker)
//        val marker=MarkerOptions()
//            .position(myLocation)
//            .title("I'm here")
//            .icon(discripter)

        val cameraOption = CameraPosition.Builder()
            .target(myLocation)//현재 위치로 바꿀 것
            .zoom(17f)
            .build()
        val camera=CameraUpdateFactory.newCameraPosition(cameraOption)


        //mMap.addMarker(marker)
        mMap.moveCamera(camera)
    }
    fun setDrawLastLocation(location: Location){
        val myLocation=LatLng(location.latitude,location.longitude)
        val discripter=getLocDrawable(R.drawable.mylocation)
        val marker=MarkerOptions()
            .position(myLocation)
            .icon(discripter)

//        val cameraOption = CameraPosition.Builder()
//            .target(myLocation)//현재 위치로 바꿀 것
//            .zoom(17f)
//            .build()
//        val camera=CameraUpdateFactory.newCameraPosition(cameraOption)

        mMap.addMarker(marker)
//        mMap.moveCamera(camera)
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
        val scaleBitmap= Bitmap.createScaledBitmap(bitmapDrawable.bitmap,130,160,false)
        return BitmapDescriptorFactory.fromBitmap(scaleBitmap)

    }
    fun getLocDrawable(drawableId:Int):BitmapDescriptor{
        //마커 아이콘 만들기
        var bitmapDrawable:BitmapDrawable
        bitmapDrawable=mainActivity.resources.getDrawable(drawableId)as BitmapDrawable

        //마커 크기 변환
        return BitmapDescriptorFactory.fromBitmap(bitmapDrawable.bitmap)
    }
    private fun buttonColor(category:String){
        binding.categoryAsian.setBackgroundResource(R.drawable.round_button)
        binding.categoryAsian.setTextColor(Color.BLACK)
        binding.categoryAsian.setTypeface(binding.categoryAsian.typeface, Typeface.NORMAL)


        binding.categoryBun.setBackgroundResource(R.drawable.round_button)
        binding.categoryBun.setTextColor(Color.BLACK)
        binding.categoryBun.setTypeface(binding.categoryAsian.typeface, Typeface.NORMAL)


        binding.categoryKor.setBackgroundResource(R.drawable.round_button)
        binding.categoryKor.setTextColor(Color.BLACK)
        binding.categoryKor.setTypeface(binding.categoryAsian.typeface, Typeface.NORMAL)


        binding.categoryJap.setBackgroundResource(R.drawable.round_button)
        binding.categoryJap.setTextColor(Color.BLACK)
        binding.categoryJap.setTypeface(binding.categoryAsian.typeface, Typeface.NORMAL)



        binding.categoryChi.setBackgroundResource(R.drawable.round_button)
        binding.categoryChi.setTextColor(Color.BLACK)
        binding.categoryChi.setTypeface(binding.categoryAsian.typeface, Typeface.NORMAL)


        binding.categoryFast.setBackgroundResource(R.drawable.round_button)
        binding.categoryFast.setTextColor(Color.BLACK)
        binding.categoryFast.setTypeface(binding.categoryAsian.typeface, Typeface.NORMAL)


        binding.categoryDo.setBackgroundResource(R.drawable.round_button)
        binding.categoryDo.setTextColor(Color.BLACK)
        binding.categoryDo.setTypeface(binding.categoryAsian.typeface, Typeface.NORMAL)



        binding.categoryCafe.setBackgroundResource(R.drawable.round_button)
        binding.categoryCafe.setTextColor(Color.BLACK)
        binding.categoryCafe.setTypeface(binding.categoryAsian.typeface, Typeface.NORMAL)


        binding.categoryChicken.setBackgroundResource(R.drawable.round_button)
        binding.categoryChicken.setTextColor(Color.BLACK)
        binding.categoryChicken.setTypeface(binding.categoryAsian.typeface, Typeface.NORMAL)


        binding.categoryPizza.setBackgroundResource(R.drawable.round_button)
        binding.categoryPizza.setTextColor(Color.BLACK)
        binding.categoryPizza.setTypeface(binding.categoryAsian.typeface, Typeface.NORMAL)

        binding.categoryAll.setBackgroundResource(R.drawable.round_button)
        binding.categoryAll.setTextColor(Color.BLACK)
        binding.categoryAll.setTypeface(binding.categoryAll.typeface, Typeface.NORMAL)


        when(category){
            "asian"->{binding.categoryAsian.setBackgroundResource(R.drawable.select_round)
                binding.categoryAsian.setTextColor(Color.WHITE)
                binding.categoryAsian.setTypeface(binding.categoryAsian.typeface, Typeface.BOLD)}
            "bun"->{binding.categoryBun.setBackgroundResource(R.drawable.select_round)
                binding.categoryBun.setTextColor(Color.WHITE)
                binding.categoryBun.setTypeface(binding.categoryBun.typeface, Typeface.BOLD)}
            "korean"->{binding.categoryKor.setBackgroundResource(R.drawable.select_round)
                binding.categoryKor.setTextColor(Color.WHITE)
                binding.categoryKor.setTypeface(binding.categoryKor.typeface, Typeface.BOLD)}
            "japan"->{binding.categoryJap.setBackgroundResource(R.drawable.select_round)
                binding.categoryJap.setTextColor(Color.WHITE)
                binding.categoryJap.setTypeface(binding.categoryJap.typeface, Typeface.BOLD)}
            "chi"->{binding.categoryChi.setBackgroundResource(R.drawable.select_round)
                binding.categoryChi.setTextColor(Color.WHITE)
                binding.categoryChi.setTypeface(binding.categoryChi.typeface, Typeface.BOLD)}
            "fast"->{binding.categoryFast.setBackgroundResource(R.drawable.select_round)
                binding.categoryFast.setTextColor(Color.WHITE)
                binding.categoryFast.setTypeface(binding.categoryFast.typeface, Typeface.BOLD)}
            "bento"->{binding.categoryDo.setBackgroundResource(R.drawable.select_round)
                binding.categoryDo.setTextColor(Color.WHITE)
                binding.categoryDo.setTypeface(binding.categoryDo.typeface, Typeface.BOLD)}
            "cafe"->{binding.categoryCafe.setBackgroundResource(R.drawable.select_round)
                binding.categoryCafe.setTextColor(Color.WHITE)
                binding.categoryCafe.setTypeface(binding.categoryCafe.typeface, Typeface.BOLD)}
            "chicken"->{binding.categoryChicken.setBackgroundResource(R.drawable.select_round)
                binding.categoryChicken.setTextColor(Color.WHITE)
                binding.categoryChicken.setTypeface(binding.categoryChicken.typeface, Typeface.BOLD)}
            "pizza"->{binding.categoryPizza.setBackgroundResource(R.drawable.select_round)
                binding.categoryPizza.setTextColor(Color.WHITE)
                binding.categoryPizza.setTypeface(binding.categoryPizza.typeface, Typeface.BOLD)}
            "all"->{binding.categoryAll.setBackgroundResource(R.drawable.select_round)
                binding.categoryAll.setTextColor(Color.WHITE)
                binding.categoryAll.setTypeface(binding.categoryAll.typeface, Typeface.BOLD)}

        }

    }

    //모든 data담아두는 List
    private var dataList= mutableListOf<dataModel>()
    private var tempList= mutableListOf<dataModel>()
    private fun markerView(category:String){
        val boardRef : DatabaseReference = database.getReference("map_contents")
        boardRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                tempList.clear()
                itemsKeyList.clear()
                for (data in snapshot.children) {
                    val item = data.getValue(dataModel::class.java)
                    if (item != null) {
                        if(category=="all") {
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
                        if(category==item.category){
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

    private fun markerClick(tag:String, position:LatLng){
        //마커 클릭 됐을 때 마커 디자인 바꾸기
        val discripter = getMarkerDrawable(R.drawable.marker_select)
        val markerOptions = MarkerOptions()
            .position(position)
            .icon(discripter)
        val marker: Marker? = mMap!!.addMarker(markerOptions)
        marker!!.tag= "select"

        //마커가 클릭 됐을 때 같은 주소끼리 배열로 만들어서 viewPager에 보이게 하기
        //같은 placeAddress를 배열로 묶기
        val cardList= ArrayList<dataModel>()
        //val itemKey= mutableListOf<String>()
        val itemKey= ArrayList<String>()
        for( i in 0 until dataList.size){
            if(dataList[i].placeAddress==tag) {
                cardList.add(dataList[i])
                itemKey.add(itemsKeyList[i])
            }
        }
        Log.d("keyitemkey", cardList.toString())
//        viewPager2.adapter=bannerAdapter(cardList)
        val vpAdapter= bannerAdapter(cardList,this, mainActivity)
        viewPager2.adapter=vpAdapter
        viewPager2.orientation=ViewPager2.ORIENTATION_HORIZONTAL
        springDotsIndicator.setViewPager2(viewPager2)

        //viewPager2 클릭 이벤트
        vpAdapter.setItemClickListener(object: bannerAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                val intent = Intent(context, DetailActivity::class.java)
                //firebase에 있는 board에 대한 데이터의 id를 가져오기
                intent.putExtra("key", itemKey[position])
                Log.d("keyitemkey", cardList.toString())
                Log.d("keyitem", itemKey.toString())
                Log.d("keyposition",position.toString())
                startActivity(intent)
            }

        })

        //viewpager2의 버튼 클릭 이벤트

        vpAdapter.setBtnClickListener(object: bannerAdapter.OnBtnClickListener{
            override fun onClick(v: View, position: Int) {
                var fragment2 = MiniListFragment()
                var bundle = Bundle()
                bundle.putParcelableArrayList("list",cardList)
                bundle.putStringArrayList("keyList",itemKey)
                fragment2.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌
                parentFragmentManager
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.home , fragment2)
                    .commit()

//                childFragmentManager.beginTransaction()
//                    .replace(R.id.mhfLayout,fragment2).addToBackStack(null).commit()

            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun notification(keyword:String,title:String){
        Log.d("kwnoti",keyword)
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ISO_DATE
        val formatted = current.format(formatter)
        val notiModel= NotiModel("Saveat - 키워드알림","\"${keyword}\" 배달 쉐어가 오픈됐습니다.",formatted.toString())
        val kwnoti=kwNotiData("\"${keyword}\" 배달 쉐어가 오픈됐습니다.",formatted.toString(),title)

        FBRef.usersRef.child(auth.currentUser?.uid.toString()).child("kwNoti").push().setValue(kwnoti)
        val token=true
        if(token) {
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
    private fun testPush(notification: PushNotification)= CoroutineScope(Dispatchers.IO).launch {
        Log.d("kwnoti_test",notification.toString())
        Log.d("pushNoti",notification.toString())
        RetrofitInstance.api.postNotification(notification)
    }



}