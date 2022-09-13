package com.example.lesson10networkandscrolledlist

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.Coil.enqueue
import com.example.lesson10networkandscrolledlist.databinding.FragmentFirstBinding
import com.google.android.material.divider.MaterialDividerItemDecoration
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class FragmentFirst : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val adapter by lazy {
        UserAdapter(
            context = requireContext(),
            onUserClicked = {
                findNavController().navigate(FragmentFirstDirections.actionFragmentFirstToFragmentSecond(it.login))
            }
        )
    }

    var currentUsers = mutableListOf<User>()

    private var currentRequest: Call<List<User>>? = null
    private var currentPage = 0

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

            swipeRefresh.setOnRefreshListener {
                executeRequest {
                    swipeRefresh.isRefreshing = false
                }
            }

            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                }
            })

            recyclerView.adapter = adapter
            recyclerView.addItemDecoration(
                object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        outRect.bottom = INTERVAL_BETWEEN_ITEMS
                    }
                }
//                MaterialDividerItemDecoration(requireContext(), MaterialDividerItemDecoration.VERTICAL)
            )

            //for processing of search menu toolbar
//            toolbar
//                .menu
//                .findItem(R.id.action_search)
//                .actionView
//                .let { it as SearchView }
//                .setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//                    override fun onQueryTextSubmit(query: String): Boolean {
//                        return false
//                    }
//
//                    override fun onQueryTextChange(query: String): Boolean {
//                        adapter.submitList(currentUsers.filter { it.login.contains(query) })
//                        return true
//                    }
//                })
        }

        executeRequest()
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentRequest?.cancel()
        _binding = null
    }

    private fun executeRequest(
        onRequestFinished: () -> Unit = {}
    ) {

       val finishRequest = {
           onRequestFinished()
           currentRequest = null
       }

        currentRequest?.cancel()
        currentRequest = GithubService.api
            .getUsers(10, 50)
            .apply {
                enqueue(object : Callback<List<User>> {
                    override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                        if (response.isSuccessful) {
                            val users = response.body() ?: return
                            currentUsers.addAll(users)
                            val items = users.map { PagingData.Item(it) } + PagingData.Loading
                            adapter.submitList(items)
                        } else {
                            handleException(HttpException(response))
                        }
                        finishRequest()
                    }

                    override fun onFailure(call: Call<List<User>>, t: Throwable) {
                        if (!call.isCanceled) {
                            handleException(t)
                        }
                        finishRequest()
                    }
                })
            }
    }

    private fun handleException(e: Throwable) {
        Toast.makeText(requireContext(), e.message ?: "Something went wrong", Toast.LENGTH_SHORT).show()
    }
    companion object {
        val INTERVAL_BETWEEN_ITEMS = 50
    }
}