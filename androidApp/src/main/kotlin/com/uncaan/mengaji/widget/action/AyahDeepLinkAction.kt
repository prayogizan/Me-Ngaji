package com.uncaan.mengaji.widget.action

import androidx.glance.action.Action
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import com.uncaan.mengaji.MainActivity
import com.uncaan.mengaji.feature.widget.domain.model.DailyAyah
import com.uncaan.mengaji.navigation.DeepLinkConstants

/**
 * Builds a Glance [Action] that launches [MainActivity] with optional deep-link extras.
 *
 * If [DailyAyah.surahNumber] and [DailyAyah.ayahNumberInSurah] are valid positive integers,
 * they are attached as [ActionParameters] to navigate directly into the reader screen
 * (CMP-504). Otherwise, a parameterless activity launch action is returned as a fallback.
 *
 * @param ayah The [DailyAyah] model whose Surah and Ayah reference will be linked.
 * @return A configured Glance [Action] ready to be passed to `.clickable()`.
 * @see MainActivity
 * @see DailyAyah
 * @see DeepLinkConstants
 */
fun createAyahDeepLinkAction(ayah: DailyAyah): Action {
    return if (ayah.surahNumber > 0 && ayah.ayahNumberInSurah > 0) {
        val surahKey = ActionParameters.Key<Int>(DeepLinkConstants.EXTRA_SURAH_NUMBER)
        val ayahKey = ActionParameters.Key<Int>(DeepLinkConstants.EXTRA_AYAH_NUMBER)

        actionStartActivity<MainActivity>(
            actionParametersOf(
                surahKey to ayah.surahNumber,
                ayahKey to ayah.ayahNumberInSurah
            )
        )
    } else {
        actionStartActivity<MainActivity>()
    }
}

