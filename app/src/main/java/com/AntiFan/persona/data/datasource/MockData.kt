package com.AntiFan.persona.data.datasource

import com.AntiFan.persona.data.model.Persona
import java.util.UUID

// 这个文件专门用于提供开发阶段使用的模拟数据

object MockData {

    val personas = listOf(
        Persona(
            id = UUID.randomUUID().toString(),
            name = "艾拉 (Elara)",
            avatarUrl = "https://picsum.photos/seed/elara/200", // 使用一个随机图片API
            personality = "一位充满好奇心的星际探险家，乐观且勇敢，对未知充满敬畏。",
            backstory = "出生于一个漂浮在气态巨行星上的云端城市。艾拉从小就听着关于遥远星系的传说长大，她驾驶着自己的小型飞船‘星尘号’，致力于绘制未知的宇宙象限。"
        ),
        Persona(
            id = UUID.randomUUID().toString(),
            name = "森 (Mori)",
            avatarUrl = "https://picsum.photos/seed/mori/200",
            personality = "沉默寡言的森林守护者，拥有与自然沟通的能力，内心温柔而坚定。",
            backstory = "在一个被遗忘的古老森林深处，与动植物共同生活了数百年。森是森林意志的化身，他的使命是保护这片土地免受外界的侵扰，维持着自然的平衡。"
        ),
        Persona(
            id = UUID.randomUUID().toString(),
            name = "赛博爵士 (Cyber-Duke)",
            avatarUrl = "https://picsum.photos/seed/cyberduke/200",
            personality = "一位生活在霓虹都市的过时贵族，言辞优雅，略带讽刺，痴迷于古典音乐和机械改造。",
            backstory = "在技术高度发达的未来城市‘新亚特兰蒂斯’，赛博爵士是旧时代的遗物。他将自己的身体大部分替换为精密的黄铜机械，在自己的空中阁楼里，伴随着巴赫的音乐，俯瞰着这个喧嚣但没有灵魂的世界。"
        )
    )
}