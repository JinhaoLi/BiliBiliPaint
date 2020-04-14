

package com.jil.paintf.activity

import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.GridItem
import com.afollestad.materialdialogs.bottomsheets.gridItems
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.jil.paintf.R
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.service.AppPaintF
import com.jil.paintf.service.AppPaintF.Companion.instance
import kotlinx.android.synthetic.main.activity_theme.*

class ThemeActivity : AppCompatActivity() {
    class ThemeFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.pre_theme)
            findPreference<Preference>("dark_mode")!!.setOnPreferenceChangeListener { _, newValue ->
                AppCompatDelegate.setDefaultNightMode(newValue.toString().toInt())
                instance!!.setTheme(R.style.AppTheme_pink)
                true
            }

            val items = listOf(
                BackgroundGridItem(R.color.colorPrimary, "Primary"),
                BackgroundGridItem(R.color.pink, "pink"),
                BackgroundGridItem(R.color.green, "green"),
                BackgroundGridItem(R.color.purple, "purple"),
                BackgroundGridItem(R.color.tea, "tea"),
                BackgroundGridItem(R.color.walnut, "walnut"),
                BackgroundGridItem(R.color.seaPine, "seaPine"),
                BackgroundGridItem(R.color.seedling, "seedling")

            )

            findPreference<Preference>("theme")?.apply {
                summary =
                    items[PreferenceManager.getDefaultSharedPreferences(requireContext()).getInt(
                        "THEME",
                        0
                    )].title
                onPreferenceClickListener =
                    Preference.OnPreferenceClickListener {
                        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                            var action: (() -> Unit)? = null

                            title(R.string.change_theme)
                            gridItems(items) { _, index, item ->
                                it.summary = item.title
                                PreferenceManager.getDefaultSharedPreferences(requireContext())
                                    .apply { putInt("THEME", index)}

                                action = {
                                    AppPaintF.ActivityCollector.recreate()
                                }
                            }
                            onDismiss { action?.invoke() }
                            cornerRadius(16.0F)
                            negativeButton(android.R.string.cancel)
                            positiveButton(R.string.apply)
                            lifecycleOwner(this@ThemeFragment)
                        }
                        true
                    }
            }
        }

        private inline fun SharedPreferences.apply(modifier: SharedPreferences.Editor.() -> Unit) {
            edit().apply { modifier() }.run { apply() }
        }
    }

    private class BackgroundGridItem(@ColorRes private val color: Int, override val title: String) : GridItem {

        override fun populateIcon(imageView: ImageView) {
            imageView.apply {
                setBackgroundColor(ContextCompat.getColor(imageView.context, color))
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                ).apply {
                    marginEnd = 4.dp
                    marginStart = 4.dp
                }
            }
        }

        private val Int.dp: Int get() = toFloat().dp.toInt()

        private val Float.dp: Float
            get() = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                this,
                Resources.getSystem().displayMetrics
            )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtil.initTheme(this)
        setContentView(R.layout.activity_theme)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_theme, ThemeFragment()).commit()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()

        }
        return super.onOptionsItemSelected(item)
    }
}
