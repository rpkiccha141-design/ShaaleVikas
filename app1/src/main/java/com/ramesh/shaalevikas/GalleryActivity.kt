package com.ramesh.shaalevikas

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GalleryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scrollView = ScrollView(this)

        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL

        scrollView.addView(container)
        setContentView(scrollView)

        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child("school_images")

        storageRef.listAll()
            .addOnSuccessListener { result ->

                for (item in result.items) {

                    item.metadata.addOnSuccessListener { metadata ->

                        val imageTitle = TextView(this)
                        imageTitle.text = item.name
                        imageTitle.textSize = 16f
                        imageTitle.setPadding(16, 24, 16, 4)

                        container.addView(imageTitle)

                        val dateText = TextView(this)

                        val millis = metadata.creationTimeMillis
                        val formattedDate = SimpleDateFormat(
                            "dd MMM yyyy, hh:mm a",
                            Locale.getDefault()
                        ).format(Date(millis))

                        dateText.text = formattedDate
                        dateText.setPadding(16, 0, 16, 8)

                        container.addView(dateText)

                        item.downloadUrl.addOnSuccessListener { uri ->

                            val imageView = ImageView(this)

                            imageView.layoutParams =
                                LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    600
                                )

                            imageView.setPadding(16, 8, 16, 24)

                            container.addView(imageView)

                            Glide.with(this)
                                .load(uri)
                                .into(imageView)
                        }
                    }
                }
            }
    }
}