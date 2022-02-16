package com.gtech.narisafety.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gtech.narisafety.interfaces.ItemClickListener
import com.gtech.narisafety.R

class NewsAdapter(
    val mContext: NewsFragment,
    private val mList: ArrayList<NewsModel>
) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = mList[position] as NewsModel
        holder.bind(model)
        holder.setItemOnClickListener(object : ItemClickListener {
            override fun onItemClick(i: Int) {
//                val bundle = bundleOf("parcel" to model)mContext.mNavController.navigate(R.id.newsFragment, bundle)
            }
        })
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview), View.OnClickListener {
        var itemClickListener: ItemClickListener? = null
        private var heaading = itemview.findViewById<TextView>(R.id.news_headline)
        private var name = itemview.findViewById<TextView>(R.id.username)
        private var image = itemview.findViewById<ImageView>(R.id.newsimage)
        private var date = itemview.findViewById<TextView>(R.id.news_date)
        private var status = itemview.findViewById<TextView>(R.id.status)
        private var description = itemview.findViewById<TextView>(R.id.news_description)
        private var sharebtn = itemview.findViewById<ImageView>(R.id.news_sharebtn)


        init {
            itemview.setOnClickListener(this)
        }

        fun bind(model: NewsModel) {
            heaading.text = model.heading
            val requestOptions =
                RequestOptions().error(R.drawable.ic_menu_gallery).placeholder(R.drawable.ic_person)
            Glide.with(itemView).load(model.image).apply(requestOptions).into(image)
            description.text = model.description
            date.text = model.getdateasformatted(model.date!!).toString()
            name.text = "by:" + model.username
            status.text = model.status
        }


        override fun onClick(p0: View?) {
            itemClickListener?.onItemClick(adapterPosition)
        }

        fun setItemOnClickListener(itemClickListener: ItemClickListener) {
            this.itemClickListener = itemClickListener
        }
    }
}