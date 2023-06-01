package ru.mirea.shumikhin.mireaproject.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import ru.mirea.shumikhin.mireaproject.databinding.FragmentProfileBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileFragment : Fragment() {

    private val REQUEST_CODE_PERMISSION = 100
    private var isWork = false
    private var imageUri: Uri? = null
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPrefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        initFileProvider()
        checkPermissions()
        loadDataFromPrefs()

        binding.btnSave.setOnClickListener{
            saveDataInPrefs()
        }

        val callback: ActivityResultCallback<ActivityResult?> =
            object : ActivityResultCallback<ActivityResult?> {
                override fun onActivityResult(result: ActivityResult?) {
                    if (result == null) return
                    if (result.resultCode === Activity.RESULT_OK) {
                        val data: Intent? = result.data
                        binding.imgProfileAvatar.setImageURI(imageUri)
                        binding.tvAvatar.visibility = View.GONE
                    }
                }
            }
        val cameraActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult(),
            callback
        )
        binding.imgProfileAvatar.setOnClickListener {
            checkPermissions()
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (isWork) {
                try {
                    val photoFile = createImageFile()
                    // генерирование пути к файлу на основе authorities
                    val authorities = requireContext().packageName + ".fileprovider"
                    imageUri = FileProvider.getUriForFile(
                        requireContext(), authorities,
                        photoFile!!
                    )
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    cameraActivityResultLauncher.launch(cameraIntent)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        binding.btnDelete.setOnClickListener{
            deleteData()
        }

        return binding.root
    }

    private fun initFileProvider() {
        val photoFile = createImageFile()
        val authorities = requireContext().packageName + ".fileprovider"
        imageUri = FileProvider.getUriForFile(requireContext(), authorities, photoFile!!)
    }
    private fun loadDataFromPrefs() {
        val name = sharedPrefs.getString(NAME_KEY, null) ?: return
        val group = sharedPrefs.getString(GROUP_KEY, null) ?: return
        val profilePicStr = sharedPrefs.getString(PROFILE_PICTURE_KEY, null) ?: return
        val profilePicUri = profilePicStr.toUri()
        binding.etUserName.setText(name)
        binding.etUserGroup.setText(group)
        binding.imgProfileAvatar.setImageURI(profilePicUri)
        binding.tvAvatar.visibility = View.GONE
    }
    private fun saveDataInPrefs() {
        with(sharedPrefs.edit()) {
            putString(NAME_KEY, binding.etUserName.text.toString())
            putString(GROUP_KEY, binding.etUserGroup.text.toString())
            putString(PROFILE_PICTURE_KEY, imageUri.toString())
            apply()
        }
        Toast.makeText(requireContext(), "Данные сохранены", Toast.LENGTH_SHORT).show()
    }

    private fun deleteData(){
        binding.tvAvatar.visibility = View.VISIBLE
        binding.etUserGroup.text = null
        binding.etUserName.text = null
        binding.imgProfileAvatar.setImageURI(null)
        with(sharedPrefs.edit()){
            clear()
            apply()
        }
        Toast.makeText(requireContext(), "Данные удалены", Toast.LENGTH_SHORT).show()
    }
    private fun checkPermissions() {
        val cameraPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        val storagePermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val hasPermissions = cameraPermission == PackageManager.PERMISSION_GRANTED && storagePermission == PackageManager.PERMISSION_GRANTED

        if (!hasPermissions) {
            isWork = true
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE_PERMISSION
            )
        }
    }
    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val imageFileName = "IMAGE_" + timeStamp + "_"
        val storageDirectory: File? =
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDirectory)
    }
    companion object {
        const val PREFS_NAME = "ProfileFragmentPrefs"
        const val NAME_KEY = "ProfileFragmentName"
        const val PROFILE_PICTURE_KEY = "ProfileFragmentPicture"
        const val GROUP_KEY = "ProfileFragmentGroup"
    }

}