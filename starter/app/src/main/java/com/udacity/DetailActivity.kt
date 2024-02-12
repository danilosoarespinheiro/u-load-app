package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val intent = intent
        val fileName = intent.getStringExtra("FILE")
        val status = intent.getStringExtra("STATUS")

        binding.contentDetail.fileNameTextView.text = "File: ${fileName}"
        binding.contentDetail.statusTextView.text = "Status: ${status}"

        binding.contentDetail.detailLayout.transitionToEnd()

        binding.contentDetail.backButton.setOnClickListener { finish() }
    }
}
