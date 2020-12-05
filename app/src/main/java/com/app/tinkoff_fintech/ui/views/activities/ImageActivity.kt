package com.app.tinkoff_fintech.ui.views.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import com.app.tinkoff_fintech.App
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.ui.contracts.ImageContractInterface
import com.app.tinkoff_fintech.ui.views.activities.DetailActivity.Companion.ARG_POST_ID
import com.app.tinkoff_fintech.ui.presenters.ImagePresenter
import com.google.android.material.transition.platform.MaterialFade
import kotlinx.android.synthetic.main.image_activity.*
import javax.inject.Inject

class ImageActivity : AppCompatActivity(), ImageContractInterface.View {

    companion object {
        private const val REQUEST_STORAGE_PERMISSION = 42
    }

    @Inject
    lateinit var presenter: ImagePresenter
    lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        window.enterTransition = MaterialFade().apply {
            addTarget(R.id.containerImage)
            duration = 600L
        }
        window.returnTransition = MaterialFade().apply {
            addTarget(R.id.containerImage)
            duration = 400L
        }
        window.allowEnterTransitionOverlap = true

        (application as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_activity)
        presenter.attachView(this)
    }

    override fun init() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.materialBlack)

        val url = intent.getStringExtra(ARG_POST_ID)
        presenter.load(this, url, imageView)

        share.setOnClickListener { shareImage() }
        download.setOnClickListener { checkForPermission() }
        createAlertDialog()
    }

    override fun showProgress() {
        alertDialog.show()
    }

    override fun hideProgress() {
        alertDialog.hide()
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun createAlertDialog() {
        alertDialog = AlertDialog.Builder(this)
            .apply {
                setView(ProgressBar(this@ImageActivity).apply { setPadding(0, 20, 0, 20) })
            }
            .create()
    }

    private fun shareImage() {
        presenter.shareImage(imageView.drawToBitmap())
    }

    private fun saveImage() {
        presenter.saveImage(imageView.drawToBitmap())
    }

    private fun checkForPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            saveImage()
            return
        }
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_STORAGE_PERMISSION
            )
        } else {
            saveImage()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveImage()
                }
            }
        }
    }
}
