package com.capstone_design.a1209_app.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.chat.ChatList_RVAdapter
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModels.ChatRoomData
import com.capstone_design.a1209_app.databinding.FragmentChatBinding
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef.Companion.chatRoomsRef
import com.capstone_design.a1209_app.utils.FBRef.Companion.userRoomsRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding

    //리사이클러뷰에 들어갈 아이템 추가
    val items = mutableListOf<ChatRoomData>()
    lateinit var rv: RecyclerView
    lateinit var rvAdapter: ChatList_RVAdapter
    var chatRoom_count = 0
    val userRoomsKeyList = mutableListOf<String>()
    var isExitBtnClick = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)

        rv = binding.chatListRv

        //채팅방 들어온 인원대로 프로필 사진 보여주기 구현해야됨(최대 4명)

        //채팅방 클릭시 어댑터에서 intent로 채팅방키 값 넘겨주기 위해 매개변수로 넣어줌
        rvAdapter =
            ChatList_RVAdapter(
                items,
                requireActivity().getApplicationContext(),
                userRoomsKeyList
            )

        //리사이클러뷰 어댑터 연결
        rv.adapter = rvAdapter
        rv.layoutManager = LinearLayoutManager(requireActivity().getApplicationContext())
        //아이템 사이 구분선 추가
        //rv.addItemDecoration(DividerItemDecoration(requireActivity().getApplicationContext(), 1))

        //사용자가 참여한 채팅방만 보여줌
        userRoomsRef.child(Auth.current_uid)
            .addValueEventListener(getUserRoomListener())

        //클릭할때마다 이미지 변경
        binding.exitBtn.setOnClickListener {
            if (isExitBtnClick == 0) {
                binding.exitBtn.setImageResource(R.drawable.trash_open)
                binding.exitText.visibility = View.VISIBLE
                isExitBtnClick = 1
                rvAdapter.updateCheckBox(true)
                rvAdapter.notifyDataSetChanged()
            } else {
                binding.exitBtn.setImageResource(R.drawable.trash_round)
                binding.exitText.visibility = View.INVISIBLE
                isExitBtnClick = 0
                rvAdapter.updateCheckBox(false)
                rvAdapter.notifyDataSetChanged()
            }
        }

        //휴지통 아이콘 눌렀을 때 보이는 "나가기" 글자를 눌렀을때 이벤트
        binding.exitText.setOnClickListener {
            if (ChatList_RVAdapter.checked_chatRoomKey_List.isNotEmpty()) {
                //삭제 경고 팝업띄우기
                val dialog = Dialog(requireContext())
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.custom_dialog)
                dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setCanceledOnTouchOutside(true)
                dialog.setCancelable(true)
                dialog.findViewById<TextView>(R.id.tv_content).text = "삭제된 채팅방은 내용을 복구할 수 없어요.\n선택한 채팅방에서 나가시겠어요?"
                dialog.show()

                val okButton = dialog.findViewById<Button>(R.id.btn_yes)

                okButton.setOnClickListener {
                    for (key in ChatList_RVAdapter.checked_chatRoomKey_List) {
                        exit(key)
                    }
                    //팝업창 없앰
                    dialog.dismiss()
                }
                val noButton = dialog.findViewById<Button>(R.id.btn_no)

                noButton.setOnClickListener {
                    //팝업창 없앰
                    dialog.dismiss()
                    //휴지통 버튼 안눌린 상태로 바꾸기
                    binding.exitBtn.setImageResource(R.drawable.trash_round)
                    binding.exitText.visibility = View.INVISIBLE
                    isExitBtnClick = 0
                    rvAdapter.updateCheckBox(false)
                    rvAdapter.notifyDataSetChanged()
                }

            }
        }

        return binding.root
    }

    //사용자가 참여한 채팅방만 보여줌
    fun getUserRoomListener(): ValueEventListener {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userRoomsKeyList.clear()
                chatRoom_count = 0
                for (data in snapshot.children) {
                    userRoomsKeyList.add(data.key.toString())
                    chatRoom_count++
                    binding.chatroomNum.setText(chatRoom_count.toString())
                }
                chatRoomsRef.addValueEventListener(getChatRoomListener())
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }
        return listener
    }

    fun getChatRoomListener(): ValueEventListener {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                items.clear()
                for (data in snapshot.children) {
                    if (userRoomsKeyList.contains(data.key)) {
                        val roomData = data.getValue(ChatRoomData::class.java)
                        items.add(roomData!!)
                    }
                }
                rvAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }
        return listener
    }


    fun exit(chatroomkey: String) {
        //UserRooms에서 채팅방 키 삭제(결과: 해당 유저의 화면에서 안보이게됨)
        userRoomsRef.child(Auth.current_uid).child(chatroomkey!!).removeValue()

        //chatRooms에서 사용자 삭제
        chatRoomsRef.child(chatroomkey!!).child("users").child(Auth.current_uid)
            .removeValue()
    }
}