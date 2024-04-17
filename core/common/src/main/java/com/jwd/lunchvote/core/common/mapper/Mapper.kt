package com.jwd.lunchvote.core.common.mapper

interface Mapper<LEFT, RIGHT> {
    fun mapToRight(from: LEFT): RIGHT
}