package com.app.tinkoff_fintech.recycler.holders

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.network.models.news.Career
import com.app.tinkoff_fintech.network.models.news.ProfileInformation
import kotlinx.android.synthetic.main.view_holder_profile.view.*

class InformationViewHolder private constructor(view: View) : BaseViewHolder(view) {

    companion object {
        fun create(parent: ViewGroup): InformationViewHolder {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_holder_profile, parent, false)
            return InformationViewHolder(view)
        }
    }

    fun bind(profileInformation: ProfileInformation?) {
        if (profileInformation == null)
            return
        itemView.profileInformationLayout.visibility = VISIBLE
        with(itemView.profileInformationLayout) {
            setDomain(profileInformation.domain)
            setPhoto(profileInformation.photo_max)
            setName(profileInformation.first_name, profileInformation.last_name)
            setLastSeen(profileInformation.last_seen.time * 1000)
            setDate(profileInformation.bdate)
            setCountry(profileInformation.country.title)
            setCity(profileInformation.city.title)
            setEducation(profileInformation.university_name)
            setFollowers(profileInformation.followers_count)
            setAbout(profileInformation.about)
            setCareer(extractCareer(profileInformation.career))
        }
    }

    private fun extractCareer(list: List<Career>) =
        if (list.isEmpty()) ""
        else {
            val group = list[0].group_name
            val city = list[0].city_name
            val date = if (list[0].until == 0)
                "c " + list[0].from + " г."
            else (list[0].from.toString() + " - " + list[0].until)
            val position = list[0].position
            "$group, $city, $date, $position"
        }
}