package pl.debuguj.system.driver

import com.fasterxml.jackson.databind.ObjectMapper
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.debuguj.system.calculations.FeeCalculator
import pl.debuguj.system.exceptions.VehicleNotExistsInDbException
import pl.debuguj.system.spot.*
import spock.lang.Shared
import spock.lang.Specification

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [DriverController])
class DriverControllerSpec extends Specification {

    @Value('${uri.driver.start}')
    private String uriStartMeter
    @Value('${uri.driver.stop}')
    private String uriStopMeter
    @Value('${date.time.format}')
    private String dateTimePattern

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private ObjectMapper objectMapper

    @SpringBean
    SpotRepo spotRepo = Stub()
    @SpringBean
    ArchivedSpotRepo archivedSpotRepo = Stub()
    @SpringBean
    FeeCalculator feeCalculator = Stub()
    @Shared
    static Spot spot = new Spot('WZE12345', DriverType.REGULAR, LocalDateTime.now())

    static ArchivedSpot archivedSpot = new ArchivedSpot(spot, spot.beginDateTime.plusHours(2L))

    def 'MockMvc should be created'() {
        expect: 'mockMvc must be not null'
        mockMvc
    }

    def 'should return correct payload, format and value'() {
        given: 'spot returned by repo'
        spotRepo.save(_ as Spot) >> spot

        when: 'perform request'
        def results = mockMvc.perform(post(uriStartMeter)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(spot)))

        then: 'return correct status'
        results.andExpect(status().isOk())

        and: 'return correct values in content'
        results.andExpect(content().contentType('application/json'))
        results.andExpect(content().json(objectMapper.writeValueAsString(spot)))
    }

    def 'should return redirection because vehicle is active'() {
        given: 'exception returned by repo'
        spotRepo.existsByVehiclePlate(_ as String) >> Boolean.TRUE

        when: 'perform request'
        def results = mockMvc.perform(post(uriStartMeter)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(spot)))

        then: 'return correct status'
        results.andExpect(status().is3xxRedirection())

        and: 'correct type of context negotiation'
        results.andExpect(content().contentType('application/json'))
    }

    def 'should return NotFound because vehicle is not active'() {
        given: 'throw exception when method find by plate invoked'
        spotRepo.findByVehiclePlate(_ as String) >>
                { throw new VehicleNotExistsInDbException('WZE12345') }

        when: 'perform request'
        def results = mockMvc.perform(patch(uriStopMeter, spot.getVehiclePlate())
                .param('finishDate', spot.beginDateTime.format(DateTimeFormatter.ofPattern(dateTimePattern)))
                .contentType(MediaType.APPLICATION_JSON))

        then: 'return status NotFound'
        results.andExpect(status().isNotFound())

        and: 'correct type of context negotiation'
        results.andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    def 'should return correct fee'() {
        given: 'return optional of spot'
        spotRepo.findByVehiclePlate(_ as String) >> spot

        and: 'fee from archived repository'
        archivedSpotRepo.save(_ as ArchivedSpot) >> archivedSpot

        when: 'perform request'
        def results = mockMvc.perform(patch(uriStopMeter, spot.getVehiclePlate())
                .param('finishDate', spot.beginDateTime.plusHours(2L).format(DateTimeFormatter.ofPattern(dateTimePattern)))
                .contentType('application/json'))

        then: 'return status isOk'
        results.andExpect(status().isOk())

        and: 'correct type of context negotiation'
        results.andExpect(content().contentType('application/json'))
    }
}
