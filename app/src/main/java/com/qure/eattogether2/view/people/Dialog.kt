package com.qure.eattogether2.view.people

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import com.qure.eattogether2.R


class Dialog(context : Context) {

    private val dlg = Dialog(context)
    private lateinit var lblDesc : TextView
    private lateinit var btnOK : TextView
    private lateinit var btnCancel : TextView
    private lateinit var listener : MyDialogOKClickedListener


    fun start(content : String) {
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(R.layout.dialog)
        dlg.setCancelable(false)

        lblDesc = dlg.findViewById(R.id.textEdit)
        lblDesc.text = content

        btnOK = dlg.findViewById(R.id.submit)
        btnOK.setOnClickListener {

            dlg.dismiss()
        }

        btnCancel = dlg.findViewById(R.id.cancel)
        btnCancel.setOnClickListener {
            dlg.dismiss()
        }

        dlg.show()
    }

    fun setOnOKClickedListener(listener: (String) -> Unit) {
        this.listener = object: MyDialogOKClickedListener {
            override fun onOKClicked(content: String) {
                listener(content)
            }
        }
    }


    interface MyDialogOKClickedListener {
        fun onOKClicked(content : String)
    }



}