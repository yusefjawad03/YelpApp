import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.yelpapp.R
import com.example.yelpapp.YelpBusiness
import com.squareup.picasso.Picasso

class YelpAdapter(val yelps: List<YelpBusiness>): RecyclerView.Adapter<YelpAdapter.ViewHolder>() {
    class ViewHolder(rootLayout: View): RecyclerView.ViewHolder(rootLayout) {
        val restaurantText: TextView=rootLayout.findViewById(R.id.restaurant_name)
        val categoryText: TextView=rootLayout.findViewById(R.id.Category)
        val rating: TextView=rootLayout.findViewById(R.id.rating)
        val icon: ImageView=rootLayout.findViewById(R.id.icon)
        val cardView: CardView=rootLayout.findViewById(R.id.cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //create a new viewHolder
        //need to read the xml row
        Log.d("VH", "inside onCreateViewHolder")
        val layoutInflater: LayoutInflater= LayoutInflater.from(parent.context)
        val rootLayout: View=layoutInflater.inflate(R.layout.yelplayout, parent, false)
        val viewHolder=ViewHolder(rootLayout)
        return viewHolder
    }

    override fun getItemCount(): Int {
        Log.d("VH", "inside counting the size of the array")
        return yelps.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentYelpBusiness=yelps[position]
        holder.restaurantText.text= currentYelpBusiness.restaurantName
        holder.categoryText.text= currentYelpBusiness.category
        holder.rating.text=currentYelpBusiness.rating.toString()

        if (currentYelpBusiness.icon.isNotEmpty()) {
            Picasso.get().setIndicatorsEnabled(true)
            Picasso.get()
                .load(currentYelpBusiness.icon)
                .into(holder.icon)
        }
        Log.d("VH", "inside onBindViewHolder on position $position")
        val url=currentYelpBusiness.url
        val yelpListingContext=holder.cardView.context

        holder.cardView.setOnClickListener{
            val intent=Intent(Intent.ACTION_VIEW)
            intent.data= Uri.parse(url)
            yelpListingContext.startActivity(intent)
        }
    }

}