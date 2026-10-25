package com.example.moonday.data.repository

import com.example.moonday.data.api.FreeAstroApiService
import com.example.moonday.data.model.MoonMonthResponse
import com.example.moonday.data.model.MoonPhaseResponse
import com.example.moonday.data.model.MoonVisualInfo
import com.example.moonday.data.model.PhaseInfo
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MoonRepositoryTest {

    private class FakeApiService(
        private val shouldReturnError: Boolean = false
    ) : FreeAstroApiService {
        override suspend fun getMoonPhase(
            date: String?,
            includeVisuals: Boolean,
            includeZodiac: Boolean,
            includeSpecial: Boolean,
            includeTraditionalMoon: Boolean,
            includeInterpretation: Boolean
        ): MoonPhaseResponse {
            if (shouldReturnError) {
                throw RuntimeException("API Error")
            }
            return MoonPhaseResponse(
                timestamp = "2026-04-15T12:00:00Z",
                phase = PhaseInfo(
                    name = "Full Moon",
                    illumination = 0.99,
                    isWaxing = true
                ),
                moonVisual = MoonVisualInfo(
                    type = "svg",
                    svg = "<svg><circle r='10'/></svg>"
                )
            )
        }

        override suspend fun getMoonMonth(
            year: Int,
            month: Int,
            includeVisuals: Boolean,
            includeZodiac: Boolean,
            includeTraditionalMoon: Boolean
        ): MoonMonthResponse {
            if (shouldReturnError) {
                throw RuntimeException("API Error")
            }
            return MoonMonthResponse(
                year = year,
                month = month,
                days = emptyList()
            )
        }
    }

    @Test
    fun `getMoonPhase returns success result when api succeeds`() = runTest {
        val fakeApi = FakeApiService(shouldReturnError = false)
        val repository = MoonRepositoryImpl(fakeApi, useAsyncHttpClient = false)

        val result = repository.getMoonPhase("2026-04-15")

        assertTrue(result.isSuccess)
        val response = result.getOrNull()
        assertEquals("Full Moon", response?.phase?.name)
        assertEquals("<svg><circle r='10'/></svg>", response?.moonVisual?.svg)
    }

    @Test
    fun `getMoonPhase returns failure result when api throws exception`() = runTest {
        val fakeApi = FakeApiService(shouldReturnError = true)
        val repository = MoonRepositoryImpl(fakeApi, useAsyncHttpClient = false)

        val result = repository.getMoonPhase("2026-04-15")

        assertTrue(result.isFailure)
        assertEquals("API Error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getMoonMonth returns success result when api succeeds`() = runTest {
        val fakeApi = FakeApiService(shouldReturnError = false)
        val repository = MoonRepositoryImpl(fakeApi, useAsyncHttpClient = false)

        val result = repository.getMoonMonth(2026, 4)

        assertTrue(result.isSuccess)
        val response = result.getOrNull()
        assertEquals(2026, response?.year)
        assertEquals(4, response?.month)
    }
}
