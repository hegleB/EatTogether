package com.qure.eattogether2.view.people

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.os.CountDownTimer
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.qure.eattogether2.R
import com.qure.eattogether2.data.Barcode
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


class BarcodeDialog(context: Context) {


    private val dlg = Dialog(context)

    private lateinit var btnCancel: ImageView
    private lateinit var barcodeImage: ImageView
    private lateinit var clockImage: ImageView
    private lateinit var recreate_barcode: ImageView
    private lateinit var text1: TextView
    private lateinit var counter: TextView
    private lateinit var text3: TextView
    private var mCountDown: CountDownTimer? = null


    private lateinit var listener: MyDialogOKClickedListener


    fun start(content: String) {

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(R.layout.barcode_dialog)
        dlg.setCancelable(false)
        barcodeImage = dlg.findViewById(R.id.image_barcode)
        btnCancel = dlg.findViewById(R.id.barcode_close)
        counter = dlg.findViewById(R.id.counter)
        btnCancel.setOnClickListener {
            dlg.dismiss()
        }
        dlg.show()
    }

    fun setOnOKClickedListener(listener: (String) -> Unit) {
        this.listener = object : MyDialogOKClickedListener {
            override fun onOKClicked(content: String) {
                listener(content)
            }
        }
    }


    interface MyDialogOKClickedListener {
        fun onOKClicked(content: String)
    }





}