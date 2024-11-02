package com.jwd.lunchvote.mapper

interface BiMapper<LEFT, RIGHT> {
    fun mapToRight (from: LEFT): RIGHT
    fun mapToLeft (from: RIGHT): LEFT
}