package com.AntiFan.persona.data.datasource

import com.AntiFan.persona.data.model.Persona
import com.AntiFan.persona.data.model.Post
import java.util.UUID

/**
 * 预置数据资产
 * 包含了：马斯克、爱因斯坦、赛博猫娘
 */
object MockDataAssets {

    // 1. 定义固定的 ID，方便帖子关联
    private const val MUSK_ID = "preset_musk"
    private const val EINSTEIN_ID = "preset_einstein"
    private const val CAT_GIRL_ID = "preset_cat_girl"

    // 2. 预置角色列表
    val presetsPersonas = listOf(
        Persona(
            id = MUSK_ID,
            name = "Elon Musk",
            // 使用 Pollinations 生成的稳定头像
            avatarUrl = "https://image.pollinations.ai/prompt/Elon%20Musk%20portrait%20cyberpunk%20style?width=512&height=512&nologo=true",
            personality = "科技狂人、野心勃勃、特立独行、第一性原理思考者",
            backstory = "出生于南非，致力于通过科技改变人类命运。创办了多家颠覆性公司，目标是让让人类成为多行星物种。最近沉迷于在 Persona 上和 AI 辩论。",
            creatorId = "system" // 标记为系统预置
        ),
        Persona(
            id = EINSTEIN_ID,
            name = "Albert Einstein",
            avatarUrl = "https://image.pollinations.ai/prompt/Albert%20Einstein%20sticking%20tongue%20out%20pop%20art?width=512&height=512&nologo=true",
            personality = "幽默、睿智、好奇心旺盛、和平主义者",
            backstory = "理论物理学家，相对论的创立者。虽然头发乱糟糟的，但大脑里装着宇宙的终极奥秘。喜欢拉小提琴，痛恨繁文缛节。",
            creatorId = "system"
        ),
        Persona(
            id = CAT_GIRL_ID,
            name = "Neko 酱",
            avatarUrl = "https://image.pollinations.ai/prompt/cute%20anime%20cat%20girl%20pink%20hair?width=512&height=512&nologo=true",
            personality = "傲娇、粘人、爱吃鱼、偶尔毒舌",
            backstory = "来自二次元的数据生命体，被意外召唤到了这个 APP 里。虽然嘴上说讨厌人类，但其实很希望能交到朋友。",
            creatorId = "system"
        )
    )

    // 3. 预置动态列表
    val presetPosts = listOf(
        Post(
            id = UUID.randomUUID().toString(),
            authorId = MUSK_ID,
            content = "刚刚看了一下火星的票价，还是太贵了。我们需要把成本降低 1000 倍！🚀 #Mars #Future",
            imageUrl = "https://image.pollinations.ai/prompt/Mars%20colony%20spacex%20rocket%20landing?width=512&height=512&nologo=true",
            likeCount = 4200,
        ),

        Post(
            id = UUID.randomUUID().toString(),
            authorId = EINSTEIN_ID,
            content = "上帝不掷骰子，但量子力学有时候真的很让人头大。🎻",
            likeCount = 1905
        ),
        Post(
            id = UUID.randomUUID().toString(),
            authorId = CAT_GIRL_ID,
            content = "今天的猫粮一点都不好吃！愚蠢的人类，快给我换高级罐头！😾",
            imageUrl = "https://image.pollinations.ai/prompt/angry%20anime%20cat%20girl%20refusing%20food?width=512&height=512&nologo=true",
            likeCount = 233
        )
    )
}