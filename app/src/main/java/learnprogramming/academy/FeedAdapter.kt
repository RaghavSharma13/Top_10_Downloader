package learnprogramming.academy

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class ViewHolder(v: View) {
    val appNameView: TextView = v.findViewById(R.id.textViewAppName)
    val appArtistView: TextView = v.findViewById(R.id.textViewAppCreator)
    val appSummaryView: TextView = v.findViewById(R.id.textViewAppSummary)
}

private const val TAG = "feedAdapter"
class FeedAdapter(
    context: Context,
    private val resource: Int,
    private var applications: List<FeedEntry>
) : ArrayAdapter<FeedEntry>(context, resource) {
    private val inflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        Log.d(TAG, "getCount")
        return applications.size
    }

    fun setFeed(feed: List<FeedEntry>) {
        this.applications = feed
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            Log.d(TAG, "creating a new view")
            view = inflater.inflate(resource, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            Log.d(TAG, "reusing a view")
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val currentRecord = applications[position]

        viewHolder.appNameView.text = currentRecord.name
        viewHolder.appArtistView.text = currentRecord.artist
        viewHolder.appSummaryView.text = currentRecord.summary
        return view
    }
}
