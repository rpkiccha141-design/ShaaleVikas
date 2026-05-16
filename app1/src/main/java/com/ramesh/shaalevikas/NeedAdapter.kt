package com.ramesh.shaalevikas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class NeedAdapter(
    private val needList: MutableList<Need>,
    private val onPledge: (String) -> Unit
) : RecyclerView.Adapter<NeedAdapter.NeedViewHolder>() {

    class NeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.txtTitle)
        val description: TextView = itemView.findViewById(R.id.txtDescription)
        val cost: TextView = itemView.findViewById(R.id.txtCost)
        val progress: ProgressBar = itemView.findViewById(R.id.progressBar)
        val pledge: Button = itemView.findViewById(R.id.btnPledge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NeedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_need, parent, false)
        return NeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: NeedViewHolder, position: Int) {
        val need = needList[position]

        holder.title.text = need.title
        holder.description.text = need.description
        holder.cost.text = "Cost Estimate: ₹${need.cost}"
        holder.progress.progress = need.progress

        holder.pledge.setOnClickListener {
            val newProgress = (need.progress + 10).coerceAtMost(100)
            needList[position] = need.copy(progress = newProgress)
            notifyItemChanged(position)

            onPledge(need.title)

            Toast.makeText(
                holder.itemView.context,
                "Thank you for supporting ${need.title}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int = needList.size
}