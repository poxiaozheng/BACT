package com.example.bact.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.example.bact.R
import com.example.bact.databinding.ActivityMainBinding
import com.example.bact.util.DisplayUtil
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private var isHasOriginImage = false
    private var isHasProcessedImage = false
    private var isProcessFinish = false
    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {

        initStatusBar(ContextCompat.getColor(this@MainActivity, R.color.white))

        supportActionBar?.hide()

        val padding = DisplayUtil.dp2px(applicationContext, 40f).toInt()
        val originImage = binding.originCardView.image.apply {
            setPadding(padding, 0, padding, 0)
            setImageResource(R.drawable.camera_250)
        }
        binding.originCardView.text.apply {
            text = "原图"
        }
        val processedImage = binding.processedCardView.image.apply {
            setPadding(padding, 0, padding, 0)
            setImageResource(R.drawable.processed_place_holder_250)
        }
        binding.processedCardView.text.apply {
            text = "处理结果图"
        }

        initOriginImageFromAlbum(originImage)

        initTab()

        bindingClick()
    }

    private fun bindingClick() {
        binding.startUpload.setOnClickListener {
            if (isProcessFinish) {
                Toast.makeText(this, "照片已保存至系统相册", Toast.LENGTH_SHORT).show()
                //保存照片至相册
            } else {
                it.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
                //添加上传照片代码,上传完处理完成要改为true
                scope.launch {
                    withContext(Dispatchers.IO) {
                        delay(1500)
                    }
                }
                binding.progressBar.visibility = View.GONE
                binding.startUpload.apply {
                    text = "保存图片至相册"
                    visibility = View.VISIBLE
                }
                binding.reset.visibility = View.VISIBLE
            }
        }

        binding.reset.setOnClickListener {

        }

        binding.originCardView.image.setOnClickListener {
            if (isHasOriginImage) {
                //双指缩放
            } else {
                //打开相册选择图片
            }
        }

        binding.processedCardView.image.setOnClickListener {
            if (isHasProcessedImage) {
                //打开相册选择图片
            }
        }
    }

    private fun openAlbum() {

    }

    private fun initOriginImageFromAlbum(originImage: ImageView) {
        getShareImageUri()?.let {
            originImage.apply {
                setPadding(0, 0, 0, 0)
                setImageURI(it)
            }
        }
        isHasOriginImage = true
    }

    private fun initStatusBar(@ColorInt color: Int) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window = window.apply {
                //设置修改状态栏
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                //clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                //设置状态栏的颜色
                statusBarColor = color
            }

            if (isLightColor(color)) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }

        }
    }

    private fun isLightColor(@ColorInt color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) >= 0.5
    }

    private fun initTab() {
        val textView2X = binding.textView2X.apply {
            isSelected = true
        }
        val textView4X = binding.textView4X
        val textView8X = binding.textView8X
        val textView16X = binding.textView16X

        textView2X.setOnClickListener {
            textView2X.isSelected = true
            textView4X.isSelected = false
            textView8X.isSelected = false
            textView16X.isSelected = false
        }

        textView4X.setOnClickListener {
            textView2X.isSelected = false
            textView4X.isSelected = true
            textView8X.isSelected = false
            textView16X.isSelected = false
        }

        textView8X.setOnClickListener {
            textView2X.isSelected = false
            textView4X.isSelected = false
            textView8X.isSelected = true
            textView16X.isSelected = false
        }

        textView16X.setOnClickListener {
            textView2X.isSelected = false
            textView4X.isSelected = false
            textView8X.isSelected = false
            textView16X.isSelected = true
        }

        val textViewNoise1 = binding.textViewNoise1.apply {
            isSelected = true
        }
        val textViewNoise2 = binding.textViewNoise2
        val textViewNoise3 = binding.textViewNoise3
        val textViewNoise4 = binding.textViewNoise4

        textViewNoise1.setOnClickListener {
            textViewNoise1.isSelected = true
            textViewNoise2.isSelected = false
            textViewNoise3.isSelected = false
            textViewNoise4.isSelected = false
        }

        textViewNoise2.setOnClickListener {
            textViewNoise1.isSelected = false
            textViewNoise2.isSelected = true
            textViewNoise3.isSelected = false
            textViewNoise4.isSelected = false
        }

        textViewNoise3.setOnClickListener {
            textViewNoise1.isSelected = false
            textViewNoise2.isSelected = false
            textViewNoise3.isSelected = true
            textViewNoise4.isSelected = false
        }

        textViewNoise4.setOnClickListener {
            textViewNoise1.isSelected = false
            textViewNoise2.isSelected = false
            textViewNoise3.isSelected = false
            textViewNoise4.isSelected = true
        }

    }

    private fun getShareImageUri(): Uri? {
        val intent = intent
        val extras = intent.extras
        val action = intent.action
        if (Intent.ACTION_SEND == action) {
            extras?.let {
                if (it.containsKey(Intent.EXTRA_STREAM)) {
                    try {
                        return it.getParcelable(Intent.EXTRA_STREAM) as Uri?
                        //val path = getRealPathFromURI(this, uri)
                    } catch (e: Exception) {
                        Log.d(TAG, e.toString())
                    }
                }
            }
        }
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

//    private fun getRealPathFromURI(activity: Activity, uri: Uri?): String? {
//        val arr = arrayOf(MediaStore.Images.Media.DATA)
//        val cursor = activity.managedQuery(uri, arr, null, null, null)
//            ?: return uri?.path
//        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//        cursor.moveToFirst()
//        return cursor.getString(columnIndex)
//    }

}