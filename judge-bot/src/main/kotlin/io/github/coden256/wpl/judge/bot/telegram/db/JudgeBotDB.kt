package io.github.coden256.wpl.judge.bot.telegram.db

import io.github.coden256.telegram.db.BotDB
import io.github.coden256.telegram.db.db
import org.telegram.abilitybots.api.db.MapDBContext

const val DB = "DB_NAME"

class JudgeBotDB(filename: String) :
    MapDBContext(db(filename)), BotDB {

    private val ownerMessages
        get() = getMap<String, String>(DB)


}