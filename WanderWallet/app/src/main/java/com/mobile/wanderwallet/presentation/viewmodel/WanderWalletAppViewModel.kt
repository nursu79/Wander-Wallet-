package com.mobile.wanderwallet.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.mobile.wanderwallet.data.repository.WanderWalletApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WanderWalletAppViewModel @Inject constructor(
    private val apiRepository: WanderWalletApiRepository
): ViewModel()