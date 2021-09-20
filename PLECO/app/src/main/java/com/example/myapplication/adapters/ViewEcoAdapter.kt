package com.example.myapplication.adapters

import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.utils.Photo
import kotlinx.android.synthetic.main.activity_uploaded_eco.*


class ViewEcoAdapter(private var ecoList: ArrayList<Photo>) :
    RecyclerView.Adapter<ViewEcoAdapter.MyPageViewHolder>() {


    class MyPageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iv_eco_item = itemView?.findViewById<ImageView>(R.id.iv_eco_item)
        val tv_eco = itemView?.findViewById<TextView>(R.id.tv_fname_item)


        fun bind(photo: Photo) {
            Log.d("리사이클러뷰 끼우기", photo.uri.toString() + photo.eco_id)
            iv_eco_item.setImageURI(photo.uri.toUri())
            tv_eco.setText(photo.eco_id)

        }
    }


    // 화면을 최초 로딩하여 만들어진 View가 없는 경우, xml파일을 inflate하여 ViewHolder를 생성
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewEcoAdapter.MyPageViewHolder {
        // create a new view
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_eco, parent, false)
        return MyPageViewHolder(itemView)
    }

    // 위의 onCreateViewHolder에서 만든 view와 실제 입력되는 각각의 데이터를 연결
    override fun onBindViewHolder(holder: MyPageViewHolder, position: Int) {
        holder?.bind(ecoList[position])
        Log.d("리사이클러뷰 불러짐", "성공")
//
//        holder?.itemView.setOnClickListener{
//            val intent = Intent(holder.itemView?.context, RecommendFoodActivity::class.java)
//            intent.putExtra("food_name",ecoList[position].food_id)
//            ContextCompat.startActivity(holder.itemView.context, intent, null)
//        }

        holder?.itemView.setOnLongClickListener {
            val dialog = AlertDialog.Builder(it.context)
            dialog.setMessage("사진을 삭제하시겠습니까?")
            dialog.setPositiveButton("네", DialogInterface.OnClickListener { _, _ ->
                removeItem(position)
                notifyItemRemoved(position)
            })
            dialog.setNegativeButton("아니오", DialogInterface.OnClickListener { _, _ ->
            })
            dialog.show()
            true
        }

    }


    fun removeItem(position: Int){
        ecoList.remove(ecoList[position])
    }



    //  RecyclerView로 만들어지는 item의 총 개수를 반환
    override fun getItemCount() = ecoList.size


}
