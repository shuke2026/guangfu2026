package com.pv.sales.ui.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pv.sales.data.SolarDatabase
import com.pv.sales.data.database.AppDatabase
import com.pv.sales.data.entity.CalculationRecordEntity
import com.pv.sales.data.enums.*
import com.pv.sales.engine.CalculationEngine
import com.pv.sales.model.CalculationResult
import com.pv.sales.util.PdfGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isProMode: Boolean = false,
    val roofArea: String = "",
    val selectedScene: InstallScene = InstallScene.RESIDENTIAL,
    val selectedProvince: String = "",
    val selectedCity: String = "",
    val provinceExpanded: Boolean = false,
    val cityExpanded: Boolean = false,
    // 专业参数
    val selectedIndustry: IndustryType? = null,
    val selectedElectricityType: ElectricityType = ElectricityType.RESIDENTIAL,
    val selectedGenerationMode: GenerationMode = GenerationMode.SELF_USE,
    val selectedPanelPower: PanelPower = PanelPower.W550,
    val industryExpanded: Boolean = false,
    val panelPowerExpanded: Boolean = false,
    val peakPrice: String = "",
    val flatPrice: String = "",
    val valleyPrice: String = "",
    val tiltAngle: String = "25",
    val systemEfficiency: String = "82",
    // 结果
    val result: CalculationResult? = null
)

class HomeViewModel(private val database: AppDatabase) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun updateProMode(isPro: Boolean) {
        _uiState.value = _uiState.value.copy(isProMode = isPro)
    }

    fun updateRoofArea(value: String) {
        _uiState.value = _uiState.value.copy(roofArea = value)
    }

    fun updateScene(scene: InstallScene) {
        _uiState.value = _uiState.value.copy(selectedScene = scene)
    }

    fun updateProvince(province: String) {
        _uiState.value = _uiState.value.copy(selectedProvince = province, selectedCity = "")
    }

    fun updateCity(city: String) {
        val state = _uiState.value
        // 自动填充电价
        val prices = SolarDatabase.getElectricityPrice(state.selectedProvince, city, state.selectedElectricityType)
        _uiState.value = state.copy(
            selectedCity = city,
            peakPrice = prices?.peakPrice?.toString() ?: "",
            flatPrice = prices?.flatPrice?.toString() ?: "",
            valleyPrice = prices?.valleyPrice?.toString() ?: ""
        )
    }

    fun updateProvinceExpanded(expanded: Boolean) {
        _uiState.value = _uiState.value.copy(provinceExpanded = expanded)
    }

    fun updateCityExpanded(expanded: Boolean) {
        _uiState.value = _uiState.value.copy(cityExpanded = expanded)
    }

    fun updateIndustry(industry: IndustryType) {
        _uiState.value = _uiState.value.copy(selectedIndustry = industry)
    }

    fun updateIndustryExpanded(expanded: Boolean) {
        _uiState.value = _uiState.value.copy(industryExpanded = expanded)
    }

    fun updateElectricityType(type: ElectricityType) {
        val state = _uiState.value
        // 重新填充电价
        val prices = SolarDatabase.getElectricityPrice(state.selectedProvince, state.selectedCity, type)
        _uiState.value = state.copy(
            selectedElectricityType = type,
            peakPrice = prices?.peakPrice?.toString() ?: "",
            flatPrice = prices?.flatPrice?.toString() ?: "",
            valleyPrice = prices?.valleyPrice?.toString() ?: ""
        )
    }

    fun updateGenerationMode(mode: GenerationMode) {
        _uiState.value = _uiState.value.copy(selectedGenerationMode = mode)
    }

    fun updatePanelPower(power: PanelPower) {
        _uiState.value = _uiState.value.copy(selectedPanelPower = power)
    }

    fun updatePanelPowerExpanded(expanded: Boolean) {
        _uiState.value = _uiState.value.copy(panelPowerExpanded = expanded)
    }

    fun updatePeakPrice(value: String) {
        _uiState.value = _uiState.value.copy(peakPrice = value)
    }

    fun updateFlatPrice(value: String) {
        _uiState.value = _uiState.value.copy(flatPrice = value)
    }

    fun updateValleyPrice(value: String) {
        _uiState.value = _uiState.value.copy(valleyPrice = value)
    }

    fun updateTiltAngle(value: String) {
        _uiState.value = _uiState.value.copy(tiltAngle = value)
    }

    fun updateSystemEfficiency(value: String) {
        _uiState.value = _uiState.value.copy(systemEfficiency = value)
    }

    fun calculate(): CalculationResult? {
        val state = _uiState.value
        val roofArea = state.roofArea.toDoubleOrNull() ?: return null

        return CalculationEngine.calculate(
            roofArea = roofArea,
            scene = state.selectedScene,
            province = state.selectedProvince,
            city = state.selectedCity,
            generationMode = state.selectedGenerationMode,
            electricityType = state.selectedElectricityType,
            industryType = if (state.selectedScene == InstallScene.FACTORY) state.selectedIndustry else null,
            panelPower = state.selectedPanelPower,
            systemEfficiency = (state.systemEfficiency.toDoubleOrNull() ?: 82.0) / 100.0,
            tiltAngle = state.tiltAngle.toDoubleOrNull() ?: 25.0,
            customPeakPrice = state.peakPrice.toDoubleOrNull(),
            customFlatPrice = state.flatPrice.toDoubleOrNull(),
            customValleyPrice = state.valleyPrice.toDoubleOrNull()
        )
    }

    fun saveCalculation(result: CalculationResult) {
        viewModelScope.launch {
            val record = CalculationRecordEntity(
                roofArea = result.roofArea,
                scene = result.scene,
                province = result.province,
                city = result.city,
                installedCapacity = result.installedCapacity,
                panelCount = result.panelCount,
                dailyGeneration = result.dailyGeneration,
                monthlyGeneration = result.monthlyGeneration,
                yearlyGeneration = result.yearlyGeneration,
                total25yGeneration = result.total25yGeneration,
                totalInvestment = result.totalInvestment,
                monthlySaving = result.monthlySaving,
                yearlySaving = result.yearlySaving,
                total25ySaving = result.total25ySaving,
                staticPayback = result.staticPayback,
                dynamicPayback = result.dynamicPayback,
                coverageRatio = result.coverageRatio,
                savingRate = result.savingRate,
                carbonReduction = result.carbonReduction,
                treesEquivalent = result.treesEquivalent,
                generationMode = result.generationMode,
                industryType = result.industryType,
                electricityType = result.electricityType,
                panelPower = result.panelPower
            )
            database.calculationRecordDao().insertRecord(record)
        }
    }

    fun generatePdf(context: Context, result: CalculationResult) {
        viewModelScope.launch {
            try {
                val file = PdfGenerator.generate(context, result)
                Toast.makeText(context, "PDF已生成: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                PdfGenerator.sharePdf(context, file)
            } catch (e: Exception) {
                Toast.makeText(context, "PDF生成失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    class Factory(private val database: AppDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(database) as T
        }
    }
}
