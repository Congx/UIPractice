package com.example.uipractice.bean

/**
 * @date 2019-12-29
 * @Author luffy
 * @description 公众号列表实体类
 */
data class PublishBean(
    val children: List<Any>,
    val courseId: Int,
    val id: Int,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val userControlSetTop: Boolean,
    val visible: Int
)