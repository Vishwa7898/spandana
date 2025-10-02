package com.example.spandana.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spandana.databinding.ItemCategoryBinding
import com.example.spandana.models.Category

class CategoriesAdapter(
    private val onCategoryClick: (Category) -> Unit
) : ListAdapter<Category, CategoriesAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.categoryName.text = category.name
            binding.categoryIcon.setImageResource(category.iconRes)

            // Icon color set කිරීම
            binding.categoryIcon.setColorFilter(Color.parseColor(category.color))

            // Card background color set කිරීම (light version)
            val lightColor = getLightColor(category.color)
            binding.categoryCard.setCardBackgroundColor(Color.parseColor(lightColor))

            binding.root.setOnClickListener {
                onCategoryClick(category)
            }
        }

        private fun getLightColor(hexColor: String): String {
            return when(hexColor) {
                "#007AFF" -> "#E6F2FF" // Blue
                "#5856D6" -> "#F0EFFF" // Purple
                "#FF2D55" -> "#FFEEF2" // Pink
                "#AF52DE" -> "#F6EDFF" // Light Purple
                "#FF3B30" -> "#FFEEED" // Red
                "#4CD964" -> "#EDF9EE" // Green
                "#FF9500" -> "#FFF4E6" // Orange
                "#5AC8FA" -> "#EBF9FF" // Light Blue
                else -> "#F2F2F7" // Default
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }
}