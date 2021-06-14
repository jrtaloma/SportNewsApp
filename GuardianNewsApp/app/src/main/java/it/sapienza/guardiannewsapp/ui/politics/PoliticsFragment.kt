package it.sapienza.guardiannewsapp.ui.politics

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import it.sapienza.guardiannewsapp.*
import it.sapienza.guardiannewsapp.news.News
import it.sapienza.guardiannewsapp.news.NewsAdapter
import kotlinx.android.synthetic.main.fragment_politics.*

// https://guides.codepath.com/android/using-the-recyclerview
// https://www.geeksforgeeks.org/how-to-implement-swipe-down-to-refresh-in-android-using-android-studio/

// https://medium.com/androiddevelopers/viewmodels-a-simple-example-ed5ac416317e
// https://betterprogramming.pub/everything-to-understand-about-viewmodel-400e8e637a58

class PoliticsFragment : Fragment() {

    private lateinit var googleIdToken: String
    private lateinit var googleEmail: String
    private lateinit var viewModel: SharedViewModel
    private lateinit var adp: NewsAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = activity?.let {
            ViewModelProvider(it).get(SharedViewModel::class.java)
        } ?: throw Exception("Activity is null")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_politics, container, false)
    }

    // populate the views now that the layout has been inflated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as HomepageActivity).supportActionBar?.title = viewModel.accountName

        googleIdToken = viewModel.googleIdToken
        googleEmail = viewModel.googleEmail

        if (viewModel.politicsNewsAdapter != null) {
            Log.i("info", "Getting NewsAdapter from SharedViewModel")
            adp = viewModel.politicsNewsAdapter!!
            adp.setOnItemClicked { loadWebPage(it) }
            adp.setOnItemLongClicked { createFavorite(it) }
        } else {
            adp = NewsAdapter(POLITICS, { loadWebPage(it) }, { createFavorite(it) } )
            viewModel.politicsNewsAdapter = adp
        }

        politics_list_recycler_view.apply {
            // set a LinearLayoutManager to position items
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to populate items
            adapter = adp
            // add borders between items
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        }

        // set refresh event listener
        swipeRefreshLayout = view.findViewById(R.id.politics_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener { refreshContent() }

        // set search event listener
        politics_search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchContent(query!!)
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        // set floating action button listener
        politics_floatingActionButton.setOnClickListener {
            loadNextContent()
        }
    }

    private fun refreshContent() {
        adp.getAll()
        viewModel.politicsNewsAdapter = adp
        swipeRefreshLayout.isRefreshing = false
    }

    private fun searchContent(query: String) {
        adp.search(query)
        viewModel.politicsNewsAdapter = adp
        Toast.makeText(activity, "Found results", Toast.LENGTH_SHORT).show()
    }

    private fun loadNextContent() {
        adp.getAllNext()
        viewModel.politicsNewsAdapter = adp
        Toast.makeText(activity, "More results", Toast.LENGTH_SHORT).show()
    }

    private fun loadWebPage(news: News) {
        try {
            if (news.id != "") {
                val intent = Intent(activity, WebpageActivity::class.java).apply {
                    putExtra(GOOGLE_ID_TOKEN, googleIdToken)
                    putExtra(ACCOUNT_EMAIL, googleEmail)
                    putExtra(NEWS_ID, news.id)
                    putExtra(WEB_PUBLICATION_DATE, news.webPublicationDate)
                    putExtra(WEB_TITLE, news.webTitle)
                    putExtra(WEB_PAGE_URL, news.webUrl)
                }
                startActivity(intent)
            }
        } catch (e: NullPointerException) {
            Log.e("error", e.toString())
        }
    }

    private fun createFavorite(news: News) : Boolean {
        if (news.id != "") {
            Toast.makeText(activity, "Insertion in favorites", Toast.LENGTH_SHORT).show()
            viewModel.favoritesAdapter?.createFavorite(news, viewModel.accountName)
        }
        return true
    }

}