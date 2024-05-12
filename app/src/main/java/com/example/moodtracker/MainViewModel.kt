package com.example.moodtracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EntryState(
    var month: Int = 0,
    var day: Int = 0,
    var mood: Int = 0,
    var test: Int = 0


)

data class CountState(
    var happyCount: Int = 0,
    var contentCount: Int = 0,
    var okayCount: Int = 0,
    var moodyCount: Int = 0,
    var angryCount: Int = 0,
    var moodChart: List<Int> = emptyList(),
    var moodChartList: MutableList<ChartMonthItem> = emptyList<ChartMonthItem>().toMutableList()
)

data class OutputState(
    var recordList: List<EntryDay> = emptyList()
)

class TestViewModel(private val dao: EntryDayDao): ViewModel()
{
    private val _currentMonth = MutableStateFlow(todayInfo.month)
    var currentMonth = _currentMonth.asStateFlow()

    private val _state = MutableStateFlow(EntryState())
    var state = _state.asStateFlow()

    private val _outputState = MutableStateFlow(OutputState())
    var outputState = _outputState.asStateFlow()

    private val _countState = MutableStateFlow(CountState())
    var countState = _countState.asStateFlow()


    fun setState(month: Int, day: Int){
        state.value.month = month
        state.value.day = day
    }

    fun incrementMonth(){
        if (_currentMonth.value == 11) {
            _currentMonth.value = 0
        }
        else {
            _currentMonth.value += 1

        }
        getMonthRecords(_currentMonth.value)
    }

    fun decrementMonth(){
        if (_currentMonth.value == 0) {
            _currentMonth.value = 11
        }
        else {
            _currentMonth.value -= 1
        }
        getMonthRecords(_currentMonth.value)
    }

    fun insertEntry() {
        val month = _state.value.month
        val day = _state.value.day
        val mood = _state.value.mood
        val id = (month.toString()+  '_' + day.toString() + "_1")
        val entryDay = EntryDay(id = id, month = month, day = day, mood = mood)
        viewModelScope.launch { dao.upsertEntry(entryDay) }


    }

    fun getMonthRecords(month: Int) {
        viewModelScope.launch { dao.getMonthRecords(month).collect { records -> _outputState.value.recordList = records } }
    }

    fun deleteEntry(month: Int, day: Int) {
        viewModelScope.launch { dao.updateEntry(EntryDay(month.toString()+ '_' + day.toString() + "_0", month, day, 0)) }
        //getMonthRecords(_currentMonth.value)
    }

    fun getMood(){
        viewModelScope.launch { dao.getNumberOfMood(1).collect {num -> _countState.value.happyCount = num} }
        viewModelScope.launch { dao.getNumberOfMood(2).collect {num -> _countState.value.contentCount = num} }
        viewModelScope.launch { dao.getNumberOfMood(3).collect {num -> _countState.value.okayCount = num} }
        viewModelScope.launch { dao.getNumberOfMood(4).collect {num -> _countState.value.angryCount = num} }
        viewModelScope.launch { dao.getNumberOfMood(5).collect {num -> _countState.value.moodyCount = num} }
    }

    fun getMoodChart(month: Int){
        viewModelScope.launch { dao.getAverageMood(month).collect{ list -> _countState.value.moodChart = list} }
    }

    //In future use this code for LazyRow of MoodCharts for each month
    fun initMoodChartList() {
        for (i in 0..11) {
            _countState.value.moodChartList.add(ChartMonthItem(i, emptyList()))
        }
        for (i in 0..11) {
            viewModelScope.launch { dao.getAverageMood(i).collect{ list -> _countState.value.moodChartList[i].yValues = list} }
        }
    }


    //DEBUG
    fun debugDeleteAllRecords(){
        viewModelScope.launch { dao.deleteAllRecords() }
    }

    fun debugInitDatabase(){
        var limit: Int
        var month: Int
        var day: Int
        for (i in 0 until 12) {
            limit = if (i == 1){ 29 } else{ 32 }
            month = i
            for (j in 1 until limit) {
                day = j
                viewModelScope.launch { dao.upsertEntry(EntryDay((month.toString()+  '_' + day.toString() + "_0"), month, day, 0)) }
            }
        }
    }
    fun debugCheckDB(){
        if(viewModelScope.launch(Dispatchers.IO) { dao.getEntry(0,1).collect{ test -> _state.value.test = test } }.isCompleted){
            if(_state.value.test != 1){
                debugDeleteAllRecords()
                debugInitDatabase()
            }
        }
    }
}
