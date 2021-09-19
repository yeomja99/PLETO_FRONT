package com.example.myapplication.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.utils.PleeList
import com.example.myapplication.view.PleeInfoActivity

class PleeListAdapter(
    private val context: Context, private val dataList: MutableList<PleeList>
): RecyclerView.Adapter<PleeListAdapter.ItemViewHolder>() {
    var mPosition = 0

    fun GetPosition():Int {
        return mPosition
    }

    private fun setPosition(position:Int){
        mPosition = position
    }

    fun addItem(pleeList: PleeList){
        dataList.add(pleeList)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int){
        if (position > 0) {
            dataList.removeAt(position)
            //notifyItemRemoved(position)
            notifyDataSetChanged()
        }
    }

    inner class ItemViewHolder(itemView:View):
            RecyclerView.ViewHolder(itemView) {
        private val pleePhoto = itemView.findViewById<ImageView>(R.id.iv_pleePhoto)
        private val pleeNum = itemView.findViewById<TextView>(R.id.tv_pleeNum)
        private val pleeName = itemView.findViewById<TextView>(R.id.tv_pleeName)


        fun bind(pleeList: PleeList, context: Context) {
            if (pleeList.photo != "") {
                val resourceId = context.resources.getIdentifier(pleeList.photo, "drawable", context.packageName)

                if (resourceId > 0) {
                    pleePhoto.setImageResource(resourceId)
                    //tv에 데이터 세팅
                    pleeName.text = pleeList.name.toString()
                } else {
                    pleePhoto.setImageResource(R.mipmap.ic_launcher_round)
                }
            } else {
                pleePhoto.setImageResource(R.drawable.ic_nonplee)
                pleeName.setText("???")
            }
            pleeNum.text = pleeList.num.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_list_plee, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(dataList[position], context)
        holder.itemView.setOnClickListener { view ->
            setPosition(position)
//            Toast.makeText(view.context, "$position 클릭!!!", Toast.LENGTH_SHORT).show()

            //플리 클릭하면 확대 페이지 보여주기(다른 activity로 넘어가기)
            val intent = Intent(context, PleeInfoActivity::class.java)
            intent.putExtra("image_name", dataList[position].photo) //이미지 data intent에 넣기
            intent.putExtra("plee_name", dataList[position].name)
            intent.putExtra("plee_info", dataList[position].info)
            context.startActivity(intent) //intent 시작
        }
   }
}