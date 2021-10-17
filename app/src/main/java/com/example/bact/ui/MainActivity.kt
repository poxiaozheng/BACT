package com.example.bact.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.example.bact.BACTApplication
import com.example.bact.R
import com.example.bact.databinding.ActivityMainBinding
import com.example.bact.model.response.PostOriginImageResponse
import com.example.bact.model.response.QueryProgressResponse
import com.example.bact.service.network.BACTNetwork
import com.example.bact.util.AlbumIOUtil
import com.example.bact.util.DisplayUtil
import com.example.bact.util.ExceptionUtil
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.*
import java.util.*
import kotlin.properties.Delegates

class MainActivity : BaseActivity() {

    companion object {
        private const val TAG = "MainActivityTAG"
    }

    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding!!
    private val viewModel: MainActivityViewModel by viewModels()
    private val scope = MainScope()
    private var padding by Delegates.notNull<Int>()

    private lateinit var textView2X: TextView
    private lateinit var textView4X: TextView
    private lateinit var textView8X: TextView
    private lateinit var textView16X: TextView

    private lateinit var textViewNoise1: TextView
    private lateinit var textViewNoise2: TextView
    private lateinit var textViewNoise3: TextView
    private lateinit var textViewNoise4: TextView

    private lateinit var originImage: ImageView
    private lateinit var processedImage: ImageView

    private lateinit var openAlbumLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initStatusBar(ContextCompat.getColor(this, R.color.white))
        padding = DisplayUtil.dp2px(applicationContext, 40f).toInt()
        init()
    }

    private fun init() {
        originImage = binding.originCardView.image.apply {
            if (viewModel.getOriginImageUri() != null) {
                setPadding(0, 0, 0, 0)
                setImageURI(viewModel.getOriginImageUri())
            } else {
                setPadding(padding, 0, padding, 0)
                setImageResource(R.drawable.camera_250)
            }
        }
        openAlbumLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                setOriginImageFromAlbum(uri)
            }
        binding.originCardView.text.apply {
            text = "原图"
        }
        processedImage = binding.processedCardView.image.apply {
            if (viewModel.getProcessedImageUri() != null) {
                setPadding(0, 0, 0, 0)
                setImageURI(viewModel.getProcessedImageUri())
            } else {
                setPadding(padding, 0, padding, 0)
                setImageResource(R.drawable.processed_place_holder_250)
            }
        }
        binding.processedCardView.text.apply {
            text = "处理结果图"
        }

        initOriginImageFromAlbum()

        initTab()

        bindingClick()

        viewModel.processedBitmap.observe(this) {
            processedImage.setImageBitmap(it)
        }

    }

    private fun bindingClick() {
        binding.startUpload.setOnClickListener {
            Log.d(TAG, "开始上传图片！")
            if (!viewModel.isHasOriginImage.value!!) {
                Toast.makeText(this, "还未选择原图！", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.setIsClickable(false)
                it.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
                binding.reset.visibility = View.GONE
                scope.launch {
                    postImage()
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
                    enterImagePreview(viewModel.getOriginImageUri()!!)
                } else {
                    Log.d(TAG, "viewModel.isHasOriginImage.value == false")
                    openAlbum()
                }
            }
        }

        processedImage.setOnClickListener {
            if (viewModel.isHasProcessedImage.value == true) {
                enterImagePreview(viewModel.getProcessedImageUri()!!)
            }
        }
    }

    private fun resetUi() {
        originImage.apply {
            setPadding(padding, 0, padding, 0)
            setImageResource(R.drawable.camera_250)
        }
        processedImage.apply {
            setPadding(padding, 0, padding, 0)
            setImageResource(R.drawable.processed_place_holder_250)
        }
        viewModel.setIsHasOriginImage(false)
        viewModel.setIsHasProcessedImage(false)
        viewModel.setOriginImageUri(null)
        viewModel.setProcessedImageUri(null)
        resetScaleTextView()
        resetNoiseGradeTextView()
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
        viewModel.setOriginImageUri(uri)
        viewModel.setIsHasOriginImage(true)
        originImage.apply {
            setPadding(0, 0, 0, 0)
            setImageURI(uri)
        }
    }

    private fun initTab() {
        textView2X = binding.textView2X
        textView4X = binding.textView4X
        textView8X = binding.textView8X
        textView16X = binding.textView16X

        when (viewModel.scale.value) {
            2 -> selectTextView2X()
            4 -> selectTextView4X()
            8 -> selectTextView8X()
            16 -> selectTextView16X()
        }

        textView2X.setOnClickListener {
            if (viewModel.isClickable.value!!) {
                resetScaleTextView()
            }
        }

        textView4X.setOnClickListener {
            if (viewModel.isClickable.value!!) {
                selectTextView4X()
                viewModel.setScale(4)
            }
        }

        textView8X.setOnClickListener {
            if (viewModel.isClickable.value!!) {
                selectTextView8X()
                viewModel.setScale(8)
            }
        }

        textView16X.setOnClickListener {
            if (viewModel.isClickable.value!!) {
                selectTextView16X()
                viewModel.setScale(16)
            }
        }

        textViewNoise1 = binding.textViewNoise1
        textViewNoise2 = binding.textViewNoise2
        textViewNoise3 = binding.textViewNoise3
        textViewNoise4 = binding.textViewNoise4

        when (viewModel.noiseGrade.value) {
            0 -> selectTextViewNoise1()
            1 -> selectTextViewNoise2()
            2 -> selectTextViewNoise3()
            3 -> selectTextViewNoise4()
        }

        textViewNoise1.setOnClickListener {
            if (viewModel.isClickable.value!!) {
                resetNoiseGradeTextView()
            }
        }

        textViewNoise2.setOnClickListener {
            if (viewModel.isClickable.value!!) {
                selectTextViewNoise2()
                viewModel.setNoiseGrade(1)
            }
        }

        textViewNoise3.setOnClickListener {
            if (viewModel.isClickable.value!!) {
                selectTextViewNoise3()
                viewModel.setNoiseGrade(2)
            }
        }

        textViewNoise4.setOnClickListener {
            if (viewModel.isClickable.value!!) {
                selectTextViewNoise4()
                viewModel.setNoiseGrade(3)
            }
        }

    }

    private fun resetScaleTextView() {
        selectTextView2X()
        viewModel.setScale(2)
    }

    private fun resetNoiseGradeTextView() {
        selectTextViewNoise1()
        viewModel.setNoiseGrade(0)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        _binding = null
    }

    private suspend fun postImage() {
        withContext(Dispatchers.IO) {
            val bitmap = AlbumIOUtil.uriToBitmap(
                this@MainActivity,
                viewModel.getOriginImageUri()!!
            )
            val pictureArray = AlbumIOUtil.bitmapToByteArray(bitmap)
            val scale = viewModel.scale.value
            val noiseGrade = viewModel.noiseGrade.value
            if (scale != null && noiseGrade != null) {
                Log.d(TAG, "scale:$scale")
                Log.d(TAG, "noiseGrade:$noiseGrade")

                val mediaType = MediaType.parse("image/jpg")
                val requestBody = RequestBody.create(mediaType, pictureArray)
                val request = Request.Builder()
                    .url("http://192.168.88.194:8081/bact/postOriginImage?scale=$scale&noiseGrade=$noiseGrade")
                    .method("POST", requestBody)
                    .addHeader("Content-Type", "image/jpg")
                    .build()

                val response = BACTApplication.okHttpClient!!.newCall(request).execute()
                Log.d(TAG, "response:$response")
                val responseData = response.body()?.string()
                val gson = Gson()
                val postOriginImageResponse =
                    gson.fromJson(responseData, PostOriginImageResponse::class.java)

                when (postOriginImageResponse.statusCode) {
                    0 -> {
                        val processedImageId = postOriginImageResponse.imageId
                        viewModel.setProcessedImageId(processedImageId)
                        val receipt = postOriginImageResponse.receipt
                        viewModel.setReceipt(receipt)
                        Log.d(TAG, "图片上传成功，imageID：$processedImageId，receipt：$receipt")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, "图片上传成功", Toast.LENGTH_SHORT).show()
                        }
                        while (!viewModel.isHasProcessedImage.value!!) {
                            if (queryProgress()) {
                                break
                            } else {
                                delay(15000)
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
                BACTNetwork.queryProgress(viewModel.getProcessedImageId(), viewModel.getReceipt())
            }
        when (queryProgressResponse.statusCode) {
            0 -> {
                val imageUrl = queryProgressResponse.imageUrl
                viewModel.setImageUrl(imageUrl)

                val imageRequest = Request.Builder().url(imageUrl).get().build()

                val response = BACTApplication.okHttpClient?.newCall(imageRequest)?.execute()

                val imageContent = response?.body()?.bytes()
                if (imageContent != null) {
                    val imageBitMap =
                        BitmapFactory.decodeByteArray(imageContent, 0, imageContent.size, null)

                    withContext(Dispatchers.Main) {
                        viewModel.setProcessedBitmap(imageBitMap)
                        processedImage.setImageBitmap(imageBitMap)
                        viewModel.setIsHasProcessedImage(true)
                        val uri = AlbumIOUtil.addBitmapToAlbum(
                            this@MainActivity,
                            imageBitMap,
                            "image/png",
                            Bitmap.CompressFormat.PNG
                        )
                        Log.d(TAG, "uri:$uri")
                        viewModel.setProcessedImageUri(uri)
                    }
                }

                withContext(Dispatchers.Main) {
                    Log.d(TAG, "图片转换成功，imageUrl：$imageUrl")
                    //更新UI
                    binding.progressBar.visibility = View.GONE
                    binding.startUpload.visibility = View.VISIBLE
                    binding.reset.visibility = View.VISIBLE
                    viewModel.setIsClickable(true)
                    Toast.makeText(
                        this@MainActivity, "图片转换成功，已保存至系统相册！",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return true
            }
            -1 -> {
                Log.d(TAG, "图片转换还未完成")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "图片转换还未完成", Toast.LENGTH_SHORT).show()
                }
                return false
            }
            else -> {
                return false
            }
        }
    }

    private fun selectTextView2X() {
        textView2X.isSelected = true
        textView4X.isSelected = false
        textView8X.isSelected = false
        textView16X.isSelected = false
    }

    private fun selectTextView4X() {
        textView2X.isSelected = false
        textView4X.isSelected = true
        textView8X.isSelected = false
        textView16X.isSelected = false
    }

    private fun selectTextView8X() {
        textView2X.isSelected = false
        textView4X.isSelected = false
        textView8X.isSelected = true
        textView16X.isSelected = false
    }

    private fun selectTextView16X() {
        textView2X.isSelected = false
        textView4X.isSelected = false
        textView8X.isSelected = false
        textView16X.isSelected = true
    }

    private fun selectTextViewNoise1() {
        textViewNoise1.isSelected = true
        textViewNoise2.isSelected = false
        textViewNoise3.isSelected = false
        textViewNoise4.isSelected = false
    }

    private fun selectTextViewNoise2() {
        textViewNoise1.isSelected = false
        textViewNoise2.isSelected = true
        textViewNoise3.isSelected = false
        textViewNoise4.isSelected = false
    }

    private fun selectTextViewNoise3() {
        textViewNoise1.isSelected = false
        textViewNoise2.isSelected = false
        textViewNoise3.isSelected = true
        textViewNoise4.isSelected = false
    }

    private fun selectTextViewNoise4() {
        textViewNoise1.isSelected = false
        textViewNoise2.isSelected = false
        textViewNoise3.isSelected = false
        textViewNoise4.isSelected = true
    }

}