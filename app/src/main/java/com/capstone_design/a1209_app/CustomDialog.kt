package com.capstone_design.a1209_app

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.capstone_design.a1209_app.databinding.BoardWriteConfirmBinding

class CustomDialog:DialogFragment() {
    private var _binding: BoardWriteConfirmBinding?=null
    private val binding get()= _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding= BoardWriteConfirmBinding.inflate(inflater,container,false)
       val view=binding.root
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        binding.dialogBtn1.setOnClickListener {
            buttonClickListener.onButton1Clicked()
            dismiss()
        }
        binding.dialogBtn2.setOnClickListener {
            buttonClickListener.onButton2Clicked()
            dismiss()
        }



        return view
    }
    // 인터페이스
    interface OnButtonClickListener {
        fun onButton1Clicked()
        fun onButton2Clicked()
    }
    // 클릭 이벤트 설정
    fun setButtonClickListener(buttonClickListener: OnButtonClickListener) {
        this.buttonClickListener = buttonClickListener
    }
    // 클릭 이벤트 실행
    private lateinit var buttonClickListener: OnButtonClickListener
}