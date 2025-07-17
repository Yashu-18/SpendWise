package com.example.spendwise.UI

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer


fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner) { value ->
        observer.onChanged(value)
        removeObservers(lifecycleOwner)
    }
}