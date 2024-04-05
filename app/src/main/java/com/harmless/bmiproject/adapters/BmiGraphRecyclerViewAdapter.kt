package com.harmless.bmiproject.adapters

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView
import com.harmless.bmiproject.R
import com.harmless.bmiproject.models.TrackModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class BmiGraphRecyclerViewAdapter(private val context: Context): RecyclerView.Adapter<BmiGraphRecyclerViewAdapter.TrackHolder>(){

        private var bmiTrack: List<TrackModel>? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.bar_graph_candle, parent,false)
            return TrackHolder(view)
        }

        override fun onBindViewHolder(holder: TrackHolder, position: Int) {
            val bmi = bmiTrack?.get(position)
            Log.d("lol", "onBindViewHolder: POSITION $position")

            holder.bmi.text = roundToTwoDecimals(bmi!!.bmi)+ " BMI"

            //get the date in the format of "31st"

            val originalDate = bmi.date
            val dateFormat = SimpleDateFormat("d'${getDayOfMonthSuffix(originalDate!!)}'", Locale.getDefault())
            val formattedDate = dateFormat.format(originalDate)


            //(bmi.bmi *3).toInt() *

                val relativeParams = holder.candle.layoutParams
            var height = Math.pow(bmi.bmi , 1.5).toInt()

            if(height > 200){
                height = 200
            }
                relativeParams.height = height
                barColor(bmi.bmi, holder)

            holder.candle.setOnClickListener {//on click listenert to make the the point on the top of the candle visible after the user has clicked on the candle
                val shouldShow = holder.bmiPointer.visibility != View.VISIBLE
                holder.bmiPointer.visibility = if (shouldShow) View.VISIBLE else View.INVISIBLE
                holder.bmi.visibility = if (shouldShow) View.VISIBLE else View.INVISIBLE
            }
            val currentDate = Date()//gets the current date
            val cal = Calendar.getInstance()
            cal.time = currentDate
            cal.add(Calendar.DATE, -1)
            val yesterday = cal.time //gets the date of yesterday
            if (isSameDay(bmi.date, currentDate)) {
                holder.date.text = "Today"

            }else if (isSameDay(bmi.date, yesterday)) {
                holder.date.text = "Yesterday"
            } else {
                holder.date.text = formattedDate
            }


            if (position >  0) {
                val previousItemDate = bmiTrack!![position -  1].date
                val currentItemDate = bmi.date
                val calendar = Calendar.getInstance()
                calendar.time = previousItemDate
                val previousMonth = calendar.get(Calendar.MONTH)
                calendar.time = currentItemDate
                val currentMonth = calendar.get(Calendar.MONTH)

                if (previousMonth != currentMonth) {
                    // The current item starts a new month, make the monthHeader view visible
                    holder.view.visibility = View.VISIBLE
                } else {
                    holder.view.visibility = View.GONE
                }
            }
            if(position == 0) {
                val params: MarginLayoutParams = holder.bar.layoutParams as MarginLayoutParams
                params.rightMargin = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    60f,
                    holder.bar.resources.displayMetrics
                ).toInt()
                holder.bar.layoutParams = params
                }
            else if(position == bmiTrack!!.size-1){
                val params: MarginLayoutParams = holder.bar.layoutParams as MarginLayoutParams
                params.leftMargin = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    30f,
                    holder.bar.resources.displayMetrics
                ).toInt()
                holder.bar.layoutParams = params
            }
            val params: MarginLayoutParams = holder.bar.layoutParams as MarginLayoutParams

            // Reset margins for each item
            params.rightMargin = 0
            params.leftMargin = 0

            // Set margins for the first and last items
            if (position == 0) {
                params.rightMargin = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    60f,
                    holder.bar.resources.displayMetrics
                ).toInt()
            } else if (position == bmiTrack!!.size - 1) {
                params.leftMargin = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    30f,
                    holder.bar.resources.displayMetrics
                ).toInt()
            }

            holder.bar.layoutParams = params
            }


        override fun getItemCount(): Int {
            return bmiTrack?.size?: 0
        }

    fun isSameDay(date1: Date, date2: Date): Boolean {//method to check if it the same day of a candle and returns a boolean
        val cal1 = Calendar.getInstance()
        cal1.time = date1
        val cal2 = Calendar.getInstance()
        cal2.time = date2
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun getDayOfMonthSuffix(date: Date): String {
        val cal = Calendar.getInstance()
        cal.time = date
        val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
        return when {
            dayOfMonth in 11..13 -> "th"
            else -> when (dayOfMonth % 10) {
                1 -> "st"
                2 -> "nd"
                3 -> "rd"
                else -> "th"
            }
        }
    }

        fun setData( _bmiTrack: List<TrackModel>) {
            bmiTrack = _bmiTrack
            notifyDataSetChanged()
        }

        fun roundToTwoDecimals(number: Double): String {
            return "%.1f".format(number)
        }

    fun Int.dpToPx(): Int {
        val scale = context.resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }


    private fun barColor(bmi: Double, holder: TrackHolder){//dynamically changes the candle color of the candle if the users bmi is in a certain weight

            if(bmi >= 30.0){
              holder.candle.setBackgroundResource(R.drawable.bar_candle_red)
                holder.bmiPointer.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_red))
            }
            if(25.0 <= bmi && bmi <= 29.9){
                holder.candle.setBackgroundResource(R.drawable.bar_candle_orange)
                holder.bmiPointer.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_orange))

            }
            if(18.5 <= bmi && bmi <= 24.9){
                holder.candle.setBackgroundResource(R.drawable.bar_candle_green)
                holder.bmiPointer.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_green))
            }
            if(bmi < 18.5){
                holder.candle.setBackgroundResource(R.drawable.bar_candle_blue)
                holder.bmiPointer.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_blue))
            }

        }

    class TrackHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val bmiPointer: CardView = itemView.findViewById(R.id.topPointer)

            val candle: RelativeLayout = itemView.findViewById(R.id.candle)
            val date: TextView = itemView.findViewById(R.id.date)
            val bmi: TextView = itemView.findViewById(R.id.bmi_value)
            val view: View = itemView.findViewById(R.id.view)
            val bar: RelativeLayout = itemView.findViewById(R.id.graph_chart)


        }
    }
