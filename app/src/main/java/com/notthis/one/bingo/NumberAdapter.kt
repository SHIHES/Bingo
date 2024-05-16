package com.notthis.one.bingo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.notthis.one.bingo.databinding.CellNumberBinding

class NumberAdapter : ListAdapter<Int, NumberAdapter.NumberViewHolder>(NumberDiffUtil) {

  val selectedPosition = mutableListOf<Int>()

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
          if (selectedPosition.contains(bindingAdapterPosition)) {
            selectedPosition.remove(bindingAdapterPosition)
            setBackgroundColor(Color.WHITE)
          } else {
            selectedPosition.add(bindingAdapterPosition)
            setBackgroundColor(Color.YELLOW)
          }
        }
      }
    }
  }

  fun restore() {
    selectedPosition.clear()
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