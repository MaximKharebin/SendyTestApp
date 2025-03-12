package com.wynndie.sendytestapp.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.wynndie.sendytestapp.auth.domain.FormatPhone
import org.junit.Before
import org.junit.Test

class FormatPhoneTest {

    lateinit var formatPhone: FormatPhone

    @Before
    fun setUp() {
        formatPhone = FormatPhone()
    }

    @Test
    fun `insert value return formatted value`() {
        val entries = listOf(
            Pair("9091231212", "9091231212"),
            Pair("+79091231212", "9091231212"),
            Pair("89091231212", "9091231212"),
            Pair("+765654569091231212456564231564564", "9091231212"),
            Pair("", ""),
            Pair("+7", ""),
            Pair("+74546", ""),
            Pair("+754659", "9"),
            Pair("8", "")
        )

        entries.forEach {
            val newValue = formatPhone(it.first)
            assertThat(newValue).isEqualTo(it.second)
        }
    }

}