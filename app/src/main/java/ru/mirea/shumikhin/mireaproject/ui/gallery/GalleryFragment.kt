package ru.mirea.shumikhin.mireaproject.ui.gallery

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ru.mirea.shumikhin.mireaproject.databinding.FragmentGalleryBinding
import java.io.File
import java.io.IOException

class GalleryFragment : Fragment() {

    private lateinit var binding: FragmentGalleryBinding
    private val REQUEST_CODE_PERMISSION = 200
    private var isWork = false
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var recordFilePath: String = ""
    private val TAG = GalleryFragment::class.java.simpleName


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGalleryBinding.inflate(inflater, container, false)

        checkPermissions()
        var isStartRecording = true
        var isStartPlaying = true
        binding.btnPlay.isEnabled = false
        recordFilePath = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_MUSIC),
            "/audiorecordtest.3gp"
        ).absolutePath

        binding.btnRecord.setOnClickListener {
            if (isStartRecording) {
                binding.btnRecord.text = "Stop recording";
                binding.btnPlay.isEnabled = false;
                startRecording()
            } else {
                binding.btnRecord.text = "Start recording";
                binding.btnPlay.isEnabled = true;
                stopRecording()
            }
            isStartRecording = !isStartRecording;
        }
        binding.btnPlay.setOnClickListener {
            if (isStartPlaying) {
                binding.btnPlay.text = "Stop playing";
                binding.btnRecord.isEnabled = false;
                startPlaying()
            } else {
                binding.btnPlay.text = "Start playing";
                binding.btnRecord.isEnabled = false;
                stopPlaying()
            }
            isStartPlaying = !isStartPlaying;
        }

        return binding.root
    }

    private fun startRecording() {
        recorder = MediaRecorder()
        recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder?.setOutputFile(recordFilePath)
        recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        try {
            recorder!!.prepare()
        } catch (e: IOException) {
            Log.e(TAG, "prepare() failed")
        }
        recorder?.start()
    }

    private fun stopRecording() {
        recorder?.stop()
        recorder?.release()
        recorder = null
    }

    private fun startPlaying() {
        player = MediaPlayer()
        try {
            player!!.setDataSource(recordFilePath)
            player!!.prepare()
            player!!.start()
        } catch (e: IOException) {
            Log.e(TAG, "prepare() failed")
        }
    }

    private fun stopPlaying() {
        player!!.release()
        player = null
    }

    private fun checkPermissions() {
        val audioRecordPermissionStatus = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.RECORD_AUDIO
        )
        val storagePermissionStatus =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (audioRecordPermissionStatus == PackageManager.PERMISSION_GRANTED && storagePermissionStatus
            == PackageManager.PERMISSION_GRANTED
        ) {
            isWork = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), REQUEST_CODE_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_PERMISSION -> isWork = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
        if (!isWork) requireActivity().finish()
    }
}