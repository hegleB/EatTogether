package com.qure.presenation.utils

import android.Manifest
import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.qure.presenation.R
import gun0912.tedbottompicker.TedBottomPicker
import java.util.ArrayList

class BottomImagePicker(val context: Context, val requestActivity: FragmentActivity) {

    fun getSnackBarMessage(
        constrainLayout: ConstraintLayout,
        deniedPermissions: ArrayList<String>
    ) {
        Snackbar.make(
            constrainLayout, "Permission Denied\n" +
                    deniedPermissions.toString(), Snackbar.LENGTH_LONG
        ).show()
    }

    fun setPermission(permissionListener: PermissionListener) {
        TedPermission.with(context)
            .setPermissionListener(permissionListener)
            .setRationaleMessage("사진을 추가하기 위해서는 권한 설정이 필요합니다.")
            .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다..")
            .setPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .check()
    }

    fun openImagePicker(SelectMaxCountErrorText: String, CompleteButtonText: String) =
        with(TedBottomPicker.with(requestActivity)) {
            setPeekHeight(1600)
            showGalleryTile(false)
            setPreviewMaxCount(1000)
            setSelectMaxCount(3)
            setSelectMaxCountErrorText(SelectMaxCountErrorText)
            showTitle(false)
            setTitleBackgroundResId(R.color.light_red)
            setGalleryTileBackgroundResId(R.color.white)
            setCompleteButtonText(CompleteButtonText)
            setEmptySelectionText("사진 선택")
        }
}