package com.example.foodguard.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.foodguard.R
import com.example.foodguard.data.PostViewModel
import com.example.foodguard.data.post.PostModel
import com.example.foodguard.data.post.PostWithAuthor
import com.example.foodguard.data.user.UserModel
import com.example.foodguard.utils.decodeBase64ToImage
import com.example.foodguard.utils.encodeImageToBase64
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import getCities
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditPostFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_post, container, false)
    }

    private var connectedUserId : String = FirebaseAuth.getInstance().currentUser!!.uid
    private var mainUser: UserModel? = null
    private val viewModel: PostViewModel by activityViewModels()

    private lateinit var imageViewUpload: ImageView
    private lateinit var base64Image: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var currentPostId = arguments?.getString("post_id");
        var currentPost : PostWithAuthor ?= null;

        val addressAutoComplete = view.findViewById<AutoCompleteTextView>(R.id.address_input)

        lifecycleScope.launch {
            val cities = getCities()
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, cities)
            addressAutoComplete.setAdapter(adapter)
        }

        viewModel.getAllPosts().observe(viewLifecycleOwner, {
            if (it.isEmpty()) viewModel.refreshPostsFromRemote()

            val postList = it
            currentPostId?.let {
                val postId = it
                currentPost = postList.find { post -> post.post.id == postId }
            }

            val description = view.findViewById<TextView>(R.id.description_input)
            val address = view.findViewById<AutoCompleteTextView>(R.id.address_input)
            val date = view.findViewById<TextView>(R.id.date_time_input) // TODO: change to date picker
            val servings = view.findViewById<TextView>(R.id.servings_input)
            val isDelivered = view.findViewById<CheckBox>(R.id.mark_as_delivered)

            description.text = currentPost?.post?.description
            address.setText(currentPost?.post?.address)
            date.text = currentPost?.post?.expiration_date
            servings.text = currentPost?.post?.serving.toString()
            isDelivered.isChecked = currentPost?.post?.is_delivered ?: false

            val profileImage = view.findViewById<ImageView>(R.id.profile_image)
            val username = view.findViewById<TextView>(R.id.username)

            username.text = currentPost?.author?.display_name
            currentPost?.author?.profile_picture?.let {
                if (it != "") {
                    val bitmap = decodeBase64ToImage(it)
                    profileImage.setImageBitmap(bitmap)
                }
            }

            imageViewUpload = view.findViewById<ImageView>(R.id.uploaded_image)

            currentPost?.post?.image?.let {
                base64Image = it
                val bitmap = decodeBase64ToImage(it)
                imageViewUpload.setImageBitmap(bitmap)
            }

            val changeImageButton = view.findViewById<Button>(R.id.change_image_button)

            changeImageButton.setOnClickListener {
                uploadImage()
            }

            val updateButton = view.findViewById<Button>(R.id.update_button)
            updateButton.setOnClickListener {

                val newPost = PostModel(
                    id = currentPost?.post?.id ?: "",
                    description = description.text.toString(),
                    address = address.text.toString(),
                    expiration_date = date.text.toString(),
                    serving = servings.text.toString().toInt(),
                    author_id = connectedUserId,
                    image = base64Image,
                    is_delivered = isDelivered.isChecked
                )
                viewModel.savePost(newPost);

                Toast.makeText(requireContext(), "Post updated successfully", Toast.LENGTH_SHORT).show()

                val action = EditPostFragmentDirections.actionEditPostFragmentToPostsListFragment(currentPost?.post?.id ?: "")
                Navigation.findNavController(it).navigate(action)
            }

            date.setOnClickListener {
                showMaterialDateTimePicker(date)
            }
        })
    }

    private fun showMaterialDateTimePicker(editText: TextView) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds()) // Default selection: Today
            .setTheme(R.style.CustomDatePicker)
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