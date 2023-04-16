package com.example.storyappsubmission.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.storyappsubmission.data.remote.result.Result
import com.example.storyappsubmission.ViewModel.StoryViewModel
import com.example.storyappsubmission.ViewModel.StoryViewModelFactory
import com.example.storyappsubmission.data.remote.response.Story
import com.example.storyappsubmission.databinding.FragmentDetailStoryBinding


class DetailStoryFragment : Fragment() {
    private var _binding: FragmentDetailStoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var resultdata: Story

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailStoryBinding.inflate(inflater, container, false)
        val factory: StoryViewModelFactory = StoryViewModelFactory.getInstance(requireContext())
        val viewModel: StoryViewModel by viewModels {
            factory
        }
        val id = DetailStoryFragmentArgs.fromBundle(arguments as Bundle).defaultID
        viewModel.getDetailedStory(id).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {

                    }
                    is Result.Error -> {
                        Toast.makeText(context, result.error, Toast.LENGTH_SHORT).show()
                    }
                    is Result.Success -> {
                        val arrayResult = result.data.story
                        resultdata = result.data.story
                        binding.createdAt.text = arrayResult.createdAt
                        binding.deskripsi.text = arrayResult.description
                        binding.namaUser.text = arrayResult.name
                        Glide.with(requireContext())
                            .load(arrayResult.photoUrl)
                            .into(binding.storyImage)
                    }
                }
            }
        }
        return binding.root
    }


}