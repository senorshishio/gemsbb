package gemsbb

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.OK

import grails.rest.*
import grails.converters.*
import grails.transaction.Transactional
import org.bson.types.ObjectId

@Transactional(readOnly = true)
class PlanController {
    static responseFormats = ['json']
    static allowedMethods = [save: "POST", update: "PUT", index: "GET"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        if(params.projectId != null) {
            def result = Plan.findAllByProject(new ObjectId(params.projectId))
            respond result, model:[planCount: result.size()]
        }
        else {
            respond Plan.list(params), model:[planCount: Plan.count()]
        }
    }

    def show(Plan plan) {
        respond plan
    }

    @Transactional
    def save(Plan plan) {
        if (plan == null) {
            transactionStatus.setRollbackOnly()
            render status: NOT_FOUND
            return
        }

        if (plan.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond plan.errors, view:'create'
            return
        }

        plan.save flush:true

        respond plan, [status: CREATED, view:"show"]
    }

    @Transactional
    def update(Plan plan) {
        if (plan == null) {
            transactionStatus.setRollbackOnly()
            render status: NOT_FOUND
            return
        }

        if (plan.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond plan.errors, view:'edit'
            return
        }

        plan.save flush:true

        respond plan, [status: OK, view:"show"]
    }

    @Transactional
    def delete(Plan plan) {

        if (plan == null) {
            transactionStatus.setRollbackOnly()
            render status: NOT_FOUND
            return
        }

        plan.delete flush:true

        render status: NO_CONTENT
    }
}
