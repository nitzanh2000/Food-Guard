package com.example.foodguard.ui.fragments.PostList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodguard.R
import com.example.foodguard.data.PostViewModel

class PostListFragment : Fragment() {
    private lateinit var postsList: RecyclerView
    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postsList = view.findViewById(R.id.posts_list)
        context.let { initPostList() }
        viewModel.getAllPosts().observe(viewLifecycleOwner) {
            if (it.isEmpty()) viewModel.invalidatePosts()
            (postsList.adapter as PostAdapter).updatePostsList(it)
        }

    }

    private fun initPostList() {
        postsList.run {
            layoutManager = LinearLayoutManager(context)
            adapter = PostAdapter{ id ->
                val action = PostListFragmentDirections.actionPostsListFragmentToPostDetailsFragment(id)
                findNavController().navigate(action)
            }
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
    }
}