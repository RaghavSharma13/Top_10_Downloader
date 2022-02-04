package learnprogramming.academy

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*


class FeedEntry{
    var name: String = ""
    var artist: String = ""
    var releaseDate: String = ""
    var summary: String = ""
    var imageURL: String = ""

    override fun toString(): String {
        return """
            name = $name
            artist = $artist
            releaseDate = $releaseDate
            imageURL = $imageURL
        """.trimIndent()
    }
}


private const val TAG = "mainActivity"
private const val FEED_TYPE_KEY = "FEED_TYPE"
private const val FEED_LIMIT_KEY = "FEED_LIMIT"

class MainActivity : AppCompatActivity() {

    private var feedType: String = "topfreeapplications"
    private var feedLimit: Int = 10
    private var feedUrl: String = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/%s/limit=%d/xml"
    private val viewModel: MainActivityViewModel by lazy {ViewModelProviders.of(this).get(MainActivityViewModel::class.java)}

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate begin.")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(savedInstanceState!=null){
            feedType = savedInstanceState.getString(FEED_TYPE_KEY,"topfreeapplications")
            feedLimit = savedInstanceState.getInt(FEED_LIMIT_KEY, 10)
        }
        val feedAdapter = FeedAdapter(this, R.layout.list_view_item, EMPTY_FEED_LIST)
        xmlListView.adapter = feedAdapter
        viewModel.feedData.observe(this, {
            feedAdapter.setFeed(it)
        })
//        downloadFeed()
        viewModel.fetchData(feedUrl.format(feedType, feedLimit))
        Log.d(TAG, "onCreate end.")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.downloader_menu, menu)
        if (feedLimit == 10) menu?.findItem(R.id.menuTop10)?.isChecked = true
        else menu?.findItem(R.id.menuTop25)?.isChecked = true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var newInputFeedType: String = feedType
        var newInputFeedLimit: Int = feedLimit
        when (item.itemId) {
            R.id.menuFeedRefresh -> {
                viewModel.fetchData(feedUrl)
                return true
            }
            R.id.menuFreeApps -> newInputFeedType = "topfreeapplications"
            R.id.menuPaidApps -> newInputFeedType = "toppaidapplications"
            R.id.menuSongs -> newInputFeedType = "topsongs"
            R.id.menuTop10, R.id.menuTop25 -> {
                if (!item.isChecked) {
                    item.isChecked = true
                    newInputFeedLimit = 35 - newInputFeedLimit
                }
            }
            else -> return super.onOptionsItemSelected(item)
        }
        Log.d(TAG, "newInputLimit = $newInputFeedLimit, newInputType = $newInputFeedType")
        if (newInputFeedLimit != feedLimit || newInputFeedType != feedType) {
            feedType = newInputFeedType
            feedLimit = newInputFeedLimit

            Log.d(TAG, feedUrl.format(feedType, feedLimit))
            viewModel.fetchData(feedUrl.format(feedType, feedLimit))
        }
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(FEED_TYPE_KEY, feedType)
        outState.putInt(FEED_LIMIT_KEY, feedLimit)
    }
}
