package pl.debuguj.system.spot

import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime


@Repository
@Transactional(readOnly = true)
interface ArchivedSpotRepo : BaseArchivedSpotRepo<ArchivedSpot, Long> {

    fun findAllByBeginTimestamp(beginTimestamp: LocalDateTime): List<ArchivedSpot>
}