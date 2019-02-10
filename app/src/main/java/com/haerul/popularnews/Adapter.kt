package com.haerul.popularnews

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = articles[position]
        val itemView = holder.itemView
        val requestOptions = RequestOptions()
        requestOptions.placeholder(Utils.randomDrawbleColor)
        requestOptions.error(Utils.randomDrawbleColor)
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
        requestOptions.centerCrop()

       /* Glide.with(context)
                .load(model.urlToImage!!)
                .apply(requestOptions)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                        itemView.prograss_load_photo.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        itemView.prograss_load_photo.visibility = View.GONE
                        return false
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(itemView.coverPic)
*/

        itemView.title.text = model.title
        itemView.desc.text = model.description
        itemView.source.text = model.source!!.name
        itemView.time.text = " \u2022 " + Utils.DateToTimeFormat(model.publishedAt!!)
        itemView.publishedAt.text = Utils.DateFormat(model.publishedAt!!)
        itemView.author.text = model.author

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

    inner class MyViewHolder(itemView: View, private var onItemClickListener: OnItemClickListener) :
            RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View) = onItemClickListener.onItemClick(v, adapterPosition)
    }
}
