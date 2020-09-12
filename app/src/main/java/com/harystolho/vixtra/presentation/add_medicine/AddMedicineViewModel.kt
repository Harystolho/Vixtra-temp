package com.harystolho.vixtra.presentation.add_medicine

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harystolho.vixtra.core.entity.Medicine
import com.harystolho.vixtra.core.service.MedicineService
import kotlinx.coroutines.launch
import java.util.*
import kotlin.random.Random

class AddMedicineViewModel(
    private val medicineService: MedicineService
) : ViewModel() {

    private val model = AddMedicineModel()

    val action = MutableLiveData<AddMedicineAction>()

    val isLoading = MutableLiveData(false)
    val error = MutableLiveData<AddMedicineError>()

    fun updateModel(
        medicine: String? = null,
        description: String? = null,
        hourInterval: Int? = null,
        repetition: Int? = null,
        startTime: Calendar? = null
    ) {
        medicine?.let { model.medicine = it }
        description?.let { model.description = it }
        hourInterval?.let { model.hourInterval = it }
        repetition?.let { model.repetition = it }
        startTime?.let { model.startTime = it }
    }

    fun save() {
        if (!isValid()) return
        isLoading.value = true

        viewModelScope.launch {
            val medicine = createMedicine()
            medicineService.save(medicine)
            
            isLoading.value = false
        }
    }

    private fun createMedicine(): Medicine {
        return Medicine(
            Random.nextLong(),
            model.medicine!!,
            model.description,
            model.hourInterval ?: 8,
            model.startTime ?: Calendar.getInstance(),
            model.repetition
        )
    }

    private fun isValid(): Boolean {
        if (model.medicine.isNullOrEmpty()) {
            error.value = AddMedicineError.MEDICINE_FIELD
            return false
        }

        if (model.hourInterval == null || model.hourInterval!! < 0) {
            error.value = AddMedicineError.HOUR_FIELD
            return false
        }

        return true
    }

}

class AddMedicineModel(
    var medicine: String? = null,
    var description: String? = null,
    var hourInterval: Int? = null,
    var startTime: Calendar? = null,
    var repetition: Int = 1
)

sealed class AddMedicineAction {
    object Finish : AddMedicineAction()
}

enum class AddMedicineError {
    MEDICINE_FIELD, HOUR_FIELD
}