package com.app.tinkoff_fintech.ui.views.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.app.tinkoff_fintech.App
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.network.VkService
import com.app.tinkoff_fintech.states.NewPostState
import com.app.tinkoff_fintech.ui.contracts.NewPostContractInterface
import com.app.tinkoff_fintech.ui.presenters.NewPostPresenter
import com.app.tinkoff_fintech.utils.CreateFileFromUri
import com.app.tinkoff_fintech.utils.ImageLoad
import com.app.tinkoff_fintech.vk.wall.Doc
import com.app.tinkoff_fintech.vk.wall.photo.ResponseX
import kotlinx.android.synthetic.main.new_post_activity.*
import kotlinx.android.synthetic.main.new_post_settings.view.*
import java.io.File
import javax.inject.Inject

class NewPostActivity : AppCompatActivity(), NewPostContractInterface.View {

    @Inject
    lateinit var presenter: NewPostPresenter
    @Inject
    lateinit var vkService: VkService
    @Inject
    lateinit var createFileFromUri: CreateFileFromUri
    @Inject
    lateinit var progressDialog: AlertDialog
    @Inject
    lateinit var imageLoad: ImageLoad

    private val listParameterFile = mutableListOf<String>()
    private val postState = NewPostState()

    companion object {
        const val OWNER_PHOTO = "ownerPhoto"
        const val OWNER_NAME = "ownerName"
        const val PICK_PHOTO = "pickPhoto"
        private const val EMPTY = ""
        private const val IMAGE_PICK_CODE = 41
        private const val FILE_PICK_CODE = 42
        private const val PERMISSION_CODE = 43
        private const val TYPE_FILE_PHOTO = "photo"
        private const val TYPE_FILE_DOC = "doc"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).addNewPostComponent(this, this)
        (application as App).newPostComponent?.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_post_activity)
        presenter.attachView(this)
    }

    override fun onDestroy() {
        presenter.unsubscribe()
        (application as App).clearNewPostComponent()
        super.onDestroy()
    }

    override fun init() {
        imageLoad.execute(intent.getStringExtra(OWNER_PHOTO), ownerPhoto)
        ownerName.text = intent.getStringExtra(OWNER_NAME)
        if (intent.getBooleanExtra(PICK_PHOTO, false))
            pickImage()
        initListeners()
    }

    private fun initListeners() {
        mainRelativeLayout.setOnClickListener { editText.requestFocus() }
        editText.addTextChangedListener(textWatcher)
        iconDone.setOnClickListener { contentCheck() }
        iconClose.setOnClickListener { setResult(Activity.RESULT_CANCELED); finish() }
        pickPhoto.setOnClickListener { chooseImage() }
        addFile.setOnClickListener { chooseFile() }
        settings.setOnClickListener { chooseSettings() }
        removeLoadedImage.setOnClickListener { removeLoadedImage() }
        removeLoadedFile.setOnClickListener { removeLoadedFile() }
    }

    private fun contentCheck() {
        if (postState.postParameterMessage == EMPTY && listParameterFile.size == 0) return
        if (listParameterFile.isNotEmpty())
            postState.postParameterFile = listParameterFile.toString().substring(1, listParameterFile.toString().length - 1).replace(EMPTY, "")
        post()
    }

    private fun post() {
        presenter.post(postState)
    }

    override fun showError(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .show()
    }

    override fun showProgress() {
        progressDialog.show()
    }

    override fun hideProgress() {
        progressDialog.cancel()
    }

    override fun posted() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun chooseImage() {
        if (checkPermission())
            pickImage()
    }

    private fun chooseFile() {
        if (checkPermission())
            pickFile()
    }

    private fun chooseSettings() {
        val settingsView = LayoutInflater.from(this).inflate(R.layout.new_post_settings, null)
        settingsView.switch1.apply {
            isChecked = postState.postParameterOnlyFriends == 1
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) postState.postParameterOnlyFriends = 1 else 0
            }
        }
        settingsView.switch2.apply {
            isChecked = postState.postParameterCloseComments == 0
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) postState.postParameterCloseComments = 0 else 1
            }
        }
        settingsView.switch3.apply {
            isChecked = postState.postParameterMuteNotifications == 1
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) postState.postParameterMuteNotifications = 1 else 0
            }
        }
        AlertDialog.Builder(this)
            .setView(settingsView)
            .show()
    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
            ) {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, PERMISSION_CODE)
                return false
            }
        return true
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (p3 > 0) isEnableButtonCheck(true)
            else isEnableButtonCheck(false)
            postState.postParameterMessage = editText.text.toString()
        }
    }

    private fun isEnableButtonCheck(boolean: Boolean) {
        if (boolean) iconDone.setImageResource(R.drawable.ic_done_enable)
        else iconDone.setImageResource(R.drawable.ic_done_disable)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImage()
                } else Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "*/*" }
        startActivityForResult(intent, FILE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK)
            when (requestCode) {
                IMAGE_PICK_CODE -> {
                    val file = File(data?.data?.path?.replace("/raw", ""))
                    uploadPhotoToServer(file)
                }
                FILE_PICK_CODE -> {
                    val file = createFileFromUri.execute(data?.data)
                    if (file == null)
                        showError("Не удалось получить файл", "")
                    else uploadFileToServer(file)
                }
            }
    }

    private fun uploadFileToServer(file: File) {
        presenter.uploadFileToServer(file)
    }

    private fun uploadPhotoToServer(file: File) {
        presenter.uploadPhotoToServer(file)
    }

    override fun successLoadedPhoto(item: ResponseX) {
        shareLoadedActions(TYPE_FILE_PHOTO, item.owner_id.toString() + "_" + item.id)
        containerLoadedImage.visibility = View.VISIBLE
        imageLoad.execute(item.sizes.last().url, loadedImage)
    }

    override fun successLoadedFile(item: Doc) {
        shareLoadedActions(TYPE_FILE_DOC, item.owner_id.toString() + "_" + item.id)
        containerLoadedFile.visibility = View.VISIBLE
        nameFile.text = item.title
    }

    private fun removeLoadedImage() {
        shareRemoveLoadedActions(TYPE_FILE_PHOTO)
        containerLoadedImage.visibility = View.GONE
    }

    private fun removeLoadedFile() {
        shareRemoveLoadedActions(TYPE_FILE_DOC)
        containerLoadedFile.visibility = View.GONE
    }

    private fun shareLoadedActions(type: String, otherPath: String) {
        hideProgress()
        listParameterFile.remove(listParameterFile.find { it.contains(type) })
        listParameterFile.add(type + otherPath)
        isEnableButtonCheck(true)
    }

    private fun shareRemoveLoadedActions(type: String) {
        listParameterFile.remove(listParameterFile.find { it.contains(type) })
        if (postState.postParameterMessage.isEmpty() && listParameterFile.isEmpty() )
            isEnableButtonCheck(false)
    }

}