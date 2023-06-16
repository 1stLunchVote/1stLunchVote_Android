package com.jwd.lunchvote.core.test.base

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.test.util.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule

/*
    * BaseStateViewModel을 상속받은 ViewModel을 테스트할 때 사용하는 추상 클래스입니다.
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseStateViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK protected lateinit var savedStateHandle: SavedStateHandle

    protected abstract val initialState : Parcelable?

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)

        every { savedStateHandle.get<Parcelable>("viewState") } returns initialState

        initSetup()
    }

    abstract fun initSetup()
}