package com.msq.code.customer.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.customer.service.valetcarwash.models.stripe.CardsListData
import com.msq.databinding.ItemPaymentMethodsBinding
import com.msq.util.CardValidator

typealias CardListener = (String, Int) -> Unit

class PaymentMethodAdapter(
    var payMethodsList: MutableList<CardsListData>,
    val mCardListener: CardListener,
) : RecyclerView.Adapter<PaymentMethodAdapter.Item>() {

    class Item(var binding: ItemPaymentMethodsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Item {
        return Item(
            ItemPaymentMethodsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return payMethodsList.size
    }

    override fun onBindViewHolder(holder: Item, position: Int) {
        Glide.with(holder.binding.root.context)
            .load(CardValidator.getCardIcon(payMethodsList.get(position).brand))
            .into(holder.binding.ivCard)
        holder.binding.tvCardNumber.setText("**** **** **** ${payMethodsList.get(position).last4}")
        holder.binding.cbMethod.isChecked = payMethodsList.get(position).default
        holder.binding.tvCardName.text = payMethodsList.get(position).brand
//        holder.binding.cbPaymentMethod.isChecked = payMethodsList.get(position).default
        holder.binding.tvRemoveCard.setOnClickListener { mCardListener("delete", position) }
        holder.binding.root.setOnClickListener { mCardListener("update", position) }

    }
}
