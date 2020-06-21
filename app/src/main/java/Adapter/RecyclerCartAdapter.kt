package Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codiinggeek.food.R
import model.CartItems

class RecyclerCartAdapter(val context: Context, val cartItems:ArrayList<CartItems>):
    RecyclerView.Adapter<RecyclerCartAdapter.ViewHolderCart>(){

    class ViewHolderCart(view: View): RecyclerView.ViewHolder(view){
        val textViewOrderItem: TextView =view.findViewById(R.id.textViewOrderItem)
        val textViewOrderItemPrice: TextView =view.findViewById(R.id.textViewOrderItemPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCart {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.recycler_single_cart,parent,false)
        return ViewHolderCart(view)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    override fun onBindViewHolder(holder: ViewHolderCart, position: Int) {
        val cartItemObject=cartItems[position]
        holder.textViewOrderItem.text=cartItemObject.itemName
        holder.textViewOrderItemPrice.text="Rs. "+cartItemObject.itemPrice
    }
}