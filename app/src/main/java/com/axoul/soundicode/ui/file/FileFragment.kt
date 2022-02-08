package com.axoul.soundicode.ui.file

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.axoul.soundicode.audio.Decoder
import com.axoul.soundicode.databinding.FragmentFileBinding
import java.io.IOError

class FileFragment : Fragment() {
    private lateinit var binding: FragmentFileBinding
    private lateinit var browseBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFileBinding.inflate(inflater, container, false)
        val root = binding.root
        browseBtn = binding.buttonBrowse
        browseBtn.setOnClickListener { openFileChooser() }
        return root
    }

    private fun openFileChooser() {
        val openDocumentIntent = Intent(Intent.ACTION_GET_CONTENT)
        openDocumentIntent.addCategory(Intent.CATEGORY_OPENABLE)
        openDocumentIntent.type = "audio/*"
        activityResultLauncher.launch(openDocumentIntent)
    }

    private var activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val uri: Uri?
            if (data != null) {
                uri = data.data
                try {
                    URItoArray(uri)
                } catch (e: IOError) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun URItoArray(path: Uri?) {
        val parcelFileDescriptor = requireContext().contentResolver.openFileDescriptor(path!!, "r")
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val decoder = Decoder(fileDescriptor)
        decoder.startDecoder()
        val test = decoder.shortData

    }
}