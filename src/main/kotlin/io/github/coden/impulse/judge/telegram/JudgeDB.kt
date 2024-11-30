package io.github.coden.impulse.judge.telegram

import io.github.coden.telegram.db.BotDB
import io.github.coden.telegram.db.db
import org.telegram.abilitybots.api.db.MapDBContext

open class JudgeDB(filename: String) :
    MapDBContext(db(filename)), BotDB {

}