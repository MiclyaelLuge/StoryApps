package com.example.storyappsubmission.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyappsubmission.R
import com.example.storyappsubmission.ViewModel.StoryViewModel
import com.example.storyappsubmission.ViewModel.StoryViewModelFactory
import com.example.storyappsubmission.ViewModel.TokenViewModel
import com.example.storyappsubmission.ViewModel.TokenViewModelFactory
import com.example.storyappsubmission.data.remote.response.ListStoryItem
import com.example.storyappsubmission.data.remote.result.Result
import com.example.storyappsubmission.databinding.FragmentStoryListBinding
import com.example.storyappsubmission.utils.LoginPreferences

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class StoryListFragment : Fragment() {
    private var _binding: FragmentStoryListBinding? = null
    private val binding get() = _binding!!
    private lateinit var storyadapter: StoryAdapter

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.show()
        _binding = FragmentStoryListBinding.inflate(inflater, container, false)
        binding.progressBar.visibility = View.GONE

        val rvUser = binding.storyList
        rvUser.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.storyList.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.storyList.addItemDecoration(itemDecoration)
        val factory: StoryViewModelFactory = StoryViewModelFactory.getInstance(requireContext())
        val storyViewModel: StoryViewModel by viewModels {
            factory
        }
        storyViewModel.getAllStory().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(context, result.error, Toast.LENGTH_SHORT).show()

                    }
                    is Result.Success -> {
                        setStoryData(result.data)
                        binding.progressBar.visibility = View.GONE

                    }

                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }


        })

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.addStory -> {
                        view?.findNavController()
                            ?.navigate(R.id.action_storyListFragment_to_addStoryFragment)
                        true
                    }
                    R.id.logout -> {
                        logout()
                        true
                    }
                    else -> return true
                }
            }

        }, viewLifecycleOwner, androidx.lifecycle.Lifecycle.State.RESUMED)
        return binding.root

    }

    private fun setStoryData(data: List<ListStoryItem>) {
        if (data.isEmpty()) {
            Toast.makeText(context, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show()
        }
        storyadapter = StoryAdapter(data)
        binding.storyList.adapter = storyadapter
        storyadapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem) {
                showSelectedStory(data.id)
            }
        })

    }

    private fun showSelectedStory(id: String) {
        val toDetailStoryFragment =
            StoryListFragmentDirections.actionStoryListFragmentToDetailStoryFragment()
        toDetailStoryFragment.defaultID = id
        findNavController().navigate(toDetailStoryFragment)

    }

    private fun logout() {
        val pref = LoginPreferences.getInstance(requireContext().dataStore)
        val tokenViewModel =
            ViewModelProvider(this, TokenViewModelFactory(pref))[TokenViewModel::class.java]
        tokenViewModel.deleteToken()
        activity?.finishAffinity()
    }

}