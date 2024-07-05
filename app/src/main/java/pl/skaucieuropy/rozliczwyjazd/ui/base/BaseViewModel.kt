package pl.skaucieuropy.rozliczwyjazd.ui.base

import androidx.lifecycle.ViewModel
import pl.skaucieuropy.rozliczwyjazd.utils.SingleLiveEvent

/**
 * Base class for View Models to declare the common LiveData objects in one place
 */
abstract class BaseViewModel : ViewModel() {

    val navigationCommand: SingleLiveEvent<NavigationCommand> = SingleLiveEvent()
    val showErrorMessage: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBarInt: SingleLiveEvent<Int> = SingleLiveEvent()
    val showToast: SingleLiveEvent<String> = SingleLiveEvent()
    val showToastInt: SingleLiveEvent<Int> = SingleLiveEvent()
    val hideKeyboard: SingleLiveEvent<Boolean> = SingleLiveEvent()

    fun showToast(message: String) {
        showToast.value = message
    }

    fun showToast(messageResId: Int) {
        showToastInt.value = messageResId
    }
}