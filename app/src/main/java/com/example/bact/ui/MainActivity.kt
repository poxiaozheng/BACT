package com.example.bact.ui

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.bact.R
import com.example.bact.databinding.ActivityMainBinding
import com.example.bact.model.response.PostOriginImageResponse
import com.example.bact.model.response.QueryProgressResponse
import com.example.bact.service.network.BACTNetwork
import com.example.bact.util.AlbumIOUtil
import com.example.bact.util.DisplayUtil
import com.example.bact.util.ExceptionUtil
import kotlinx.coroutines.*

class MainActivity : BaseActivity() {

    companion object {
        private const val TAG = "MainActivityTAG"
    }

    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding!!
    private val viewModel: MainActivityViewModel by viewModels()
    private val scope = MainScope()
    private var isSelectOriginImage = false

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
    private lateinit var processedImageId: String
    private lateinit var receipt: String
    private lateinit var imageUrl: String
    private var processedBitmap: Bitmap? = null

    private lateinit var openAlbumLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
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
                isSelectOriginImage = true
                //val bitmap = AlbumIOUtil.uriToBitmap(this, uri)
                //AlbumIOUtil.addBitmapToAlbum(this, bitmap)
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
            if (viewModel.isProcessedFinish.value!!) {
                //保存照片至相册
                val bitmap = AlbumIOUtil.uriToBitmap(this, originImageUri)
                AlbumIOUtil.addBitmapToAlbum(this, bitmap)
                resetUi()
                Toast.makeText(this, "照片已保存至系统相册", Toast.LENGTH_SHORT).show()
            } else {
                if (!isSelectOriginImage) {
                    Toast.makeText(this, "还未选择原图！", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.setIsClickable(false)
                    it.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE

                    //上传照片代码,查询处理进度
                    //TODO: 之后解注释
                    scope.launch {
                        postImage()
                    }
                }
            }
        }

        binding.reset.setOnClickListener {
            resetUi()
        }

        originImage.setOnClickListener {
            if (viewModel.isClickable.value!!) {
                if (viewModel.isHasOriginImage.value == true) {
                    Log.d(TAG, "viewModel.isHasOriginImage.value == true")
                    enterImagePreview(originImageUri)
                } else {
                    Log.d(TAG, "viewModel.isHasOriginImage.value == false")
                    openAlbum()
                }
            }
        }

        processedImage.setOnClickListener {
            if (viewModel.isHasProcessedImage.value == true) {
                enterImagePreview(processedImageUri)
            }
        }
    }

    private fun resetUi() {
        binding.originCardView.image.setImageResource(R.drawable.camera_250)
        binding.processedCardView.image.setImageResource(R.drawable.processed_place_holder_250)
        resetScaleTextView()
        resetNoiseGradeTextView()
        binding.reset.visibility = View.GONE
        binding.startUpload.text = "开始上传"
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
        AlbumIOUtil.getShareImageUri(this)?.let {
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
            if (viewModel.isClickable.value!!) {
                resetScaleTextView()
            }
        }

        textView4X.setOnClickListener {
            if (viewModel.isClickable.value!!) {
                textView2X.isSelected = false
                textView4X.isSelected = true
                textView8X.isSelected = false
                textView16X.isSelected = false
                viewModel.setScale(4)
            }
        }

        textView8X.setOnClickListener {
            if (viewModel.isClickable.value!!) {
                textView2X.isSelected = false
                textView4X.isSelected = false
                textView8X.isSelected = true
                textView16X.isSelected = false
                viewModel.setScale(8)
            }
        }

        textView16X.setOnClickListener {
            if (viewModel.isClickable.value!!) {
                textView2X.isSelected = false
                textView4X.isSelected = false
                textView8X.isSelected = false
                textView16X.isSelected = true
                viewModel.setScale(16)
            }
        }

        textViewNoise1 = binding.textViewNoise1.apply {
            isSelected = true
        }
        textViewNoise2 = binding.textViewNoise2
        textViewNoise3 = binding.textViewNoise3
        textViewNoise4 = binding.textViewNoise4

        textViewNoise1.setOnClickListener {
            if (viewModel.isClickable.value!!) {
                resetNoiseGradeTextView()
            }
        }

        textViewNoise2.setOnClickListener {
            if (viewModel.isClickable.value!!) {
                textViewNoise1.isSelected = false
                textViewNoise2.isSelected = true
                textViewNoise3.isSelected = false
                textViewNoise4.isSelected = false
                viewModel.setNoiseGrade(1)
            }
        }

        textViewNoise3.setOnClickListener {
            if (viewModel.isClickable.value!!) {
                textViewNoise1.isSelected = false
                textViewNoise2.isSelected = false
                textViewNoise3.isSelected = true
                textViewNoise4.isSelected = false
                viewModel.setNoiseGrade(2)
            }
        }

        textViewNoise4.setOnClickListener {
            if (viewModel.isClickable.value!!) {
                textViewNoise1.isSelected = false
                textViewNoise2.isSelected = false
                textViewNoise3.isSelected = false
                textViewNoise4.isSelected = true
                viewModel.setNoiseGrade(3)
            }
        }

    }

    private fun resetScaleTextView() {
        textView2X.isSelected = true
        textView4X.isSelected = false
        textView8X.isSelected = false
        textView16X.isSelected = false
        viewModel.setScale(2)
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
        _binding = null
    }

    private suspend fun postImage() {
        withContext(Dispatchers.IO) {
            val pictureArray = AlbumIOUtil.bitmapToByteArray(
                AlbumIOUtil.uriToBitmap(
                    this@MainActivity,
                    originImageUri
                )
            )
            val scale = viewModel.scale.value
            val noiseGrade = viewModel.noiseGrade.value
            if (pictureArray != null && scale != null && noiseGrade != null) {
                val postOriginImageResponse =
                    ExceptionUtil.dealException({ PostOriginImageResponse(-100, "", "") }) {
                        BACTNetwork.postOriginImage(pictureArray, scale, noiseGrade)
                    }
                when (postOriginImageResponse.statusCode) {
                    0 -> {
                        processedImageId = postOriginImageResponse.imageId
                        receipt = postOriginImageResponse.receipt
                        Log.d(TAG, "图片上传成功，imageID：$processedImageId，receipt：$receipt")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, "图片上传成功", Toast.LENGTH_SHORT).show()
                        }
                        while (!viewModel.isProcessedFinish.value!!) {
                            if (queryProgress()) {
                                viewModel.setIsProcessedFinish(true)
                                break
                            } else {
                                delay(2000)
                            }
                        }
                    }
                    -1 -> {
                        Log.d(TAG, "图片上传失败")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, "图片上传失败", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else -> {
                        Log.d(TAG, "系统状态异常！")
                    }
                }
            }
        }
    }

    private suspend fun queryProgress(): Boolean {
        val queryProgressResponse =
            ExceptionUtil.dealException({ QueryProgressResponse(-100, "") }) {
                BACTNetwork.queryProgress(processedImageId, receipt)
            }
        when (queryProgressResponse.statusCode) {
            0 -> {
                imageUrl = queryProgressResponse.imageUrl

                //TODO 将URL变为bitmap展示
                // viewModel.setIsHasProcessedImage(true)
                viewModel.setIsProcessedFinish(true)
                Log.d(TAG, "图片转换成功，imageUrl：$imageUrl")
                //更新UI
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.startUpload.apply {
                        text = "保存图片至相册"
                        visibility = View.VISIBLE
                    }
                    binding.reset.visibility = View.VISIBLE
                    viewModel.setIsClickable(true)
                    Toast.makeText(
                        this@MainActivity, "图片转换成功，可以保存图片至相册",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return true
            }
            1 -> {
                Log.d(TAG, "图片转换失败")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "图片转换失败", Toast.LENGTH_SHORT).show()
                }
                return false
            }
            else -> {
                return false
            }
        }
    }

}