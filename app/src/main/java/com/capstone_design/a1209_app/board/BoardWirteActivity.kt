package com.capstone_design.a1209_app.board

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.capstone_design.a1209_app.AddressSearchActivity
import com.capstone_design.a1209_app.CustomDialog
import com.capstone_design.a1209_app.chat.ChatRoomActivity
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModels.ChatData
import com.capstone_design.a1209_app.dataModels.dataModel
import com.capstone_design.a1209_app.dataModels.ChatRoomData
import com.capstone_design.a1209_app.dataModels.addressData
import com.capstone_design.a1209_app.databinding.ActivityBoardWirteBinding
import com.capstone_design.a1209_app.fcm.NotiModel
import com.capstone_design.a1209_app.fcm.PushNotification
import com.capstone_design.a1209_app.fcm.RetrofitInstance
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef
import com.capstone_design.a1209_app.utils.FBRef.Companion.chatRoomsRef
import com.capstone_design.a1209_app.utils.FBRef.Companion.userRoomsRef
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BoardWirteActivity : AppCompatActivity() {
    //글쓰기 화면에 스피너 넣어야함.
    private lateinit var binding: ActivityBoardWirteBinding
    private lateinit var auth: FirebaseAuth
    private val items = mutableListOf<dataModel>()
    private val tokenList = mutableListOf<String>()
    var time = ""
    private var lat = ""
    private var lng = ""

    private val categoryList = mutableListOf<Button>()
    private val personList = mutableListOf<Button>()

    private var placeAddress = ""
    private var placeDetail = ""

    //사진
    private val pickStorage = 1001
    var setImage: Boolean = false
    var questPicture: Uri? = null
    private var imageUri: Uri? = null
    lateinit var current_nickname: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_wirte)
        // Write a message to the database
        val database = Firebase.database

        var category = ""

        //새글알림용 category
        var categoryNoti = ""

        //현재 사용자 닉네임 가져오기
        getCurrentNickname()

        //버튼 클릭시 category에 값 할당하기
        val cate_kor: Button = binding.categoryKor
        categoryList.add(cate_kor)
        val cate_asian: Button = binding.categoryAsian
        categoryList.add(cate_asian)
        val cate_bun: Button = binding.categoryBun
        categoryList.add(cate_bun)
        val cate_jap: Button = binding.categoryJap
        categoryList.add(cate_jap)
        val cate_chicken: Button = binding.categoryChicken
        categoryList.add(cate_chicken)
        val cate_pizza: Button = binding.categoryPizza
        categoryList.add(cate_pizza)
        val cate_fast: Button = binding.categoryFast
        categoryList.add(cate_fast)
        val cate_bento: Button = binding.categoryDo
        categoryList.add(cate_bento)
        val cate_cafe: Button = binding.categoryCafe
        categoryList.add(cate_cafe)
        val cate_chi: Button = binding.categoryChi
        categoryList.add(cate_chi)


        binding.categoryKor.setOnClickListener {
            categoryNoti = "한식"
            category = "korean"
            buttonSelect()
            cate_kor.isSelected = true
        }

        binding.categoryAsian.setOnClickListener {
            buttonSelect()
            cate_asian.isSelected = true
            category = "asian"
            categoryNoti = "아시안, 양식"
        }
        binding.categoryBun.setOnClickListener {
            buttonSelect()
            cate_bun.isSelected = true
            category = "bun"
            categoryNoti = "분식"
        }
        binding.categoryChicken.setOnClickListener {
            categoryNoti = "치킨"
            buttonSelect()
            cate_chicken.isSelected = true
            category = "chicken"

        }
        binding.categoryPizza.setOnClickListener {
            categoryNoti = "피자"
            category = "pizza"
            buttonSelect()
            cate_pizza.isSelected = true
        }
        binding.categoryFast.setOnClickListener {
            categoryNoti = "패스트푸드"
            category = "fastfood"
            buttonSelect()
            cate_fast.isSelected = true
        }
        binding.categoryJap.setOnClickListener {
            categoryNoti = "일식"
            category = "japan"
            buttonSelect()
            cate_jap.isSelected = true
        }

        binding.categoryDo.setOnClickListener {
            categoryNoti = "도시락"
            category = "bento"
            buttonSelect()
            cate_bento.isSelected = true
        }
        binding.categoryCafe.setOnClickListener {
            categoryNoti = "카페, 디저트"
            category = "cafe"
            buttonSelect()
            cate_cafe.isSelected = true
        }
        binding.categoryChi.setOnClickListener {
            categoryNoti = "중식"
            category = "chi"
            buttonSelect()
            cate_chi.isSelected = true
        }

        var quick = ""
        val timequick: Button = binding.quick
        val timeSelect: Button = binding.timeSelect
        binding.quick.setOnClickListener {
            quick = "1"
            time = "바로 주문"
            timequick.isSelected = true
            timeSelect.isSelected = false
            binding.directInput.isEnabled = false
        }



        binding.timeSelect.setOnClickListener {
            quick = ""
            var hour = ""
            var min = ""
            var day = ""

            timeSelect.isSelected = true
            timequick.isSelected = false
            binding.directInput.isEnabled = true
            //스피너로 시간 구현하기
            var hourData = resources.getStringArray(R.array.array_hours)
            var minData = resources.getStringArray(R.array.array_minutes)


            var adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, hourData)
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerHours.adapter = adapter1
            binding.spinnerHours.setSelection(0)

            var adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, minData)
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerMinutes.adapter = adapter2
            binding.spinnerMinutes.setSelection(0)
            binding.spinnerHours.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }

                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        hour = hourData[p2]



                        binding.spinnerMinutes.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(p0: AdapterView<*>?) {

                                }

                                override fun onItemSelected(
                                    p0: AdapterView<*>?,
                                    p1: View?,
                                    p2: Int,
                                    p3: Long
                                ) {
                                    min = minData[p2]

                                    val timeAm: Button = binding.timeAm
                                    val timePm: Button = binding.timePm
                                    binding.timeAm.setOnClickListener {
                                        timePm.isSelected = false
                                        timeAm.isSelected = true
                                        day = "AM"
                                        time = "${day} ${hour}${min}"
                                    }
                                    binding.timePm.setOnClickListener {
                                        timeAm.isSelected = false
                                        timePm.isSelected = true
                                        day = "PM"
                                        time = "${day} ${hour}${min}"

                                    }

                                }
                            }
                    }
                }

        }

        binding.picBtn.setOnClickListener {
            pickImage()
        }

        val one: Button = binding.person1List
        val two: Button = binding.person2List
        val three: Button = binding.person3List
        val four: Button = binding.personNList

        personList.add(one)
        personList.add(two)
        personList.add(three)
        personList.add(four)
        var person = "2"
        binding.person1List.setOnClickListener {
            person = "2"
            personButtonSelect()
            one.isSelected = true
        }

        binding.person2List.setOnClickListener {
            person = "3"
            personButtonSelect()
            two.isSelected = true
        }

        binding.person3List.setOnClickListener {
            person = "4"
            personButtonSelect()
            three.isSelected = true
        }

        binding.personNList.setOnClickListener {
            person = "5"
            personButtonSelect()
            four.isSelected = true
        }
        //주소검색
        binding.searchBtn.setOnClickListener {
            val intent = Intent(this, AddressSearchActivity::class.java).putExtra(
                "page",
                "BoardWirteActivity"
            )
            startActivity(intent)
        }
        //주소
        auth = Firebase.auth
        val schRef: DatabaseReference =
            database.getReference("users").child(auth.currentUser?.uid.toString()).child("address")
        schRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (DataModel in snapshot.children) {
                    val item = DataModel.getValue(addressData::class.java)
                    if (item != null) {
                        if (item.set == "1") {
                            binding.placeList.setText(item.address + " " + item.detail)
                            placeAddress = item.address
                            placeDetail = item.detail
                            lat = item.lat
                            lng = item.lng
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        binding.saveBtn.setOnClickListener {

            val dialog = CustomDialog()

            dialog.setButtonClickListener(object : CustomDialog.OnButtonClickListener {
                override fun onButton1Clicked() {
                    //"아니오를 누른다면
                }

                override fun onButton2Clicked() {
                    //"예"를 누른다면
                    val title_dm = binding.titleList.text.toString()
                    val category_dm = category
                    val person_dm = person
                    var image_dm = "0"
                    var quick_dm = quick

                    val time_dm = time
                    //스피너로 입력하기
                    var fee_dm = ""
                    if (binding.textFee.text.toString() == "") {
                        fee_dm = "0원"
                    } else {
                        fee_dm = binding.textFee.text.toString().plus("원")
                    }
                    Log.d("BWA", fee_dm)
                    val place_dm = binding.placeList.text.toString()
                    val mention_dm = binding.mention.text.toString()
                    val link_dm = binding.link.text.toString()
                    var latLng = LatLng(lat.toDouble(), lng.toDouble())

                    //Log.d("아이디",current_uid)
                    val writer_uid = Auth.current_uid
                    val code = title_dm.replace(" ", "")
                    //이미지 업로드
                    if (setImage == true) {
                        Log.d("setImage", "img")

                        FirebaseStorage.getInstance().reference.child("map_contents") // firebase storage에 이미지 저장
                            .child("${writer_uid}+${code}") //다른 걸로 할 수 없을 까...
                            .child("image")
                            .putFile(imageUri!!)
                            .addOnSuccessListener {
                                FirebaseStorage.getInstance().reference.child("map_contents") // firebase storage에 이미지 저장
                                    .child("${writer_uid}+${code}")
                                    .child("image").downloadUrl.addOnSuccessListener {
                                        questPicture = it
                                        Log.d("tag_syccc", "$questPicture")
                                        FBRef.board.child("${writer_uid}+${code}").child("image")
                                            .setValue(questPicture.toString())
                                        image_dm = questPicture.toString()
                                    }
                            }
                            .addOnFailureListener {

                            }
                    }


                    //채팅방 생성
                    var chatroomkey = chatRoomsRef.push().key
                    val chatRoomData = ChatRoomData(title_dm, writer_uid)
                    //채팅방 정보 저장
                    chatRoomsRef.child(chatroomkey!!).setValue(chatRoomData)
                    chatRoomsRef.child(chatroomkey!!).child("users").child(writer_uid)
                        .setValue(true)
                    //각 사용자가 무슨 채팅방에 참여하고 있는지 저장
                    userRoomsRef.child(writer_uid).child(chatroomkey).setValue(true)

                    val model = dataModel(
                        title_dm,
                        category_dm,
                        image_dm,
                        person_dm,
                        time_dm,
                        quick_dm,
                        fee_dm,
                        place_dm,
                        placeAddress,
                        placeDetail,
                        link_dm,
                        mention_dm,
                        latLng.latitude.toString(),
                        latLng.longitude.toString(),
                        //글쓴이 정보 추가
                        writer_uid,
                        chatroomkey
                    )
                    items.add(model)

                    //push알림
                    //앱에서 직접 다른 사람에게 푸시메세지 보내기
                    //새글알림
                    val current = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ISO_DATE
                    val formatted = current.format(formatter)

                    //카테고리에 해당하는 token 배열 가져오기
                    //var tokenList= mutableListOf<String>()
                    if(category!="") {
                        val schRef: DatabaseReference =
                            database.getReference("notification").child(category)
                        schRef.addValueEventListener(object : ValueEventListener {
                            val notiModel = NotiModel(
                                "Saveat - 새글알림",
                                "\"${categoryNoti}\" 카테고리에 새 글이 올라왔습니다.",
                                formatted.toString(),
                                title_dm,
                                chatroomkey
                            )

                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (DataModel in snapshot.children) {
                                    val item = DataModel.getValue(String::class.java)
                                    if (item != null) {
                                        var token = item.toString()
                                        val pushModel = PushNotification(notiModel, "${token}")
                                        testPush(pushModel)
                                        //Log.d("tokenList",item.toString())
                                    }
                                }
//                    Log.d("tockenList_1",tokenList.toString())
                            }


                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                    }
                    //채팅방 정보에 게시글 키 저장
                    chatRoomsRef.child(chatroomkey!!).child("boardKey")
                        .setValue("${writer_uid}+${title_dm.replace(" ", "")}")
                    //채팅방 정보에 게시글 키 저장
                    chatRoomsRef.child(chatroomkey!!).child("isClosed").setValue(false)
                    //지인(map-contents)
                    FBRef.board.child("${writer_uid}+${code}").setValue(model)
//                    //글을 쓴 총대니까 채팅방으로 바로 이동
//                    val intent = Intent(this, ChatRoomActivity::class.java).putExtra("채팅방키", chatroomkey)
//                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
//                    //현재 액티비티 종료시키기
//                    finish()
                    Chat(chatroomkey)
                }

            })
            dialog.show(supportFragmentManager, "CustomDialog")
            //btn2를 눌렀을 때

        }

    }

    private fun Chat(roomKey: String) {
        //입장 메시지
        val enter_chatData =
            ChatData(
                current_nickname,
                "enter",
                Auth.current_email!!,
                Auth.current_uid,
                System.currentTimeMillis()
            )
        //주문서 작성해달라는 공지 메시지 보내기
        val notice_chatData =
            ChatData(
                "notice",
                "[공지] 미리 주문서에 메뉴 올려주세요:)",
                "notice",
                "notice",
                System.currentTimeMillis()
            )
        chatRoomsRef.child(roomKey!!).child("messages").push()
            .setValue(enter_chatData)
        chatRoomsRef.child(roomKey!!).child("messages").push()
            .setValue(notice_chatData)

        //글을 쓴 총대니까 채팅방으로 바로 이동
        val intent = Intent(this, ChatRoomActivity::class.java).putExtra("채팅방키", roomKey)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        //현재 액티비티 종료시키기
        finish()
    }

    //새글 알림 보내기
    private fun testPush(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        Log.d("pushNoti", notification.toString())
        RetrofitInstance.api.postNotification(notification)
    }

    // 이미지 불러오기
    private fun pickImage() {
        var intent = Intent(Intent.ACTION_GET_CONTENT) // 갤러리 앱 호출
        intent.type = "image/*"

        startActivityForResult(intent, pickStorage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("call", "call")
        if (resultCode == RESULT_OK) {
            if (requestCode == pickStorage) {
                val pickedImage: Uri? = data?.data
                if (pickedImage != null) {
                    imageUri = pickedImage
                }
            }
            binding.picList.setText(imageUri.toString())
            //Glide.with(this).load(imageUri).into(complete_picture)  // 화면에 출력
            setImage = true // 이미지 업로드 했음을 알림
        }
    }

    private fun buttonSelect() {
        for (i in categoryList) {
            i.isSelected = false
        }
    }

    private fun personButtonSelect() {
        for (i in personList) {
            i.isSelected = false
        }
    }

    fun getCurrentNickname() {
        FBRef.usersRef.child(Auth.current_uid).child("nickname").get().addOnSuccessListener {
            current_nickname = it.getValue().toString()
        }
    }
}