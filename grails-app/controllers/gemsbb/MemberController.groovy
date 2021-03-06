package gemsbb

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.OK

import grails.rest.*
import grails.converters.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class MemberController {
    static responseFormats = ['json']
    static allowedMethods = [save: "POST", update: "PUT", index: "GET"]

    def index(Integer max) {
        def queryParams = params
        queryParams.max = Math.min(max ?: 10, 100)

        def query = Member.where {
        }

        if(queryParams.email != null) {
            query = query.where {
                email == queryParams.email
            }
        }

        respond query.findAll(), model:[memberCount: query.count()]
    }

    def show(Member member) {
        respond member
    }

    @Transactional
    def save(Member member) {
        if (member == null) {
            transactionStatus.setRollbackOnly()
            render status: NOT_FOUND
            return
        }

        if (member.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond member.errors, view:'create'
            return
        }

        member.save flush:true

        respond member, [status: CREATED, view:"show"]
    }

    @Transactional
    def update(Member member) {
        if (member == null) {
            transactionStatus.setRollbackOnly()
            render status: NOT_FOUND
            return
        }

        if (member.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond member.errors, view:'edit'
            return
        }

        member.save(flush: true)

        respond(member, [status: OK, view:"show"])
    }

    // Se debe filtrar por empresa!!!
    /*@Override
    protected Member queryForResource(Serializable id) {
        Book.where {
            id == id && author.id = params.authorId
        }.find()
    }*/
}
