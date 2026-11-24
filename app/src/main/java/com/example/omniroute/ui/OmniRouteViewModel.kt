package com.example.omniroute.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omniroute.data.model.*
import com.example.omniroute.data.repository.OmniRouteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NavigationTab(val title: String) {
    DASHBOARD("Dashboard"),
    PROVIDERS("Providers"),
    COMBOS("Combos"),
    PLAYGROUND("Playground"),
    TRAFFIC("Inspector")
}

class OmniRouteViewModel(
    private val repository: OmniRouteRepository = OmniRouteRepository()
) : ViewModel() {

    private val _currentTab = MutableStateFlow(NavigationTab.DASHBOARD)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _providerSearchQuery = MutableStateFlow("")
    val providerSearchQuery: StateFlow<String> = _providerSearchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<ProviderCategory?>(null)
    val selectedCategory: StateFlow<ProviderCategory?> = _selectedCategory.asStateFlow()

    val providers = repository.providers
    val combos = repository.combos
    val activeCombo = repository.activeCombo
    val chatMessages = repository.chatMessages
    val telemetryLogs = repository.telemetryLogs
    val freeTierPools = repository.freeTierPools
    val simulate429OnPrimary = repository.simulate429OnPrimary
    val compressionEnabled = repository.compressionEnabled
    val isGenerating = repository.isGenerating

    val filteredProviders = combine(providers, _providerSearchQuery, _selectedCategory) { list, query, category ->
        list.filter { provider ->
            val matchesQuery = query.isBlank() ||
                    provider.name.contains(query, ignoreCase = true) ||
                    provider.models.any { it.name.contains(query, ignoreCase = true) }
            val matchesCategory = category == null || provider.category == category
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Dialog states
    private val _editingProvider = MutableStateFlow<Provider?>(null)
    val editingProvider: StateFlow<Provider?> = _editingProvider.asStateFlow()

    private val _isCreateComboOpen = MutableStateFlow(false)
    val isCreateComboOpen: StateFlow<Boolean> = _isCreateComboOpen.asStateFlow()

    private val _selectedTelemetry = MutableStateFlow<TelemetryLog?>(null)
    val selectedTelemetry: StateFlow<TelemetryLog?> = _selectedTelemetry.asStateFlow()

    private val _promptInput = MutableStateFlow("")
    val promptInput: StateFlow<String> = _promptInput.asStateFlow()

    fun setTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun setProviderSearch(query: String) {
        _providerSearchQuery.value = query
    }

    fun setCategoryFilter(category: ProviderCategory?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    fun openEditProvider(provider: Provider) {
        _editingProvider.value = provider
    }

    fun closeEditProvider() {
        _editingProvider.value = null
    }

    fun saveProviderKey(providerId: String, key: String) {
        repository.updateProviderApiKey(providerId, key)
        closeEditProvider()
    }

    fun testProvider(providerId: String) {
        repository.testProviderConnection(providerId)
    }

    fun setActiveCombo(combo: Combo) {
        repository.setActiveCombo(combo)
    }

    fun openCreateCombo() {
        _isCreateComboOpen.value = true
    }

    fun closeCreateCombo() {
        _isCreateComboOpen.value = false
    }

    fun createCombo(name: String, desc: String, strategy: ComboStrategy, targets: List<ComboTarget>) {
        repository.addCustomCombo(name, desc, strategy, targets)
        closeCreateCombo()
    }

    fun toggleSimulate429() {
        repository.toggleSimulate429()
    }

    fun toggleCompression() {
        repository.setCompressionEnabled(!compressionEnabled.value)
    }

    fun resetCircuitBreakers() {
        repository.resetCircuitBreakers()
    }

    fun setPromptInput(text: String) {
        _promptInput.value = text
    }

    fun sendPrompt() {
        val text = _promptInput.value.trim()
        if (text.isNotBlank() && !isGenerating.value) {
            _promptInput.value = ""
            viewModelScope.launch {
                repository.executeChat(text)
            }
        }
    }

    fun clearChat() {
        repository.clearChat()
    }

    fun selectTelemetry(log: TelemetryLog?) {
        _selectedTelemetry.value = log
    }
}
