package com.example.foodguard.ui.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.foodguard.R
import com.example.foodguard.data.PostViewModel
import com.example.foodguard.data.post.PostModel
import com.example.foodguard.data.user.UserModel
import com.example.foodguard.utils.decodeBase64ToImage
import com.example.foodguard.utils.encodeImageToBase64
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UploadPostFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload_post, container, false)
    }

    private var connectedUserId : String = FirebaseAuth.getInstance().currentUser!!.uid
    private var mainUser: UserModel? = null
    private val viewModel: PostViewModel by activityViewModels()

    private lateinit var imageViewUpload: ImageView
    private var base64Image: String = "";

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPageData(view)
        setUploadListener()
    }

    private fun setUploadListener() {
        val descriptionInput = view?.findViewById<TextView>(R.id.description_input) 
        val addressInput = view?.findViewById<TextView>(R.id.address_input)
        val dateInput = view?.findViewById<TextView>(R.id.date_time_input)
        val servingsInput = view?.findViewById<TextView>(R.id.servings_input)
        
        val uploadButton = view?.findViewById<Button>(R.id.upload_button)
        uploadButton?.setOnClickListener {
            val description = descriptionInput?.text.toString()
            val address = addressInput?.text.toString()
            val date = dateInput?.text.toString()
            val servings = servingsInput?.text.toString()

            if (base64Image == "" || description == "" || address == "" || date == "" || servings == "") {
                Toast.makeText(requireContext(), "Oops, maybe you missed some filed..", Toast.LENGTH_LONG)
                    .show()
            } else {
                val post = PostModel(
                    description = description,
                    address = address,
                    expiration_date = date,
                    serving = servings.toInt(),
                    author_id = connectedUserId,
                    image = base64Image
                )
                viewModel.addPost(post)

                Toast.makeText(requireContext(), "Post uploaded successfully", Toast.LENGTH_SHORT)
                    .show()

                val navController = findNavController()
                navController.navigate(R.id.action_uploadPostFragment_to_postsListFragment)
            }
        }
    }

    private fun initPageData(view: View) {
        val usernameText = view.findViewById<TextView>(R.id.username)
        val imageView = view.findViewById<ImageView>(R.id.profile_image)
        imageViewUpload = view.findViewById<ImageView>(R.id.upload_photo)

        viewModel.getUserById(connectedUserId).observe(viewLifecycleOwner) { user ->
            user?.let {
                mainUser = it
                usernameText.text = it.display_name

                if (it.profile_picture != "") {
                    imageView.setImageBitmap(decodeBase64ToImage(it.profile_picture ?: ""))
                } else {
                    val googleImage = FirebaseAuth.getInstance().currentUser?.photoUrl

                    if (googleImage !== null) {
                        Glide.with(this)
                            .load(googleImage)
                            .circleCrop()
                            .into(imageView)
                        imageView.setImageURI(googleImage)
                    }
                }
            } ?: run {
                // Handle user not found
                usernameText.text = "Guest"
            }
        }

        val selectImageButton = view.findViewById<Button>(R.id.browse_files_button)
        selectImageButton.setOnClickListener {
            uploadImage()
        }

        val dateTimeInput: EditText = view.findViewById(R.id.date_time_input)
        dateTimeInput.setOnClickListener {
            showMaterialDateTimePicker(dateTimeInput)
        }
    }


    private fun showMaterialDateTimePicker(editText: EditText) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds()) // Default selection: Today
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val calendar = Calendar.getInstance().apply {
                timeInMillis = selection
            }

            // Open Time Picker after selecting date
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H) // Use TimeFormat.CLOCK_24H for 24-hour format
                .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                .setMinute(calendar.get(Calendar.MINUTE))
                .setTheme(R.style.CustomTimePicker) // Apply custom theme
                .setTitleText("Select Time")
                .build()

            timePicker.addOnPositiveButtonClickListener {
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
                calendar.set(Calendar.MINUTE, timePicker.minute)

                // Format and set the selected date & time
                val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                editText.setText(dateTimeFormat.format(calendar.time))
            }

            timePicker.show(childFragmentManager, "TIME_PICKER")
        }

        datePicker.show(childFragmentManager, "DATE_PICKER")
    }

    val REQUEST_IMAGE_PICK = 1234

    private fun uploadImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data
            uri?.let {
                imageViewUpload.setImageURI(uri)

                base64Image = encodeImageToBase64(uri, requireContext())
            }
        }
    }
}
