package com.example.data

import com.example.model.OddsTrend
import com.example.model.PickOutcome
import com.example.model.SportCategory
import com.example.model.SportsMatch
import kotlin.math.roundToInt
import kotlin.random.Random

object MockMatchesProvider {

    private var replacementCounter = 100

    fun createInitialMatches(): List<SportsMatch> = listOf(
        // FOOTBALL (6 matches: 2 LIVE, 4 UPCOMING)
        SportsMatch(
            id = "fb_1",
            sport = SportCategory.FOOTBALL,
            league = "Metro Premier Cup",
            homeTeam = "Northbridge FC",
            awayTeam = "Harbor United",
            startTimeLabel = "LIVE 62'",
            kickoffCountdownSeconds = 0,
            isLive = true,
            elapsedSeconds = 18,
            totalSimulatedSeconds = 48,
            homeScore = 2,
            awayScore = 1,
            liveDetail = "2nd Half",
            homeOdds = 1.65,
            drawOdds = 3.55,
            awayOdds = 4.90,
        ),
        SportsMatch(
            id = "fb_2",
            sport = SportCategory.FOOTBALL,
            league = "Continental Division",
            homeTeam = "Apex Rovers",
            awayTeam = "Summit Athletic",
            startTimeLabel = "LIVE 34'",
            kickoffCountdownSeconds = 0,
            isLive = true,
            elapsedSeconds = 10,
            totalSimulatedSeconds = 45,
            homeScore = 0,
            awayScore = 0,
            liveDetail = "1st Half",
            homeOdds = 2.15,
            drawOdds = 3.10,
            awayOdds = 3.45,
        ),
        SportsMatch(
            id = "fb_3",
            sport = SportCategory.FOOTBALL,
            league = "Metro Premier Cup",
            homeTeam = "Eastgate City",
            awayTeam = "Silverpine FC",
            startTimeLabel = "In 18m",
            kickoffCountdownSeconds = 20,
            isLive = false,
            totalSimulatedSeconds = 42,
            homeOdds = 1.92,
            drawOdds = 3.40,
            awayOdds = 3.85,
        ),
        SportsMatch(
            id = "fb_4",
            sport = SportCategory.FOOTBALL,
            league = "Northern Shield",
            homeTeam = "Ironwood Wanderers",
            awayTeam = "Crescent Town",
            startTimeLabel = "In 45m",
            kickoffCountdownSeconds = 35,
            isLive = false,
            totalSimulatedSeconds = 45,
            homeOdds = 2.45,
            drawOdds = 3.20,
            awayOdds = 2.85,
        ),
        SportsMatch(
            id = "fb_5",
            sport = SportCategory.FOOTBALL,
            league = "Coastal Super League",
            homeTeam = "Bayview Rangers",
            awayTeam = "Oakridge Strikers",
            startTimeLabel = "Today 19:30",
            kickoffCountdownSeconds = 50,
            isLive = false,
            totalSimulatedSeconds = 45,
            homeOdds = 1.75,
            drawOdds = 3.60,
            awayOdds = 4.40,
        ),
        SportsMatch(
            id = "fb_6",
            sport = SportCategory.FOOTBALL,
            league = "Continental Division",
            homeTeam = "Valiant Sporting",
            awayTeam = "Highland Dynamos",
            startTimeLabel = "Today 21:00",
            kickoffCountdownSeconds = 65,
            isLive = false,
            totalSimulatedSeconds = 45,
            homeOdds = 2.60,
            drawOdds = 3.15,
            awayOdds = 2.65,
        ),

        // CRICKET (5 matches: 1 LIVE, 4 UPCOMING)
        SportsMatch(
            id = "cr_1",
            sport = SportCategory.CRICKET,
            league = "T20 Emerald Series",
            homeTeam = "Coastal Titans",
            awayTeam = "Sunridge Challengers",
            startTimeLabel = "LIVE 14.3 Ov",
            kickoffCountdownSeconds = 0,
            isLive = true,
            elapsedSeconds = 14,
            totalSimulatedSeconds = 44,
            homeScore = 138,
            awayScore = 122,
            liveDetail = "14.3 Ov",
            homeOdds = 1.72,
            drawOdds = 8.50,
            awayOdds = 2.25,
        ),
        SportsMatch(
            id = "cr_2",
            sport = SportCategory.CRICKET,
            league = "T20 Emerald Series",
            homeTeam = "Metro Monarchs",
            awayTeam = "Valley Strikers",
            startTimeLabel = "In 25m",
            kickoffCountdownSeconds = 24,
            isLive = false,
            totalSimulatedSeconds = 40,
            homeOdds = 1.88,
            drawOdds = 7.80,
            awayOdds = 2.05,
        ),
        SportsMatch(
            id = "cr_3",
            sport = SportCategory.CRICKET,
            league = "One-Day Trophy",
            homeTeam = "Redwood Falcons",
            awayTeam = "Emerald Capitals",
            startTimeLabel = "Today 18:00",
            kickoffCountdownSeconds = 42,
            isLive = false,
            totalSimulatedSeconds = 44,
            homeOdds = 2.30,
            drawOdds = 6.90,
            awayOdds = 1.78,
        ),
        SportsMatch(
            id = "cr_4",
            sport = SportCategory.CRICKET,
            league = "T20 Emerald Series",
            homeTeam = "Crown Kings",
            awayTeam = "Harbor Hurricanes",
            startTimeLabel = "Today 20:15",
            kickoffCountdownSeconds = 56,
            isLive = false,
            totalSimulatedSeconds = 44,
            homeOdds = 1.95,
            drawOdds = 8.20,
            awayOdds = 1.98,
        ),
        SportsMatch(
            id = "cr_5",
            sport = SportCategory.CRICKET,
            league = "Premier Over Cup",
            homeTeam = "Deccan Pioneers",
            awayTeam = "Summit Royals",
            startTimeLabel = "Tomorrow 14:00",
            kickoffCountdownSeconds = 72,
            isLive = false,
            totalSimulatedSeconds = 45,
            homeOdds = 2.10,
            drawOdds = 7.50,
            awayOdds = 1.86,
        ),

        // BASKETBALL (5 matches: 1 LIVE, 4 UPCOMING)
        SportsMatch(
            id = "bb_1",
            sport = SportCategory.BASKETBALL,
            league = "Apex Pro Hoops",
            homeTeam = "Summit Sixers",
            awayTeam = "Riverfront Hawks",
            startTimeLabel = "LIVE Q3 04:18",
            kickoffCountdownSeconds = 0,
            isLive = true,
            elapsedSeconds = 16,
            totalSimulatedSeconds = 46,
            homeScore = 76,
            awayScore = 72,
            liveDetail = "Q3",
            homeOdds = 1.62,
            drawOdds = 9.20,
            awayOdds = 2.45,
        ),
        SportsMatch(
            id = "bb_2",
            sport = SportCategory.BASKETBALL,
            league = "Apex Pro Hoops",
            homeTeam = "Vanguard Blaze",
            awayTeam = "Canyon Wolves",
            startTimeLabel = "In 15m",
            kickoffCountdownSeconds = 22,
            isLive = false,
            totalSimulatedSeconds = 42,
            homeOdds = 1.80,
            drawOdds = 9.40,
            awayOdds = 2.10,
        ),
        SportsMatch(
            id = "bb_3",
            sport = SportCategory.BASKETBALL,
            league = "Metro Court League",
            homeTeam = "Lakeside Bears",
            awayTeam = "Steelworks Giants",
            startTimeLabel = "Today 19:45",
            kickoffCountdownSeconds = 38,
            isLive = false,
            totalSimulatedSeconds = 44,
            homeOdds = 2.25,
            drawOdds = 9.00,
            awayOdds = 1.74,
        ),
        SportsMatch(
            id = "bb_4",
            sport = SportCategory.BASKETBALL,
            league = "Apex Pro Hoops",
            homeTeam = "Pacific Breakers",
            awayTeam = "Skyline Flyers",
            startTimeLabel = "Today 21:30",
            kickoffCountdownSeconds = 54,
            isLive = false,
            totalSimulatedSeconds = 44,
            homeOdds = 1.90,
            drawOdds = 9.50,
            awayOdds = 1.96,
        ),
        SportsMatch(
            id = "bb_5",
            sport = SportCategory.BASKETBALL,
            league = "Continental Hoop Cup",
            homeTeam = "Northstar Guards",
            awayTeam = "Beacon Mavericks",
            startTimeLabel = "Tomorrow 16:00",
            kickoffCountdownSeconds = 70,
            isLive = false,
            totalSimulatedSeconds = 45,
            homeOdds = 1.68,
            drawOdds = 9.10,
            awayOdds = 2.35,
        ),

        // TENNIS (4 matches: 1 LIVE, 3 UPCOMING)
        SportsMatch(
            id = "tn_1",
            sport = SportCategory.TENNIS,
            league = "Grand Hardcourt Open",
            homeTeam = "L. Navarro",
            awayTeam = "M. Kovalenko",
            startTimeLabel = "LIVE Set 2",
            kickoffCountdownSeconds = 0,
            isLive = true,
            elapsedSeconds = 12,
            totalSimulatedSeconds = 42,
            homeScore = 1,
            awayScore = 1,
            liveDetail = "Set 2 (4-3)",
            homeOdds = 1.84,
            drawOdds = 6.50,
            awayOdds = 2.02,
        ),
        SportsMatch(
            id = "tn_2",
            sport = SportCategory.TENNIS,
            league = "Grand Hardcourt Open",
            homeTeam = "D. Takahashi",
            awayTeam = "R. Morales",
            startTimeLabel = "In 30m",
            kickoffCountdownSeconds = 26,
            isLive = false,
            totalSimulatedSeconds = 40,
            homeOdds = 1.58,
            drawOdds = 7.20,
            awayOdds = 2.55,
        ),
        SportsMatch(
            id = "tn_3",
            sport = SportCategory.TENNIS,
            league = "Emerald Clay Classic",
            homeTeam = "S. Lindqvist",
            awayTeam = "A. Moreau",
            startTimeLabel = "Today 18:45",
            kickoffCountdownSeconds = 46,
            isLive = false,
            totalSimulatedSeconds = 42,
            homeOdds = 2.12,
            drawOdds = 6.80,
            awayOdds = 1.82,
        ),
        SportsMatch(
            id = "tn_4",
            sport = SportCategory.TENNIS,
            league = "Emerald Clay Classic",
            homeTeam = "E. Vance",
            awayTeam = "K. Petrov",
            startTimeLabel = "Today 20:30",
            kickoffCountdownSeconds = 60,
            isLive = false,
            totalSimulatedSeconds = 44,
            homeOdds = 1.95,
            drawOdds = 6.90,
            awayOdds = 1.95,
        ),
    )

    private val teamPools = mapOf(
        SportCategory.FOOTBALL to listOf(
            "Westfield Athletic", "Portside FC", "Cedarwood United", "Bayside Rovers",
            "Granite City", "Aurora Town", "Midland Strikers", "Redcliff Rangers",
        ),
        SportCategory.CRICKET to listOf(
            "Emerald Knights", "Horizon Royals", "Savanna Scorchers", "Port Hurricanes",
            "Metro Sixers", "Highland Falcons", "Coral Mavericks", "Summit Chargers",
        ),
        SportCategory.BASKETBALL to listOf(
            "Cascade Timber", "Harbor Hornets", "Pinnacle Suns", "Driftwood Kings",
            "Capital Rockets", "Ridgeview Grizzlies", "Bayshore Clippers", "Metro Pistons",
        ),
        SportCategory.TENNIS to listOf(
            "T. Halvorsen", "C. Delacroix", "N. SATO", "P. Novak",
            "F. Conti", "J. Mercer", "H. Al-Mansoor", "B. Silva",
        ),
    )

    private val leagueBySport = mapOf(
        SportCategory.FOOTBALL to "Metro Premier Cup",
        SportCategory.CRICKET to "T20 Emerald Series",
        SportCategory.BASKETBALL to "Apex Pro Hoops",
        SportCategory.TENNIS to "Grand Hardcourt Open",
    )

    fun createReplacementMatch(sport: SportCategory): SportsMatch {
        replacementCounter++
        val pool = teamPools.getValue(sport).shuffled()
        val home = pool[0]
        val away = pool[1]
        val hOdds = roundOdds(Random.nextDouble(1.55, 2.85))
        val dOdds = when (sport) {
            SportCategory.FOOTBALL -> roundOdds(Random.nextDouble(2.95, 3.75))
            SportCategory.CRICKET -> roundOdds(Random.nextDouble(6.50, 8.80))
            SportCategory.BASKETBALL -> roundOdds(Random.nextDouble(8.50, 9.50))
            SportCategory.TENNIS -> roundOdds(Random.nextDouble(6.20, 7.60))
        }
        val aOdds = roundOdds(Random.nextDouble(1.60, 3.60))
        return SportsMatch(
            id = "${sport.name.lowercase()}_$replacementCounter",
            sport = sport,
            league = leagueBySport.getValue(sport),
            homeTeam = home,
            awayTeam = away,
            startTimeLabel = "Upcoming",
            kickoffCountdownSeconds = Random.nextInt(25, 65),
            isLive = false,
            totalSimulatedSeconds = Random.nextInt(38, 48),
            homeOdds = hOdds,
            drawOdds = dOdds,
            awayOdds = aOdds,
        )
    }

    fun fluctuateOdds(current: Double): Pair<Double, OddsTrend> {
        val delta = Random.nextDouble(-0.12, 0.13)
        val updated = roundOdds((current + delta).coerceIn(1.15, 9.50))
        val trend = when {
            updated > current + 0.005 -> OddsTrend.UP
            updated < current - 0.005 -> OddsTrend.DOWN
            else -> OddsTrend.NONE
        }
        return updated to trend
    }

    fun resolveOutcomeForFinishedMatch(match: SportsMatch): Triple<PickOutcome, Int, Int> {
        // Weighted slightly by odds while keeping realistic unpredictability
        val invHome = 1.0 / match.homeOdds
        val invDraw = 1.0 / match.drawOdds
        val invAway = 1.0 / match.awayOdds
        val total = invHome + invDraw + invAway
        val roll = Random.nextDouble() * total

        val chosenOutcome = when {
            roll < invHome -> PickOutcome.HOME
            roll < invHome + invDraw -> PickOutcome.DRAW
            else -> PickOutcome.AWAY
        }

        var h = match.homeScore
        var a = match.awayScore

        when (chosenOutcome) {
            PickOutcome.HOME -> if (h <= a) h = a + Random.nextInt(1, 3)
            PickOutcome.AWAY -> if (a <= h) a = h + Random.nextInt(1, 3)
            PickOutcome.DRAW -> {
                val tied = maxOf(h, a)
                h = tied
                a = tied
            }
        }
        return Triple(chosenOutcome, h, a)
    }

    fun roundOdds(value: Double): Double = (value * 100.0).roundToInt() / 100.0
}
