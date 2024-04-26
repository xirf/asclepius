package com.dicoding.asclepius.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.data.response.ArticlesItem
import com.dicoding.asclepius.databinding.ItemNewsBinding

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.NewsAdapterHolder>() {
    private val listNews = ArrayList<ArticlesItem>()
    private lateinit var onItemClickCallback: OnItemClickCallback

    class NewsAdapterHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(news: ArticlesItem) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(news.urlToImage)
                    .into(ivNews)
                tvTitle.text = news.title
                tvDescription.text = news.description
            }
        }
    }

    fun setListNews(items: List<ArticlesItem>) {
        val isDifferent = DiffUtil.calculateDiff(DiffUtilCallback(listNews, items))
        listNews.clear()
        listNews.addAll(listNews)
        isDifferent.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapterHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsAdapterHolder(binding)
    }

    override fun getItemCount(): Int = listNews.size

    override fun onBindViewHolder(
        holder: NewsAdapterHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        holder.bind(listNews[position])
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listNews[position])
        }
    }

    fun setOnClickCallback(onClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onClickCallback
    }

    fun interface OnItemClickCallback {
        fun onItemClicked(data: ArticlesItem)
    }

    override fun onBindViewHolder(holder: NewsAdapterHolder, position: Int) {
        holder.bind(listNews[position])
    }

    class DiffUtilCallback(
        private val oldList: List<ArticlesItem>,
        private val newList: List<ArticlesItem>
    ) :
        DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return oldItem.javaClass == newItem.javaClass
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return oldItem == newItem
        }
    }

}