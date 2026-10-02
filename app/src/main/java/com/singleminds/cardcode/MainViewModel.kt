package com.singleminds.cardcode

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

enum class ScreenState { Picker, Form, Studio }
enum class Template { Wifi, Link, Contact }

@Parcelize
data class AppState(
    val screen: ScreenState = ScreenState.Picker,
    val template: Template = Template.Wifi,
    
    val wifiSsid: String = "",
    val wifiPass: String = "",
    val wifiSecurity: String = "WPA",
    val linkUrl: String = "",
    val linkLabel: String = "",
    val contactName: String = "",
    val contactPhone: String = "",
    val contactEmail: String = "",

    val theme: CardTheme = CardTheme.Ink,
    val headline: String = "",
    val subtitle: String = "",
    val showPasswordOnCard: Boolean = false
) : Parcelable

class MainViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _state = MutableStateFlow(savedStateHandle.get<AppState>("app_state") ?: AppState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.collect { savedStateHandle["app_state"] = it }
        }
    }

    fun updateState(transform: (AppState) -> AppState) {
        _state.update(transform)
    }

    fun selectTemplate(template: Template) {
        val defaultHeadline = when (template) {
            Template.Wifi -> "Scan to join our Wi-Fi"
            Template.Link -> "Scan to open"
            Template.Contact -> "Scan to save my contact"
        }
        _state.update { it.copy(
            template = template, 
            screen = ScreenState.Form,
            headline = defaultHeadline
        ) }
    }

    fun getPayload(): String {
        val s = _state.value
        return when (s.template) {
            Template.Wifi -> PayloadFormatter.formatWifi(s.wifiSsid, s.wifiPass, s.wifiSecurity)
            Template.Link -> PayloadFormatter.formatUrl(s.linkUrl)
            Template.Contact -> PayloadFormatter.formatContact(s.contactName, s.contactPhone, s.contactEmail)
        }
    }
}
