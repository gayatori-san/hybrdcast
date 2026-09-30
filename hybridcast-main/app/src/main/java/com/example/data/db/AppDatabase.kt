package com.example.data.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Entity(tableName = "cached_forecasts")
data class CachedForecastEntity(
    @PrimaryKey
    val id: String, // e.g., "pune_temp_2026-09-28T12:00"
    val cityId: String,
    val variableId: String,
    val timestamp: String,
    val epochMillis: Long,
    val ecmwfVal: Double,
    val gfsVal: Double,
    val iconVal: Double,
    val gemVal: Double,
    val ensembleMean: Double,
    val ensembleSpread: Double,
    val observed: Double?,
    val isLive: Boolean
)

@Dao
interface ForecastDao {
    @Query("SELECT * FROM cached_forecasts WHERE cityId = :cityId AND variableId = :variableId ORDER BY epochMillis ASC")
    suspend fun getCached(cityId: String, variableId: String): List<CachedForecastEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<CachedForecastEntity>)

    @Query("DELETE FROM cached_forecasts WHERE cityId = :cityId AND variableId = :variableId")
    suspend fun deleteForCityVar(cityId: String, variableId: String)
}

@Database(entities = [CachedForecastEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun forecastDao(): ForecastDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "atmoblend_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
