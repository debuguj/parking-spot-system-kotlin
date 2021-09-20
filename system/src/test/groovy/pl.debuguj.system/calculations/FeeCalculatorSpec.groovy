package pl.debuguj.system.calculations

import org.spockframework.spring.SpringBean
import pl.debuguj.system.external.systems.CurrencyRate
import pl.debuguj.system.external.systems.CurrencyRateHandler
import pl.debuguj.system.spot.ArchivedSpot
import pl.debuguj.system.spot.DriverType
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import java.time.LocalDateTime

class FeeCalculatorSpec extends Specification {

    @Shared
    @Subject
    FeeCalculator feeCalculator
    private CurrencyRateHandler currencyRateHandler = Stub()

    @Shared
    ArchivedSpot archivedSpot
    @Shared
    LocalDateTime defBeginTimestamp = LocalDateTime.now()
    @Shared
    LocalDateTime defEndTimestamp = LocalDateTime.now().plusHours(2L)
    @Shared
    String defaultVehiclePlate = 'WZE12345'

    def setup() {
        feeCalculator = new FeeCalculator(currencyRateHandler)
        archivedSpot = new ArchivedSpot(defaultVehiclePlate, DriverType.REGULAR, defBeginTimestamp, defEndTimestamp)
    }

    @Unroll
    def "should return fee equals to #assumedFee for REGULAR driver and given default currency rate PLN"() {
        given: 'archivedSpot with valid input'
        def archivedSpot = new ArchivedSpot(defaultVehiclePlate, DriverType.REGULAR,
                LocalDateTime.parse(beginDate), LocalDateTime.parse(endDate))

        and: 'PLN as default currency and rate'
        currencyRateHandler.getCurrencyRate() >> CurrencyRate.PLN

        expect: 'correct fee value'
        assumedFee == feeCalculator.getFee(archivedSpot)

        where: "valid #fee for period between #beginDate and #endDate"
        beginDate             | endDate               || assumedFee
        '2020-06-12T11:15:48' | '2020-06-12T11:35:12' || 1.0
        '2020-06-12T11:15:48' | '2020-06-12T12:35:12' || 3.0
        '2020-06-12T11:15:48' | '2020-06-12T13:35:12' || 7.0
        '2020-06-12T11:15:48' | '2020-06-12T16:35:12' || 63.0
        '2020-06-12T00:15:48' | '2020-06-12T15:35:12' || 65535.0
        '2020-06-12T11:15:48' | '2020-06-13T11:14:12' || 16777215.0
        '2020-06-12T10:10:10' | '2020-06-12T22:13:10' || 8191.0
    }

    @Unroll
    def "should return fee equals to #assumedFee for period #beginDate - #endDate for VIP driver and given default currency rate"() {
        given: 'archived spot with valid input'
        ArchivedSpot archivedSpot1 = new ArchivedSpot(defaultVehiclePlate, DriverType.VIP,
                LocalDateTime.parse(beginDate), LocalDateTime.parse(endDate))

        and: 'PLN as default currency and rate'
        currencyRateHandler.getCurrencyRate() >> CurrencyRate.PLN

        expect: 'correct fee value'
        assumedFee == feeCalculator.getFee(archivedSpot1)

        where: "valid #assumedFee between #beginDate and #endDate"
        beginDate             | endDate               || assumedFee
        '2020-10-12T11:15:48' | '2020-10-12T11:35:12' || 0.0
        '2020-10-12T11:15:48' | '2020-10-12T12:35:12' || 2.0
        '2020-10-12T11:15:48' | '2020-10-12T13:35:12' || 5.0
        '2020-10-12T11:15:48' | '2020-10-12T16:35:12' || 26.4
        '2020-10-12T00:15:48' | '2020-10-12T15:35:12' || 1747.6
        '2020-10-12T11:15:48' | '2020-10-13T11:14:12' || 44887.0
    }
}
