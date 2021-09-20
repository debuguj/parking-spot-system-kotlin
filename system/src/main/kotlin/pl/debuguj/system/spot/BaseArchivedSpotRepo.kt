package pl.debuguj.system.spot

import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository
import java.io.Serializable

@NoRepositoryBean
interface BaseArchivedSpotRepo<T, ID : Serializable> : Repository<T, ID> {

    fun save(entity: T): ArchivedSpot

}