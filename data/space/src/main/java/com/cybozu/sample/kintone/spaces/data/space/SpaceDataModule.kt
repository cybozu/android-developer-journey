package com.cybozu.sample.kintone.spaces.data.space

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SpaceDataModule {
    @Binds
    abstract fun bindSpaceRepository(localSpaceRepository: LocalSpaceRepository): SpaceRepository
}
