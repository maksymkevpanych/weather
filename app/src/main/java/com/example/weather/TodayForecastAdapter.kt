import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.networking.HourlyWeather
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TodayForecastAdapter(private val hourlyWeatherList: List<HourlyWeather>) :
    RecyclerView.Adapter<TodayForecastAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hourly_forecast, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hourlyWeather = hourlyWeatherList[position]
        holder.bind(hourlyWeather)
    }

    override fun getItemCount(): Int {
        return hourlyWeatherList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        private val tempTextView: TextView = itemView.findViewById(R.id.tempTextView)
        private val conditionImageView: ImageView = itemView.findViewById(R.id.conditionImageView)

        fun bind(hourlyWeather: HourlyWeather) {
            // Parse the time from the weather data
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val weatherTime = hourlyWeather.time // Assuming hourlyWeather.time is in "yyyy-MM-dd HH:mm" format

            // Convert time string to Date object
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(weatherTime)
            // Format date to show only hour and minute
            timeTextView.text = timeFormat.format(date ?: Date())

            tempTextView.text = "${hourlyWeather.temp_c}°C"
            Picasso.get().load("https:${hourlyWeather.condition.icon}").into(conditionImageView)
        }
    }
}
