package com.example.foodguard.ui.fragments.PostList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodguard.R
import com.example.foodguard.data.post.PostWithAuthor
import com.example.foodguard.utils.decodeBase64ToImage

class PostAdapter(val onPostClick: (String) -> Unit) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(postView: View) : RecyclerView.ViewHolder(postView) {
        val description: TextView = postView.findViewById(R.id.post_description)
        val address: TextView = postView.findViewById(R.id.location_text)
        val expirationDate: TextView = postView.findViewById(R.id.date_time_text)
        val serving: TextView = postView.findViewById(R.id.servings_text)
        val image: ImageView = postView.findViewById(R.id.post_image)

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
            val bitmap = decodeBase64ToImage(it)
            holder.authorImage.setImageBitmap(bitmap)
        }

        currentPost.post.image?.let {
            val bitmap = decodeBase64ToImage(it)
            holder.image.setImageBitmap(bitmap)
        }

//        holder.itemView.setOnClickListener {
//            onReviewClicked(currentPost.review.id)
//        }
    }

    fun updatePostsList(newPostsList: List<PostWithAuthor>) {
        this.posts = newPostsList
        notifyDataSetChanged()
    }
}