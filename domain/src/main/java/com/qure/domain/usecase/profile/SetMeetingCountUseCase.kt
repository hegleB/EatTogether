package com.qure.domain.usecase.profile

import com.qure.domain.repository.MeetingRepository
import javax.inject.Inject

class SetMeetingCountUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) {
    suspend operator fun invoke(uid: String, count: Int) =
        meetingRepository.setMeetingCount(uid, count)
}