package com.haerul.popularnews

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.haerul.popularnews.models.Article
import kotlinx.android.synthetic.main.item.view.*


class Adapter(private val articles: MutableList<Article>, private val context: Context) : RecyclerView.Adapter<Adapter.MyViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item, parent, false)
        return MyViewHolder(view, onItemClickListener!!)
    }

    override fun onBindViewHolder(holders: MyViewHolder, position: Int) {
        val model = articles[position]

        val requestOptions = RequestOptions()
        requestOptions.placeholder(Utils.randomDrawbleColor)
        requestOptions.error(Utils.randomDrawbleColor)
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
        requestOptions.centerCrop()

        Glide.with(context)
                .load(model.urlToImage)
                .apply(requestOptions)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                        holders.progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        holders.progressBar.visibility = View.GONE
                        return false
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holders.itemView.img)
        holders.title.text = model.title
        holders.desc.text = model.description
        holders.source.text = model.source!!.name
        holders.time.text = " \u2022 " + Utils.DateToTimeFormat(model.publishedAt!!)
        holders.published_ad.text = Utils.DateFormat(model.publishedAt!!)
        holders.author.text = model.author

    }

    override fun getItemCount(): Int {
        return articles.size
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    inner class MyViewHolder(itemView: View, internal var onItemClickListener: OnItemClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        internal var title: TextView
        internal var desc: TextView
        internal var author: TextView
        internal var published_ad: TextView
        internal var source: TextView
        internal var time: TextView
        internal var progressBar: ProgressBar

        init {

            itemView.setOnClickListener(this)
            title = itemView.findViewById(R.id.title)
            desc = itemView.findViewById(R.id.desc)
            author = itemView.findViewById(R.id.author)
            published_ad = itemView.findViewById(R.id.publishedAt)
            source = itemView.findViewById(R.id.source)
            time = itemView.findViewById(R.id.time)
            progressBar = itemView.findViewById(R.id.prograss_load_photo)

        }

        override fun onClick(v: View) {
            onItemClickListener.onItemClick(v, adapterPosition)
        }
    }
}
