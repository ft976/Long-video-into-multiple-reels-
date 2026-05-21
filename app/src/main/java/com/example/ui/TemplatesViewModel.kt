package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CutTemplate
import com.example.data.TemplateRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TemplatesViewModel(private val repository: TemplateRepository) : ViewModel() {

    val uiState: StateFlow<List<CutTemplate>> = repository.allTemplates
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveTemplate(template: CutTemplate) {
        viewModelScope.launch {
            repository.insert(template)
        }
    }

    fun deleteTemplate(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }
}

class TemplatesViewModelFactory(private val repository: TemplateRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TemplatesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TemplatesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
