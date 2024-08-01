package com.example.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.networking.ForecastDay
import com.squareup.picasso.Picasso

class DaysForecastAdapter(private val forecastDayList: List<ForecastDay>) :
    RecyclerView.Adapter<DaysForecastAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily_forecast, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val forecastDay = forecastDayList[position]
        holder.bind(forecastDay)
    }

    override fun getItemCount(): Int {
        return forecastDayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val maxTempTextView: TextView = itemView.findViewById(R.id.maxTempTextView)
        private val minTempTextView: TextView = itemView.findViewById(R.id.minTempTextView)
        private val conditionImageView: ImageView = itemView.findViewById(R.id.conditionImageView)

        fun bind(forecastDay: ForecastDay) {
            dateTextView.text = forecastDay.date
            maxTempTextView.text = "${forecastDay.day.maxtemp_c}°C"
            minTempTextView.text = "${forecastDay.day.mintemp_c}°C"
            Picasso.get().load("https:${forecastDay.day.condition.icon}").into(conditionImageView)
        }
    }
}
