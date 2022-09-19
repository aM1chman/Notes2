package com.example.sqllite.db

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sqllite.EditActivity
import com.example.sqllite.R

class MyAdapter(listArrayM:ArrayList<ListItem>,contextM: Context): RecyclerView.Adapter<MyAdapter.MyHolder>() {
        val listArray = listArrayM
        val context = contextM

    class MyHolder(itemView: View,contextM: Context): RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val context = contextM
        fun bind(item:ListItem){
            tvTitle.text = item.title
            tvTime.text = item.time

            itemView.setOnClickListener{
                val intent = Intent(context, EditActivity::class.java).apply {

                    putExtra(MyIntentConstants.I_TITLE_KEY, item.title)
                    putExtra(MyIntentConstants.I_DESC_KEY, item.desc)
                    putExtra(MyIntentConstants.I_URI_KEY, item.uri)
                    putExtra(MyIntentConstants.I_ID_KEY, item.id)

                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyHolder(inflater.inflate(R.layout.rc_item, parent, false),context)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.bind(listArray[position])
    }

    override fun getItemCount(): Int {
        return listArray.size
    }

    fun updateAdapter(listItems:List<ListItem>) {
            listArray.clear()
            listArray.addAll(listItems)
            notifyDataSetChanged()
    }

    fun removeItem(pos:Int, dbManager: MyDbManager) {
        dbManager.removeItemFromDb(listArray[pos].id.toString())
        listArray.removeAt(pos)
        notifyItemRangeChanged(0,listArray.size)
        notifyItemRemoved(pos)
    }

}