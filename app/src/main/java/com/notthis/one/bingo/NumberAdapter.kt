package com.notthis.one.bingo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.notthis.one.bingo.databinding.CellNumberBinding

class NumberAdapter : ListAdapter<Int, NumberAdapter.NumberViewHolder>(NumberDiffUtil) {

  val selectedPositions = mutableListOf<Int>()

  override fun onBindViewHolder(
    holder: NumberViewHolder,
    position: Int
  ) {
    holder.bind(getItem(position))
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): NumberViewHolder {
    return NumberViewHolder(
      CellNumberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
  }

  inner class NumberViewHolder(
    private val binding: CellNumberBinding,
  ) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
      number: Int
    ) {
      binding.tvNumber.apply {
        text = number.toString()
        setOnClickListener {
          if (selectedPositions.contains(bindingAdapterPosition)) {
            selectedPositions.remove(bindingAdapterPosition)
            setBackgroundColor(Color.WHITE)
          } else {
            selectedPositions.add(bindingAdapterPosition)
            setBackgroundColor(Color.YELLOW)
          }
        }
      }
    }
  }

  fun restore() {
    selectedPositions.clear()
    notifyDataSetChanged()
  }

  companion object NumberDiffUtil : DiffUtil.ItemCallback<Int>() {
    override fun areItemsTheSame(
      oldItem: Int,
      newItem: Int
    ): Boolean {
      return oldItem == newItem
    }

    override fun areContentsTheSame(
      oldItem: Int,
      newItem: Int
    ): Boolean {
      return oldItem === newItem
    }
  }
}