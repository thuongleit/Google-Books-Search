package me.thuongle.googlebookssearch.repository

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import me.thuongle.googlebookssearch.model.Result
import me.thuongle.googlebookssearch.util.InstantAppExecutors
import me.thuongle.googlebookssearch.util.mock
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.verify
import java.io.IOException

@RunWith(JUnit4::class)
class NetworkHandlerTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var networkHandler: NetworkHandler<Foo>
    private val testObj: Foo = Foo("test")

    @Test
    fun `return OK from network`() {
        networkHandler = object : NetworkHandler<Foo>(InstantAppExecutors()) {
            override fun execute(): Foo? {
                return testObj
            }
        }

        val observer = mock<Observer<Result<Foo>>>()
        val result = networkHandler.result
        result.observeForever(observer)
        verify(observer).onChanged(Result.success(testObj))
        assertThat(result.value, `is`(Result.success(testObj)))
    }

    @Test
    fun `return error from network`() {
        val error = IOException("Error")
        networkHandler = object : NetworkHandler<Foo>(InstantAppExecutors()) {
            override fun execute(): Foo? {
                throw error
            }
        }

        val observer = mock<Observer<Result<Foo>>>()
        val result = networkHandler.result
        result.observeForever(observer)
        verify(observer).onChanged(Result.error(error.message!!, error))
        assertThat(result.value, `is`(Result.error(error.message!!, error)))
    }

    private data class Foo(var value: String)
}