package io.github.coden.impulse.judge.telegram

import io.github.coden.telegram.db.BotDB
import org.mapdb.DBMaker
import org.telegram.abilitybots.api.db.MapDBContext

open class JudgeDB: MapDBContext(DBMaker.memoryDB().make()), BotDB {
}