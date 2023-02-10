/*
 * xvii - messenger for vk
 * Copyright (C) 2021  TwoEightNine
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package global.msnthrp.xvii.data.dialogs

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface DialogsDao {

    @Query("SELECT * FROM dialogs WHERE :me = me ORDER BY isPinned DESC, timeStamp DESC")
    fun getDialogs(me: Int): Single<List<Dialog>>

    @Query("SELECT * FROM dialogs WHERE :peerId = peerId AND :me = me")
    fun getDialogs(me: Int, peerId: Int): Single<Dialog>

    @Query("SELECT * FROM dialogs WHERE peerId IN (:peerIds) AND :me = me")
    fun getDialogs(me: Int, peerIds: List<Int>): Single<List<Dialog>>

    @Query("SELECT peerId FROM dialogs WHERE isPinned = 1 AND :me = me")
    fun getPinned(me: Int): Single<List<Int>>

    @Query("SELECT peerId FROM dialogs WHERE isStarred = 1 AND :me = me")
    fun getStarred(me: Int): Single<List<Int>>

    @Query("SELECT * FROM dialogs WHERE isStarred = 1 AND :me = me ORDER BY isPinned DESC, timeStamp DESC")
    fun getStarredDialogs(me: Int): Single<List<Dialog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDialog(dialog: Dialog): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDialogs(vararg dialogs: Dialog): Completable

    @Delete
    fun removeDialog(dialog: Dialog): Single<Int>

    @Query("DELETE FROM dialogs where isPinned = 0 and alias = '' and :me = me")
    fun removeAll(me: Int): Completable

    fun getLargeListOfDialogs(me: Int, peerIds: List<Int>): Single<List<Dialog>> {
        return if (peerIds.size < 1000) {
            getDialogs(me, peerIds)
        } else {
            getDialogs(me).map { allDialogs ->
                allDialogs.filter { it.peerId in peerIds }
            }
        }
    }
}