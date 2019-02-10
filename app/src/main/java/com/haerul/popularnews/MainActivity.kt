package com.haerul.popularnews

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.haerul.popularnews.api.ApiClient
import com.haerul.popularnews.api.ApiInterface
import com.haerul.popularnews.models.Article
import com.haerul.popularnews.models.News
import kotlinx.android.synthetic.main.item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private var recyclerView: RecyclerView? = null
    private var articles: MutableList<Article> = ArrayList()
    private var adapter: Adapter? = null
    private var topHeadline: TextView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var errorLayout: RelativeLayout? = null
    private var errorImage: ImageView? = null
    private var errorTitle: TextView? = null
    private var errorMessage: TextView? = null
    private var btnRetry: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout!!.setOnRefreshListener(this)
        swipeRefreshLayout!!.setColorSchemeResources(R.color.colorAccent)

        topHeadline = findViewById(R.id.topheadelines)
        recyclerView = findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this@MainActivity)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.isNestedScrollingEnabled = false

        onLoadingSwipeRefresh("")

        errorLayout = findViewById(R.id.errorLayout)
        errorImage = findViewById(R.id.errorImage)
        errorTitle = findViewById(R.id.errorTitle)
        errorMessage = findViewById(R.id.errorMessage)
        btnRetry = findViewById(R.id.btnRetry)

    }

    private fun loadJson(keyword: String) {
        errorLayout!!.visibility = View.GONE
        swipeRefreshLayout!!.isRefreshing = true
        val apiInterface = ApiClient.apiClient.create(ApiInterface::class.java)
        val country = Utils.country
        val language = Utils.language
        val call: Call<News>
        call = when {
            keyword.isNotEmpty() -> apiInterface.getNewsSearch(keyword, language, "publishedAt", API_KEY)
            else -> apiInterface.getNews(country, API_KEY)
        }

        call.enqueue(object : Callback<News> {
            override fun onResponse(call: Call<News>, response: Response<News>) {
                when {
                    response.isSuccessful && response.body()!!.article != null -> {
                        if (!articles.isEmpty()) {
                            articles.clear()
                        }
                        articles = response.body()!!.article!!
                        adapter = Adapter(articles, this@MainActivity)
                        recyclerView!!.adapter = adapter
                        adapter!!.notifyDataSetChanged()
                        initListener()
                        topHeadline!!.visibility = View.VISIBLE
                        swipeRefreshLayout!!.isRefreshing = false


                    }
                    else -> {
                        topHeadline!!.visibility = View.INVISIBLE
                        swipeRefreshLayout!!.isRefreshing = false
                        val errorCode = when (response.code()) {
                            404 -> "404 not found"
                            500 -> "500 server broken"
                            else -> "unknown error"
                        }

                        showErrorMessage(
                                R.drawable.no_result,
                                "No Result",
                                "Please Try Again!\n$errorCode")
                    }
                }
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                topHeadline!!.visibility = View.INVISIBLE
                swipeRefreshLayout!!.isRefreshing = false
                showErrorMessage(
                        R.drawable.oops,
                        "Oops..",
                        "Network failure, Please Try Again\n$t")
            }
        })

    }


    private fun initListener() {
        adapter!!.setOnItemClickListener(object : Adapter.OnItemClickListener {

            override fun onItemClick(view: View, position: Int) {
                Toast.makeText(this@MainActivity, "xxx0", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@MainActivity, NewsDetailActivity::class.java)
                val article = articles[position]
                intent.putExtra("url", article.url)
                intent.putExtra("title", article.title)
                intent.putExtra("img", article.urlToImage)
                intent.putExtra("date", article.publishedAt)
                intent.putExtra("source", article.source!!.name)
                intent.putExtra("author", article.author)

                val pair = Pair.create<View, String>(view.coverPic, ViewCompat.getTransitionName(view.coverPic))
                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@MainActivity,
                        pair
                )
                startActivity(intent, optionsCompat.toBundle())
            }

        })
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        val searchMenuItem = menu.findItem(R.id.action_search)
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = "Search Latest News..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.length > 2) {
                    onLoadingSwipeRefresh(query)
                } else {
                    Toast.makeText(this@MainActivity, "Type more than two letters!", Toast.LENGTH_SHORT).show()
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        searchMenuItem.icon.setVisible(false, false)

        return true
    }

    override fun onRefresh() {
        loadJson("")
    }

    private fun onLoadingSwipeRefresh(keyword: String) {
        swipeRefreshLayout!!.post { loadJson(keyword) }
    }

    private fun showErrorMessage(imageView: Int, title: String, message: String) {
        if (errorLayout!!.visibility == View.GONE) {
            errorLayout!!.visibility = View.VISIBLE
        }
        errorImage!!.setImageResource(imageView)
        errorTitle!!.text = title
        errorMessage!!.text = message
        btnRetry!!.setOnClickListener { v -> onLoadingSwipeRefresh("") }
    }

    companion object {

        val API_KEY = "b806459b4e684364bd79a792b8f02abc"
    }
}
