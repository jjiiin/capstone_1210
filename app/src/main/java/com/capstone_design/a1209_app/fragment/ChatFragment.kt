package com.capstone_design.a1209_app.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone_design.a1209_app.chat.ChatList_RVAdapter
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.dataModels.ChatRoomData
import com.capstone_design.a1209_app.databinding.FragmentChatBinding
import com.capstone_design.a1209_app.utils.Auth
import com.capstone_design.a1209_app.utils.FBRef.Companion.chatRoomsRef
import com.capstone_design.a1209_app.utils.FBRef.Companion.userRoomsRef
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError


class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding


    //리사이클러뷰에 들어갈 아이템 추가
    val items = mutableListOf<ChatRoomData>()
    val chatroomkeys = mutableListOf<String>()
    lateinit var rv: RecyclerView
    lateinit var rvAdapter: ChatList_RVAdapter
    var chatRoom_count = 0
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

        binding.homeTab.setOnClickListener {
            it.findNavController().navigate(R.id.action_chatFragment_to_homeFragment)
        }
        binding.noteTab.setOnClickListener {
            it.findNavController().navigate(R.id.action_chatFragment_to_noteFragment)
        }
        binding.myTab.setOnClickListener {
            it.findNavController().navigate(R.id.action_chatFragment_to_myFragment)
        }

        rv = binding.chatListRv
        //채팅방 클릭시 해당 채팅방 하위에 데이터베이스 생성하기 위해 키값 넘겨줌
        rvAdapter =
            ChatList_RVAdapter(items, requireActivity().getApplicationContext(), chatroomkeys, isExitBtnClick)
        //리사이클러뷰 어댑터 연결
        rv.adapter = rvAdapter
        rv.layoutManager = LinearLayoutManager(requireActivity().getApplicationContext())
        //아이템 사이 구분선 추가
        //rv.addItemDecoration(DividerItemDecoration(requireActivity().getApplicationContext(), 1))

        //사용자가 참여한 채팅방만 보여줌
        getChatRoomsList()

        //클릭할때마다 이미지 변경
        binding.exitBtn.setOnClickListener {
            if (isExitBtnClick == 0) {
                binding.exitBtn.setImageResource(R.drawable.trash_open)
                binding.exitText.visibility = View.VISIBLE
                isExitBtnClick = 1
                //rvAdapter.notifyDataSetChanged()
            } else {
                binding.exitBtn.setImageResource(R.drawable.trash_round)
                binding.exitText.visibility = View.INVISIBLE
                isExitBtnClick = 0
                //rvAdapter.notifyDataSetChanged()
            }
        }

        return binding.root
    }

    //사용자가 참여한 채팅방만 보여줌
    private fun getChatRoomsList() {
        userRoomsRef.child(Auth.current_uid)
            .addChildEventListener(
                object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        //사용자가 참여한 채팅방을 리사이클러뷰에 추가
                        chatRoomsRef.child(snapshot.key.toString()).get().addOnSuccessListener {
                            val key = it.key
                            //Log.d("뭘까나", it.toString())
                            items.add(it.getValue(ChatRoomData::class.java)!!)
                            //Log.d("뭘까나", items.toString())
                            //채팅방 고유 키 저장
                            chatroomkeys.add(key!!)
                            rvAdapter.notifyDataSetChanged()
                            chatRoom_count++
                            //Log.d("채팅방수", chatRoom_count.toString())
                            binding.chatroomNum.setText(chatRoom_count.toString())
                        }
                    }

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?
                    ) {
                        val key = snapshot.key
                        Log.d("키다", key.toString())
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {
                        val key = snapshot.key
                        Log.d("키다", key.toString())
                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                        val key = snapshot.key
                        Log.d("키다", key.toString())
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("키다", error.toString())
                    }

                }
            )
    }

}