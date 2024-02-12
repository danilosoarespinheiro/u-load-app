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

        binding.contentDetail.apply {
            fileNameTextView.text = getString(R.string.file_resource, intent.getStringExtra(FILE))
            statusTextView.text = getString(R.string.status_resource, intent.getStringExtra(STATUS))
            detailLayout.transitionToEnd()
            backButton.setOnClickListener { finish() }
        }
    }
}
