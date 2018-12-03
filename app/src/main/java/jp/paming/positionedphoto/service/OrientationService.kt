package jp.paming.positionedphoto.service

import android.content.Context
import android.content.res.Configuration

enum class Orientation{
    Portrait(),
    Landscape(),
}

interface OrientationService{
    fun orientation(): Orientation
}
class OrientationServiceImpl(val context: Context): OrientationService {
    override fun orientation(): Orientation {
        return when (context.resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> Orientation.Landscape
            else -> Orientation.Portrait
        }
    }
}