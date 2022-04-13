package com.artworkspace.storyapp.ui.create

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.lifecycleScope
import com.artworkspace.storyapp.R
import com.artworkspace.storyapp.databinding.ActivityCreateStoryBinding
import com.artworkspace.storyapp.utils.MediaUtility
import com.artworkspace.storyapp.utils.MediaUtility.reduceFileImage
import com.artworkspace.storyapp.utils.MediaUtility.uriToFile
import com.artworkspace.storyapp.utils.animateVisibility
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


@AndroidEntryPoint
class CreateStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateStoryBinding
    private lateinit var currentPhotoPath: String

    private var getFile: File? = null
    private var token: String = ""

    private val viewModel: CreateViewModel by viewModels()

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val file = File(currentPhotoPath).also { getFile = it }
            val os: OutputStream

            // Rotate image to correct orientation
            val bitmap = BitmapFactory.decodeFile(getFile?.path)
            val exif = ExifInterface(currentPhotoPath)
            val orientation: Int = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            val rotatedBitmap: Bitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
                ExifInterface.ORIENTATION_NORMAL -> bitmap
                else -> bitmap
            }

            // Convert rotated image to file
            try {
                os = FileOutputStream(file)
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                os.flush()
                os.close()

                getFile = file
            } catch (e: Exception) {
                e.printStackTrace()
            }

            binding.imageView.setImageBitmap(rotatedBitmap)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            uriToFile(selectedImg, this).also { getFile = it }

            binding.imageView.setImageURI(selectedImg)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        lifecycleScope.launchWhenCreated {
            launch {
                viewModel.getAuthToken().collect { authToken ->
                    if (!authToken.isNullOrEmpty()) token = authToken
                }
            }
        }

        binding.btnCamera.setOnClickListener { startIntentCamera() }
        binding.btnGallery.setOnClickListener { startIntentGallery() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.create_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_check -> {
                uploadStory()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * Handle uploading story task
     */
    private fun uploadStory() {
        setLoadingState(true)

        val etDescription = binding.etDescription
        var isValid = true

        // Validation for the description edit text
        if (etDescription.text.toString().isBlank()) {
            etDescription.error = getString(R.string.desc_empty_field_error)
            isValid = false
        }

        // Validation for the image
        if (getFile == null) {
            showSnackbar(getString(R.string.empty_image_error))
            isValid = false
        }

        // Required content is valid and ready to upload
        if (isValid) {
            val file = reduceFileImage(getFile as File)
            val description =
                etDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            lifecycleScope.launchWhenStarted {
                launch {
                    viewModel.uploadImage(token, imageMultipart, description).collect { response ->
                        response.onSuccess {
                            Toast.makeText(
                                this@CreateStoryActivity,
                                getString(R.string.story_upload),
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }

                        response.onFailure {
                            setLoadingState(false)
                            showSnackbar(getString(R.string.image_upload_failed))
                        }
                    }
                }
            }
        } else setLoadingState(false)
    }

    /**
     * Start implicit intent gallery
     */
    private fun startIntentGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    /**
     * Start implicit intent camera
     */
    private fun startIntentCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        MediaUtility.createTempFile(application).also {
            val photoUri = FileProvider.getUriForFile(
                this,
                "com.artworkspace.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherIntentCamera.launch(intent)
        }
    }

    /**
     * Show snackbar with custom message
     *
     * @param message Message to display
     */
    private fun showSnackbar(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    /**
     * Set loading state to the related views
     *
     * @param isLoading Loading state
     */
    private fun setLoadingState(isLoading: Boolean) {
        binding.apply {
            btnCamera.isEnabled = !isLoading
            btnGallery.isEnabled = !isLoading
            etDescription.isEnabled = !isLoading

            viewLoading.animateVisibility(isLoading)
        }
    }
}