package com.example.data

import kotlinx.coroutines.flow.Flow

class TemplateRepository(private val templateDao: TemplateDao) {
    val allTemplates: Flow<List<CutTemplate>> = templateDao.getAllTemplates()

    suspend fun getTemplate(id: Int): CutTemplate? = templateDao.getTemplateById(id)

    suspend fun insert(template: CutTemplate) = templateDao.insertTemplate(template)

    suspend fun deleteById(id: Int) = templateDao.deleteTemplateById(id)
}
