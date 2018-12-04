package jp.paming.positionedphoto

import android.app.Application
import jp.paming.positionedphoto.service.PhotoRepository
import jp.paming.positionedphoto.service.PhotoRepositoryImpl

class MyApp: Application() {
    // TODO UIテストする時にはDaggerでここを書き換えれば良いはず。
    val photoRepository: PhotoRepository
        get() = PhotoRepositoryImpl(this)
}