package com.AntiFan.persona.ui.screens.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.AntiFan.persona.data.manager.UserManager
import com.AntiFan.persona.data.model.PostWithAuthor
import com.AntiFan.persona.data.repository.IPersonaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialViewModel @Inject constructor(
    private val repository: IPersonaRepository,
    private val userManager: UserManager
) : ViewModel() {

    private val _feed = MutableStateFlow<List<PostWithAuthor>>(emptyList())
    val feed: StateFlow<List<PostWithAuthor>> = _feed.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // 自动监听用户变化并刷新
        viewModelScope.launch {
            userManager.currentUserId.collectLatest { userId ->
                fetchFeedInternal(userId)
            }
        }
    }

    // ✅ 供 UI 调用：手动刷新 (自动获取当前用户)
    fun refresh() {
        val currentId = userManager.getCurrentUserId()
        fetchFeedInternal(currentId)
    }

    // 内部实际加载逻辑
    private fun fetchFeedInternal(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _feed.value = repository.getSocialFeed(userId)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleLike(item: PostWithAuthor) {
        val newIsLiked = !item.isLiked
        val newCount = if (newIsLiked) item.post.likeCount + 1 else item.post.likeCount - 1

        _feed.value = _feed.value.map { currentItem ->
            if (currentItem.post.id == item.post.id) {
                currentItem.copy(
                    isLiked = newIsLiked,
                    post = currentItem.post.copy(likeCount = newCount)
                )
            } else {
                currentItem
            }
        }

        val currentUserId = userManager.getCurrentUserId()
        viewModelScope.launch {
            repository.toggleLike(currentUserId, item.post.id, newIsLiked)
        }
    }

    fun toggleFollow(authorId: String, currentFollowStatus: Boolean) {
        val newStatus = !currentFollowStatus

        _feed.value = _feed.value.map { item ->
            if (item.post.authorId == authorId) {
                item.copy(authorIsFollowed = newStatus)
            } else {
                item
            }
        }

        val currentUserId = userManager.getCurrentUserId()
        viewModelScope.launch {
            repository.toggleFollow(currentUserId, authorId, newStatus)
        }
    }
}