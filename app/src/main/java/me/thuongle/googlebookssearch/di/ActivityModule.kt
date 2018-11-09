package me.thuongle.googlebookssearch.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import me.thuongle.googlebookssearch.ui.search.SearchActivity

@Module(includes = [ViewModelModule::class])
abstract class ActivityModule {
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeSearchActivity(): SearchActivity
}