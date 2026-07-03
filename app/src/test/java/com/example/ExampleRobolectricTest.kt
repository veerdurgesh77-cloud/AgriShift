package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.getMatchingRecommendation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("AgriShift", appName)
  }

  @Test
  fun `test sugarcane and black clay matching`() {
    val recommendation = getMatchingRecommendation("Sugarcane", "Black Clay")
    assertNotNull(recommendation)
    assertEquals("Tractor", recommendation.targetCategory)
    assertEquals("Heavy Duty Tractor (55HP+)", recommendation.machineryType)
    assertEquals("Deep Subsoiler (2-tyne / 3-tyne)", recommendation.attachmentSize)
  }

  @Test
  fun `test wheat and alluvial matching`() {
    val recommendation = getMatchingRecommendation("Wheat", "Alluvial")
    assertNotNull(recommendation)
    assertEquals("Combine Harvester", recommendation.targetCategory)
    assertEquals("14-feet Cutter Bar Harvester", recommendation.attachmentSize)
  }

  @Test
  fun `test rice and clay soil matching`() {
    val recommendation = getMatchingRecommendation("Paddy/Rice", "Clayey / Black")
    assertNotNull(recommendation)
    assertEquals("Tractor", recommendation.targetCategory)
    assertEquals("Cage Wheels (dual-fitment) + Rotavator", recommendation.attachmentSize)
  }

  @Test
  fun `test cotton and sandy soil matching`() {
    val recommendation = getMatchingRecommendation("Cotton", "Sandy")
    assertNotNull(recommendation)
    assertEquals("Seed Drill", recommendation.targetCategory)
    assertEquals("Tractor (45HP)", recommendation.machineryType)
  }
}

