package me.amryousef.converter.presentation

sealed class ViewState {
    object Loading: ViewState()
    data class Ready(val items: List<ViewStateItem>): ViewState()
    object Error : ViewState()
}