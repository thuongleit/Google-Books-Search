package me.thuongle.googlebookssearch.api

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GoogleVolumeResponseTest {

    @Test
    fun `create empty instance`() {
        val emptyVolume = GoogleVolumeResponse.createEmpty()
        assertThat(emptyVolume, `is`(notNullValue()))
        assertThat(emptyVolume.kind, `is`(""))
        assertThat(emptyVolume.items.isEmpty(), `is`(true))
        assertThat(emptyVolume.totalItems, `is`(0))
    }
}