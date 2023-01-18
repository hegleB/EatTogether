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

    fun showSnackBarMessage(
        constrainLayout: ConstraintLayout,
        deniedPermissions: ArrayList<String>
    ) {
        SnackBar.show(constrainLayout, PERMISSION_DENIED + deniedPermissions.toString())
    }

    fun setPermission(permissionListener: PermissionListener) {
        with(TedPermission.with(context)) {
            setPermissionListener(permissionListener)
            setRationaleMessage(RATIONALE_MESSAGE)
            setDeniedMessage(DENIED_MESSAGE)
            setPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            check()
        }
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
            setEmptySelectionText(EMPTY_SELECTION_TEXT)
        }

    companion object {
        const val PERMISSION_DENIED = "Permission Denied\n"
        const val RATIONALE_MESSAGE = "사진을 추가하기 위해서는 권한 설정이 필요합니다."
        const val DENIED_MESSAGE = "[설정] > [권한] 에서 권한을 허용할 수 있습니다.."
        const val EMPTY_SELECTION_TEXT = "사진 선택"
    }
}