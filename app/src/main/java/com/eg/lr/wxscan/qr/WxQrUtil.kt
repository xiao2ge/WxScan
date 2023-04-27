package com.eg.lr.wxscan.qr

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.annotation.RawRes
import com.eg.lr.wxscan.R
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

object WxQrUtil {

    const val EXTRA_CONTRACT_DATA = "EXTRA_CONTRACT_DATA"
    private const val REQUEST_CODE = 808

    init {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    fun install(app: Application?) {
        MMKV.initialize(app)
        initWeChatModelFile(app)
    }

    /**
     * 加载模型
     */
    private fun initWeChatModelFile(app: Application?) {
        GlobalScope.launch(Dispatchers.IO) {
            MMKV.defaultMMKV()?.encode(
                MMKVKey.WeChatQRCodeDetectProtoTxt,
                copyFromAssets(app, R.raw.detect_prototxt, "wechat_qrcode", "detect.prototxt")
            )
            MMKV.defaultMMKV()?.encode(
                MMKVKey.WeChatQRCodeDetectCaffeModel,
                copyFromAssets(app, R.raw.detect_caffemodel, "wechat_qrcode", "detect.caffemodel")
            )
            MMKV.defaultMMKV()?.encode(
                MMKVKey.WeChatQRCodeSrProtoTxt,
                copyFromAssets(app, R.raw.sr_prototxt, "wechat_qrcode", "sr.prototxt")
            )
            MMKV.defaultMMKV()?.encode(
                MMKVKey.WeChatQRCodeSrCaffeModel,
                copyFromAssets(app, R.raw.sr_caffemodel, "wechat_qrcode", "sr.caffemodel")
            )
        }
    }

    fun copyFromAssets(
        app: Application?,
        @RawRes resId: Int,
        targetDir: String,
        targetFileName: String
    ): String {
        val targetDirFile = app?.getDir(targetDir, Context.MODE_PRIVATE)
        val targetFile = File(targetDirFile, targetFileName)
        targetFile.outputStream().use {
            app?.resources?.openRawResource(resId)?.copyTo(it)
        }
        return targetFile.absolutePath
    }

    fun toScan(from: Activity) {
        val intent = Intent(from, CameraXActivity::class.java)
        from.startActivityForResult(intent, REQUEST_CODE)
    }

    fun parseResult(requestCode: Int, resultCode: Int, data: Intent?): String? {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && data != null) {
            return data.getStringExtra(EXTRA_CONTRACT_DATA)
        }
        return null
    }
}