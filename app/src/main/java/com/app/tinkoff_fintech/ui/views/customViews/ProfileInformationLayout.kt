package com.app.tinkoff_fintech.ui.views.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.utils.DateFormatter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.profile_information_layout.view.*
import kotlinx.android.synthetic.main.profile_information_layout.view.date

class ProfileInformationLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attributeSet, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.profile_information_layout, this, true)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = MeasureSpec.getSize(widthMeasureSpec)
        var height = 0

        measureChildWithMargins(domain, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(delimiter1, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(photo, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(name, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(lastSeen, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(delimiter2, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(iconDate, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(date, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(iconCountry, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(country, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(iconCity, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(city, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(iconEducation, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(education, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(iconFollowers, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(followers, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(iconCareer, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(career, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(iconAbout, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(about, widthMeasureSpec, 0, heightMeasureSpec, height)

        height =
            domain.marginTop + domain.measuredHeight + domain.marginBottom +
                    delimiter1.marginTop + delimiter1.measuredHeight + delimiter1.marginBottom +
                    photo.marginTop + photo.measuredHeight + photo.marginBottom +
                    delimiter2.marginTop + delimiter2.measuredHeight + delimiter2.marginBottom


        if (date.text.isNotEmpty())
            height += date.marginTop + date.measuredHeight + date.marginBottom
        else iconDate.visibility = View.GONE

        if (country.text.isNotEmpty())
            height += country.marginTop + country.measuredHeight + country.marginBottom
        else iconCountry.visibility = View.GONE

        if (city.text.isNotEmpty())
            height += city.marginTop + city.measuredHeight + city.marginBottom
        else iconCity.visibility = View.GONE

        if (education.text.isNotEmpty())
            height += education.marginTop + education.measuredHeight + education.marginBottom
        else iconEducation.visibility = View.GONE

        if (followers.text.isNotEmpty())
            height += followers.marginTop + followers.measuredHeight + followers.marginBottom
        else iconFollowers.visibility = View.GONE

        if (career.text.isNotEmpty())
            height += career.marginTop + career.measuredHeight + career.marginBottom
        else iconCareer.visibility = View.GONE

        if (about.text.isNotEmpty())
            height += about.marginTop + about.measuredHeight + about.marginBottom
        else iconAbout.visibility = View.GONE

        setMeasuredDimension(desiredWidth, resolveSize(height, heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var currentTop = domain.marginTop
        var currentLeft = 0
        domain.layout(
            domain.marginStart,
            currentTop,
            domain.marginStart + domain.measuredWidth,
            currentTop + domain.measuredHeight
        )
        currentTop += domain.measuredHeight + domain.marginBottom + delimiter1.marginTop
        delimiter1.layout(
            delimiter1.marginStart,
            currentTop,
            delimiter1.marginStart + delimiter1.measuredWidth,
            currentTop + delimiter1.measuredHeight
        )
        currentTop += delimiter1.measuredHeight + delimiter1.marginBottom + photo.marginTop
        photo.layout(
            photo.marginStart,
            currentTop,
            photo.marginStart + photo.measuredWidth,
            currentTop + photo.measuredHeight
        )
        currentLeft = photo.marginStart + photo.measuredWidth + photo.marginEnd
        name.layout(
            currentLeft + name.marginStart,
            currentTop + photo.measuredHeight / 2 - name.marginBottom - name.measuredHeight,
            currentLeft + name.marginStart + name.measuredWidth,
            currentTop + photo.measuredHeight / 2 - name.marginBottom
        )
        lastSeen.layout(
            currentLeft + lastSeen.marginStart,
            currentTop + photo.measuredHeight / 2 + lastSeen.marginTop,
            currentLeft + lastSeen.marginStart + lastSeen.measuredWidth,
            currentTop + photo.measuredHeight / 2 + lastSeen.marginTop + lastSeen.measuredHeight
        )
        currentTop += photo.measuredHeight + photo.marginBottom + delimiter2.marginTop
        delimiter2.layout(
            delimiter2.marginStart,
            currentTop,
            delimiter2.marginStart + delimiter2.measuredWidth,
            currentTop + delimiter2.measuredHeight
        )
        currentTop += delimiter2.measuredHeight + delimiter2.marginBottom

        if (date.text.isNotEmpty()) {
            iconDate.layout(
                iconDate.marginStart,
                currentTop + iconDate.marginTop,
                iconDate.marginStart + iconDate.measuredWidth,
                currentTop + iconDate.marginTop + iconDate.measuredHeight
            )
            currentLeft =
                iconDate.marginStart + iconDate.measuredWidth + iconDate.marginEnd + date.marginStart
            date.layout(
                currentLeft,
                currentTop + date.marginTop,
                currentLeft + date.measuredWidth,
                currentTop + date.marginTop + date.measuredHeight
            )
            currentTop += date.marginTop + date.measuredHeight + date.marginBottom
        }

        if (country.text.isNotEmpty()) {
            iconCountry.layout(
                iconCountry.marginStart,
                currentTop + iconCountry.marginTop,
                iconCountry.marginStart + iconCountry.measuredWidth,
                currentTop + iconCountry.marginTop + iconCountry.measuredHeight
            )
            currentLeft =
                iconCountry.marginStart + iconCountry.measuredWidth + iconCountry.marginEnd + country.marginStart
            country.layout(
                currentLeft,
                currentTop + country.marginTop,
                currentLeft + country.measuredWidth,
                currentTop + country.marginTop + country.measuredHeight
            )
            currentTop += country.marginTop + country.measuredHeight + country.marginBottom
        }

        if (city.text.isNotEmpty()) {
            iconCity.layout(
                iconCity.marginStart,
                currentTop + iconCity.marginTop,
                iconCity.marginStart + iconCity.measuredWidth,
                currentTop + iconCity.marginTop + iconCity.measuredHeight
            )
            currentLeft =
                iconCity.marginStart + iconCity.measuredWidth + iconCity.marginEnd + city.marginStart
            city.layout(
                currentLeft,
                currentTop + city.marginTop,
                currentLeft + city.measuredWidth,
                currentTop + city.marginTop + city.measuredHeight
            )
            currentTop += city.marginTop + city.measuredHeight + city.marginBottom
        }

        if (education.text.isNotEmpty()) {
            iconEducation.layout(
                iconEducation.marginStart,
                currentTop + iconEducation.marginTop,
                iconEducation.marginStart + iconEducation.measuredWidth,
                currentTop + iconEducation.marginTop + iconEducation.measuredHeight
            )
            currentLeft =
                iconEducation.marginStart + iconEducation.measuredWidth + iconEducation.marginEnd + education.marginStart
            education.layout(
                currentLeft,
                currentTop + education.marginTop,
                currentLeft + education.measuredWidth,
                currentTop + education.marginTop + education.measuredHeight
            )
            currentTop += education.marginTop + education.measuredHeight + education.marginBottom
        }

        if (followers.text.isNotEmpty()) {
            iconFollowers.layout(
                iconFollowers.marginStart,
                currentTop + iconFollowers.marginTop,
                iconFollowers.marginStart + iconFollowers.measuredWidth,
                currentTop + iconFollowers.marginTop + iconFollowers.measuredHeight
            )
            currentLeft =
                iconFollowers.marginStart + iconFollowers.measuredWidth + iconFollowers.marginEnd + followers.marginStart
            followers.layout(
                currentLeft,
                currentTop + followers.marginTop,
                currentLeft + followers.measuredWidth,
                currentTop + followers.marginTop + followers.measuredHeight
            )
            currentTop += followers.marginTop + followers.measuredHeight + followers.marginBottom
        }

        if (career.text.isNotEmpty()) {
            iconCareer.layout(
                iconCareer.marginStart,
                currentTop + iconCareer.marginTop,
                iconCareer.marginStart + iconCareer.measuredWidth,
                currentTop + iconCareer.marginTop + iconCareer.measuredHeight
            )
            currentLeft =
                iconCareer.marginStart + iconCareer.measuredWidth + iconCareer.marginEnd + career.marginStart
            career.layout(
                currentLeft,
                currentTop + career.marginTop,
                currentLeft + career.measuredWidth,
                currentTop + career.marginTop + career.measuredHeight
            )
            currentTop += career.marginTop + career.measuredHeight + career.marginBottom
        }

        if (about.text.isNotEmpty()) {
            iconAbout.layout(
                iconAbout.marginStart,
                currentTop + iconAbout.marginTop,
                iconAbout.marginStart + iconAbout.measuredWidth,
                currentTop + iconAbout.marginTop + iconAbout.measuredHeight
            )
            currentLeft =
                iconAbout.marginStart + iconAbout.measuredWidth + iconAbout.marginEnd + about.marginStart
            about.layout(
                currentLeft,
                currentTop + about.marginTop,
                currentLeft + about.measuredWidth,
                currentTop + about.marginTop + about.measuredHeight
            )
        }

    }

    override fun generateLayoutParams(attrs: AttributeSet?) = MarginLayoutParams(context, attrs)

    override fun generateDefaultLayoutParams() = MarginLayoutParams(
        LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT
    )

    fun setPhoto(photoUrl: String) {
        Glide.with(context)
            .load(photoUrl)
            .into(photo)
    }

    fun setLastSeen(long: Long) {
        this.lastSeen.text = DateFormatter.lastSeen(long)
    }

    fun setDate(date: String) {
        this.date.text = context.getString(R.string.profileDate, DateFormatter.datePerson(date))
    }

    fun setDomain(domain: String) { this.domain.text = domain }

    fun setName(firstName: String, lastName: String) { this.name.text = context.getString(R.string.personName, firstName, lastName) }

    fun setCountry(country: String) { this.country.text = country }

    fun setCity(city: String) { this.city.text = city }

    fun setEducation(education: String) { this.education.text = education }

    fun setFollowers(followers: Int) { this.followers.text = if (followers > 0) context.resources.getQuantityString(R.plurals.followersCount, followers, followers) else ""}

    fun setCareer(career: String) { this.career.text = career }

    fun setAbout(about: String) { this.about.text = about }
}