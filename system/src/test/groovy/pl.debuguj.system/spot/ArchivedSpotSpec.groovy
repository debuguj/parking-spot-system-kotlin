package pl.debuguj.system.spot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.util.SerializationUtils
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import javax.validation.Validation
import javax.validation.Validator
import java.time.LocalDateTime

@DataJpaTest
class ArchivedSpotSpec extends Specification {

    @Shared
    @Subject
    ArchivedSpot archivedSpot

    @Autowired
    TestEntityManager entityManager
    @Shared
    Validator validator

    @Shared
    LocalDateTime defBeginTimestamp = LocalDateTime.now()
    @Shared
    LocalDateTime defEndTimestamp = LocalDateTime.now().plusHours(2L)
    @Shared
    String defaultVehiclePlate = 'WZE12345'

    @Shared
    Set<ArchivedSpot> archivedSpots = new HashSet<>()

    def setupSpec() {
        def vf = Validation.buildDefaultValidatorFactory()
        this.validator = vf.getValidator()
    }

    def setup() {
        archivedSpot = new ArchivedSpot(defaultVehiclePlate, DriverType.REGULAR, defBeginTimestamp, defEndTimestamp)
        archivedSpots.add(archivedSpot)
    }

    def cleanup() {
        archivedSpots.removeAll()
    }

    def 'given archived spot should be stored in set'() {
        expect: 'archived spot in set'
        archivedSpots.contains(archivedSpot)
    }

    def 'after persist to database archived spot should have id'() {
        expect: 'empty id in archived spot object'
        !archivedSpot.getId()

        when: 'persist to database'
        entityManager.persistAndFlush(archivedSpot)

        then: 'should have id from database'
        archivedSpot.getId()

        and: 'archived spot should be found in set'
        archivedSpots.contains(archivedSpot)
    }

    def 'merge should be succeed'() {
        when: 'merge archived spot'
        ArchivedSpot mergedArchivedSpot = entityManager.merge(archivedSpot)

        and: 'flush persistent context'
        entityManager.flush()

        then: 'set contains archived spot should contains default archived spot'
        archivedSpots.contains(mergedArchivedSpot)
    }

    def 'archived spot should persist in database'() {
        when: 'persist to database'
        entityManager.persistAndFlush(archivedSpot)

        and: 'archived spot was found'
        ArchivedSpot foundArchivedSpot = entityManager.find(ArchivedSpot.class, archivedSpot.id)

        and: 'flush persistent context'
        entityManager.flush()

        then: 'set contains archived spot should contains default archived spot'
        archivedSpots.contains(foundArchivedSpot)
    }

    def 'check detached archived spot'() {
        when: 'persist to database'
        entityManager.persistAndFlush(archivedSpot)

        and: 'archived spot was found'
        ArchivedSpot foundArchivedSpot = entityManager.find(ArchivedSpot.class, archivedSpot.id)

        and: 'flush persistent context'
        entityManager.flush()

        then: 'set contains archived spot should contains default archived spot'
        archivedSpots.contains(foundArchivedSpot)

        when: 'removing from set'
        archivedSpots.remove(foundArchivedSpot)

        then: 'set should not contains archived spot'
        !archivedSpots.contains(foundArchivedSpot)
    }

    def 'check finding and detaching'() {
        when: 'persist to database'
        entityManager.persistAndFlush(archivedSpot)

        and: 'archived spot was found'
        ArchivedSpot foundArchivedSpot = entityManager.find(ArchivedSpot.class, archivedSpot.id)

        and: 'detached object'
        entityManager.detach(foundArchivedSpot)

        then: 'set contains archived spot should contains default archived spot'
        archivedSpots.contains(foundArchivedSpot)
    }

    def 'validation of saved archived spot'() {
        when: 'save to database'
        entityManager.persistAndFlush(archivedSpot)

        and: 'archived spot was found'
        ArchivedSpot found = entityManager.find(ArchivedSpot.class, archivedSpot.id)

        then: 'parameters should be valid'
        with(archivedSpot) {
            vehiclePlate == found.vehiclePlate
            driverType == found.driverType
            beginTimestamp == found.beginTimestamp
            endTimestamp == found.endTimestamp
            uuid == found.uuid
        }
    }

    def 'should be serialized correctly'() {
        given: "serialization #archivedSpot to #other object"
        def other = (ArchivedSpot) SerializationUtils.deserialize(SerializationUtils.serialize(archivedSpot))

        expect: 'should return valid and correct values'
        with(other) {
            vehiclePlate == archivedSpot.vehiclePlate
            driverType == archivedSpot.driverType
            beginTimestamp == archivedSpot.beginTimestamp
            endTimestamp == archivedSpot.endTimestamp
        }
    }

    def 'should returns no error after valid input params'() {
        expect: 'valid parameters'
        with(archivedSpot) {
            vehiclePlate == defaultVehiclePlate
            driverType == DriverType.REGULAR
            driverType != DriverType.VIP
            beginTimestamp == defBeginTimestamp
            endTimestamp == defEndTimestamp
        }
    }

    def 'should returns non null params'() {
        expect: 'not null params'
        with(archivedSpot) {
            vehiclePlate
            driverType
            beginTimestamp
            endTimestamp
        }
    }

    def 'should throw NullPointerException'() {
        when: 'archive spot with invalid finish date'
        def invalidArchivedSpot = new ArchivedSpot(
                defaultVehiclePlate, DriverType.REGULAR, defBeginTimestamp, null)

        then: 'invalidArchivesSpot as null'
        !invalidArchivedSpot

        and: 'NullPointer throw'
        thrown(NullPointerException)
    }

//    def 'should throw an exception because finish date is before start date'() {
//        given: 'incorrect end timestamp'
//        def invalidEndTimestamp = defBeginTimestamp.minusHours(2L)
//
//        and: 'simple spot for test'
//        def spot = new Spot(defaultVehiclePlate, DriverType.REGULAR, defBeginTimestamp)
//
//        when: 'new archived spot created'
//        def invalidArchivedSpot = new ArchivedSpot(spot, invalidEndTimestamp)
//
//        then: 'should be null'
//        !invalidArchivedSpot
//
//        and: 'should throw an exception'
//        IncorrectFinishDateException e = thrown()
//        !e.cause
//    }

}
