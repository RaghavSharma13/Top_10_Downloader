package learnprogramming.academy

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

private const val TAG = "viewModel"
val EMPTY_FEED_LIST: List<FeedEntry> = Collections.emptyList()
class MainActivityViewModel: ViewModel() {

    private val feed = MutableLiveData<List<FeedEntry>>()
    val feedData: LiveData<List<FeedEntry>>
    get() = feed

    init{
        feed.postValue(EMPTY_FEED_LIST)
    }

    fun fetchData(urlPath: String?){
        CoroutineScope(Dispatchers.IO).launch {
            val rawData = downloadFeed(urlPath)
            val parsedData = ParseApplicationData()
            withContext(Dispatchers.Default){
                parsedData.parse(rawData)
            }
            withContext(Dispatchers.Main){
                feed.value = parsedData.applicationData
            }
        }
    }

    private fun downloadFeed(urlPath : String?):String{
        val xmlResult = StringBuilder()
        try {
            val url = URL(urlPath)
            val connection = url.openConnection() as HttpURLConnection
            val response = connection.responseCode
            Log.d(TAG, "downloadFeed: response code $response")

            connection.inputStream.buffered().reader().use{ xmlResult.append(it.readText())}
            Log.d(TAG, "received ${xmlResult.length} bytes")

            return xmlResult.toString()
        }catch (e:Exception){
            val errorMsg = when(e){
                is MalformedURLException -> "Url Exception: ${e.message}"
                is IOException -> "IOException: ${e.message}"
                is SecurityException -> "Security Exception: ${e.message}"
                else -> {
                    e.printStackTrace()
                    "Unknown Error: ${e.message}"
                }
            }
            Log.e(TAG, errorMsg)
        }
        return ""
    }
}