package io.github.coden256.wpl.judge.ruling


class Ruling(
    val action: Action,
    val path: String
) {

    val segments = path.split("/").filter { it.isNotEmpty() }

    init {
        if (!path.startsWith("/")) throw InvalidPathRootException("Invalid path $path, not starting with '/'")


        if (segments.isEmpty() || segments.size % 2 != 0) throw InvalidPathException("Invalid segments $segments, not a number of segments")
    }


    fun getSubRuling(path: String): Ruling {
        val ruling = Ruling(action, path)
        if (ruling.segments.size < segments.size) {
            throw InvalidSubRulingException("Invalid sub ruling $path, got ${ruling.segments.size}")
        }
        return ruling
    }

    override fun toString(): String {
        return path
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Ruling

        if (action != other.action) return false
        if (path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        var result = action.hashCode()
        result = 31 * result + path.hashCode()
        return result
    }
    }




class InvalidPathRootException(msg: String) : RuntimeException(msg)
class InvalidPathException(msg: String) : RuntimeException(msg)
class InvalidSubRulingException(msg: String) : RuntimeException(msg)

enum class Action(){
    BLOCK, ALLOW, FORCE
}