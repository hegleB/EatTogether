package com.qure.domain.usecase

import com.qure.domain.repository.MeetingRepository
import javax.inject.Inject

class GetMeetingCountUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) {
    suspend operator fun invoke(uid : String) = meetingRepository.getMeetingCount(uid)
}