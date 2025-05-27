package com.assistancefin.tpaofinassistance.di

import android.content.Context
import androidx.room.Room
import com.assistancefin.tpaofinassistance.data.db.AppDatabase
import com.assistancefin.tpaofinassistance.data.db.TransactionDao
import com.assistancefin.tpaofinassistance.data.repository.Repository
import com.assistancefin.tpaofinassistance.data.repository.RepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        )
            .fallbackToDestructiveMigration()  // або власна міграція
            .build()
    }


    @Provides
    fun provideTransactionDao(db: AppDatabase): TransactionDao {
        return db.getTransactionDao()
    }

    @Provides
    @Singleton
    fun provideRepository(
        transactionDao: TransactionDao
    ): Repository {
        return RepositoryImpl(transactionDao)
    }

}