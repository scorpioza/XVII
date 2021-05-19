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

package global.msnthrp.xvii.data.journal

import global.msnthrp.xvii.core.journal.JournalDataSource
import global.msnthrp.xvii.core.journal.model.JournalEvent

class DbJournalDataSource(private val journalDao: JournalDao) : JournalDataSource {

    override fun addJournalEvent(journalEvent: JournalEvent) {
        journalDao.insertEvent(journalEvent.let(JournalEntity::from))
    }

    override fun getJournalEvents(): List<JournalEvent> =
            journalDao.getAllEvents().mapNotNull(JournalEntity::toJournalEvent)

    override fun clearAll() {
        clearAllExceptRecent(0L)
    }

    override fun clearAllExceptRecent(recentThresholdTimeStamp: Long) {
        journalDao.clearAllExceptRecent(recentThresholdTimeStamp)
    }
}