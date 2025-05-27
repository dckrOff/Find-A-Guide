package uz.dckroff.findaguide.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uz.dckroff.findaguide.R

class OnboardingAdapter : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    private val titles = arrayOf(
        "Find Local Guides",
        "Book Experiences",
        "Explore with Confidence"
    )
    
    private val descriptions = arrayOf(
        "Discover experienced local guides who know the best spots and hidden gems.",
        "Book guided tours and unique experiences directly through the app.",
        "Enjoy your trip with trusted guides who speak your language."
    )
    
    private val images = intArrayOf(
        R.drawable.img1,
        R.drawable.img2,
        R.drawable.img3
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_onboarding, parent, false)
        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.titleText.text = titles[position]
        holder.descriptionText.text = descriptions[position]
        holder.imageView.setImageResource(images[position])
    }

    override fun getItemCount(): Int {
        return titles.size
    }

    class OnboardingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.tvTitle)
        val descriptionText: TextView = itemView.findViewById(R.id.tvDescription)
        val imageView: ImageView = itemView.findViewById(R.id.ivImage)
    }
} 