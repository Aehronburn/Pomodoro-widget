package it.unipd.dei.esp2023

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _newSessionName = MutableLiveData<String>("")
    val newSessionName : LiveData<String>
        get() = _newSessionName

    fun setNewSessionName(name: String) {
        _newSessionName.value = name
    }
}