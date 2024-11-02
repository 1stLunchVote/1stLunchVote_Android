package kr.co.inbody.library.mapper

interface Mapper<LEFT, RIGHT> {
    fun mapToRight(from: LEFT): RIGHT
}