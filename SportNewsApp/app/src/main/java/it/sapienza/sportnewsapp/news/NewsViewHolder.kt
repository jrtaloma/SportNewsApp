package it.sapienza.sportnewsapp.news

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.sapienza.sportnewsapp.R

// https://oozou.com/blog/a-better-way-to-handle-click-action-in-a-recyclerview-item-60

class NewsViewHolder(inflater: LayoutInflater, parent: ViewGroup, private val onItemClicked: (Int) -> Unit, private val onItemLongClicked: (Int) -> Boolean) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item, parent, false)) {
    private var webPublicationDateView : TextView? = null
    private var webTitleView : TextView? = null
    private var liveStatusView : TextView? = null

    init {
        webPublicationDateView = itemView.findViewById(R.id.list_webPublicationDate)
        webTitleView = itemView.findViewById(R.id.list_webTitle)
        liveStatusView = itemView.findViewById(R.id.list_livestatus)
        itemView.setOnClickListener {
            onItemClicked(adapterPosition)
        }
        itemView.setOnLongClickListener {
            onItemLongClicked(adapterPosition)
        }
    }

    fun bind(news: News) {
        webPublicationDateView?.text = news.webPublicationDate
        webTitleView?.text = news.webTitle
        if ("live!" == news.webTitle.trim().takeLast(5)) {
            liveStatusView?.textSize = 20f
            "LIVE".also { liveStatusView?.text = it }
        } else {
            liveStatusView?.textSize = 0f
        }
    }
}