package com.jwd.lunchvote.core.common.base

interface Mapper<LEFT, RIGHT> {
    fun mapToRight(from: LEFT): RIGHT
}