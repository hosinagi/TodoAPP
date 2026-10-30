package com.hosinagi.todoapp.demo.permissionDemo

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.databinding.ActivityPermissionDemoBinding
import com.permissionx.guolindev.PermissionX
import es.dmoral.toasty.Toasty
import java.io.File

class PermissionDemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPermissionDemoBinding

    // 系统弹窗申请拍照权限，根据用户对于授予的抉择执行不同逻辑，真正申请权限的一步
    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                // 权限申请成功，执行拍照逻辑
                takePhoto()
            } else {
                // 处理权限被永久拒绝时的弹窗
                handlePermissionDenied(android.Manifest.permission.CAMERA)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPermissionDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 设置按钮点击事件
        setupClickListeners()
    }


    // 点击事件
    private fun setupClickListeners() {
        binding.btnTakePhoto.setOnClickListener {
            // 拍照，编写方法判断当前是否有拍照的权限
            checkAndRequestCameraPermission()
        }
        binding.btnOpenGallery.setOnClickListener {
            // 打开相册，编写一个点击事件
            openGallery()
        }
        binding.btnTakePhotoX.setOnClickListener {
            // 使用permissionX库打开相机
            checkAndRequestCameraPermissionX()
        }
        binding.btnOpenGalleryX.setOnClickListener {
            // 使用permissionX库打开相册
            openGalleryX()
        }
    }

    // 拍照
    // Uri在跳转完成后会保存图片数据，定义为成员变量便于后续操作
    private var photoUri: Uri? = null
    private fun takePhoto() {
        // 1.创建一个空的图片文件，将图片保存到指定路径
        val photoFile = createImageFile()
        // 2.将file对象转成content://URI，用于后续操作
        photoUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            photoFile
        )
        // 3.通过intent跳转相机应用，action：动作
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // 4.跳转时需要将Uri传递过去，用于保存图片和授权
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        // 5.传入intent启动相机应用，不能直接使用startActivity这种老方法
        takePhotoLauncher.launch(takePictureIntent)
    }

    // 启动相机应用，新的跳转方法ActivityResultAPI
    private val takePhotoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // 判断拍照是否成功，it.resultCode == RESULT_OK就表示拍照成功
            if (it.resultCode == RESULT_OK) {
                // 处理回调结果，跳转成功后Uri存储着图片数据
                // 使用setImageURI可以将URI中存储的图片显示出来
                binding.ivPreview.setImageURI(photoUri)
                // 如果图片保存成功，则将图片复制一份保存到系统相册，方便相册查看
                photoUri?.let { uri -> copyUriToGallery(uri) }
            }
        }


    // 创建图片文件，给图片命名，加上后缀，存入指定的系统外部空间
    // 补充：这边有一个bug，照片保存到了应用专属目录，系统相册不会收录它。
    // getExternalFilesDir() 返回的是 App 私有外部目录，其它应用，如相册无法访问
    private fun createImageFile(): File {
        // 定义时间戳
        val timeStamp = System.currentTimeMillis()
        // 定义图片名称,使用时间戳保证名称唯一
        val imageFileName = "JPEG_${timeStamp}_"
        // 获取存储图片的外部存储空间的路径，官方API
        val storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
        // 返回一个File文件，图片文件，一个图片就是一个文件
        return File.createTempFile(imageFileName, ".jpg", storageDir)
        //getExternalFilesDir()：获取应用专属外部存储目录
        //DIRECTORY_PICTURES：系统预定义的子目录类型，对应Android/data/包名/files/Pictures/
    }

    // 将图片复制一份保存到系统相册
    private fun copyUriToGallery(sourceUri: Uri) {
        val name = "JPEG_${System.currentTimeMillis()}.jpg"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/TodoApp"
                )
            }
        }
        val targetUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: return
        runCatching {
            contentResolver.openInputStream(sourceUri)?.use { input ->
                contentResolver.openOutputStream(targetUri)?.use { output ->
                    input.copyTo(output)
                }
            }
            Toasty.success(this, "已保存到相册").show()
        }.onFailure {
            contentResolver.delete(targetUri, null, null)
            Toasty.error(this, "保存失败：${it.message}").show()
        }
    }


    // 判断是否有相机权限
    private fun checkAndRequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            takePhoto()
        } else {
            // 没有权限则去申请
            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    // 根据请求的权限的不同，设置不同权限被永久拒绝时的弹窗
    private fun handlePermissionDenied(permission: String) {
        // 分单次拒绝 和 多次拒绝
        // 单词拒绝和多次拒绝的弹窗的内容不同，用官方的API判断shouldShowRequestPermissionRationale
        val permissionType = when (permission) {
            android.Manifest.permission.CAMERA -> "相机"
            android.Manifest.permission.READ_EXTERNAL_STORAGE -> "相册"
            else -> "未知"
        }
        if (shouldShowRequestPermissionRationale(permission)) {
            // 为true则单次拒绝
            showPermissionDeniedMessage(permissionType)
        } else {
            // 为false则永久拒绝
            showSettingsDialog(permissionType)
        }

    }

    // 处理权限被单次拒绝时的Dialog弹窗
    private fun showPermissionDeniedMessage(permissionType: String) {
        AlertDialog.Builder(this)
            .setTitle("权限被拒绝")
            .setMessage("需要${permissionType}权限才能使用此功能。")
            .setPositiveButton("确定", null)
            .show()
    }

    // 处理权限被永久拒绝时的Dialog弹窗
    private fun showSettingsDialog(permissionType: String) {
        AlertDialog.Builder(this)
            .setTitle("需要权限")
            .setMessage("应用需要${permissionType}权限才能正常工作。请在设置中手动开启权限。")
            .setPositiveButton("去设置") { _, _ ->
                // 跳转到手机设置页面
                openAppSettings()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    // 跳转到手机设置页面
    private fun openAppSettings() {
        // 指定跳转到设置页面
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        // 设置data详细指定跳转的包名，跳转到设置中本应用的详情页
        intent.data = Uri.fromParts("package", packageName, null)
        startActivity(intent)// 开启跳转
    }

    // 相册
    // 打开相册点击事件
    private fun openGallery() {
        // 1.判断用户的手机系统版本，Android13以下还是13以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // 13以上，PhotoPicker API 无需申请权限，此方法比较安全
            // 构建PickVisualMediaRequest对象
            val pick = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)

            // 需要传入一个PickVisualMediaRequest对象
            pickImageLauncher.launch(pick)
        } else {
            // 13以下，需要申请权限，调用系统相册
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // 有权限，打开相册
                openGalleryLegacy()
            } else {
                // 无权限，申请权限
                storagePermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    // 13以上版本，用ActivityResult API 创建Photo Picker方法，调用相册
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            // 回调的结果就是URI，但是可能为空，使用let
            it?.let {
                // 使用setImageURI可以将URI中存储的图片显示出来
                binding.ivPreview.setImageURI(it)
            }
        }

    // 向用户申请Android12版本以下的读取存储权限
    private val storagePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // 权限申请成功，执行打开相册逻辑
                openGalleryLegacy()
            } else {
                // 处理权限被永久拒绝时的弹窗
                handlePermissionDenied(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

    // 更低版本的打开相册功能，Photo PickerAPI，只能用于新版
    private fun openGalleryLegacy() {
        // 使用老版本的intent跳转功能
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 100)
    }

    // 重写onActivityResult方法，处理低版本相册回调
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 判断请求码和结果码是否匹配
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // data.data才是图片的URI
            data?.data?.let {
                binding.ivPreview.setImageURI(it)
            }
        }
    }

    // permissionX打开相机
    private fun checkAndRequestCameraPermissionX() {
        PermissionX.init(this)
            // 设置要请求的权限
            .permissions(android.Manifest.permission.CAMERA)
            // 设置请求权限前的解释原因，这一步是开启解释弹窗
            .explainReasonBeforeRequest()
            // 遍历被单次拒绝的权限列表，scope：PermissionX，deniedList：被拒绝的权限列表
            .onExplainRequestReason { scope, deniedList ->
                // 调用permissionX自带的弹窗，在系统权限请求弹窗前，先弹一个我们自定义的弹窗
                scope.showRequestReasonDialog(
                    deniedList,
                    "打开相机需要权限",
                    "确定",
                    "取消"
                )
            }
            // 当用户永久拒绝时，弹窗提醒去设置中心
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "需要打开相机权限",
                    "去设置",
                    "取消"
                )
            }
            // 编写最后的回调逻辑，判断权限的申请是否全部通过
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    // 权限全部通过，打开相机
                    takePhoto()
                } else {
                    // 权限被拒绝，提示用户
                    Toasty.error(this, "权限被拒绝，请重新申请").show()
                }
            }

    }

    // permissionX打开相册
    private fun openGalleryX() {
        // Android13及以上的版本不要使用permissionX打开相册，用Photo Picker API
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pickImageLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }else{
            // Android12及以下，使用permissionX打开相册
            PermissionX.init(this)
                .permissions(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .explainReasonBeforeRequest()
                .onExplainRequestReason { scope, deniedList ->
                    scope.showRequestReasonDialog(
                        deniedList,
                        "打开相册需要读取权限",
                        "确定",
                        "取消"
                    )
                }
                .onForwardToSettings { scope, deniedList ->
                    scope.showForwardToSettingsDialog(
                        deniedList,
                        "需要打开读取权限",
                        "去设置",
                        "取消"
                    )
                }
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        // 权限全部通过，打开相册
                        openGalleryLegacy()
                    } else {
                        // 权限被拒绝，提示用户
                        Toasty.error(this, "权限被拒绝，请重新申请").show()
                    }
                }
        }
    }

}