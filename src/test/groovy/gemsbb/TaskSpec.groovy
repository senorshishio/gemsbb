package gemsbb


import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Task)
class TaskSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    def 'nullable values'() {
        when: 'null name'
        def t = new Task(responsible: new Member(name: 'christian', email: 'christian@gonzalez.cl'),
                startDate: new Date(), dueDate: new Date(), status: 'Finished')

        then: 'validation should fail'
        !t.validate()

        when: 'null responsible'
        t = new Task(name: 'Christian', startDate: new Date(), dueDate: new Date(), status: 'Finished')

        then: 'validation should fail'
        !t.validate()

        when: 'null startDate'
        t = new Task(responsible: new Member(name: 'christian', email: 'christian@gonzalez.cl'),
                name: 'Christian', dueDate: new Date(), status: 'Finished')

        then: 'validation should fail'
        !t.validate()

        when: 'null dueDate'
        t = new Task(responsible: new Member(name: 'christian', email: 'christian@gonzalez.cl'),
                name: 'Christian', startDate: new Date(), status: 'Finished')

        then: 'validation should fail'
        !t.validate()

        when: 'null status'
        t = new Task(responsible: new Member(name: 'christian', email: 'christian@gonzalez.cl'),
                name: 'Christian', startDate: new Date(), dueDate: new Date())

        then: 'validation should fail'
        !t.validate()

        when: 'null dueDate'
        t = new Task(responsible: new Member(name: 'christian', email: 'christian@gonzalez.cl'),
                name: 'Christian', startDate: new Date(), dueDate: new Date(), status: 'Finished')

        then: 'validation should pass'
        t.validate()
    }
}
