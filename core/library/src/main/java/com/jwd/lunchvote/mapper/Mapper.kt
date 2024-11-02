package com.jwd.lunchvote.mapper

interface Mapper<LEFT, RIGHT> {
    fun mapToRight(from: LEFT): RIGHT
}