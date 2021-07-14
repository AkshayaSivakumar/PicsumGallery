package com.experiment.android.picsumgallery.di.module

import com.experiment.android.picsumgallery.data.remote.PicsumApi
import com.experiment.android.picsumgallery.data.repo.DataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
internal object ViewModelModule {

    @Provides
    @ViewModelScoped
    fun provideDataRepository(picsumApi: PicsumApi) = DataRepository(picsumApi)
}