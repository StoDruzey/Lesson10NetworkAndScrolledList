package com.example.lesson10networkandscrolledlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.lesson10networkandscrolledlist.databinding.FragmentFirstBinding
import com.google.android.material.divider.MaterialDividerItemDecoration
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class FragmentFirst : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val adapter by lazy { UserAdapter(requireContext()) }

    private var currentRequest: Call<List<User>>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentFirstBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            recyclerView.adapter = adapter
            recyclerView.addItemDecoration(
                MaterialDividerItemDecoration(requireContext(), MaterialDividerItemDecoration.VERTICAL))
        }
        //                toolbar.menu
//                toolbar.inflateMenu(R.menu.menu_toolbar) //for processing menu toolbar
//                toolbar.setNavigationOnClickListener {
//                    findNavController().navigateUp() //for processing button "back"
//                }
        //for processing click on toolbar menu
//            toolbar.setOnMenuItemClickListener {
//                if (it.itemId == R.id.action_search) {
//                    Toast.makeText(requireContext(), "Hello", Toast.LENGTH_SHORT).show()
//                    true
//                } else {
//                    false
//                }
//            }
        //for processing of search menu toolbar
//            toolbar
//                .menu
//                .findItem(R.id.action_search)
//                .actionView
//                .let { it as SearchView }
//                .setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//                    override fun onQueryTextSubmit(query: String?): Boolean {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun onQueryTextChange(newText: String?): Boolean {
//                        TODO("Not yet implemented")
//                    }
//                })

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val githubApi = retrofit.create<GithubApi>()

        currentRequest = githubApi
            .getUsers(10, 50)
            .apply {
                enqueue(object : Callback<List<User>> {
                    override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                        if (response.isSuccessful) {
                            val users = response.body() ?: return
                            adapter.submitList(users)
                        } else {
                            handleException(HttpException(response))
                        }
                    }

                    override fun onFailure(call: Call<List<User>>, t: Throwable) {
                        if (!call.isCanceled) {
                            handleException(t)
                        }
                    }
                })
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentRequest?.cancel()
        _binding = null
    }

    private fun handleException(e: Throwable) {
        Toast.makeText(requireContext(), e.message ?: "Something went wrong", Toast.LENGTH_SHORT).show()
    }
}