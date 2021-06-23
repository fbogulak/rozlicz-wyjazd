package pl.skaucieuropy.rozliczwyjazd.ui.camps.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import pl.skaucieuropy.rozliczwyjazd.databinding.CampListItemBinding
import pl.skaucieuropy.rozliczwyjazd.models.Camp

class CampsListAdapter(private val clickListener: CampListener) :
    ListAdapter<Camp, CampsListAdapter.CampViewHolder>(CampDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampViewHolder {
        return CampViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CampViewHolder, position: Int) {
        val camp = getItem(position)
        holder.bind(camp, clickListener)
    }

    companion object CampDiffCallback : DiffUtil.ItemCallback<Camp>() {
        override fun areItemsTheSame(oldItem: Camp, newItem: Camp): Boolean {
            return oldItem.id.value == newItem.id.value
        }

        override fun areContentsTheSame(oldItem: Camp, newItem: Camp): Boolean {
            return oldItem == newItem
        }
    }

    class CampViewHolder(private var binding: CampListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(camp: Camp, clickListener: CampListener) {
            binding.camp = camp
            binding.onClickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): CampViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CampListItemBinding.inflate(layoutInflater, parent, false)
                return CampViewHolder(binding)
            }
        }
    }

    class CampListener(val clickListener: (camp: Camp) -> Unit) {
        fun onClick(camp: Camp) = clickListener(camp)
    }
}