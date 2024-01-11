package com.example.eventfulmb.activities

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.eventfulmb.MyApplication
import com.example.eventfulmb.databinding.ActivityDisplayImageBinding


class DisplayImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDisplayImageBinding
    private lateinit var app: MyApplication
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MyApplication

        val imageView: ImageView = binding.imageView

        imageUri = intent.getParcelableExtra("imageUri", Uri::class.java)!!

        Glide.with(this)
            .load(imageUri)
            .into(imageView)

        binding.sendButton.setOnClickListener {
            sendImageToServer()
        }
    }

    private fun sendImageToServer() {
        val imageByteArray = readImageToByteArray(imageUri)
        val topicToSendImage = "send/image"
        app.sendImage(topicToSendImage, imageByteArray)
    }

    @SuppressLint("Recycle")
    private fun readImageToByteArray(imageUri: Uri): ByteArray {
        val inputStream = contentResolver.openInputStream(imageUri)
        return inputStream?.readBytes() ?: byteArrayOf()
    }


}