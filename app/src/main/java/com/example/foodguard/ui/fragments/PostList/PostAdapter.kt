package com.example.foodguard.ui.fragments.PostList

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodguard.R
import com.example.foodguard.data.post.PostWithAuthor
import com.example.foodguard.utils.decodeBase64ToImage
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostAdapter(val onPostEditClick: (String) -> Unit, val onPostDeleteClick: (String) -> Unit) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    private var connectedUserId : String = FirebaseAuth.getInstance().currentUser!!.uid

    class PostViewHolder(postView: View) : RecyclerView.ViewHolder(postView) {
        val description: TextView = postView.findViewById(R.id.post_description)
        val address: TextView = postView.findViewById(R.id.location_text)
        val expirationDate: TextView = postView.findViewById(R.id.date_time_text)
        val serving: TextView = postView.findViewById(R.id.servings_text)
        val image: ImageView = postView.findViewById(R.id.post_image)
        val unAvailableMark: TextView = postView.findViewById(R.id.unavailable_sign)

        val authorName: TextView = postView.findViewById(R.id.username)
        val authorImage: ImageView = postView.findViewById(R.id.profile_image)
    }

    private var posts: List <PostWithAuthor> = emptyList();

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_post_details, parent, false)
        return PostViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentPost = posts[position]
        holder.description.text = currentPost.post.description
        holder.address.text = currentPost.post.address
        holder.expirationDate.text = currentPost.post.expiration_date
        holder.serving.text = currentPost.post.serving.toString()
        holder.authorName.text = currentPost.author.display_name

        currentPost.author.profile_picture?.let {
            if (it != "") {
                val bitmap = decodeBase64ToImage(it)
                holder.authorImage.setImageBitmap(bitmap)
            }
        }

        currentPost.post.image?.let {
            if (it != "") {
                val bitmap = decodeBase64ToImage(it)
                holder.image.setImageBitmap(bitmap)
            }
        }

        val editButton: ImageButton = holder.itemView.findViewById(R.id.edit_button)
        val deleteButton: ImageButton = holder.itemView.findViewById(R.id.delete_button)

        if(connectedUserId == currentPost?.author?.id) {
            editButton.visibility = View.VISIBLE
            deleteButton.visibility = View.VISIBLE
        } else {
            editButton.visibility = View.GONE
            deleteButton.visibility = View.GONE
        }

        if(isUnavailablePost(currentPost)) {
            holder.unAvailableMark.visibility = View.VISIBLE
        } else {
            holder.unAvailableMark.visibility = View.GONE
        }

        editButton.setOnClickListener {
            onPostEditClick(currentPost.post.id)
        }

        deleteButton.setOnClickListener {
            onPostDeleteClick(currentPost.post.id)
        }

    }

    fun updatePostsList(newPostsList: List<PostWithAuthor>) {
        this.posts = newPostsList
        notifyDataSetChanged()
    }

    private fun isUnavailablePost(post : PostWithAuthor): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:m", Locale.getDefault())
        val formattedExpirationDate = dateFormat.parse(post.post.expiration_date)

        var expired = false;

        try {
            expired = formattedExpirationDate.before(Date())
        }
        catch (e : Exception ){
            Log.w("Date", "Invalid date")
        }

        return expired || post.post.is_delivered
    }
}