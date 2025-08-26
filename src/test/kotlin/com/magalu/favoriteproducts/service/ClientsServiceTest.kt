package com.magalu.favoriteproducts.service

import com.magalu.favoriteproducts.domain.ClientsPort
import com.magalu.favoriteproducts.domain.ClientsTokenPort
import com.magalu.favoriteproducts.domain.model.Client
import com.magalu.favoriteproducts.domain.service.ClientsService
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import kotlin.test.assertEquals

class ClientsServiceTest {
    private val clientsPort: ClientsPort = mockk()
    private val clientsTokenPort: ClientsTokenPort = mockk()
    private val clientsService = ClientsService(clientsPort, clientsTokenPort)

    @Test
    fun `should create client successfully`() {
        val email = "foobar@yahoo.com"
        val client = Client(name = "Foo", email = email)
        val slotClient = slot<Client>()

        every { clientsTokenPort.emailAlreadyExists(client.email) } returns false
        every { clientsPort.existsByEmail(client.email) } returns false

        every { clientsPort.create(capture(slotClient)) } returns
            client.copy(id = 1, createdAt = OffsetDateTime.now(), updatedAt = OffsetDateTime.now())
        every { clientsTokenPort.setEmail(client.email) } returns true

        val result = clientsService.create(client)

        verify(exactly = 1) {
            clientsTokenPort.emailAlreadyExists(any())
            clientsPort.existsByEmail(any())
            clientsPort.create(any())
            clientsTokenPort.setEmail(slotClient.captured.email)
        }

        assertEquals(email, result.email)
    }

    @Test
    fun `should update client successfully`() {
    }
}
