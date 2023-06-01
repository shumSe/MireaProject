package ru.mirea.shumikhin.mireaproject.ui.cryptography

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import ru.mirea.shumikhin.mireaproject.databinding.FragmentCryptographyBinding
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class CryptographyFragment : Fragment() {

    private lateinit var binding: FragmentCryptographyBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCryptographyBinding.inflate(inflater, container, false)

        binding.btnEncrypt.setOnClickListener {
            setTextToFile(INPUT_FILE, binding.etInputText.text.toString())
            encrypt()
            var encryptedText = getTextFromFile(OUTPUT_FILE)
            var message = "Text: $encryptedText \n Saved in file: $OUTPUT_FILE"
            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
        }

        binding.btnDecrypt.setOnClickListener {
            var text = getTextFromFile(INPUT_FILE)
            binding.tvOutputText.text = text
            var decryptMsg = "Decrypted text: $text \n From file: $INPUT_FILE"
            Snackbar.make(binding.root, decryptMsg, Snackbar.LENGTH_LONG).show()
        }

        return binding.root
    }
    private fun encrypt() {
        val inputText = getTextFromFile(INPUT_FILE) ?: return
        val outputText = inputText.map{
            (it.code + 1).toChar()
        }.joinToString("")
        setTextToFile(OUTPUT_FILE, outputText)
    }
    private fun setTextToFile(fileName: String, text: String) {
        var outputStream: FileOutputStream
        try {
            outputStream = requireActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(text.toByteArray());
            outputStream.close();
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }
    private fun getTextFromFile(fileName: String): String? {
        var fin: FileInputStream? = null
        try {
            fin = requireActivity().openFileInput(fileName)
            val bytes = ByteArray(fin.available())
            fin.read(bytes)
            return String(bytes)
        } catch (ex: IOException) {
            Toast.makeText(requireContext(), ex.message, Toast.LENGTH_SHORT).show()
        } finally {
            try {
                if (fin != null) fin.close()
            } catch (ex: IOException) {
                Toast.makeText(requireContext(), ex.message, Toast.LENGTH_SHORT).show()
            }
        }
        return null
    }
    companion object {
        const val INPUT_FILE = "input.txt"
        const val OUTPUT_FILE = "output.txt"
    }

}