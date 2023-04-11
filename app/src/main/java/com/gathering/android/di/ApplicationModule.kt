package com.gathering.android.di

import com.gathering.android.common.ImageLoader
import com.gathering.android.common.ImageLoaderImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    fun provideImageLoader(): ImageLoader {
        return ImageLoaderImp()
    }
}