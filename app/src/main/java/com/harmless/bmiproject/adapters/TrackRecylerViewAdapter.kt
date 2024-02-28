package com.harmless.bmiproject.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.harmless.bmiproject.R
import com.harmless.bmiproject.models.TrackModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Random


class TrackRecylerViewAdapter(private val context: Context): RecyclerView.Adapter<TrackRecylerViewAdapter.TrackHolder>(){

    private var bmiTrack: List<TrackModel>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bmi_tracker_card, parent,false)
        return TrackHolder(view)
    }

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        val bmi = bmiTrack?.get(position)

        val currentDate = Date()
        val cal = Calendar.getInstance()
        cal.time = currentDate
        cal.add(Calendar.DATE, -7)
        val oneWeekAgo = cal.time



        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val weekFormat = dateFormat.format(oneWeekAgo)
        val bmiDate = dateFormat.format(bmi!!.date)

        val calYesterday = Calendar.getInstance()
        calYesterday.time = currentDate
        calYesterday.add(Calendar.DATE, -1)
        val yesterday = calYesterday.time

        val yesterdayFormatted = dateFormat.format(yesterday)

        when (bmiDate) {
            weekFormat -> {

                holder.date.visibility = View.VISIBLE
                holder.date.text = "Last Week"
            }
            yesterdayFormatted -> {

                holder.date.visibility = View.VISIBLE
                holder.date.text = "Yesterday"
            }
            else -> {
                holder.date.visibility = View.GONE
            }
        }


        holder.bmiTxt.text = roundToTwoDecimals(bmi.bmi)+ " BMI"
        holder.ageTxt.text = bmi.age

        holder.bmiCard.post {
        //adjust he relative width
            val bmiGraphWidth: Int = holder.bmiCard.width

            val percentage = (bmi.bmi / 45) * bmiGraphWidth
            Log.d("lol", "onBindViewHolder: $bmiGraphWidth")
            Log.d("lol", "onBindViewHolder: ${bmi.bmi}")
            Log.d("lol", "onBindViewHolder: $percentage")
            val relativeParams = holder.relScale.layoutParams
            relativeParams.width = percentage.toInt()
            holder.relScale.layoutParams = relativeParams
            barColor(bmi.bmi, holder)
        }

    }

    override fun getItemCount(): Int {
        return bmiTrack?.size?: 0
    }

    fun setData( _bmiTrack: List<TrackModel>) {
        bmiTrack = _bmiTrack
        notifyDataSetChanged()
    }

    fun roundToTwoDecimals(number: Double): String {
        return "%.1f".format(number)
    }

    private fun barColor(bmi: Double, holder: TrackHolder){
        val random = getRandomNumberBetween(1,2)
        if(bmi >= 30.0){
            holder.relScale.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.flat_red
                )
            )

            holder.bmiCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_red))

            //init the image that goes with the color

            when(random){
                1->{
                    Glide.with(holder.bmiImageView)
                        .load(R.drawable.lava_2d)
                        .into(holder.bmiImageView)
                }
                2->{
                    Glide.with(holder.bmiImageView)
                        .load(R.drawable.lava_2d)
                        .into(holder.bmiImageView)
                }
            }

        }
        if(25.0 <= bmi && bmi <= 29.9){
            holder.relScale.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.flat_yellow
                )
            )

            holder.bmiCard.setCardBackgroundColor(ContextCompat.getColor(context,
                R.color.card_orange
            ))
            when(random){
                1->{
                    Glide.with(holder.bmiImageView)
                        .load(R.drawable.desert)
                        .into(holder.bmiImageView)
                }
                2->{
                    Glide.with(holder.bmiImageView)
                        .load(R.drawable.desert_2d_2)
                        .into(holder.bmiImageView)
                }
            }
        }
        if(18.5 <= bmi && bmi <= 24.9){
            holder.relScale.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.flat_green
                )
            )
            holder.bmiCard.setCardBackgroundColor(ContextCompat.getColor(context,
                R.color.card_green
            ))
            when(random){
                1->{
                    Glide.with(holder.bmiImageView)
                        .load(R.drawable.green_2d_forest)
                        .into(holder.bmiImageView)
                }
                2->{
                    Glide.with(holder.bmiImageView)
                        .load(R.drawable.green_2d_forest_2)
                        .into(holder.bmiImageView)
                }
            }
        }
        if(bmi < 18.5){
            holder.relScale.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.flat_blue
                )
            )
            holder.bmiCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_blue))
            when(random){
                1->{
                    Glide.with(holder.bmiImageView)
                        .load(R.drawable.arctic_2d)
                        .into(holder.bmiImageView)
                }
                2->{
                    Glide.with(holder.bmiImageView)
                        .load(R.drawable.artic)
                        .into(holder.bmiImageView)
                }
            }
        }

    }

    fun getRandomNumberBetween(min: Int, max: Int): Int {
        val random = Random()
        return random.nextInt(max - min + 1) + min
    }



    class TrackHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bmiCard: CardView = itemView.findViewById(R.id.bmiCard)

        val bmiImageView: ImageView = itemView.findViewById(R.id.bmi_image)
        val bmiTxt: TextView = itemView.findViewById(R.id.bmi_number)
        val ageTxt: TextView = itemView.findViewById(R.id.age_number)

        val relScale: RelativeLayout = itemView.findViewById(R.id.bmi_scale_slider)

        val date = itemView.findViewById<TextView>(R.id.date)
    }


}