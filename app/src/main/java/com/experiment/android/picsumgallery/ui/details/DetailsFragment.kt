package com.experiment.android.picsumgallery.ui.details

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.experiment.android.picsumgallery.R
import com.experiment.android.picsumgallery.databinding.FragmentDetailsBinding
import com.experiment.android.picsumgallery.model.PicsumResponse
import com.experiment.android.picsumgallery.ui.base.loadDetailImage
import com.experiment.android.picsumgallery.utils.AppConstants.REQUEST_STORAGE
import com.experiment.android.picsumgallery.utils.common.createImageUrl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val args by navArgs<DetailsFragmentArgs>()

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageModel: PicsumResponse
    private lateinit var mContext: Context
    private lateinit var imageURL: String
    private lateinit var resource: Bitmap

    private val completableJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + completableJob)

    private var downloadImageFlag: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentDetailsBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner

        imageModel = args.dataModel

        loadImage()

        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun loadImage() {
        val height = if (imageModel.height > 5000) 5000 else imageModel.height
        imageURL = imageModel.id.createImageUrl(imageModel.width, height)
        binding.ivDetailsImage.loadDetailImage(imageURL, imageModel.width, height)

        binding.customProgress.visibility = View.GONE
        binding.tvMsgError.visibility = View.GONE
        binding.ivDetailsImage.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.details_menus, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_download -> {
                createBitmapImage()
                downloadImageFlag = true
                downloadImage()
                true
            }
            R.id.action_share -> {
                createBitmapImage()
                downloadImageFlag = false
                shareImage()
                true
            }
            else ->
                super.onOptionsItemSelected(item);
        }
    }

    private fun createBitmapImage() {
        Glide.with(mContext)
            .asBitmap()
            .load(imageURL)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    this@DetailsFragment.resource = resource
                }
            })
    }

    private fun downloadImage() {
        try {
            if (!getStoragePermissions()) {
                requestStoragePermissions();
            } else {
                coroutineScope.launch(Dispatchers.IO) {
                    initImageSaving()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Image downloaded successfully", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @SuppressLint("InlinedApi", "ObsoleteSdkInt")
    private fun initImageSaving() {
        val relativeLocation = Environment.DIRECTORY_PICTURES + File.pathSeparator + "Picsum"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis().toString())
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val resolver = mContext.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            uri?.let {
                val stream = resolver.openOutputStream(it)
                stream?.let {
                    if (!resource.compress(Bitmap.CompressFormat.JPEG, 90, it)) {
                        throw IOException("Failed to save bitmap.")
                    }
                } ?: throw IOException("Failed to get output stream.")
            } ?: throw IOException("Failed to create new MediaStore record")

        } catch (e: IOException) {
            if (uri != null) {
                resolver.delete(uri, null, null)
            }
            throw IOException(e)
        } finally {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
        }
    }

    private fun shareImage() {
        if (!getStoragePermissions()) {
            requestStoragePermissions()
        } else {
            val bmpUri: Uri = saveTempImage()

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
            shareIntent.type = "image/*"
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }
    }

    private fun saveTempImage(): Uri {
        val storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imagesFolder = File(storageDir, "images")
        lateinit var uri: Uri
        try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "temp_image.png")
            val stream = FileOutputStream(file)
            resource.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(
                mContext,
                mContext.packageName + ".provider",
                file
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return uri
    }

    private fun requestStoragePermissions() {
        requestPermissions(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            REQUEST_STORAGE
        )
    }

    private fun getStoragePermissions(): Boolean {
        return (ContextCompat.checkSelfPermission(
            mContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        println(requestCode)
        if (REQUEST_STORAGE == requestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (downloadImageFlag) {
                    downloadImage()
                } else {
                    shareImage()
                }
            } else {
                Toast.makeText(mContext, "Permission is denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    override fun onDestroy() {
        completableJob.cancel()
        super.onDestroy()
    }

}