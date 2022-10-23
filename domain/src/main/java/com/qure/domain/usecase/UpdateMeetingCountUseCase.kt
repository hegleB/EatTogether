package com.qure.domain.usecase

import com.qure.domain.repository.MeetingRepository
import javax.inject.Inject

class UpdateMeetingCountUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) {
    suspend operator fun invoke(uid : String, count : Int) = meetingRepository.updateMeetingCount(uid, count)
}