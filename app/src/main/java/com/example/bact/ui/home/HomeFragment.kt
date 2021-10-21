package com.example.bact.ui.home

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bact.BACTApplication
import com.example.bact.R
import com.example.bact.databinding.FragmentHomeBinding
import com.example.bact.model.response.QueryProgressResponse
import com.example.bact.service.network.BACTNetwork
import com.example.bact.service.network.ServiceCreator
import com.example.bact.ui.activity.PreviewImageActivity
import com.example.bact.ui.history.HistoryFragmentViewModel
import com.example.bact.ui.history.HistoryFragmentViewModelFactory
import com.example.bact.util.CommonUtil
import com.example.bact.util.DisplayUtil
import com.example.bact.util.ExceptionUtil
import com.example.bact.util.FileIOUtil
import kotlinx.coroutines.*
import kotlin.properties.Delegates

class HomeFragment : Fragment() {

    private val homeFragmentViewModel: HomeFragmentViewModel by activityViewModels()

    private val historyFragmentViewModel: HistoryFragmentViewModel by activityViewModels {
        HistoryFragmentViewModelFactory(BACTApplication.database.imageInfoDao())
    }

    private val scope = MainScope()
    private var padding by Delegates.notNull<Int>()
    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = _binding!!

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

    companion object {
        private const val TAG = "HomeFragment"

        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val layoutView = binding.root
        init()
        return layoutView
    }

    private fun init() {

        padding = DisplayUtil.dp2px(BACTApplication.appContext, 40f).toInt()

        originImage = binding.originCardView.image.apply {
            if (homeFragmentViewModel.getOriginImageUri() != null) {
                setPadding(0, 0, 0, 0)
                setImageURI(homeFragmentViewModel.getOriginImageUri())
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
            if (homeFragmentViewModel.getProcessedImageUri() != null) {
                setPadding(0, 0, 0, 0)
                setImageURI(homeFragmentViewModel.getProcessedImageUri())
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

        viewModelObserve()

    }

    private fun viewModelObserve() {
        homeFragmentViewModel.processedBitmap.observe(viewLifecycleOwner) {
            processedImage.apply {
                setPadding(0, 0, 0, 0)
                setImageBitmap(it)
            }
        }

        homeFragmentViewModel.scale.observe(viewLifecycleOwner) {
            when (it) {
                2 -> selectTextView2X()
                4 -> selectTextView4X()
                8 -> selectTextView8X()
                16 -> selectTextView16X()
                else -> throw Exception("scale is not match!")
            }
        }

        homeFragmentViewModel.noiseGrade.observe(viewLifecycleOwner) {
            when (it) {
                0 -> selectTextViewNoise1()
                1 -> selectTextViewNoise2()
                2 -> selectTextViewNoise3()
                3 -> selectTextViewNoise4()
                else -> throw Exception("noiseGrade is not match!")
            }
        }
    }

    private fun bindingClick() {
        binding.startUpload.setOnClickListener {
            Log.d(TAG, "开始上传图片！")
            if (!homeFragmentViewModel.getIsHasOriginImage()) {
                Toast.makeText(requireContext(), "还未选择原图！", Toast.LENGTH_SHORT).show()
            } else {
                homeFragmentViewModel.setIsClickable(false)
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
            if (homeFragmentViewModel.getIsClickable()) {
                if (homeFragmentViewModel.getIsHasOriginImage()) {
                    Log.d(TAG, "originImage:enterImagePreview")
                    enterImagePreview(homeFragmentViewModel.getOriginImageUri()!!)
                } else {
                    Log.d(TAG, "openAlbum")
                    openAlbum()
                }
            }
        }

        processedImage.setOnClickListener {
            if (homeFragmentViewModel.getIsHasProcessedImage()) {
                Log.d(TAG, "processedImage:enterImagePreview")
                enterImagePreview(homeFragmentViewModel.getProcessedImageUri()!!)
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
        homeFragmentViewModel.apply {
            setIsHasOriginImage(false)
            setIsHasProcessedImage(false)
            setOriginImageUri(null)
            setProcessedImageUri(null)
            setScale(2)
            setNoiseGrade(0)
        }
        binding.startUpload.text = "开始上传"
    }

    private fun enterImagePreview(uri: Uri) {
        val intent = Intent(requireActivity(), PreviewImageActivity::class.java)
        intent.putExtra("key", uri)
        startActivity(intent)
    }

    private fun openAlbum() {
        openAlbumLauncher.launch("image/*")
    }

    private fun initOriginImageFromAlbum() {
        FileIOUtil.getShareImageUri(requireActivity())?.let {
            setOriginImageFromAlbum(it)
        }
    }

    private fun setOriginImageFromAlbum(uri: Uri) {
        homeFragmentViewModel.setOriginImageUri(uri)
        homeFragmentViewModel.setIsHasOriginImage(true)
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

        textView2X.setOnClickListener {
            if (homeFragmentViewModel.getIsClickable()) {
                homeFragmentViewModel.setScale(2)
            }
        }

        textView4X.setOnClickListener {
            if (homeFragmentViewModel.getIsClickable()) {
                homeFragmentViewModel.setScale(4)
            }
        }

        textView8X.setOnClickListener {
            if (homeFragmentViewModel.getIsClickable()) {
                homeFragmentViewModel.setScale(8)
            }
        }

        textView16X.setOnClickListener {
            if (homeFragmentViewModel.getIsClickable()) {
                homeFragmentViewModel.setScale(16)
            }
        }

        textViewNoise1 = binding.textViewNoise1
        textViewNoise2 = binding.textViewNoise2
        textViewNoise3 = binding.textViewNoise3
        textViewNoise4 = binding.textViewNoise4

        textViewNoise1.setOnClickListener {
            if (homeFragmentViewModel.getIsClickable()) {
                homeFragmentViewModel.setNoiseGrade(0)
            }
        }

        textViewNoise2.setOnClickListener {
            if (homeFragmentViewModel.getIsClickable()) {
                homeFragmentViewModel.setNoiseGrade(1)
            }
        }

        textViewNoise3.setOnClickListener {
            if (homeFragmentViewModel.getIsClickable()) {
                homeFragmentViewModel.setNoiseGrade(2)
            }
        }

        textViewNoise4.setOnClickListener {
            if (homeFragmentViewModel.getIsClickable()) {
                homeFragmentViewModel.setNoiseGrade(3)
            }
        }

    }

    private suspend fun postImage() {
        withContext(Dispatchers.IO) {
            val bitmapWithMime = FileIOUtil.uriToBitmapWithMime(
                requireContext(),
                homeFragmentViewModel.getOriginImageUri()!!
            )
            val mimeType = bitmapWithMime.mimeTyp
            Log.d(TAG, "mimeType:$mimeType")
            Log.d(TAG, "bitmapSize:${bitmapWithMime.bitmap.byteCount}")
            val pictureArray = FileIOUtil.bitmapToByteArray(bitmapWithMime.bitmap)
            val scale = homeFragmentViewModel.scale.value
            val noiseGrade = homeFragmentViewModel.noiseGrade.value
//            val imageInfo = ImageInfo(pictureArray,scale,
//                noiseGrade,CommonUtil.timeStampToData(System.currentTimeMillis()),"a.png")
            //BACTApplication.database.imageInfoDao().insert()
            if (scale != null && noiseGrade != null) {
                Log.d(TAG, "scale:$scale")
                Log.d(TAG, "noiseGrade:$noiseGrade")
                val postOriginImageResponse =
                    BACTNetwork.startOkhttpRequest(pictureArray, scale, noiseGrade, mimeType)

                when (postOriginImageResponse.statusCode) {
                    0 -> {
                        val processedImageId = postOriginImageResponse.imageId
                        homeFragmentViewModel.setProcessedImageId(processedImageId)
                        val receipt = postOriginImageResponse.receipt
                        homeFragmentViewModel.setReceipt(receipt)
                        Log.d(TAG, "图片上传成功，imageID：$processedImageId，receipt：$receipt")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "图片上传成功,请耐心等待图片生成",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        while (!homeFragmentViewModel.getIsHasProcessedImage()) {
                            if (queryProgress()) {
                                break
                            } else {
                                delay(5000)
                            }
                        }
                    }
                    -1 -> {
                        Log.d(TAG, "图片上传失败")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "图片上传失败", Toast.LENGTH_SHORT).show()
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
                BACTNetwork.queryProgress(
                    homeFragmentViewModel.getProcessedImageId(),
                    homeFragmentViewModel.getReceipt()
                )
            }
        when (queryProgressResponse.statusCode) {
            0 -> {
                val imageUrl =
                    ServiceCreator.getServiceBaseUrl() + "/bact/output/" + queryProgressResponse.imageUrl
                homeFragmentViewModel.setImageUrl(imageUrl)

                val imageContent = FileIOUtil.getUrlImageByteArray(imageUrl)

                if (imageContent != null) {
                    val opts = BitmapFactory.Options()
                    opts.inPreferredConfig = Bitmap.Config.ARGB_8888
                    val imageBitMap = FileIOUtil.byteArrayToBitmap(imageContent, opts)

                    withContext(Dispatchers.Main) {
                        homeFragmentViewModel.setProcessedBitmap(imageBitMap)
                        homeFragmentViewModel.setIsHasProcessedImage(true)
                        val uri = FileIOUtil.addBitmapToAlbum(
                            requireContext(),
                            imageBitMap
                        )
                        homeFragmentViewModel.setProcessedImageUri(uri)
                    }
                }

                withContext(Dispatchers.Main) {
                    Log.d(TAG, "图片转换成功，imageUrl：$imageUrl")
                    binding.progressBar.visibility = View.GONE
                    binding.startUpload.visibility = View.VISIBLE
                    binding.reset.visibility = View.VISIBLE
                    homeFragmentViewModel.setIsClickable(true)
                    Toast.makeText(
                        requireContext(), "图片转换成功，已保存至系统相册！",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                val date = CommonUtil.timeStampToData(System.currentTimeMillis())
                historyFragmentViewModel.insertItem(
                    historyFragmentViewModel.newItem(
                        imageContent!!,
                        homeFragmentViewModel.scale.value!!,
                        homeFragmentViewModel.noiseGrade.value!!,
                        date,
                        imageUrl
                    )
                )
                return true
            }
            -1 -> {
                Log.d(TAG, "图片转换还未完成")
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

    override fun onDestroyView() {
        super.onDestroyView()
        scope.cancel()
        _binding = null
    }
}