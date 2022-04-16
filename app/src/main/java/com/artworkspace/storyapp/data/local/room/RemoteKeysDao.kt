package com.artworkspace.storyapp.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.artworkspace.storyapp.data.local.entity.RemoteKeys

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeys>)

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun getRemoteKeysId(id: String): RemoteKeys?

    @Query("DELETE FROM remote_keys")
    suspend fun deleteRemoteKeys()
}