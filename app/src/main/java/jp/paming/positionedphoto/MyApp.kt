package jp.paming.positionedphoto

import android.app.Application

class MyApp: Application() {
    val photoRepository:PhotoRepository
        get() = PhotoRepositoryImpl(this)
    val orientationService:OrientationService
        get() = OrientationServiceImpl(this)
}