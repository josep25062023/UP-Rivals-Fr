package com.example.up_rivals

import com.example.up_rivals.network.dto.MatchDto
import com.google.gson.Gson
import org.junit.Test
import org.junit.Assert.*

class MatchDtoTest {

    @Test
    fun testMatchDtoDeserialization() {
        val jsonResponse = """
        {
            "id": "b748bad4-c770-432c-9ec2-76c44e9ba374",
            "teamA": {
                "id": "34ae4204-06a1-45a6-a8b7-717b68bc79c6",
                "name": "Asus ",
                "logo": null,
                "captain": {
                    "id": "53672de4-e6f3-475e-8a23-6858418b223b",
                    "email": "223280@ids.upchiapas.edu.mx",
                    "name": "Prueba equipos",
                    "phone": "9661089679",
                    "profilePicture": null,
                    "institution": null,
                    "career": null,
                    "role": "player",
                    "isActive": true
                }
            },
            "teamB": {
                "id": "7a253671-0f4a-4632-910a-dffa85dbf2ea",
                "name": "Real Madrid fc",
                "logo": null,
                "captain": {
                    "id": "a3ce293c-ac5b-405a-9fdc-0c48969a33f3",
                    "email": "223282@ids.upchiapas.edu.mx",
                    "name": "Jhair Alejandro",
                    "phone": "9661089679",
                    "profilePicture": null,
                    "institution": null,
                    "career": null,
                    "role": "player",
                    "isActive": true
                }
            },
            "teamAScore": null,
            "teamBScore": null,
            "date": "2025-07-10T16:09:22.875Z",
            "status": "scheduled"
        }
        """.trimIndent()

        val gson = Gson()
        val match = gson.fromJson(jsonResponse, MatchDto::class.java)

        // Verificar que la deserialización fue exitosa
        assertNotNull(match)
        assertEquals("b748bad4-c770-432c-9ec2-76c44e9ba374", match.id)
        assertEquals("scheduled", match.status)
        assertEquals("2025-07-10T16:09:22.875Z", match.matchDate)
        
        // Verificar teamA
        assertEquals("34ae4204-06a1-45a6-a8b7-717b68bc79c6", match.teamA.id)
        assertEquals("Asus ", match.teamA.name)
        assertEquals("Prueba equipos", match.teamA.captain.name)
        assertEquals("223280@ids.upchiapas.edu.mx", match.teamA.captain.email)
        assertEquals(true, match.teamA.captain.isActive)
        
        // Verificar teamB
        assertEquals("7a253671-0f4a-4632-910a-dffa85dbf2ea", match.teamB.id)
        assertEquals("Real Madrid fc", match.teamB.name)
        assertEquals("Jhair Alejandro", match.teamB.captain.name)
        assertEquals("223282@ids.upchiapas.edu.mx", match.teamB.captain.email)
        assertEquals(true, match.teamB.captain.isActive)
        
        // Verificar scores
        assertNull(match.scoreA)
        assertNull(match.scoreB)
    }
}