package com.example.foodguard.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodguard.R
import com.example.foodguard.data.PostViewModel
import com.example.foodguard.data.user.UserModel
import com.example.foodguard.ui.auth.AuthActivity
import com.example.foodguard.ui.fragments.PostList.PostAdapter
import com.example.foodguard.utils.decodeBase64ToImage
import com.example.foodguard.utils.encodeImageToBase64
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class EditProfileFragment  : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPageData(view)
        initListeners(view);
        setupPostList(view)
    }

    private lateinit var postsList: RecyclerView

    private fun setupPostList(view: View) {
        postsList = view.findViewById(R.id.profile_page_posts_list)
        context?.let { initPostList(it) }
        viewModel.getAllPostsByUserId(connectedUserId).observe(viewLifecycleOwner, {
            it?.let {
                if(it.isEmpty()) {
                    lifecycleScope.launch {
                        viewModel.refreshPostsFromRemote()
                    }
                }
                (postsList.adapter as? PostAdapter)?.updatePostsList(it)
            }

        })
    }

    private fun initPostList(it: Context) {
        postsList.run {
            layoutManager = LinearLayoutManager(context)
            adapter = PostAdapter(
                { id ->
                    val onPostEditClick = EditProfileFragmentDirections.actionProfilePageFragmentToEditPostFragment(id)
                    findNavController().navigate(onPostEditClick)
                }, { id ->
                    viewModel.deletePostById(id)
                })
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL
                )
            )
        }
    }


    private var connectedUserId : String = FirebaseAuth.getInstance().currentUser!!.uid
    private var mainUser: UserModel? = null

    private val viewModel: PostViewModel by activityViewModels()

    private lateinit var imageView: ImageView

    private fun initPageData(view: View) {
        val usernameText = view.findViewById<TextView>(R.id.display_name_input)
        val emailText = view.findViewById<TextView>(R.id.email_input)
        imageView = view.findViewById<ImageView>(R.id.profile_image)

        viewModel.getUserById(connectedUserId).observe(viewLifecycleOwner) { user ->
            user?.let {
                mainUser = it
                usernameText.text = it.display_name
                emailText.text = it.email

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
    }

    private fun initListeners(view: View) {

        val logout = view.findViewById<ImageButton>(R.id.logout_button)
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), AuthActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        val changeProfile = view.findViewById<Button>(R.id.change_profile_photo_button)

        changeProfile.setOnClickListener {
            uploadImage()
        }

        val updateProfile = view.findViewById<Button>(R.id.update_profile_button)
        updateProfile.setOnClickListener {
            updateProfileData(view)
        }

        val cancelProfile = view.findViewById<Button>(R.id.cancel_profile_button)
        cancelProfile.setOnClickListener {
            initPageData(view)
            Toast.makeText(requireContext(), "Profile restored", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateProfileData(view: View) {
        val usernameText = view.findViewById<TextView>(R.id.display_name_input)

        mainUser?.let {
            val newUser = UserModel(id = mainUser!!.id, profile_picture = mainUser!!.profile_picture, display_name = usernameText.text.toString(), email = mainUser!!.email)
            viewModel.updateUser(newUser)

            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
        }
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
                imageView.setImageURI(uri)

                var base64Image = encodeImageToBase64(uri, requireContext())
                mainUser?.let {
                    val newUser = UserModel(id = mainUser!!.id, profile_picture = base64Image, display_name = mainUser!!.display_name, email = mainUser!!.email)
                    viewModel.updateUser(newUser)
                }

                Toast.makeText(requireContext(), "Profile picture updated successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }



}