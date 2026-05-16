package com.ramesh.shaalevikas

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class MainActivity : AppCompatActivity() {

    private lateinit var txtDonors: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NeedAdapter

    private val donors = mutableListOf<String>()
    private val needList = mutableListOf<Need>()

    private var selectedImageUri: Uri? = null

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                uploadImageToFirebase()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtDonors = findViewById(R.id.txtDonors)
        recyclerView = findViewById(R.id.recyclerNeeds)

        val btnAddNeed = findViewById<Button>(R.id.btnAddNeed)
        val btnUploadImage = findViewById<Button>(R.id.btnViewGallery)
        val btnViewGallery = findViewById<Button>(R.id.btnViewGallery)

        btnViewGallery.setOnClickListener {
            startActivity(Intent(this, GalleryActivity::class.java))
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = NeedAdapter(needList) { needTitle ->
            donors.add("Supporter helped: $needTitle")
            txtDonors.text = "Donor Hall of Fame\n" + donors.joinToString("\n")
        }

        recyclerView.adapter = adapter

        btnAddNeed.setOnClickListener {
            showAddNeedDialog()
        }

        btnUploadImage.setOnClickListener {
            imagePicker.launch("image/*")
        }

        loadNeeds()
    }

    private fun loadNeeds() {
        val ref = FirebaseDatabase.getInstance().getReference("needs")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                needList.clear()

                for (child in snapshot.children) {
                    val need = child.getValue(Need::class.java)
                    if (need != null) {
                        needList.add(need)
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun showAddNeedDialog() {
        val titleInput = EditText(this)
        titleInput.hint = "Need title"

        AlertDialog.Builder(this)
            .setTitle("Add New Need")
            .setView(titleInput)
            .setPositiveButton("Add") { _, _ ->
                val title = titleInput.text.toString().trim()

                if (title.isNotEmpty()) {
                    val ref = FirebaseDatabase.getInstance()
                        .getReference("needs")
                        .push()

                    val need = Need(
                        title = title,
                        description = "New school requirement",
                        cost = "5000",
                        progress = 0
                    )

                    ref.setValue(need)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun uploadImageToFirebase() {
        val uri = selectedImageUri ?: return

        val ref = FirebaseStorage.getInstance()
            .reference
            .child("school_images/${System.currentTimeMillis()}.jpg")

        ref.putFile(uri)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Image uploaded successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Upload failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}