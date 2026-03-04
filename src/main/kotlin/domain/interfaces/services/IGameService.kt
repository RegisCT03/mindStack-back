package com.MindStack.domain.interfaces.services

import com.MindStack.application.dtos.Request.MemoryGameRequest
import com.MindStack.application.dtos.Request.NeuroReflexRequest
import com.MindStack.application.dtos.Response.CombinedBatteryResponse
import com.MindStack.application.dtos.Response.MemoryGameResponse
import com.MindStack.application.dtos.Response.NeuroReflexResponse

interface IGameService {
    suspend fun submitNeuroReflex(userId: Int, req: NeuroReflexRequest): NeuroReflexResponse
    suspend fun submitMemoryGame(userId: Int, req: MemoryGameRequest): MemoryGameResponse
    suspend fun getCombinedBattery(userId: Int, checkinId: Int): CombinedBatteryResponse
}