package com.dicoding.asclepius.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.entity.HistoryEntity
import com.dicoding.asclepius.databinding.ItemHistoryBinding
import com.dicoding.asclepius.helper.MediaStorageHelper
import kotlin.math.roundToInt

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryAdapterHolder>() {
    private val listHistory = ArrayList<HistoryEntity>()
    private lateinit var onItemClickCallback: OnItemClickCallback

    class HistoryAdapterHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(history: HistoryEntity) {
            with(binding) {
                val mediaStorageHelper = MediaStorageHelper(root.context)

                tvResult.text = history.label
                tvConfidence.text = "${(history.confidence * 100).roundToInt()}%"

                val imageUri = mediaStorageHelper.getImageFromGallery(history.image)
                if (imageUri != null) {
                    ivHistory.setImageURI(imageUri)
                } else {
                    ivHistory.setImageResource(R.drawable.ic_place_holder)
                }
            }
        }


    }

    fun setListHistory(items: List<HistoryEntity>) {
        val isDifferent = DiffUtil.calculateDiff(DiffUtilCallback(listHistory, items))
        listHistory.clear()
        listHistory.addAll(items)
        isDifferent.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapterHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryAdapterHolder(binding)
    }

    override fun getItemCount(): Int = listHistory.size

    override fun onBindViewHolder(
        holder: HistoryAdapterHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        holder.bind(listHistory[position])
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listHistory[position])
        }
    }

    fun setOnClickCallback(onClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onClickCallback
    }

    fun interface OnItemClickCallback {
        fun onItemClicked(data: HistoryEntity)
    }

    override fun onBindViewHolder(holder: HistoryAdapterHolder, position: Int) {
        holder.bind(listHistory[position])
    }

    class DiffUtilCallback(
        private val oldList: List<HistoryEntity>,
        private val newList: List<HistoryEntity>
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