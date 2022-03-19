package com.capstone_design.a1209_app.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone_design.a1209_app.R
import com.capstone_design.a1209_app.fcm.NotiModel
import com.capstone_design.a1209_app.fcm.PushNotification
import com.capstone_design.a1209_app.fcm.RetrofitInstance
import com.squareup.okhttp.Dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


class NoteChild2Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        //앱에서 직접 다른 사람에게 푸시메세지 보내기

        val notiModel=NotiModel("a","b")

        val pushModel=PushNotification(notiModel,"cxgxEs-FShSMlefrokdLhG:APA91bEseu72EoUQNxk5jF0gXdyUmBqyPRdj8mY60XlJBP69gbmkwzF089g9FZcXB-DH72VtOaAlq1udxxhlHWEozXNZlcY3ld8Xf_IrMvDfJJW2JaV81tJDzNSCvJEJvMAdedv2QTFA")

        testPush(pushModel)

       return inflater.inflate(R.layout.fragment_note_child2, container, false)
    }

    private fun testPush(notification:PushNotification)=CoroutineScope(Dispatchers.IO).launch {
        RetrofitInstance.api.postNotification(notification)
    }


}