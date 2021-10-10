package com.example.bact.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.bact.R
import com.example.bact.databinding.ActivityMainBinding
import com.example.bact.util.AlbumUtil
import com.example.bact.util.DisplayUtil
import kotlinx.coroutines.*

class MainActivity : BaseActivity() {

    companion object {
        private const val TAG = "MainActivityTAG"
    }

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()
    private val scope = MainScope()

    private lateinit var textView2X: TextView
    private lateinit var textView4X: TextView
    private lateinit var textView8X: TextView
    private lateinit var textView16X: TextView

    private lateinit var textViewNoise1: TextView
    private lateinit var textViewNoise2: TextView
    private lateinit var textViewNoise3: TextView
    private lateinit var textViewNoise4: TextView

    private lateinit var originImage: ImageView
    private lateinit var originImageUri: Uri
    private lateinit var processedImage: ImageView
    private lateinit var processedImageUri: Uri

    private lateinit var openAlbumLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        val padding = DisplayUtil.dp2px(applicationContext, 40f).toInt()
        originImage = binding.originCardView.image.apply {
            setPadding(padding, 0, padding, 0)
            setImageResource(R.drawable.camera_250)
        }
        openAlbumLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                setOriginImageFromAlbum(uri)
                val bitmap = AlbumUtil.uriToBitmap(this, uri)
                AlbumUtil.addBitmapToAlbum(this, bitmap)
            }
        binding.originCardView.text.apply {
            text = "原图"
        }
        processedImage = binding.processedCardView.image.apply {
            setPadding(padding, 0, padding, 0)
            setImageResource(R.drawable.processed_place_holder_250)
        }
        binding.processedCardView.text.apply {
            text = "处理结果图"
        }

        initOriginImageFromAlbum()

        initTab()

        bindingClick()
    }

    private fun bindingClick() {
        binding.startUpload.setOnClickListener {
            if (viewModel.isProcessFinish.value == true) {
                Toast.makeText(this, "照片已保存至系统相册", Toast.LENGTH_SHORT).show()
                //保存照片至相册
//                val bitmap = AlbumUtil.uriToBitmap(this, uri)
//                AlbumUtil.addBitmapToAlbum(
//                    this, bitmap,)
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
            binding.originCardView.image.setImageResource(R.drawable.camera_250)
            binding.processedCardView.image.setImageResource(R.drawable.processed_place_holder_250)
            resetMultipleTextView()
            resetNoiseGradeTextView()
            binding.reset.visibility = View.GONE
            binding.startUpload.text = "开始上传"
        }

        binding.originCardView.image.setOnClickListener {
            if (viewModel.isHasOriginImage.value == true) {
                Log.d(TAG,"viewModel.isHasOriginImage.value == true")
                enterImagePreview(originImageUri)
            } else {
                Log.d(TAG,"viewModel.isHasOriginImage.value == false")
                openAlbum()
            }
        }

        binding.processedCardView.image.setOnClickListener {
            if (viewModel.isHasProcessedImage.value == true) {
                enterImagePreview(processedImageUri)
            }
        }
    }

    private fun enterImagePreview(uri: Uri) {
        val intent = Intent(this@MainActivity, PreviewImageActivity::class.java)
        intent.putExtra("key", uri)
        startActivity(intent)
    }

    private fun openAlbum() {
        openAlbumLauncher.launch("image/*")
    }

    private fun initOriginImageFromAlbum() {
        AlbumUtil.getShareImageUri(this)?.let {
            setOriginImageFromAlbum(it)
        }
    }

    private fun setOriginImageFromAlbum(uri: Uri) {
        originImageUri = uri
        viewModel.setIsHasOriginImage(true)
        originImage.apply {
            setPadding(0, 0, 0, 0)
            setImageURI(uri)
        }
    }

    private fun initTab() {
        textView2X = binding.textView2X.apply {
            isSelected = true
        }
        textView4X = binding.textView4X
        textView8X = binding.textView8X
        textView16X = binding.textView16X

        textView2X.setOnClickListener {
            resetMultipleTextView()
        }

        textView4X.setOnClickListener {
            textView2X.isSelected = false
            textView4X.isSelected = true
            textView8X.isSelected = false
            textView16X.isSelected = false
            viewModel.setMultiple(4)
        }

        textView8X.setOnClickListener {
            textView2X.isSelected = false
            textView4X.isSelected = false
            textView8X.isSelected = true
            textView16X.isSelected = false
            viewModel.setMultiple(8)
        }

        textView16X.setOnClickListener {
            textView2X.isSelected = false
            textView4X.isSelected = false
            textView8X.isSelected = false
            textView16X.isSelected = true
            viewModel.setMultiple(16)
        }

        textViewNoise1 = binding.textViewNoise1.apply {
            isSelected = true
        }
        textViewNoise2 = binding.textViewNoise2
        textViewNoise3 = binding.textViewNoise3
        textViewNoise4 = binding.textViewNoise4

        textViewNoise1.setOnClickListener {
            resetNoiseGradeTextView()
        }

        textViewNoise2.setOnClickListener {
            textViewNoise1.isSelected = false
            textViewNoise2.isSelected = true
            textViewNoise3.isSelected = false
            textViewNoise4.isSelected = false
            viewModel.setNoiseGrade(1)
        }

        textViewNoise3.setOnClickListener {
            textViewNoise1.isSelected = false
            textViewNoise2.isSelected = false
            textViewNoise3.isSelected = true
            textViewNoise4.isSelected = false
            viewModel.setNoiseGrade(2)
        }

        textViewNoise4.setOnClickListener {
            textViewNoise1.isSelected = false
            textViewNoise2.isSelected = false
            textViewNoise3.isSelected = false
            textViewNoise4.isSelected = true
            viewModel.setNoiseGrade(3)
        }

    }

    private fun resetMultipleTextView() {
        textView2X.isSelected = true
        textView4X.isSelected = false
        textView8X.isSelected = false
        textView16X.isSelected = false
        viewModel.setMultiple(2)
    }

    private fun resetNoiseGradeTextView() {
        textViewNoise1.isSelected = true
        textViewNoise2.isSelected = false
        textViewNoise3.isSelected = false
        textViewNoise4.isSelected = false
        viewModel.setNoiseGrade(0)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}